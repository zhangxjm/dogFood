package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);
    
    List<SysMenu> selectMenuTreeAll();
    
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);
    
    List<SysMenu> selectMenuList(SysMenu menu);
    
    List<SysMenu> selectMenuListByUserId(@Param("userId") Long userId, @Param("menu") SysMenu menu);
    
    SysMenu selectMenuById(@Param("menuId") Long menuId);
    
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);
    
    int hasChildByMenuId(@Param("menuId") Long menuId);
    
    int checkMenuExistRole(@Param("menuId") Long menuId);
    
    int deleteMenuById(@Param("menuId") Long menuId);
}
