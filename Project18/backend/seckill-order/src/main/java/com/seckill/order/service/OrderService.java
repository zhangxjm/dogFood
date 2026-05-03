package com.seckill.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.common.entity.Order;
import com.seckill.common.mq.SeckillOrderMessage;
import com.seckill.common.result.PageResult;
import com.seckill.common.vo.SeckillResultVO;

public interface OrderService extends IService<Order> {

    PageResult<Order> getOrderList(Integer pageNum, Integer pageSize, Long userId, Integer status);

    Order getOrderDetail(Long orderId, Long userId);

    void processSeckillOrder(SeckillOrderMessage message);

    boolean payOrder(Long orderId, Long userId, Integer payType);

    boolean cancelOrder(Long orderId, Long userId, String reason);

    boolean confirmReceive(Long orderId, Long userId);

    void processExpiredOrders();

    void cancelOrderWithStock(Long orderId);

    SeckillResultVO getSeckillResult(Long userId, Long activityId);
}
