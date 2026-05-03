-- ============================================================
-- 校园二手交易平台数据库脚本
-- 数据库: campus_trade
-- 字符集: utf8mb4
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_trade;

-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色: user-普通用户, admin-管理员',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 商品分类表 (category)
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort` INT(11) NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 3. 商品表 (product)
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '发布用户ID',
    `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '商品标题',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `images` VARCHAR(2000) DEFAULT NULL COMMENT '商品图片URL,多个用逗号分隔',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '交易地点',
    `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系方式',
    `condition` VARCHAR(50) DEFAULT NULL COMMENT '新旧程度',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待审核, active-已上架, sold-已售出, removed-已下架, rejected-审核未通过',
    `view_count` INT(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
    `favorite_count` INT(11) NOT NULL DEFAULT 0 COMMENT '收藏量',
    `is_top` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 4. 收藏表 (favorite)
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================================
-- 5. 留言/消息表 (message)
-- ============================================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `from_user_id` BIGINT(20) NOT NULL COMMENT '发送者ID',
    `to_user_id` BIGINT(20) NOT NULL COMMENT '接收者ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_from_user` (`from_user_id`),
    KEY `idx_to_user` (`to_user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言/消息表';

-- ============================================================
-- 6. 交易记录表 (transaction)
-- ============================================================
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `seller_id` BIGINT(20) NOT NULL COMMENT '卖家ID',
    `buyer_id` BIGINT(20) NOT NULL COMMENT '买家ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '交易状态: pending-待确认, completed-已完成, cancelled-已取消',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '交易备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认分类数据
INSERT INTO `category` (`name`, `icon`, `sort`, `status`) VALUES
('电子产品', '', 1, 1),
('图书教材', '', 2, 1),
('服饰鞋包', '', 3, 1),
('数码配件', '', 4, 1),
('运动户外', '', 5, 1),
('美妆护肤', '', 6, 1),
('家居日用', '', 7, 1),
('其他闲置', '', 8, 1);

-- ============================================================
-- 完成提示
-- ============================================================
SELECT '数据库初始化完成!' AS message;
