package com.seckill.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.common.entity.SeckillActivity;
import com.seckill.common.result.PageResult;
import com.seckill.seckill.dto.SeckillExecuteDTO;
import com.seckill.common.vo.SeckillResultVO;

public interface SeckillActivityService extends IService<SeckillActivity> {

    PageResult<SeckillActivity> getActivityList(Integer pageNum, Integer pageSize, Integer status, Long productId);

    SeckillActivity getActivityDetail(Long activityId);

    SeckillResultVO executeSeckill(Long userId, SeckillExecuteDTO dto);

    SeckillResultVO getSeckillResult(Long userId, Long activityId);

    boolean deductStock(Long activityId, Integer quantity);

    void addStock(Long activityId, Integer quantity);

    void initSeckillStock(Long activityId);

    void updateActivityStatus();
}
