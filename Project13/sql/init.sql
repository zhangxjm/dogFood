-- =====================================================
-- 物流追踪系统 - 数据库初始化脚本
-- 数据库：logistics
-- =====================================================

CREATE DATABASE IF NOT EXISTS logistics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE logistics;

-- =====================================================
-- 用户表
-- =====================================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role INT DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 订单表
-- =====================================================
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT COMMENT '用户ID',
    sender_name VARCHAR(50) NOT NULL COMMENT '寄件人姓名',
    sender_phone VARCHAR(20) NOT NULL COMMENT '寄件人电话',
    sender_address VARCHAR(200) NOT NULL COMMENT '寄件人地址',
    sender_lat DOUBLE COMMENT '寄件人纬度',
    sender_lng DOUBLE COMMENT '寄件人经度',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收件人电话',
    receiver_address VARCHAR(200) NOT NULL COMMENT '收件人地址',
    receiver_lat DOUBLE COMMENT '收件人纬度',
    receiver_lng DOUBLE COMMENT '收件人经度',
    goods_name VARCHAR(100) NOT NULL COMMENT '物品名称',
    weight DOUBLE COMMENT '重量(kg)',
    volume DOUBLE COMMENT '体积(m³)',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 0 COMMENT '状态：0-待审核 1-已审核 2-已取件 3-运输中 4-派送中 5-已签收 6-已取消',
    freight DECIMAL(10,2) COMMENT '运费',
    expected_time DATETIME COMMENT '预计送达时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =====================================================
-- 物流信息表
-- =====================================================
DROP TABLE IF EXISTS t_logistics;
CREATE TABLE t_logistics (
    id BIGINT PRIMARY KEY COMMENT '物流ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    current_location VARCHAR(200) COMMENT '当前位置',
    current_lat DOUBLE COMMENT '当前纬度',
    current_lng DOUBLE COMMENT '当前经度',
    status INT DEFAULT 0 COMMENT '物流状态',
    courier_name VARCHAR(50) COMMENT '快递员姓名',
    courier_phone VARCHAR(20) COMMENT '快递员电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- =====================================================
-- 物流轨迹表
-- =====================================================
DROP TABLE IF EXISTS t_logistics_track;
CREATE TABLE t_logistics_track (
    id BIGINT PRIMARY KEY COMMENT '轨迹ID',
    logistics_id BIGINT COMMENT '物流ID',
    order_id BIGINT COMMENT '订单ID',
    order_no VARCHAR(32) COMMENT '订单编号',
    location VARCHAR(200) COMMENT '位置',
    lat DOUBLE COMMENT '纬度',
    lng DOUBLE COMMENT '经度',
    description VARCHAR(500) COMMENT '状态描述',
    status INT COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_logistics_id (logistics_id),
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入管理员用户 (密码: admin123, MD5加密后)
INSERT INTO t_user (id, username, password, phone, email, role, create_time) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', '13800138000', 'admin@logistics.com', 1, NOW()),
(2, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', 'user1@example.com', 0, NOW()),
(3, 'user2', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', 'user2@example.com', 0, NOW());

-- 插入测试订单
INSERT INTO t_order (id, order_no, user_id, sender_name, sender_phone, sender_address, sender_lat, sender_lng, 
    receiver_name, receiver_phone, receiver_address, receiver_lat, receiver_lng,
    goods_name, weight, volume, status, freight, create_time) VALUES
(1001, 'LT202401011200000001', 2, '张三', '13800138001', '北京市朝阳区国贸中心', 39.9087, 116.4605,
    '李四', '13800138002', '上海市浦东新区陆家嘴', 31.2304, 121.4737,
    '电子设备', 2.5, 0.02, 3, 25.00, NOW()),
(1002, 'LT202401011200000002', 2, '王五', '13800138003', '广州市天河区珠江新城', 23.1291, 113.2644,
    '赵六', '13800138004', '深圳市南山区科技园', 22.5431, 113.9413,
    '文件资料', 0.5, 0.005, 5, 15.00, NOW()),
(1003, 'LT202401011200000003', 3, '孙七', '13800138005', '杭州市西湖区', 30.2741, 120.1551,
    '周八', '13800138006', '南京市鼓楼区', 32.0603, 118.7969,
    '服装鞋帽', 3.0, 0.05, 0, 30.00, NOW());

-- 插入物流信息
INSERT INTO t_logistics (id, order_id, order_no, current_location, current_lat, current_lng, status, courier_name, courier_phone, create_time) VALUES
(2001, 1001, 'LT202401011200000001', '上海市浦东新区中转站', 31.2200, 121.5000, 3, '张快递员', '13900139001', NOW()),
(2002, 1002, 'LT202401011200000002', '深圳市南山区派送点', 22.5300, 113.9300, 5, '李快递员', '13900139002', NOW()),
(2003, 1003, 'LT202401011200000003', '杭州市西湖区取件点', 30.2700, 120.1500, 0, NULL, NULL, NOW());

-- 插入物流轨迹
INSERT INTO t_logistics_track (id, logistics_id, order_id, order_no, location, lat, lng, description, status, create_time) VALUES
(3001, 2001, 1001, 'LT202401011200000001', '北京市朝阳区国贸中心', 39.9087, 116.4605, '订单已创建，等待审核', 0, NOW() - INTERVAL 72 HOUR),
(3002, 2001, 1001, 'LT202401011200000001', '北京市朝阳区国贸中心', 39.9087, 116.4605, '订单已审核通过', 1, NOW() - INTERVAL 70 HOUR),
(3003, 2001, 1001, 'LT202401011200000001', '北京市朝阳区国贸中心', 39.9087, 116.4605, '快递员已取件', 2, NOW() - INTERVAL 68 HOUR),
(3004, 2001, 1001, 'LT202401011200000001', '北京市大兴区转运中心', 39.7289, 116.3495, '货物已到达北京转运中心', 3, NOW() - INTERVAL 60 HOUR),
(3005, 2001, 1001, 'LT202401011200000001', '上海市浦东新区中转站', 31.2200, 121.5000, '货物已到达上海中转站，正在配送中', 3, NOW() - INTERVAL 4 HOUR),

(3006, 2002, 1002, 'LT202401011200000002', '广州市天河区珠江新城', 23.1291, 113.2644, '订单已创建，等待审核', 0, NOW() - INTERVAL 48 HOUR),
(3007, 2002, 1002, 'LT202401011200000002', '广州市天河区珠江新城', 23.1291, 113.2644, '订单已审核通过', 1, NOW() - INTERVAL 46 HOUR),
(3008, 2002, 1002, 'LT202401011200000002', '深圳市南山区科技园', 22.5431, 113.9413, '快递员正在派送中', 4, NOW() - INTERVAL 3 HOUR),
(3009, 2002, 1002, 'LT202401011200000002', '深圳市南山区科技园', 22.5431, 113.9413, '已签收，感谢使用', 5, NOW() - INTERVAL 1 HOUR),

(3010, 2003, 1003, 'LT202401011200000003', '杭州市西湖区', 30.2741, 120.1551, '订单已创建，等待审核', 0, NOW() - INTERVAL 1 HOUR);
