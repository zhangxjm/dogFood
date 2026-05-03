-- =============================================
-- 高并发秒杀系统数据库初始化脚本
-- 创建时间: 2026-05-03
-- 数据库: seckill_db
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS seckill_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE seckill_db;

-- =============================================
-- 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 商品分类表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `level` TINYINT DEFAULT 1 COMMENT '分类级别',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- =============================================
-- 商品表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_title` VARCHAR(500) DEFAULT NULL COMMENT '商品标题',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '原价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `stock_warning` INT DEFAULT 10 COMMENT '库存预警值',
    `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
    `description` TEXT COMMENT '商品描述',
    `main_image` VARCHAR(500) DEFAULT NULL COMMENT '主图',
    `sub_images` TEXT COMMENT '副图(JSON数组)',
    `detail` TEXT COMMENT '商品详情',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `sales` BIGINT DEFAULT 0 COMMENT '销量',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =============================================
-- 秒杀活动表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_seckill_activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `activity_name` VARCHAR(200) NOT NULL COMMENT '活动名称',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    `seckill_count` INT NOT NULL COMMENT '秒杀数量',
    `seckill_limit` INT DEFAULT 1 COMMENT '每人限购数量',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束, 3-已关闭',
    `stock` INT DEFAULT 0 COMMENT '剩余库存',
    `sales` INT DEFAULT 0 COMMENT '已售数量',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '活动描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- =============================================
-- 订单表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `activity_id` BIGINT DEFAULT NULL COMMENT '秒杀活动ID(普通订单为null)',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0-待付款, 1-已付款, 2-已发货, 3-已收货, 4-已取消, 5-已退款',
    `payment_type` TINYINT DEFAULT NULL COMMENT '支付方式: 1-支付宝, 2-微信, 3-银行卡',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `consignee` VARCHAR(50) NOT NULL COMMENT '收货人',
    `phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `address` VARCHAR(500) NOT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `expire_time` DATETIME DEFAULT NULL COMMENT '订单超时时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =============================================
-- 订单流水表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_order_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `operator_type` TINYINT NOT NULL COMMENT '操作人类型: 0-系统, 1-用户, 2-管理员',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `old_status` TINYINT DEFAULT NULL COMMENT '原状态',
    `new_status` TINYINT NOT NULL COMMENT '新状态',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作描述',
    `detail` VARCHAR(1000) DEFAULT NULL COMMENT '详情',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单流水表';

-- =============================================
-- 库存扣减记录表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_stock_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `activity_id` BIGINT DEFAULT NULL COMMENT '秒杀活动ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `quantity` INT NOT NULL COMMENT '扣减数量',
    `stock_before` INT NOT NULL COMMENT '扣减前库存',
    `stock_after` INT NOT NULL COMMENT '扣减后库存',
    `type` TINYINT NOT NULL COMMENT '类型: 1-扣减库存, 2-归还库存',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存扣减记录表';

-- =============================================
-- 管理员表
-- =============================================
CREATE TABLE IF NOT EXISTS `t_admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色: admin-管理员, super_admin-超级管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入管理员 (密码: admin123, 加密后)
INSERT INTO `t_admin` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$.2uXyfTQ9lT6xQ2s0G3l4e5a4s3d2f1g0h9j8k7l6m5n4b3v2c1', '系统管理员', 'super_admin');

-- 插入商品分类
INSERT INTO `t_category` (`name`, `parent_id`, `level`, `sort`) VALUES
('手机数码', 0, 1, 1),
('电脑办公', 0, 1, 2),
('家用电器', 0, 1, 3),
('服饰鞋包', 0, 1, 4),
('美妆个护', 0, 1, 5),
('食品生鲜', 0, 1, 6);

-- 插入测试商品
INSERT INTO `t_product` (`product_name`, `product_title`, `category_id`, `price`, `stock`, `stock_warning`, `main_image`, `status`, `sales`, `sort`) VALUES
('iPhone 15 Pro Max 256GB', 'Apple iPhone 15 Pro Max 256GB 钛金属 5G手机', 1, 9999.00, 100, 10, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20Max%20smartphone%20product%20photo&image_size=square', 1, 0, 1),
('MacBook Pro 14英寸 M3 Pro', 'Apple MacBook Pro 14英寸 M3 Pro芯片 18GB内存 512GB存储', 2, 14999.00, 50, 5, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo&image_size=square', 1, 0, 2),
('索尼 WH-1000XM5 无线降噪耳机', 'Sony WH-1000XM5 头戴式无线蓝牙降噪耳机', 1, 2999.00, 200, 20, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Sony%20wireless%20noise%20cancelling%20headphones%20product%20photo&image_size=square', 1, 0, 3),
('戴森 V15 无线吸尘器', 'Dyson V15 Detect 无绳吸尘器 激光检测灰尘', 3, 5490.00, 80, 8, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Dyson%20cordless%20vacuum%20cleaner%20product%20photo&image_size=square', 1, 0, 4),
('阿迪达斯 Ultraboost 跑步鞋', 'Adidas Ultraboost 22 男子跑步鞋 透气舒适', 4, 1299.00, 500, 50, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Adidas%20Ultraboost%20running%20shoes%20product%20photo&image_size=square', 1, 0, 5);

-- 插入测试秒杀活动 (设置未来时间)
INSERT INTO `t_seckill_activity` (`activity_name`, `product_id`, `seckill_price`, `seckill_count`, `seckill_limit`, `start_time`, `end_time`, `status`, `stock`, `description`) VALUES
('iPhone 15 Pro Max 限时秒杀', 1, 7999.00, 10, 1, DATE_ADD(NOW(), INTERVAL 1 MINUTE), DATE_ADD(NOW(), INTERVAL 2 HOUR), 0, 10, '限时秒杀，立省2000元！每人限购1台'),
('索尼降噪耳机 半价秒杀', 3, 1499.00, 50, 2, DATE_ADD(NOW(), INTERVAL 5 MINUTE), DATE_ADD(NOW(), INTERVAL 3 HOUR), 0, 50, '年度最低价，半价秒杀！每人限购2台');

-- =============================================
-- 创建存储过程: 取消超时订单
-- =============================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS `sp_cancel_expired_orders`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_order_id BIGINT;
    DECLARE v_order_no VARCHAR(64);
    DECLARE v_user_id BIGINT;
    DECLARE v_product_id BIGINT;
    DECLARE v_activity_id BIGINT;
    DECLARE v_quantity INT;
    DECLARE cur CURSOR FOR 
        SELECT id, order_no, user_id, product_id, activity_id, quantity 
        FROM t_order 
        WHERE status = 0 AND expire_time < NOW();
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_order_id, v_order_no, v_user_id, v_product_id, v_activity_id, v_quantity;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 更新订单状态
        UPDATE t_order SET status = 4, cancel_time = NOW(), cancel_reason = '订单超时自动取消', update_time = NOW() 
        WHERE id = v_order_id;
        
        -- 记录订单日志
        INSERT INTO t_order_log (order_id, order_no, user_id, operator_type, new_status, operation, detail)
        VALUES (v_order_id, v_order_no, v_user_id, 0, 4, '订单超时自动取消', '系统自动取消超时未支付订单');
        
        -- 归还商品库存
        UPDATE t_product SET stock = stock + v_quantity, update_time = NOW() 
        WHERE id = v_product_id;
        
        -- 如果是秒杀订单，归还秒杀库存
        IF v_activity_id IS NOT NULL THEN
            UPDATE t_seckill_activity SET stock = stock + v_quantity, sales = sales - v_quantity, update_time = NOW() 
            WHERE id = v_activity_id;
        END IF;
        
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

-- =============================================
-- 创建事件调度器: 每分钟执行一次超时订单取消
-- =============================================
SET GLOBAL event_scheduler = ON;

CREATE EVENT IF NOT EXISTS `ev_cancel_expired_orders`
ON SCHEDULE EVERY 1 MINUTE
STARTS CURRENT_TIMESTAMP
ON COMPLETION PRESERVE
ENABLE
DO
    CALL sp_cancel_expired_orders();
