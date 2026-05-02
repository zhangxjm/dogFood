package com.example.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.Result;
import com.example.admin.entity.SysMenu;
import com.example.admin.entity.SysUser;
import com.example.admin.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    public Result<List<SysMenu>> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList;
        if (userId != null && userId == 1L) {
            menuList = this.list(buildQueryWrapper(menu));
        } else {
            menuList = this.baseMapper.selectMenuListByUserId(userId, menu);
        }
        return Result.success(menuList);
    }

    public Result<List<SysMenu>> selectMenuTree(Long userId) {
        List<SysMenu> menus;
        if (userId != null && userId == 1L) {
            menus = this.list(buildQueryWrapper(new SysMenu()));
        } else {
            menus = this.baseMapper.selectMenuTreeByUserId(userId);
        }
        List<SysMenu> tree = buildMenuTree(menus, 0L);
        return Result.success(tree);
    }

    public Result<List<Map<String, Object>>> selectMenuTreeSelect(Long userId) {
        List<SysMenu> menus;
        if (userId != null && userId == 1L) {
            menus = this.list(buildQueryWrapper(new SysMenu()));
        } else {
            menus = this.baseMapper.selectMenuTreeByUserId(userId);
        }
        List<SysMenu> tree = buildMenuTree(menus, 0L);
        List<Map<String, Object>> result = buildTreeSelect(tree);
        return Result.success(result);
    }

    public Result<SysMenu> selectMenuById(Long menuId) {
        SysMenu menu = this.getById(menuId);
        return Result.success(menu);
    }

    @Transactional
    public Result<Void> insertMenu(SysMenu menu) {
        SysMenu parentMenu = this.getById(menu.getParentId());
        if (parentMenu != null && "1".equals(parentMenu.getVisible())) {
            return Result.error("父菜单已隐藏，不允许新增");
        }
        
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        
        this.save(menu);
        return Result.success();
    }

    @Transactional
    public Result<Void> updateMenu(SysMenu menu) {
        SysMenu oldMenu = this.getById(menu.getMenuId());
        if (oldMenu == null) {
            return Result.error("菜单不存在");
        }
        
        if (menu.getMenuId().equals(menu.getParentId())) {
            return Result.error("不能将自己设为父菜单");
        }
        
        menu.setUpdateTime(LocalDateTime.now());
        this.updateById(menu);
        return Result.success();
    }

    @Transactional
    public Result<Void> deleteMenuById(Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        long count = this.count(wrapper);
        if (count > 0) {
            return Result.error("存在子菜单，不允许删除");
        }
        
        this.removeById(menuId);
        return Result.success();
    }

    private LambdaQueryWrapper<SysMenu> buildQueryWrapper(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(menu.getMenuName())) {
            wrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }
        if (StringUtils.hasText(menu.getVisible())) {
            wrapper.eq(SysMenu::getVisible, menu.getVisible());
        }
        wrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return wrapper;
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                menu.setChildren(buildMenuTree(menus, menu.getMenuId()));
                tree.add(menu);
            }
        }
        return tree;
    }

    private List<Map<String, Object>> buildTreeSelect(List<SysMenu> menus) {
        return menus.stream().map(menu -> {
            Map<String, Object> node = new java.util.HashMap<>();
            node.put("id", menu.getMenuId());
            node.put("label", menu.getMenuName());
            if (!menu.getChildren().isEmpty()) {
                node.put("children", buildTreeSelect(menu.getChildren()));
            }
            return node;
        }).collect(Collectors.toList());
    }
}
