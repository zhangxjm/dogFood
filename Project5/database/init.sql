-- ============================================================
-- 企业级后台管理系统数据库初始化脚本
-- 数据库: admin_db
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `admin_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `admin_db`;

-- ============================================================
-- 权限表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '权限类型: 1-菜单, 2-按钮, 3-接口',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父级ID',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- ============================================================
-- 角色表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '角色名称',
  `code` varchar(100) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ============================================================
-- 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码(加密存储)',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态: 0-禁用, 1-启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================================
-- 用户角色关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================================
-- 角色权限关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认权限
INSERT INTO `sys_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`) VALUES
(1, '系统管理', 'system:manage', 1, 0, '/system', 'Layout', 'Setting', 1, 1),
(2, '用户管理', 'system:user:list', 1, 1, '/system/user', 'system/user/index', 'User', 1, 1),
(3, '角色管理', 'system:role:list', 1, 1, '/system/role', 'system/role/index', 'Avatar', 2, 1),
(4, '权限管理', 'system:permission:list', 1, 1, '/system/permission', 'system/permission/index', 'Lock', 3, 1),
(5, '用户查询', 'system:user:query', 3, 2, NULL, NULL, NULL, 1, 1),
(6, '用户新增', 'system:user:add', 3, 2, NULL, NULL, NULL, 2, 1),
(7, '用户编辑', 'system:user:edit', 3, 2, NULL, NULL, NULL, 3, 1),
(8, '用户删除', 'system:user:delete', 3, 2, NULL, NULL, NULL, 4, 1),
(9, '角色查询', 'system:role:query', 3, 3, NULL, NULL, NULL, 1, 1),
(10, '角色新增', 'system:role:add', 3, 3, NULL, NULL, NULL, 2, 1),
(11, '角色编辑', 'system:role:edit', 3, 3, NULL, NULL, NULL, 3, 1),
(12, '角色删除', 'system:role:delete', 3, 3, NULL, NULL, NULL, 4, 1),
(13, '权限查询', 'system:permission:query', 3, 4, NULL, NULL, NULL, 1, 1),
(14, '权限新增', 'system:permission:add', 3, 4, NULL, NULL, NULL, 2, 1),
(15, '权限编辑', 'system:permission:edit', 3, 4, NULL, NULL, NULL, 3, 1),
(16, '权限删除', 'system:permission:delete', 3, 4, NULL, NULL, NULL, 4, 1);

-- 插入默认角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `status`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
(2, '普通管理员', 'ADMIN', '普通管理员，拥有部分管理权限', 1),
(3, '普通用户', 'USER', '普通用户，仅有查看权限', 1);

-- 插入默认用户 (密码: admin123，使用 bcrypt 加密)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
(1, 'admin', '$2b$10$EqckLxQj7P5m6j8k9l0mNO.PQrSTUVWXYZabcdefghijklmnopqrst', '超级管理员', 'admin@example.com', '13800138000', 1),
(2, 'user', '$2b$10$EqckLxQj7P5m6j8k9l0mNO.PQrSTUVWXYZabcdefghijklmnopqrst', '普通用户', 'user@example.com', '13800138001', 1);

-- 插入用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 3);

-- 插入角色权限关联 (超级管理员拥有所有权限)
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 9), (2, 13),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 9), (3, 13);
