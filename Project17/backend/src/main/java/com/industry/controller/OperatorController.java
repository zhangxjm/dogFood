package com.industry.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.common.Result;
import com.industry.entity.Operator;
import com.industry.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService operatorService;

    @GetMapping
    public Result<Page<Operator>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        return Result.success(operatorService.page(page, size, realName, role, status));
    }

    @GetMapping("/list")
    public Result<List<Operator>> listAll() {
        return Result.success(operatorService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Operator> getById(@PathVariable Long id) {
        return Result.success(operatorService.getById(id));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Operator operator = operatorService.login(username, password);
        if (operator != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("user", operator);
            result.put("token", "token_" + operator.getId());
            return Result.success(result);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Operator operator) {
        return Result.success(operatorService.save(operator));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Operator operator) {
        return Result.success(operatorService.update(operator));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(operatorService.delete(id));
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(operatorService.updateStatus(id, status));
    }
}
