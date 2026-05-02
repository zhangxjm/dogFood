package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.entity.DiningTable;
import com.restaurant.service.DiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/table")
@RequiredArgsConstructor
public class DiningTableController {
    
    private final DiningTableService diningTableService;
    
    @GetMapping
    public String list() {
        return "table/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public Result<Page<DiningTable>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String tableNo,
            @RequestParam(required = false) Integer status) {
        
        Page<DiningTable> result = diningTableService.pageList(page, pageSize, tableNo, status);
        return Result.success(result);
    }
    
    @GetMapping("/list-enabled")
    @ResponseBody
    public Result<List<DiningTable>> listEnabled(
            @RequestParam(required = false) Integer status) {
        
        List<DiningTable> list;
        if (status != null) {
            list = diningTableService.listByStatus(status);
        } else {
            list = diningTableService.list();
        }
        return Result.success(list);
    }
    
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add() {
        return "table/add";
    }
    
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> save(@RequestBody DiningTable diningTable) {
        if (diningTableService.getByTableNo(diningTable.getTableNo()) != null) {
            return Result.error("餐桌编号已存在");
        }
        boolean result = diningTableService.saveTable(diningTable);
        return result ? Result.success(true) : Result.error("保存失败");
    }
    
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        DiningTable diningTable = diningTableService.getById(id);
        model.addAttribute("table", diningTable);
        return "table/edit";
    }
    
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Result<Boolean> update(@RequestBody DiningTable diningTable) {
        boolean result = diningTableService.updateTable(diningTable);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @PostMapping("/status")
    @ResponseBody
    public Result<Boolean> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        boolean result = diningTableService.updateStatus(id, status);
        return result ? Result.success(true) : Result.error("更新失败");
    }
    
    @GetMapping("/qrcode/{id}")
    @ResponseBody
    public Result<String> getQrCode(@PathVariable Long id) {
        DiningTable table = diningTableService.getById(id);
        if (table == null) {
            return Result.error("餐桌不存在");
        }
        String qrCode = table.getQrCode();
        if (qrCode == null) {
            qrCode = diningTableService.generateQrCode(id);
            table.setQrCode(qrCode);
            diningTableService.updateById(table);
        }
        return Result.success(qrCode);
    }
}
