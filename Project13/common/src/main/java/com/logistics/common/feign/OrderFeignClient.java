package com.logistics.common.feign;

import com.logistics.common.entity.Order;
import com.logistics.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

    @GetMapping("/{id}")
    Result<Order> getById(@PathVariable("id") Long id);

    @GetMapping("/orderNo/{orderNo}")
    Result<Order> getByOrderNo(@PathVariable("orderNo") String orderNo);

    @PutMapping("/status/{orderId}")
    Result<Void> updateStatus(@PathVariable("orderId") Long orderId, @RequestParam("status") Integer status);

    @GetMapping("/user/{userId}")
    Result<List<Order>> getByUserId(@PathVariable("userId") Long userId);
}
