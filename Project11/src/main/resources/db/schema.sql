-- 创建数据库
CREATE DATABASE IF NOT EXISTS restaurant_pos DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE restaurant_pos;

-- 员工表
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'CASHIER' COMMENT '角色：ADMIN-店长, CASHIER-收银员, KITCHEN-后厨',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 餐桌表
CREATE TABLE IF NOT EXISTS dining_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    table_no VARCHAR(20) NOT NULL UNIQUE COMMENT '餐桌编号',
    table_name VARCHAR(50) COMMENT '餐桌名称(如：A区1号)',
    capacity INT NOT NULL DEFAULT 4 COMMENT '容纳人数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-空闲, 1-使用中, 2-已预订',
    qr_code VARCHAR(255) COMMENT '二维码内容(点餐链接)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_table_no (table_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐桌表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(255) COMMENT '图片URL',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    table_id BIGINT COMMENT '餐桌ID',
    table_name VARCHAR(50) COMMENT '餐桌名称',
    customer_count INT NOT NULL DEFAULT 1 COMMENT '用餐人数',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    payable_amount DECIMAL(10,2) NOT NULL COMMENT '应付金额',
    paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
    payment_method VARCHAR(20) COMMENT '支付方式：CASH-现金, WECHAT-微信, ALIPAY-支付宝, CARD-刷卡',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待点餐, 1-待支付, 2-已支付, 3-已完成, 4-已取消, 5-已退款',
    remark VARCHAR(255) COMMENT '备注',
    cashier_id BIGINT COMMENT '收银员ID',
    cashier_name VARCHAR(50) COMMENT '收银员姓名',
    kitchen_status TINYINT NOT NULL DEFAULT 0 COMMENT '后厨状态：0-未确认, 1-已确认, 2-制作中, 3-已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    paid_time DATETIME COMMENT '支付时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_order_no (order_no),
    INDEX idx_table_id (table_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_kitchen_status (kitchen_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    dish_price DECIMAL(10,2) NOT NULL COMMENT '菜品单价',
    quantity INT NOT NULL COMMENT '数量',
    total_price DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    remark VARCHAR(255) COMMENT '备注(如：少辣)',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待制作, 1-制作中, 2-已完成, 3-已退菜',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS refund_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_reason VARCHAR(255) COMMENT '退款原因',
    refund_method VARCHAR(20) COMMENT '退款方式：原路退回',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除, 1-已删除',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 初始化管理员账号 (密码: admin123)
INSERT INTO employee (username, password, name, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', '13800138000', 'ADMIN', 1),
('cashier', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '收银员小张', '13800138001', 'CASHIER', 1),
('kitchen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '后厨老李', '13800138002', 'KITCHEN', 1);

-- 初始化菜品分类
INSERT INTO category (name, sort, status) VALUES
('热菜', 1, 1),
('凉菜', 2, 1),
('主食', 3, 1),
('饮品', 4, 1),
('汤类', 5, 1);

-- 初始化菜品
INSERT INTO dish (category_id, name, price, description, status, sort) VALUES
(1, '宫保鸡丁', 38.00, '经典川菜，鸡肉鲜嫩，花生酥脆', 1, 1),
(1, '红烧肉', 48.00, '肥而不腻，入口即化', 1, 2),
(1, '糖醋里脊', 42.00, '酸甜可口，外酥里嫩', 1, 3),
(1, '麻婆豆腐', 28.00, '麻辣鲜香，嫩滑可口', 1, 4),
(2, '凉拌黄瓜', 18.00, '清爽可口，开胃小菜', 1, 1),
(2, '凉拌木耳', 22.00, '营养丰富，口感爽脆', 1, 2),
(3, '米饭', 2.00, '香软白米饭', 1, 1),
(3, '面条', 12.00, '手工面条，劲道十足', 1, 2),
(4, '可乐', 6.00, '冰镇可乐', 1, 1),
(4, '橙汁', 12.00, '鲜榨橙汁', 1, 2),
(5, '紫菜蛋花汤', 12.00, '鲜美可口，营养丰富', 1, 1),
(5, '番茄鸡蛋汤', 10.00, '家常美味，酸甜可口', 1, 2);

-- 初始化餐桌
INSERT INTO dining_table (table_no, table_name, capacity, status) VALUES
('A01', 'A区1号', 4, 0),
('A02', 'A区2号', 4, 0),
('A03', 'A区3号', 6, 0),
('B01', 'B区1号', 8, 0),
('B02', 'B区2号', 10, 0),
('C01', 'C区1号', 2, 0),
('C02', 'C区2号', 2, 0);
