package com.attendance.controller;

import com.attendance.common.PageResult;
import com.attendance.common.Result;
import com.attendance.entity.SysPost;
import com.attendance.service.SysPostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理 Controller
 */
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class SysPostController {

    private final SysPostService sysPostService;

    @GetMapping("/list")
    public Result<List<SysPost>> list(
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(postName)) {
            wrapper.like(SysPost::getPostName, postName);
        }
        if (status != null) {
            wrapper.eq(SysPost::getStatus, status);
        }
        wrapper.orderByAsc(SysPost::getSort);
        List<SysPost> list = sysPostService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result<PageResult<SysPost>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(postName)) {
            wrapper.like(SysPost::getPostName, postName);
        }
        if (status != null) {
            wrapper.eq(SysPost::getStatus, status);
        }
        wrapper.orderByAsc(SysPost::getSort);
        
        Page<SysPost> page = new Page<>(current, size);
        sysPostService.page(page, wrapper);
        
        PageResult<SysPost> result = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysPost> getById(@PathVariable Long id) {
        SysPost post = sysPostService.getById(id);
        return Result.success(post);
    }

    @PostMapping
    public Result<SysPost> save(@RequestBody SysPost post) {
        sysPostService.save(post);
        return Result.success("新增成功", post);
    }

    @PutMapping
    public Result<SysPost> update(@RequestBody SysPost post) {
        sysPostService.updateById(post);
        return Result.success("修改成功", post);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysPostService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysPostService.removeByIds(ids);
        return Result.success();
    }
}
