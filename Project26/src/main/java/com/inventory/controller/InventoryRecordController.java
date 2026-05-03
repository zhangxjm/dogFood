package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.entity.InventoryRecord;
import com.inventory.service.InventoryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventory-records")
@RequiredArgsConstructor
public class InventoryRecordController {

    private final InventoryRecordService inventoryRecordService;

    @GetMapping
    public Result<PageResult<InventoryRecord>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        PageResult<InventoryRecord> result = inventoryRecordService.pageQuery(
                current, size, keyword, productId, warehouseId, type, startTime, endTime
        );
        return Result.success(result);
    }
}
