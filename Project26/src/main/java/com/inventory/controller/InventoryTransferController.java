package com.inventory.controller;

import com.inventory.common.PageResult;
import com.inventory.common.Result;
import com.inventory.dto.InventoryTransferDTO;
import com.inventory.entity.InventoryTransfer;
import com.inventory.entity.InventoryTransferItem;
import com.inventory.security.UserPrincipal;
import com.inventory.service.InventoryTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class InventoryTransferController {

    private final InventoryTransferService transferService;

    @GetMapping
    public Result<PageResult<InventoryTransfer>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<InventoryTransfer> result = transferService.pageQuery(current, size, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        InventoryTransfer transfer = transferService.getDetail(id);
        List<InventoryTransferItem> items = transferService.getItems(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("transfer", transfer);
        result.put("items", items);
        
        return Result.success(result);
    }

    @PostMapping
    public Result<InventoryTransfer> create(
            @RequestBody InventoryTransferDTO dto,
            @AuthenticationPrincipal UserPrincipal user) {
        InventoryTransfer transfer = transferService.createTransfer(dto, user);
        return Result.success("创建成功", transfer);
    }

    @PostMapping("/{id}/execute")
    public Result<Void> execute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        transferService.executeTransfer(id, user);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        transferService.cancelTransfer(id);
        return Result.success();
    }
}
