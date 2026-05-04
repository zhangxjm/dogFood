-- 创建数据库
CREATE DATABASE IF NOT EXISTS homemaking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE homemaking;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, WORKER-师傅, ADMIN-管理员',
    status INT DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 师傅表
CREATE TABLE IF NOT EXISTS worker (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    service_category_id BIGINT COMMENT '服务分类ID',
    id_card VARCHAR(18) COMMENT '身份证号',
    id_card_front VARCHAR(255) COMMENT '身份证正面照',
    id_card_back VARCHAR(255) COMMENT '身份证反面照',
    skill_desc TEXT COMMENT '技能描述',
    work_years INT DEFAULT 0 COMMENT '工作年限',
    rating DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
    order_count INT DEFAULT 0 COMMENT '接单数量',
    audit_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    audit_remark VARCHAR(255) COMMENT '审核备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师傅表';

-- 服务分类表
CREATE TABLE IF NOT EXISTS service_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- 服务项目表
CREATE TABLE IF NOT EXISTS service_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '服务名称',
    description TEXT COMMENT '服务描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    unit VARCHAR(20) COMMENT '单位',
    image VARCHAR(255) COMMENT '图片',
    status INT DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    worker_id BIGINT COMMENT '师傅ID',
    service_item_id BIGINT NOT NULL COMMENT '服务项目ID',
    service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
    service_quantity INT DEFAULT 1 COMMENT '服务数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    service_address VARCHAR(255) NOT NULL COMMENT '服务地址',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    service_time DATETIME NOT NULL COMMENT '预约服务时间',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待接单, ACCEPTED-服务中, COMPLETED-已完成, RATED-已评价, CANCELLED-已取消',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 评价表
CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    worker_id BIGINT NOT NULL COMMENT '师傅ID',
    rating INT NOT NULL COMMENT '评分：1-5星',
    content TEXT COMMENT '评价内容',
    images VARCHAR(1000) COMMENT '评价图片，多个用逗号分隔',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type VARCHAR(20) DEFAULT 'SYSTEM' COMMENT '类型：SYSTEM-系统公告, ACTIVITY-活动公告',
    status INT DEFAULT 1 COMMENT '状态：0-隐藏, 1-显示',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 初始化数据
-- 管理员账号：admin / 123456 (BCrypt加密)
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('admin', '$2a$10$m7uG57oBkkfXGMaRLJHfoOdyGH/K/yfWDH4EA/oBUAVdVqZby50Rq', '管理员', '13800138000', 'ADMIN', 1);

-- 普通用户：user1 / 123456 (BCrypt加密)
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('user1', '$2a$10$m7uG57oBkkfXGMaRLJHfoOdyGH/K/yfWDH4EA/oBUAVdVqZby50Rq', '张三', '13800138001', 'USER', 1);

-- 服务分类
INSERT INTO service_category (name, icon, sort, status) VALUES
('保洁服务', 'cleaning', 1, 1),
('维修服务', 'repair', 2, 1),
('保姆服务', 'nanny', 3, 1),
('搬家服务', 'moving', 4, 1);

-- 服务项目
INSERT INTO service_item (category_id, name, description, price, unit, status, sort) VALUES
(1, '日常保洁', '包括客厅、卧室、厨房、卫生间等区域的日常清洁', 99.00, '次', 1, 1),
(1, '深度保洁', '全面深度清洁，包括油烟机、冰箱等家电表面清洁', 199.00, '次', 1, 2),
(1, '开荒保洁', '新房装修后的首次全面清洁', 299.00, '次', 1, 3),
(2, '家电维修', '各类家电上门维修服务', 150.00, '次', 1, 1),
(2, '水电维修', '家庭水电线路维修和安装', 100.00, '次', 1, 2),
(2, '管道疏通', '下水道、马桶等管道疏通服务', 80.00, '次', 1, 3),
(3, '月嫂服务', '专业月嫂，提供产后护理和新生儿照料', 15000.00, '月', 1, 1),
(3, '育儿嫂', '专业育儿嫂，提供婴幼儿照料和启蒙教育', 8000.00, '月', 1, 2),
(3, '钟点工', '按小时提供家政服务', 50.00, '小时', 1, 3),
(4, '居民搬家', '专业居民搬家服务，提供包装和搬运', 300.00, '次', 1, 1),
(4, '公司搬迁', '企业办公室搬迁服务', 500.00, '次', 1, 2),
(4, '家具拆装', '专业家具拆装服务', 100.00, '件', 1, 3);

-- 公告
INSERT INTO announcement (title, content, type, status, sort) VALUES
('欢迎使用家政服务平台', '欢迎使用我们的家政服务预约管理系统，如有任何问题请联系客服。', 'SYSTEM', 1, 1),
('新用户专享优惠', '新用户首次下单可享受8折优惠，快来体验吧！', 'ACTIVITY', 1, 2);
