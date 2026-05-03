package com.seckill.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.constant.RedisConstants;
import com.seckill.common.entity.Order;
import com.seckill.common.entity.Product;
import com.seckill.common.entity.SeckillActivity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.mq.SeckillOrderMessage;
import com.seckill.common.result.PageResult;
import com.seckill.common.util.DistributedLockUtils;
import com.seckill.order.mapper.OrderMapper;
import com.seckill.order.service.OrderService;
import com.seckill.common.vo.SeckillResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StreamBridge streamBridge;

    @Override
    public PageResult<Order> getOrderList(Integer pageNum, Integer pageSize, Long userId, Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        
        page = page(page, wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize);
    }

    @Override
    public Order getOrderDetail(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw BusinessException.of("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw BusinessException.of("无权访问该订单");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processSeckillOrder(SeckillOrderMessage message) {
        Long activityId = message.getActivityId();
        Long userId = message.getUserId();
        Long productId = message.getProductId();
        Integer quantity = message.getQuantity();
        String orderNo = message.getOrderNo();
        
        String resultKey = RedisConstants.SECKILL_ORDER_RESULT_KEY + activityId + ":" + userId;
        
        try {
            String lockKey = RedisConstants.ORDER_LOCK_KEY + orderNo;
            boolean locked = DistributedLockUtils.tryLock(lockKey, 5, 30, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("获取订单锁失败: orderNo={}", orderNo);
                return;
            }
            
            try {
                Order existOrder = baseMapper.selectByOrderNo(orderNo);
                if (existOrder != null) {
                    log.warn("订单已存在: orderNo={}", orderNo);
                    return;
                }
                
                Order order = new Order();
                order.setOrderNo(orderNo);
                order.setUserId(userId);
                order.setProductId(productId);
                order.setActivityId(activityId);
                order.setProductName("秒杀商品-" + productId);
                order.setProductImage(null);
                order.setUnitPrice(message.getSeckillPrice());
                order.setQuantity(quantity);
                order.setTotalAmount(message.getSeckillPrice().multiply(BigDecimal.valueOf(quantity)));
                order.setStatus(0);
                order.setExpireTime(LocalDateTime.now().plusMinutes(15));
                order.setConsignee("测试用户");
                order.setPhone("13800138000");
                order.setAddress("测试地址");
                
                save(order);
                
                SeckillResultVO result = SeckillResultVO.builder()
                        .status("success")
                        .message("秒杀成功")
                        .orderNo(orderNo)
                        .activityId(activityId)
                        .build();
                
                redisTemplate.opsForValue().set(resultKey, result, 1, TimeUnit.HOURS);
                
                log.info("秒杀订单创建成功: userId={}, activityId={}, orderNo={}", userId, activityId, orderNo);
                
            } finally {
                DistributedLockUtils.unlock(lockKey);
            }
            
        } catch (Exception e) {
            log.error("处理秒杀订单失败: userId={}, activityId={}", userId, activityId, e);
            
            SeckillResultVO result = SeckillResultVO.builder()
                    .status("fail")
                    .message("秒杀失败: " + e.getMessage())
                    .activityId(activityId)
                    .build();
            
            redisTemplate.opsForValue().set(resultKey, result, 1, TimeUnit.HOURS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Long userId, Integer payType) {
        Order order = getById(orderId);
        if (order == null) {
            throw BusinessException.of("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw BusinessException.of("无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw BusinessException.of("订单状态不正确");
        }
        
        order.setStatus(1);
        order.setPaymentType(payType);
        order.setPaymentTime(LocalDateTime.now());
        order.setPayAmount(order.getTotalAmount());
        
        updateById(order);
        
        log.info("订单支付成功: orderId={}, userId={}", orderId, userId);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            throw BusinessException.of("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw BusinessException.of("无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw BusinessException.of("订单状态不正确，无法取消");
        }
        
        cancelOrderWithStock(orderId);
        
        log.info("订单取消成功: orderId={}, userId={}, reason={}", orderId, userId, reason);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceive(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw BusinessException.of("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw BusinessException.of("无权操作该订单");
        }
        if (order.getStatus() != 2) {
            throw BusinessException.of("订单状态不正确");
        }
        
        order.setStatus(3);
        updateById(order);
        
        log.info("订单确认收货: orderId={}, userId={}", orderId, userId);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processExpiredOrders() {
        List<Order> expiredOrders = baseMapper.selectExpiredOrders();
        if (expiredOrders == null || expiredOrders.isEmpty()) {
            return;
        }
        
        for (Order order : expiredOrders) {
            try {
                cancelOrderWithStock(order.getId());
                log.info("超时订单已取消: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
            } catch (Exception e) {
                log.error("取消超时订单失败: orderId={}", order.getId(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderWithStock(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            return;
        }
        
        baseMapper.cancelOrder(orderId);
        
        if (order.getActivityId() != null) {
            String stockKey = RedisConstants.SECKILL_STOCK_KEY + order.getActivityId();
            redisTemplate.opsForValue().increment(stockKey, order.getQuantity());
        }
        
        log.info("订单取消并返还库存: orderId={}, productId={}, activityId={}, quantity={}", 
                orderId, order.getProductId(), order.getActivityId(), order.getQuantity());
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
}
