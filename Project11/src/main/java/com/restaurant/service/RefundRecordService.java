package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.RefundRecord;

import java.time.LocalDate;

public interface RefundRecordService extends IService<RefundRecord> {
    
    Page<RefundRecord> pageList(Integer page, Integer pageSize, String orderNo, LocalDate startDate, LocalDate endDate);
    
    RefundRecord createRefund(Long orderId, String reason, Long operatorId);
    
    RefundRecord getByOrderId(Long orderId);
}
