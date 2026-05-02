package com.restaurant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Employee;
import com.restaurant.entity.Orders;
import com.restaurant.entity.RefundRecord;
import com.restaurant.mapper.RefundRecordMapper;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.OrdersService;
import com.restaurant.service.RefundRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecord> implements RefundRecordService {
    
    private final OrdersService ordersService;
    private final EmployeeService employeeService;
    
    @Override
    public Page<RefundRecord> pageList(Integer page, Integer pageSize, String orderNo, LocalDate startDate, LocalDate endDate) {
        Page<RefundRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<RefundRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(orderNo)) {
            wrapper.like(RefundRecord::getOrderNo, orderNo);
        }
        if (startDate != null) {
            wrapper.ge(RefundRecord::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(RefundRecord::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(RefundRecord::getCreateTime);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundRecord createRefund(Long orderId, String reason, Long operatorId) {
        Orders orders = ordersService.getById(orderId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (orders.getStatus() == Orders.STATUS_REFUNDED) {
            throw new RuntimeException("该订单已退款");
        }
        
        if (orders.getStatus() != Orders.STATUS_PAID && orders.getStatus() != Orders.STATUS_COMPLETED) {
            throw new RuntimeException("只有已支付或已完成的订单可以退款");
        }
        
        RefundRecord refund = new RefundRecord();
        refund.setOrderId(orderId);
        refund.setOrderNo(orders.getOrderNo());
        refund.setRefundAmount(orders.getPaidAmount());
        refund.setRefundReason(reason);
        refund.setRefundMethod("原路退回");
        
        if (operatorId != null) {
            Employee employee = employeeService.getById(operatorId);
            if (employee != null) {
                refund.setOperatorId(operatorId);
                refund.setOperatorName(employee.getName());
            }
        }
        
        this.save(refund);
        
        orders.setStatus(Orders.STATUS_REFUNDED);
        ordersService.updateById(orders);
        
        return refund;
    }
    
    @Override
    public RefundRecord getByOrderId(Long orderId) {
        return this.lambdaQuery()
                .eq(RefundRecord::getOrderId, orderId)
                .one();
    }
}
