-- 进销存库存管理系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS inventory_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inventory_system;

-- 操作员表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(50) NOT NULL DEFAULT 'USER' COMMENT '角色',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作员表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    permissions TEXT COMMENT '权限列表，JSON格式',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '操作员ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作类型',
    method VARCHAR(255) COMMENT '方法名称',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    ip VARCHAR(50) COMMENT 'IP地址',
    status INT DEFAULT 1 COMMENT '状态：1-成功 0-失败',
    error_msg TEXT COMMENT '错误信息',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    sort INT DEFAULT 0 COMMENT '排序',
    description VARCHAR(255) COMMENT '分类描述',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    description TEXT COMMENT '供应商描述',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_supplier_name (supplier_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    manager VARCHAR(50) COMMENT '仓库管理员',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '仓库地址',
    description TEXT COMMENT '仓库描述',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_warehouse_code (warehouse_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    unit VARCHAR(20) COMMENT '单位',
    specification VARCHAR(100) COMMENT '规格',
    purchase_price DECIMAL(10, 2) DEFAULT 0 COMMENT '采购价格',
    sale_price DECIMAL(10, 2) DEFAULT 0 COMMENT '销售价格',
    min_stock INT DEFAULT 0 COMMENT '最低库存预警',
    max_stock INT DEFAULT 0 COMMENT '最高库存预警',
    description TEXT COMMENT '商品描述',
    status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_category_id (category_id),
    INDEX idx_product_code (product_code),
    INDEX idx_product_name (product_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    frozen_quantity INT DEFAULT 0 COMMENT '冻结数量',
    last_purchase_time DATETIME COMMENT '最后采购时间',
    last_sale_time DATETIME COMMENT '最后销售时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_product_warehouse (product_id, warehouse_id),
    INDEX idx_product_id (product_id),
    INDEX idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 采购订单表
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '采购单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    warehouse_id BIGINT NOT NULL COMMENT '入库仓库ID',
    total_amount DECIMAL(10, 2) DEFAULT 0 COMMENT '总金额',
    operator_id BIGINT COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员名称',
    status INT DEFAULT 0 COMMENT '状态：0-待入库 1-已入库 2-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_no (order_no),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- 采购订单明细表
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '采购订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '采购数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    amount DECIMAL(10, 2) COMMENT '金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

-- 销售订单表
CREATE TABLE IF NOT EXISTS sale_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '销售单号',
    customer_name VARCHAR(100) COMMENT '客户名称',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    warehouse_id BIGINT NOT NULL COMMENT '出库仓库ID',
    total_amount DECIMAL(10, 2) DEFAULT 0 COMMENT '总金额',
    operator_id BIGINT COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员名称',
    status INT DEFAULT 0 COMMENT '状态：0-待出库 1-已出库 2-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_no (order_no),
    INDEX idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

-- 销售订单明细表
CREATE TABLE IF NOT EXISTS sale_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '销售订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '销售数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    amount DECIMAL(10, 2) COMMENT '金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表';

-- 库存调拨单表
CREATE TABLE IF NOT EXISTS inventory_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    transfer_no VARCHAR(50) NOT NULL UNIQUE COMMENT '调拨单号',
    from_warehouse_id BIGINT NOT NULL COMMENT '调出仓库ID',
    to_warehouse_id BIGINT NOT NULL COMMENT '调入仓库ID',
    operator_id BIGINT COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员名称',
    status INT DEFAULT 0 COMMENT '状态：0-待调拨 1-已调拨 2-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_transfer_no (transfer_no),
    INDEX idx_from_warehouse (from_warehouse_id),
    INDEX idx_to_warehouse (to_warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存调拨单表';

-- 库存调拨明细表
CREATE TABLE IF NOT EXISTS inventory_transfer_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    transfer_id BIGINT NOT NULL COMMENT '调拨单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '调拨数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_transfer_id (transfer_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存调拨明细表';

-- 库存流水记录表
CREATE TABLE IF NOT EXISTS inventory_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    record_no VARCHAR(50) NOT NULL UNIQUE COMMENT '流水号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：PURCHASE-采购入库 SALE-销售出库 TRANSFER_IN-调拨入库 TRANSFER_OUT-调拨出库',
    reference_id BIGINT COMMENT '关联业务单ID',
    reference_no VARCHAR(50) COMMENT '关联业务单号',
    quantity INT NOT NULL COMMENT '变动数量',
    before_quantity INT COMMENT '变动前数量',
    after_quantity INT COMMENT '变动后数量',
    operator_id BIGINT COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员名称',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水记录表';
