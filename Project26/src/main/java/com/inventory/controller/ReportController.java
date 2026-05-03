package com.inventory.controller;

import com.inventory.common.Result;
import com.inventory.entity.Inventory;
import com.inventory.entity.Product;
import com.inventory.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SaleOrderMapper saleOrderMapper;
    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final InventoryRecordMapper inventoryRecordMapper;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> result = new HashMap<>();
        
        Long totalProducts = productMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
        );
        result.put("totalProducts", totalProducts);
        
        Long totalInventory = inventoryMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Inventory>()
        );
        result.put("totalInventory", totalInventory);
        
        List<Inventory> inventories = inventoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Inventory>()
        );
        
        int totalQuantity = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        result.put("totalQuantity", totalQuantity);
        
        BigDecimal totalStockValue = inventories.stream()
                .map(inv -> {
                    Product product = productMapper.selectById(inv.getProductId());
                    if (product != null && product.getPurchasePrice() != null) {
                        return product.getPurchasePrice().multiply(new BigDecimal(inv.getQuantity()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalStockValue", totalStockValue);
        
        List<Map<String, Object>> warningList = inventories.stream()
                .filter(inv -> {
                    Product product = productMapper.selectById(inv.getProductId());
                    if (product == null) return false;
                    return inv.getQuantity() < product.getMinStock() ||
                            (product.getMaxStock() > 0 && inv.getQuantity() > product.getMaxStock());
                })
                .map(inv -> {
                    Map<String, Object> item = new HashMap<>();
                    Product product = productMapper.selectById(inv.getProductId());
                    item.put("productId", inv.getProductId());
                    item.put("productName", product != null ? product.getProductName() : "未知");
                    item.put("warehouseId", inv.getWarehouseId());
                    item.put("quantity", inv.getQuantity());
                    item.put("minStock", product != null ? product.getMinStock() : 0);
                    item.put("maxStock", product != null ? product.getMaxStock() : 0);
                    item.put("warningType", product != null && inv.getQuantity() < product.getMinStock() ? "LOW" : "HIGH");
                    return item;
                })
                .toList();
        result.put("warningList", warningList);
        result.put("warningCount", warningList.size());
        
        return Result.success(result);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Long pendingPurchase = purchaseOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.inventory.entity.PurchaseOrder>()
                        .eq(com.inventory.entity.PurchaseOrder::getStatus, 0)
        );
        result.put("pendingPurchase", pendingPurchase);
        
        Long pendingSale = saleOrderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.inventory.entity.SaleOrder>()
                        .eq(com.inventory.entity.SaleOrder::getStatus, 0)
        );
        result.put("pendingSale", pendingSale);
        
        return Result.success(result);
    }
}
