package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    
    List<SysRole> selectRoleList(SysRole role);
    
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);
    
    List<String> selectRolePermissionByUserId(@Param("userId") Long userId);
    
    SysRole selectRoleById(@Param("roleId") Long roleId);
    
    int deleteRoleById(@Param("roleId") Long roleId);
    
    int deleteRoleByIds(@Param("roleIds") Long[] roleIds);
    
    int checkRoleNameUnique(@Param("roleName") String roleName);
    
    int checkRoleKeyUnique(@Param("roleKey") String roleKey);
}
