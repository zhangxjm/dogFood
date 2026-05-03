-- =============================================
-- 校园社团管理系统数据库初始化脚本
-- 数据库名称: campus_club
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_club DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_club;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1学生 2社长 3管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 社团分类表
-- =============================================
DROP TABLE IF EXISTS `club_category`;
CREATE TABLE `club_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社团分类表';

-- =============================================
-- 3. 社团表
-- =============================================
DROP TABLE IF EXISTS `club`;
CREATE TABLE `club` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '社团ID',
    `name` VARCHAR(100) NOT NULL COMMENT '社团名称',
    `logo` VARCHAR(255) DEFAULT NULL COMMENT '社团Logo',
    `description` TEXT COMMENT '社团描述',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `president_id` BIGINT NOT NULL COMMENT '社长用户ID',
    `max_members` INT DEFAULT 100 COMMENT '最大成员数',
    `current_members` INT DEFAULT 0 COMMENT '当前成员数',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_president` (`president_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社团表';

-- =============================================
-- 4. 社团成员表
-- =============================================
DROP TABLE IF EXISTS `club_member`;
CREATE TABLE `club_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员ID',
    `club_id` BIGINT NOT NULL COMMENT '社团ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` TINYINT DEFAULT 2 COMMENT '角色：1社长 2普通成员',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待审核 1已通过 2已拒绝 3已退出',
    `apply_remark` VARCHAR(255) DEFAULT NULL COMMENT '申请备注',
    `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `join_time` DATETIME DEFAULT NULL COMMENT '加入时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_club_user` (`club_id`, `user_id`),
    KEY `idx_club` (`club_id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社团成员表';

-- =============================================
-- 5. 活动表
-- =============================================
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `club_id` BIGINT NOT NULL COMMENT '社团ID',
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题',
    `cover` VARCHAR(255) DEFAULT NULL COMMENT '活动封面',
    `description` TEXT COMMENT '活动描述',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `max_participants` INT DEFAULT 50 COMMENT '最大参与人数',
    `current_participants` INT DEFAULT 0 COMMENT '当前参与人数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0已取消 1报名中 2进行中 3已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_club` (`club_id`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =============================================
-- 6. 活动报名表
-- =============================================
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0已取消 1已报名 2已完成',
    `sign_time` DATETIME DEFAULT NULL COMMENT '签到时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    KEY `idx_activity` (`activity_id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化用户数据（密码统一为 123456）
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `phone`, `gender`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EX', '系统管理员', 'admin@campus.com', '13800138000', 1, 3, 1),
('president', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EX', '张三', 'zhangsan@campus.com', '13800138001', 1, 2, 1),
('student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EX', '李四', 'lisi@campus.com', '13800138002', 2, 1, 1),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EX', '王五', 'wangwu@campus.com', '13800138003', 1, 1, 1),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EX', '赵六', 'zhaoliu@campus.com', '13800138004', 2, 1, 1);

-- 初始化社团分类数据
INSERT INTO `club_category` (`name`, `description`, `sort`, `status`) VALUES
('文学艺术类', '书法、绘画、音乐、舞蹈等文艺类社团', 1, 1),
('体育运动类', '篮球、足球、羽毛球、健身等体育类社团', 2, 1),
('学术科技类', '计算机、数学、物理、化学等学术类社团', 3, 1),
('公益实践类', '志愿者、环保、支教等公益实践类社团', 4, 1),
('兴趣爱好类', '摄影、动漫、棋牌、手工等兴趣类社团', 5, 1);

-- 初始化社团数据
INSERT INTO `club` (`name`, `logo`, `description`, `category_id`, `president_id`, `max_members`, `current_members`, `status`) VALUES
('计算机协会', NULL, '计算机协会致力于推广计算机知识，开展编程培训、技术交流等活动', 3, 2, 100, 5, 1),
('篮球社', NULL, '篮球社是热爱篮球运动的同学聚集地，定期组织篮球比赛和训练', 2, 2, 50, 15, 1),
('书法协会', NULL, '书法协会传承中华书法艺术，开展书法教学、展览等活动', 1, 2, 30, 8, 1),
('志愿者协会', NULL, '志愿者协会组织各类志愿服务活动，传递爱心，服务社会', 4, 2, 200, 50, 1);

-- 初始化社团成员数据
INSERT INTO `club_member` (`club_id`, `user_id`, `role`, `status`, `join_time`) VALUES
(1, 2, 1, 1, NOW()),  -- 张三是计算机协会社长
(1, 3, 2, 1, NOW()),  -- 李四是计算机协会成员
(1, 4, 2, 1, NOW()),  -- 王五是计算机协会成员
(2, 2, 1, 1, NOW()),  -- 张三是篮球社社长
(2, 5, 2, 1, NOW()),  -- 赵六是篮球社成员
(3, 2, 1, 1, NOW());  -- 张三是书法协会社长

-- 初始化活动数据
INSERT INTO `activity` (`club_id`, `title`, `cover`, `description`, `location`, `start_time`, `end_time`, `max_participants`, `current_participants`, `status`) VALUES
(1, 'Java编程入门培训', NULL, '面向零基础同学的Java编程入门培训，由资深学长授课', '教学楼A101', '2026-05-10 14:00:00', '2026-05-10 17:00:00', 30, 5, 1),
(1, '黑客马拉松编程大赛', NULL, '48小时编程挑战赛，展示你的技术实力！', '创新创业中心', '2026-05-20 08:00:00', '2026-05-22 08:00:00', 50, 10, 1),
(2, '新生篮球赛', NULL, '欢迎新同学加入篮球社，通过比赛认识新朋友！', '篮球场', '2026-05-15 16:00:00', '2026-05-15 18:00:00', 20, 8, 1),
(4, '校园清洁日活动', NULL, '让我们一起为美丽校园贡献一份力量', '校园各区域', '2026-05-25 09:00:00', '2026-05-25 11:00:00', 100, 30, 1);

-- 初始化活动报名数据
INSERT INTO `activity_registration` (`activity_id`, `user_id`, `status`) VALUES
(1, 3, 1),  -- 李四报名Java培训
(1, 4, 1),  -- 王五报名Java培训
(2, 3, 1);  -- 李四报名黑客马拉松

-- 提交事务
COMMIT;

-- =============================================
-- 数据库初始化完成
-- =============================================
-- 默认账户信息：
-- 系统管理员：admin / 123456
-- 社长：president / 123456
-- 学生：student / 123456
-- =============================================
