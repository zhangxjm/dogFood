package com.seckill.order.controller;

import com.seckill.common.entity.Order;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理接口", description = "管理后台订单管理接口")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "获取订单分页列表")
    @GetMapping("/page")
    public Result<PageResult<Order>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status) {
        PageResult<Order> result = orderService.getOrderList(pageNum, pageSize, userId, status);
        return Result.success(result);
    }

    @Operation(summary = "订单发货")
    @PostMapping("/ship/{id}")
    public Result<Boolean> shipOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确");
        }
        
        order.setStatus(2);
        orderService.updateById(order);
        
        return Result.success("发货成功", true);
    }

    @Operation(summary = "手动处理超时订单")
    @PostMapping("/process-expired")
    public Result<Boolean> processExpiredOrders() {
        orderService.processExpiredOrders();
        return Result.success(true);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduledProcessExpiredOrders() {
        orderService.processExpiredOrders();
    }
}
