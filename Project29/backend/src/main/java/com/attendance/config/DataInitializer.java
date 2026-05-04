package com.attendance.config;

import cn.hutool.crypto.digest.BCrypt;
import com.attendance.entity.SysDept;
import com.attendance.entity.SysPost;
import com.attendance.entity.SysRole;
import com.attendance.entity.SysUser;
import com.attendance.entity.SysUserRole;
import com.attendance.service.SysDeptService;
import com.attendance.service.SysPostService;
import com.attendance.service.SysRoleService;
import com.attendance.service.SysUserRoleService;
import com.attendance.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysDeptService sysDeptService;
    private final SysPostService sysPostService;
    private final SysRoleService sysRoleService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;

    private static final String DEFAULT_PASSWORD = "admin123";

    @Override
    @Transactional
    public void run(String... args) {
        log.info("正在检查初始化数据...");
        
        initDepts();
        initPosts();
        initRoles();
        initAdminUser();
        
        log.info("初始化数据检查完成");
    }

    private void initDepts() {
        long count = sysDeptService.count();
        if (count > 0) {
            log.info("部门数据已存在，跳过初始化");
            return;
        }

        log.info("正在初始化部门数据...");
        
        SysDept hq = createDept(1L, 0L, "总公司", "HQ", "张总", "13800138000", 0);
        SysDept hr = createDept(2L, 1L, "人事部", "HR", "李经理", "13800138001", 1);
        SysDept tech = createDept(3L, 1L, "技术部", "TECH", "王经理", "13800138002", 2);
        createDept(4L, 1L, "财务部", "FINANCE", "赵经理", "13800138003", 3);
        createDept(5L, 3L, "前端组", "FE", "刘组长", "13800138004", 1);
        createDept(6L, 3L, "后端组", "BE", "陈组长", "13800138005", 2);
    }

    private SysDept createDept(Long id, Long parentId, String name, String code, String leader, String phone, int sort) {
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setParentId(parentId);
        dept.setDeptName(name);
        dept.setDeptCode(code);
        dept.setLeader(leader);
        dept.setPhone(phone);
        dept.setSort(sort);
        dept.setStatus(1);
        dept.setDeleted(0);
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        sysDeptService.save(dept);
        return dept;
    }

    private void initPosts() {
        long count = sysPostService.count();
        if (count > 0) {
            log.info("岗位数据已存在，跳过初始化");
            return;
        }

        log.info("正在初始化岗位数据...");
        
        createPost(1L, "总经理", "CEO", "高级", "公司总经理", 1);
        createPost(2L, "部门经理", "MANAGER", "中级", "部门负责人", 2);
        createPost(3L, "高级工程师", "SENIOR_DEV", "中级", "高级开发工程师", 3);
        createPost(4L, "中级工程师", "MID_DEV", "初级", "中级开发工程师", 4);
        createPost(5L, "初级工程师", "JUNIOR_DEV", "初级", "初级开发工程师", 5);
        createPost(6L, "行政专员", "ADMIN_STAFF", "初级", "行政专员", 6);
        createPost(7L, "人事专员", "HR_STAFF", "初级", "人事专员", 7);
    }

    private void createPost(Long id, String name, String code, String level, String desc, int sort) {
        SysPost post = new SysPost();
        post.setId(id);
        post.setPostName(name);
        post.setPostCode(code);
        post.setPostLevel(level);
        post.setDescription(desc);
        post.setSort(sort);
        post.setStatus(1);
        post.setDeleted(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        sysPostService.save(post);
    }

    private void initRoles() {
        long count = sysRoleService.count();
        if (count > 0) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        log.info("正在初始化角色数据...");
        
        createRole(1L, "超级管理员", "SUPER_ADMIN", "拥有所有权限", 1);
        createRole(2L, "部门经理", "DEPT_MANAGER", "部门管理员，拥有部门管理权限", 2);
        createRole(3L, "人事专员", "HR_STAFF", "人事管理人员，负责人事管理", 3);
        createRole(4L, "普通员工", "EMPLOYEE", "普通员工，拥有基本权限", 4);
    }

    private void createRole(Long id, String name, String key, String desc, int sort) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setRoleName(name);
        role.setRoleKey(key);
        role.setDescription(desc);
        role.setSort(sort);
        role.setStatus(1);
        role.setDeleted(0);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        sysRoleService.save(role);
    }

    private void initAdminUser() {
        SysUser admin = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, "admin")
                .one();

        if (admin == null) {
            log.info("正在初始化管理员账号...");
            
            admin = createUser(1L, "admin", "系统管理员", "admin@company.com", "13800000000", 2L);
            createUserRole(1L, 1L, 1L);
        } else {
            String currentPwd = admin.getPassword();
            if (currentPwd == null || !currentPwd.startsWith("$2a$")) {
                log.info("更新管理员密码为BCrypt加密格式...");
                admin.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
                sysUserService.updateById(admin);
            }
        }

        SysUser hr001 = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, "hr001")
                .one();
        if (hr001 == null) {
            log.info("正在初始化人事专员账号...");
            hr001 = createUser(2L, "hr001", "李小红", "lihong@company.com", "13800000001", 2L);
            createUserRole(2L, 2L, 3L);
        }

        SysUser tech001 = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, "tech001")
                .one();
        if (tech001 == null) {
            log.info("正在初始化普通员工账号...");
            tech001 = createUser(3L, "tech001", "王技术", "wangtech@company.com", "13800000002", 3L);
            createUserRole(3L, 3L, 4L);
        }

        log.info("默认账号密码: admin / {}", DEFAULT_PASSWORD);
    }

    private SysUser createUser(Long id, String username, String nickname, String email, String phone, Long deptId) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDeptId(deptId);
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.save(user);
        return user;
    }

    private void createUserRole(Long id, Long userId, Long roleId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setId(id);
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        sysUserRoleService.save(userRole);
    }
}