package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.dto.PurchaseOrderDTO;
import com.inventory.entity.PurchaseOrder;
import com.inventory.entity.PurchaseOrderItem;
import com.inventory.security.UserPrincipal;
import com.inventory.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public Result<PageResult<PurchaseOrder>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<PurchaseOrder> result = purchaseOrderService.pageQuery(current, size, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.getDetail(id);
        List<PurchaseOrderItem> items = purchaseOrderService.getItems(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return Result.success(result);
    }

    @PostMapping
    public Result<PurchaseOrder> create(
            @RequestBody PurchaseOrderDTO dto,
            @AuthenticationPrincipal UserPrincipal user) {
        PurchaseOrder order = purchaseOrderService.createOrder(dto, user);
        return Result.success("创建成功", order);
    }

    @PostMapping("/{id}/in-stock")
    public Result<Void> inStock(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        purchaseOrderService.inStock(id, user);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return Result.success();
    }
}
