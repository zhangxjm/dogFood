package com.example.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.Result;
import com.example.admin.entity.SysDept;
import com.example.admin.mapper.SysDeptMapper;
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
public class SysDeptService extends ServiceImpl<SysDeptMapper, SysDept> {

    public Result<List<SysDept>> selectDeptList(SysDept dept) {
        List<SysDept> deptList = this.list(buildQueryWrapper(dept));
        List<SysDept> result = buildDeptTree(deptList, 0L);
        return Result.success(result);
    }

    public Result<List<Map<String, Object>>> selectDeptTree() {
        List<SysDept> deptList = this.list(buildQueryWrapper(new SysDept()));
        List<SysDept> treeList = buildDeptTree(deptList, 0L);
        List<Map<String, Object>> result = buildTreeSelect(treeList);
        return Result.success(result);
    }

    public Result<SysDept> selectDeptById(Long deptId) {
        SysDept dept = this.getById(deptId);
        return Result.success(dept);
    }

    @Transactional
    public Result<Void> insertDept(SysDept dept) {
        SysDept parentDept = this.getById(dept.getParentId());
        if (parentDept != null && !"0".equals(parentDept.getStatus())) {
            return Result.error("父部门已停用，不允许新增");
        }
        
        dept.setAncestors(parentDept != null ? parentDept.getAncestors() + "," + dept.getParentId() : "0");
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        
        this.save(dept);
        return Result.success();
    }

    @Transactional
    public Result<Void> updateDept(SysDept dept) {
        SysDept oldDept = this.getById(dept.getDeptId());
        if (oldDept == null) {
            return Result.error("部门不存在");
        }
        
        SysDept newParentDept = this.getById(dept.getParentId());
        if (newParentDept != null && oldDept != null) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        
        dept.setUpdateTime(LocalDateTime.now());
        this.updateById(dept);
        
        if ("0".equals(dept.getStatus()) && newParentDept != null && "1".equals(newParentDept.getStatus())) {
            return Result.error("该部门的父部门已停用，请先启用父部门");
        }
        
        return Result.success();
    }

    @Transactional
    public Result<Void> deleteDeptById(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        long count = this.count(wrapper);
        if (count > 0) {
            return Result.error("存在子部门，不允许删除");
        }
        
        LambdaQueryWrapper<com.example.admin.entity.SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(com.example.admin.entity.SysUser::getDeptId, deptId);
        
        this.removeById(deptId);
        return Result.success();
    }

    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dept.getDeptName())) {
            wrapper.like(SysDept::getDeptName, dept.getDeptName());
        }
        if (StringUtils.hasText(dept.getStatus())) {
            wrapper.eq(SysDept::getStatus, dept.getStatus());
        }
        wrapper.orderByAsc(SysDept::getOrderNum);
        return wrapper;
    }

    private List<SysDept> buildDeptTree(List<SysDept> deptList, Long parentId) {
        List<SysDept> tree = new ArrayList<>();
        for (SysDept dept : deptList) {
            if (dept.getParentId().equals(parentId)) {
                dept.setChildren(buildDeptTree(deptList, dept.getDeptId()));
                tree.add(dept);
            }
        }
        return tree;
    }

    private List<Map<String, Object>> buildTreeSelect(List<SysDept> deptList) {
        return deptList.stream().map(dept -> {
            Map<String, Object> node = new java.util.HashMap<>();
            node.put("id", dept.getDeptId());
            node.put("label", dept.getDeptName());
            if (!dept.getChildren().isEmpty()) {
                node.put("children", buildTreeSelect(dept.getChildren()));
            }
            return node;
        }).collect(Collectors.toList());
    }

    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SysDept::getAncestors, oldAncestors + "," + deptId);
        List<SysDept> children = this.list(wrapper);
        
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        
        if (!children.isEmpty()) {
            this.updateBatchById(children);
        }
    }
}
