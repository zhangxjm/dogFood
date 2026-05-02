package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    
    List<OrderDetail> getByOrderId(Long orderId);
    
    boolean saveOrderDetails(Long orderId, List<OrderDetail> details);
    
    boolean updateDetailStatus(Long detailId, Integer status);
    
    boolean refundDetail(Long detailId, Integer quantity, String reason);
}
