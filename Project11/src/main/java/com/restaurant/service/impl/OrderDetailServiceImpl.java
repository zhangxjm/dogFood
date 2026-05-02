package com.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.OrderDetail;
import com.restaurant.mapper.OrderDetailMapper;
import com.restaurant.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    
    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        return this.lambdaQuery()
                .eq(OrderDetail::getOrderId, orderId)
                .orderByAsc(OrderDetail::getCreateTime)
                .list();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrderDetails(Long orderId, List<OrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return true;
        }
        for (OrderDetail detail : details) {
            detail.setOrderId(orderId);
            detail.setStatus(OrderDetail.STATUS_PENDING);
            if (detail.getTotalPrice() == null && detail.getDishPrice() != null && detail.getQuantity() != null) {
                detail.setTotalPrice(detail.getDishPrice().multiply(new java.math.BigDecimal(detail.getQuantity())));
            }
        }
        return this.saveBatch(details);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDetailStatus(Long detailId, Integer status) {
        OrderDetail detail = new OrderDetail();
        detail.setId(detailId);
        detail.setStatus(status);
        return this.updateById(detail);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundDetail(Long detailId, Integer quantity, String reason) {
        OrderDetail detail = this.getById(detailId);
        if (detail == null) {
            return false;
        }
        detail.setStatus(OrderDetail.STATUS_REFUNDED);
        return this.updateById(detail);
    }
}
