package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.Inventory;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Result<PageResult<Inventory>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId) {
        PageResult<Inventory> result = inventoryService.pageQuery(current, size, keyword, warehouseId, categoryId);
        return Result.success(result);
    }

    @GetMapping("/{productId}/{warehouseId}")
    public Result<Inventory> getByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        Inventory inventory = inventoryService.getByProductAndWarehouse(productId, warehouseId);
        return Result.success(inventory);
    }

    @GetMapping("/warning")
    public Result<List<Inventory>> getWarningInventory() {
        List<Inventory> result = inventoryService.getWarningInventory();
        return Result.success(result);
    }
}
