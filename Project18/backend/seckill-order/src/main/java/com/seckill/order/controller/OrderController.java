package com.seckill.order.controller;

import com.seckill.common.annotation.RateLimiter;
import com.seckill.common.entity.Order;
import com.seckill.common.result.PageResult;
import com.seckill.common.result.Result;
import com.seckill.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单接口", description = "订单查询、支付、取消等接口")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "获取订单列表")
    @RateLimiter(limit = 50, windowTime = 1)
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageResult<Order> result = orderService.getOrderList(pageNum, pageSize, userId, status);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        Order order = orderService.getOrderDetail(id, userId);
        return Result.success(order);
    }

    @Operation(summary = "订单支付")
    @PostMapping("/pay/{id}")
    public Result<Boolean> payOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer payType) {
        boolean success = orderService.payOrder(id, userId, payType);
        return Result.success("支付成功", success);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        boolean success = orderService.cancelOrder(id, userId, reason);
        return Result.success("取消成功", success);
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{id}")
    public Result<Boolean> confirmReceive(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        boolean success = orderService.confirmReceive(id, userId);
        return Result.success("确认成功", success);
    }
}
