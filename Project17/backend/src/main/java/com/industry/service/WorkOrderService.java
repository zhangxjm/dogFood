package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.entity.WorkOrder;
import com.industry.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    public Page<WorkOrder> page(int page, int size, String title, Integer status, Long assignTo, Integer priority) {
        Page<WorkOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkOrder::getDeleted, 0);
        
        if (StringUtils.hasText(title)) {
            wrapper.like(WorkOrder::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (assignTo != null) {
            wrapper.eq(WorkOrder::getAssignTo, assignTo);
        }
        if (priority != null) {
            wrapper.eq(WorkOrder::getPriority, priority);
        }
        
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return workOrderMapper.selectPage(pageParam, wrapper);
    }

    public WorkOrder getById(Long id) {
        return workOrderMapper.selectById(id);
    }

    public boolean save(WorkOrder workOrder) {
        workOrder.setOrderNo(generateOrderNo());
        workOrder.setStatus(0);
        return workOrderMapper.insert(workOrder) > 0;
    }

    public boolean update(WorkOrder workOrder) {
        return workOrderMapper.updateById(workOrder) > 0;
    }

    public boolean delete(Long id) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        workOrder.setDeleted(1);
        return workOrderMapper.updateById(workOrder) > 0;
    }

    public boolean updateStatus(Long id, Integer status, String resultRemark) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        workOrder.setStatus(status);
        
        if (status == 1) {
            workOrder.setStartTime(LocalDateTime.now());
        } else if (status == 2) {
            workOrder.setEndTime(LocalDateTime.now());
            workOrder.setResultRemark(resultRemark);
        }
        
        return workOrderMapper.updateById(workOrder) > 0;
    }

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "WO" + LocalDateTime.now().format(formatter) + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
}
