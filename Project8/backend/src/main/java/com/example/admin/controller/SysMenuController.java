package com.example.admin.controller;

import com.example.admin.common.Result;
import com.example.admin.entity.LoginUser;
import com.example.admin.entity.SysMenu;
import com.example.admin.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysMenu>> list(SysMenu menu, @AuthenticationPrincipal LoginUser loginUser) {
        return menuService.selectMenuList(menu, loginUser.getUser().getUserId());
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysMenu>> tree(@AuthenticationPrincipal LoginUser loginUser) {
        return menuService.selectMenuTree(loginUser.getUser().getUserId());
    }

    @GetMapping("/treeSelect")
    public Result<List<Map<String, Object>>> treeSelect(@AuthenticationPrincipal LoginUser loginUser) {
        return menuService.selectMenuTreeSelect(loginUser.getUser().getUserId());
    }

    @GetMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result<SysMenu> getInfo(@PathVariable Long menuId) {
        return menuService.selectMenuById(menuId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Void> add(@RequestBody SysMenu menu, @AuthenticationPrincipal LoginUser loginUser) {
        menu.setCreateBy(loginUser.getUser().getUserName());
        return menuService.insertMenu(menu);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> edit(@RequestBody SysMenu menu, @AuthenticationPrincipal LoginUser loginUser) {
        menu.setUpdateBy(loginUser.getUser().getUserName());
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    public Result<Void> remove(@PathVariable Long menuId) {
        return menuService.deleteMenuById(menuId);
    }
}
