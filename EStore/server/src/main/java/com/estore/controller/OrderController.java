package com.estore.controller;

import com.estore.common.PageResult;
import com.estore.common.Result;
import com.estore.entity.Order;
import com.estore.entity.OrderItem;
import com.estore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/user/orders")
    public Result<Order> create(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Object> params) {
        try {
            Long addressId = Long.valueOf(params.get("addressId").toString());
            @SuppressWarnings("unchecked")
            List<Long> cartIds = (List<Long>) params.get("cartIds");
            
            Order order = orderService.create(userId, addressId, cartIds);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/user/orders")
    public Result<PageResult<Order>> list(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PageResult<Order> result = orderService.getByUserId(userId, status, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/user/orders/{id}")
    public Result<Map<String, Object>> getById(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            Order order = orderService.getById(id, userId);
            List<OrderItem> items = orderService.getOrderItems(order.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("items", items);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/orders/{id}/pay")
    public Result<Order> pay(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            Order order = orderService.pay(userId, id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/orders/{id}/confirm")
    public Result<Order> confirm(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            Order order = orderService.confirm(userId, id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/orders/{id}/cancel")
    public Result<Order> cancel(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        try {
            Order order = orderService.cancel(userId, id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/admin/orders")
    public Result<PageResult<Order>> listAll(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        try {
            PageResult<Order> result = orderService.listAll(status, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/admin/orders/{id}/delivery")
    public Result<Order> delivery(@PathVariable Long id) {
        try {
            Order order = orderService.delivery(id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/admin/orders/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        try {
            Order order = orderService.getByOrderNo(String.valueOf(id));
            if (order == null) {
                order = orderService.getByOrderNo(orderService.getByOrderNo(String.valueOf(id)).getOrderNo());
            }
            List<OrderItem> items = orderService.getOrderItems(order.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("items", items);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
