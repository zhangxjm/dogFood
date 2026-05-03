package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.dto.SaleOrderDTO;
import com.inventory.entity.SaleOrder;
import com.inventory.entity.SaleOrderItem;
import com.inventory.security.UserPrincipal;
import com.inventory.service.SaleOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale-orders")
@RequiredArgsConstructor
public class SaleOrderController {

    private final SaleOrderService saleOrderService;

    @GetMapping
    public Result<PageResult<SaleOrder>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<SaleOrder> result = saleOrderService.pageQuery(current, size, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        SaleOrder order = saleOrderService.getDetail(id);
        List<SaleOrderItem> items = saleOrderService.getItems(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return Result.success(result);
    }

    @PostMapping
    public Result<SaleOrder> create(
            @RequestBody SaleOrderDTO dto,
            @AuthenticationPrincipal UserPrincipal user) {
        SaleOrder order = saleOrderService.createOrder(dto, user);
        return Result.success("创建成功", order);
    }

    @PostMapping("/{id}/out-stock")
    public Result<Void> outStock(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        saleOrderService.outStock(id, user);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        saleOrderService.cancelOrder(id);
        return Result.success();
    }
}
