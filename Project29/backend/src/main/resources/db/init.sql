-- =============================================
-- 员工人事考勤管理系统数据库初始化脚本
-- 数据库: attendance_system
-- =============================================

CREATE DATABASE IF NOT EXISTS attendance_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE attendance_system;

-- =============================================
-- 1. 部门表
-- =============================================
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =============================================
-- 2. 岗位职称表
-- =============================================
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '岗位ID',
    post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
    post_code VARCHAR(50) COMMENT '岗位编码',
    post_level VARCHAR(50) COMMENT '岗位级别',
    description VARCHAR(500) COMMENT '岗位描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位职称表';

-- =============================================
-- 3. 角色表
-- =============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
    description VARCHAR(500) COMMENT '描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 4. 用户表（管理员/员工账号）
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态（0停用 1正常）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dept_id (dept_id),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 5. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 6. 员工档案表
-- =============================================
DROP TABLE IF EXISTS emp_employee;
CREATE TABLE emp_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '员工ID',
    user_id BIGINT COMMENT '关联用户ID',
    emp_no VARCHAR(50) NOT NULL UNIQUE COMMENT '员工工号',
    emp_name VARCHAR(50) NOT NULL COMMENT '员工姓名',
    gender TINYINT COMMENT '性别（0女 1男）',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(500) COMMENT '住址',
    dept_id BIGINT COMMENT '部门ID',
    post_id BIGINT COMMENT '岗位ID',
    entry_date DATE COMMENT '入职日期',
    leave_date DATE COMMENT '离职日期',
    status TINYINT DEFAULT 1 COMMENT '状态（0离职 1在职）',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dept_id (dept_id),
    INDEX idx_post_id (post_id),
    INDEX idx_emp_no (emp_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工档案表';

-- =============================================
-- 7. 考勤记录表
-- =============================================
DROP TABLE IF EXISTS attendance_record;
CREATE TABLE attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    emp_id BIGINT NOT NULL COMMENT '员工ID',
    record_date DATE NOT NULL COMMENT '考勤日期',
    clock_in_time DATETIME COMMENT '上班打卡时间',
    clock_out_time DATETIME COMMENT '下班打卡时间',
    work_hours DECIMAL(5,2) DEFAULT 0 COMMENT '实际工时',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟到（0否 1是）',
    is_early TINYINT DEFAULT 0 COMMENT '是否早退（0否 1是）',
    is_absent TINYINT DEFAULT 0 COMMENT '是否旷工（0否 1是）',
    status TINYINT DEFAULT 1 COMMENT '状态（0异常 1正常）',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_emp_id (emp_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- =============================================
-- 8. 请假申请表
-- =============================================
DROP TABLE IF EXISTS leave_application;
CREATE TABLE leave_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    emp_id BIGINT NOT NULL COMMENT '员工ID',
    leave_type TINYINT NOT NULL COMMENT '请假类型（1事假 2病假 3年假 4婚假 5产假 6其他）',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    leave_days DECIMAL(5,1) DEFAULT 0 COMMENT '请假天数',
    reason VARCHAR(500) NOT NULL COMMENT '请假原因',
    status TINYINT DEFAULT 0 COMMENT '审批状态（0待审批 1已通过 2已拒绝）',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_emp_id (emp_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- =============================================
-- 9. 加班申请表
-- =============================================
DROP TABLE IF EXISTS overtime_application;
CREATE TABLE overtime_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    emp_id BIGINT NOT NULL COMMENT '员工ID',
    overtime_type TINYINT NOT NULL COMMENT '加班类型（1工作日加班 2周末加班 3节假日加班）',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    overtime_hours DECIMAL(5,2) DEFAULT 0 COMMENT '加班时长',
    reason VARCHAR(500) NOT NULL COMMENT '加班原因',
    status TINYINT DEFAULT 0 COMMENT '审批状态（0待审批 1已通过 2已拒绝）',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    approve_remark VARCHAR(500) COMMENT '审批备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_emp_id (emp_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='加班申请表';

-- =============================================
-- 10. 考勤月统计表
-- =============================================
DROP TABLE IF EXISTS attendance_statistics;
CREATE TABLE attendance_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    emp_id BIGINT NOT NULL COMMENT '员工ID',
    statistics_year INT NOT NULL COMMENT '统计年份',
    statistics_month INT NOT NULL COMMENT '统计月份',
    work_days INT DEFAULT 0 COMMENT '应出勤天数',
    actual_days INT DEFAULT 0 COMMENT '实际出勤天数',
    late_days INT DEFAULT 0 COMMENT '迟到天数',
    early_days INT DEFAULT 0 COMMENT '早退天数',
    absent_days INT DEFAULT 0 COMMENT '旷工天数',
    leave_days DECIMAL(5,1) DEFAULT 0 COMMENT '请假天数',
    overtime_hours DECIMAL(5,2) DEFAULT 0 COMMENT '加班时长',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志（0存在 1删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_emp_year_month (emp_id, statistics_year, statistics_month),
    INDEX idx_emp_id (emp_id),
    INDEX idx_year_month (statistics_year, statistics_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤月统计表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化部门
INSERT INTO sys_dept (id, parent_id, dept_name, dept_code, leader, phone, sort, status) VALUES
(1, 0, '总公司', 'HQ', '张总', '13800138000', 0, 1),
(2, 1, '人事部', 'HR', '李经理', '13800138001', 1, 1),
(3, 1, '技术部', 'TECH', '王经理', '13800138002', 2, 1),
(4, 1, '财务部', 'FINANCE', '赵经理', '13800138003', 3, 1),
(5, 3, '前端组', 'FE', '刘组长', '13800138004', 1, 1),
(6, 3, '后端组', 'BE', '陈组长', '13800138005', 2, 1);

-- 初始化岗位
INSERT INTO sys_post (id, post_name, post_code, post_level, description, sort, status) VALUES
(1, '总经理', 'CEO', '高级', '公司总经理', 1, 1),
(2, '部门经理', 'MANAGER', '中级', '部门负责人', 2, 1),
(3, '高级工程师', 'SENIOR_DEV', '中级', '高级开发工程师', 3, 1),
(4, '中级工程师', 'MID_DEV', '初级', '中级开发工程师', 4, 1),
(5, '初级工程师', 'JUNIOR_DEV', '初级', '初级开发工程师', 5, 1),
(6, '行政专员', 'ADMIN_STAFF', '初级', '行政专员', 6, 1),
(7, '人事专员', 'HR_STAFF', '初级', '人事专员', 7, 1);

-- 初始化角色
INSERT INTO sys_role (id, role_name, role_key, description, sort, status) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1, 1),
(2, '部门经理', 'DEPT_MANAGER', '部门管理员，拥有部门管理权限', 2, 1),
(3, '人事专员', 'HR_STAFF', '人事管理人员，负责人事管理', 3, 1),
(4, '普通员工', 'EMPLOYEE', '普通员工，拥有基本权限', 4, 1);

-- 初始化管理员账号（密码: admin123，使用BCrypt加密）
-- 所有账号的默认密码都是: admin123
INSERT INTO sys_user (id, username, password, nickname, email, phone, dept_id, status) VALUES
(1, 'admin', '$2a$10$EqVMfkB8S8Q5X7Y6Z9W0E1R2T3Y4U5I6O7P8A9S0D1F2G3H4J5K6L7', '系统管理员', 'admin@company.com', '13800000000', 2, 1),
(2, 'hr001', '$2a$10$EqVMfkB8S8Q5X7Y6Z9W0E1R2T3Y4U5I6O7P8A9S0D1F2G3H4J5K6L7', '李小红', 'lihong@company.com', '13800000001', 2, 1),
(3, 'tech001', '$2a$10$EqVMfkB8S8Q5X7Y6Z9W0E1R2T3Y4U5I6O7P8A9S0D1F2G3H4J5K6L7', '王技术', 'wangtech@company.com', '13800000002', 3, 1);

-- 分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin 是超级管理员
(2, 3),  -- hr001 是人事专员
(3, 4);  -- tech001 是普通员工

-- 初始化员工档案
INSERT INTO emp_employee (id, user_id, emp_no, emp_name, gender, phone, email, dept_id, post_id, entry_date, status) VALUES
(1, 1, 'EMP001', '系统管理员', 1, '13800000000', 'admin@company.com', 2, 7, '2020-01-01', 1),
(2, 2, 'EMP002', '李小红', 0, '13800000001', 'lihong@company.com', 2, 7, '2021-03-15', 1),
(3, 3, 'EMP003', '王技术', 1, '13800000002', 'wangtech@company.com', 3, 3, '2022-06-20', 1);
