package com.logistics.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.dto.OrderCreateDTO;
import com.logistics.common.entity.Order;
import com.logistics.common.result.Result;
import com.logistics.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto);
        return Result.success("订单创建成功", order);
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable("id") Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }

    @GetMapping("/orderNo/{orderNo}")
    public Result<Order> getByOrderNo(@PathVariable("orderNo") String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        return Result.success(order);
    }

    @GetMapping("/page")
    public Result<Page<Order>> pageList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<Order> page = orderService.pageList(current, size, userId, status, keyword);
        return Result.success(page);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Order>> getByUserId(@PathVariable("userId") Long userId) {
        List<Order> orders = orderService.getByUserId(userId);
        return Result.success(orders);
    }

    @PutMapping("/status/{orderId}")
    public Result<Void> updateStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("status") Integer status) {
        orderService.updateStatus(orderId, status);
        return Result.success();
    }

    @PostMapping("/review/{orderId}")
    public Result<Void> reviewOrder(
            @PathVariable("orderId") Long orderId,
            @RequestParam("approved") Boolean approved) {
        orderService.reviewOrder(orderId, approved);
        return Result.success(approved ? "审核通过" : "审核拒绝", null);
    }

    @PostMapping("/save")
    public Result<Void> saveOrUpdate(@RequestBody Order order) {
        orderService.saveOrUpdate(order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        orderService.removeById(id);
        return Result.success();
    }
}
