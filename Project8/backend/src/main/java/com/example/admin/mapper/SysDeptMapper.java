package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    
    List<SysDept> selectDeptList(SysDept dept);
    
    SysDept selectDeptById(@Param("deptId") Long deptId);
    
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId);
    
    int selectChildrenCountByParentId(@Param("parentId") Long parentId);
    
    int checkDeptExistUser(@Param("deptId") Long deptId);
    
    int deleteDeptById(@Param("deptId") Long deptId);
}
