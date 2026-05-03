package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.Supplier;
import com.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public Result<PageResult<Supplier>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        PageResult<Supplier> result = supplierService.pageQuery(current, size, keyword);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Supplier>> listAll() {
        List<Supplier> result = supplierService.listAll();
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return Result.success(supplier);
    }

    @PostMapping
    public Result<Supplier> create(@RequestBody Supplier supplier) {
        supplierService.createSupplier(supplier);
        return Result.success("创建成功", supplier);
    }

    @PutMapping("/{id}")
    public Result<Supplier> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.updateSupplier(supplier);
        return Result.success("更新成功", supplier);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return Result.success();
    }
}
