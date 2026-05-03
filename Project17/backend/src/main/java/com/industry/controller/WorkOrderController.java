package com.industry.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.common.Result;
import com.industry.entity.WorkOrder;
import com.industry.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping
    public Result<Page<WorkOrder>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assignTo,
            @RequestParam(required = false) Integer priority) {
        return Result.success(workOrderService.page(page, size, title, status, assignTo, priority));
    }

    @GetMapping("/{id}")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        return Result.success(workOrderService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.save(workOrder));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.update(workOrder));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(workOrderService.delete(id));
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String resultRemark) {
        return Result.success(workOrderService.updateStatus(id, status, resultRemark));
    }
}
