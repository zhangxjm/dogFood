package com.seckill.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.constant.MqConstants;
import com.seckill.common.constant.RedisConstants;
import com.seckill.common.entity.Product;
import com.seckill.common.entity.SeckillActivity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.mq.SeckillOrderMessage;
import com.seckill.common.result.PageResult;
import com.seckill.common.util.DistributedLockUtils;
import com.seckill.common.util.SnowflakeIdGenerator;
import com.seckill.seckill.dto.SeckillExecuteDTO;
import com.seckill.seckill.mapper.SeckillActivityMapper;
import com.seckill.seckill.service.SeckillActivityService;
import com.seckill.common.vo.SeckillResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity> implements SeckillActivityService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StreamBridge streamBridge;

    @Override
    public PageResult<SeckillActivity> getActivityList(Integer pageNum, Integer pageSize, Integer status, Long productId) {
        Page<SeckillActivity> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<SeckillActivity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SeckillActivity::getStatus, status);
        }
        if (productId != null) {
            wrapper.eq(SeckillActivity::getProductId, productId);
        }
        wrapper.orderByDesc(SeckillActivity::getCreateTime);
        
        page = page(page, wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize);
    }

    @Override
    public SeckillActivity getActivityDetail(Long activityId) {
        String cacheKey = RedisConstants.SECKILL_ACTIVITY_KEY + activityId;
        
        SeckillActivity activity = (SeckillActivity) redisTemplate.opsForValue().get(cacheKey);
        if (activity != null) {
            return activity;
        }
        
        activity = getById(activityId);
        if (activity == null) {
            throw BusinessException.of("秒杀活动不存在");
        }
        
        redisTemplate.opsForValue().set(cacheKey, activity, 5, TimeUnit.MINUTES);
        
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillResultVO executeSeckill(Long userId, SeckillExecuteDTO dto) {
        Long activityId = dto.getActivityId();
        SeckillActivity activity = getActivityDetail(activityId);
        
        validateSeckill(activity, userId, dto.getQuantity());
        
        String stockKey = RedisConstants.SECKILL_STOCK_KEY + activityId;
        String userKey = RedisConstants.SECKILL_USER_KEY + activityId + ":" + userId;
        String resultKey = RedisConstants.SECKILL_ORDER_RESULT_KEY + activityId + ":" + userId;
        
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userKey))) {
            throw BusinessException.of("您已参与过该秒杀活动");
        }
        
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            throw BusinessException.of("商品已售罄");
        }
        
        String lockKey = RedisConstants.SECKILL_LOCK_KEY + activityId;
        try {
            boolean locked = DistributedLockUtils.tryLock(lockKey, 5, 30, TimeUnit.SECONDS);
            if (!locked) {
                redisTemplate.opsForValue().increment(stockKey);
                throw BusinessException.of("系统繁忙，请稍后再试");
            }
            
            redisTemplate.opsForValue().set(userKey, 1, 24, TimeUnit.HOURS);
            
            String orderNo = SnowflakeIdGenerator.generateId();
            
            SeckillOrderMessage message = SeckillOrderMessage.builder()
                    .activityId(activityId)
                    .productId(activity.getProductId())
                    .userId(userId)
                    .quantity(dto.getQuantity())
                    .seckillPrice(activity.getSeckillPrice())
                    .orderNo(orderNo)
                    .createTime(LocalDateTime.now())
                    .build();
            
            streamBridge.send("seckillOrderOutput", message);
            
            SeckillResultVO result = SeckillResultVO.builder()
                    .status("pending")
                    .message("秒杀排队中，请稍后查看结果")
                    .activityId(activityId)
                    .build();
            
            redisTemplate.opsForValue().set(resultKey, result, 1, TimeUnit.HOURS);
            
            log.info("秒杀请求已发送到消息队列: userId={}, activityId={}, orderNo={}", userId, activityId, orderNo);
            
            return result;
            
        } catch (Exception e) {
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.delete(userKey);
            throw e;
        } finally {
            DistributedLockUtils.unlock(lockKey);
        }
    }

    @Override
    public SeckillResultVO getSeckillResult(Long userId, Long activityId) {
        String resultKey = RedisConstants.SECKILL_ORDER_RESULT_KEY + activityId + ":" + userId;
        SeckillResultVO result = (SeckillResultVO) redisTemplate.opsForValue().get(resultKey);
        
        if (result == null) {
            String userKey = RedisConstants.SECKILL_USER_KEY + activityId + ":" + userId;
            Boolean hasKey = redisTemplate.hasKey(userKey);
            
            if (Boolean.TRUE.equals(hasKey)) {
                return SeckillResultVO.builder()
                        .status("pending")
                        .message("秒杀处理中，请稍后再试")
                        .activityId(activityId)
                        .build();
            }
            
            return SeckillResultVO.builder()
                    .status("fail")
                    .message("未查询到秒杀记录")
                    .activityId(activityId)
                    .build();
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long activityId, Integer quantity) {
        int rows = baseMapper.deductStock(activityId, quantity);
        if (rows <= 0) {
            log.warn("秒杀库存扣减失败: activityId={}, quantity={}", activityId, quantity);
            return false;
        }
        
        String cacheKey = RedisConstants.SECKILL_ACTIVITY_KEY + activityId;
        redisTemplate.delete(cacheKey);
        
        log.info("秒杀库存扣减成功: activityId={}, quantity={}", activityId, quantity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long activityId, Integer quantity) {
        baseMapper.addStock(activityId, quantity);
        
        String stockKey = RedisConstants.SECKILL_STOCK_KEY + activityId;
        redisTemplate.opsForValue().increment(stockKey, quantity);
        
        String cacheKey = RedisConstants.SECKILL_ACTIVITY_KEY + activityId;
        redisTemplate.delete(cacheKey);
        
        log.info("秒杀库存返还: activityId={}, quantity={}", activityId, quantity);
    }

    @Override
    public void initSeckillStock(Long activityId) {
        SeckillActivity activity = getById(activityId);
        if (activity == null) {
            return;
        }
        
        String stockKey = RedisConstants.SECKILL_STOCK_KEY + activityId;
        redisTemplate.opsForValue().set(stockKey, activity.getStock());
        
        String activityKey = RedisConstants.SECKILL_ACTIVITY_KEY + activityId;
        redisTemplate.opsForValue().set(activityKey, activity, 30, TimeUnit.MINUTES);
        
        log.info("秒杀活动库存预热完成: activityId={}, stock={}", activityId, activity.getStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<SeckillActivity> startWrapper = new LambdaQueryWrapper<>();
        startWrapper.eq(SeckillActivity::getStatus, 0)
                   .le(SeckillActivity::getStartTime, now)
                   .gt(SeckillActivity::getEndTime, now);
        
        for (SeckillActivity activity : list(startWrapper)) {
            activity.setStatus(1);
            updateById(activity);
            initSeckillStock(activity.getId());
            log.info("秒杀活动已开始: activityId={}", activity.getId());
        }
        
        LambdaQueryWrapper<SeckillActivity> endWrapper = new LambdaQueryWrapper<>();
        endWrapper.in(SeckillActivity::getStatus, 0, 1)
                  .le(SeckillActivity::getEndTime, now);
        
        for (SeckillActivity activity : list(endWrapper)) {
            activity.setStatus(2);
            updateById(activity);
            
            String stockKey = RedisConstants.SECKILL_STOCK_KEY + activity.getId();
            redisTemplate.delete(stockKey);
            
            log.info("秒杀活动已结束: activityId={}", activity.getId());
        }
    }

    private void validateSeckill(SeckillActivity activity, Long userId, Integer quantity) {
        LocalDateTime now = LocalDateTime.now();
        
        if (activity.getStatus() != 1) {
            if (activity.getStartTime().isAfter(now)) {
                throw BusinessException.of("秒杀活动尚未开始");
            }
            if (activity.getEndTime().isBefore(now)) {
                throw BusinessException.of("秒杀活动已结束");
            }
            throw BusinessException.of("秒杀活动未开始或已结束");
        }
        
        if (quantity > activity.getSeckillLimit()) {
            throw BusinessException.of("每人限购" + activity.getSeckillLimit() + "件");
        }
        
        String stockKey = RedisConstants.SECKILL_STOCK_KEY + activity.getId();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(stockKey))) {
            initSeckillStock(activity.getId());
        }
    }
}
