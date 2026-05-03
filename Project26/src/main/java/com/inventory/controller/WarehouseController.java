package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.Warehouse;
import com.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public Result<PageResult<Warehouse>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        PageResult<Warehouse> result = warehouseService.pageQuery(current, size, keyword);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Warehouse>> listAll() {
        List<Warehouse> result = warehouseService.listAll();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.getById(id);
        return Result.success(warehouse);
    }

    @PostMapping
    public Result<Warehouse> create(@RequestBody Warehouse warehouse) {
        warehouseService.createWarehouse(warehouse);
        return Result.success("创建成功", warehouse);
    }

    @PutMapping("/{id}")
    public Result<Warehouse> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseService.updateWarehouse(warehouse);
        return Result.success("更新成功", warehouse);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return Result.success();
    }
}
