CREATE DATABASE IF NOT EXISTS device_monitor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE device_monitor;

DROP TABLE IF EXISTS maintenance_log;
DROP TABLE IF EXISTS work_order;
DROP TABLE IF EXISTS operator;
DROP TABLE IF EXISTS alarm;
DROP TABLE IF EXISTS device_data;
DROP TABLE IF EXISTS device;

CREATE TABLE device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型',
    status INT DEFAULT 1 COMMENT '状态：0-离线，1-在线，2-故障',
    location VARCHAR(200) COMMENT '安装位置',
    install_date DATE COMMENT '安装日期',
    temperature_threshold DECIMAL(5,2) DEFAULT 80.00 COMMENT '温度阈值',
    voltage_min DECIMAL(5,2) DEFAULT 200.00 COMMENT '电压最小值',
    voltage_max DECIMAL(5,2) DEFAULT 240.00 COMMENT '电压最大值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

CREATE TABLE device_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    temperature DECIMAL(5,2) COMMENT '温度',
    voltage DECIMAL(5,2) COMMENT '电压',
    current DECIMAL(5,2) COMMENT '电流',
    power DECIMAL(8,2) COMMENT '功率',
    runtime INT DEFAULT 0 COMMENT '运行时长(分钟)',
    status INT DEFAULT 1 COMMENT '状态：0-离线，1-在线，2-故障',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备数据表';

CREATE TABLE alarm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    alarm_type VARCHAR(50) NOT NULL COMMENT '报警类型',
    alarm_level INT DEFAULT 1 COMMENT '报警级别：1-一般，2-重要，3-紧急',
    alarm_message VARCHAR(500) COMMENT '报警信息',
    current_value VARCHAR(100) COMMENT '当前值',
    threshold_value VARCHAR(100) COMMENT '阈值',
    status INT DEFAULT 0 COMMENT '状态：0-未处理，1-已处理',
    handle_time DATETIME COMMENT '处理时间',
    handle_user VARCHAR(50) COMMENT '处理人',
    handle_remark VARCHAR(500) COMMENT '处理备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警表';

CREATE TABLE operator (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role INT DEFAULT 1 COMMENT '角色：1-普通运维，2-管理员',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维人员表';

CREATE TABLE work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单编号',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    alarm_id BIGINT COMMENT '报警ID',
    order_type INT DEFAULT 1 COMMENT '工单类型：1-日常维护，2-故障处理，3-设备检修',
    priority INT DEFAULT 1 COMMENT '优先级：1-低，2-中，3-高，4-紧急',
    title VARCHAR(200) NOT NULL COMMENT '工单标题',
    description TEXT COMMENT '工单描述',
    assign_to BIGINT COMMENT '指派运维人员ID',
    status INT DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成，3-已关闭',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '完成时间',
    result_remark TEXT COMMENT '处理结果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_device_id (device_id),
    INDEX idx_assign_to (assign_to),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

CREATE TABLE maintenance_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    work_order_id BIGINT COMMENT '工单ID',
    operator_id BIGINT COMMENT '运维人员ID',
    maintenance_type VARCHAR(50) COMMENT '维护类型',
    content TEXT COMMENT '维护内容',
    result VARCHAR(500) COMMENT '维护结果',
    cost_time INT COMMENT '耗时(分钟)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维记录表';

INSERT INTO device (device_code, device_name, device_type, status, location, install_date, temperature_threshold, voltage_min, voltage_max) VALUES
('DEV001', '数控车床CNC-001', '数控车床', 1, 'A车间-1号生产线', '2023-01-15', 85.00, 200.00, 240.00),
('DEV002', '铣床Milling-002', '铣床', 1, 'A车间-2号生产线', '2023-02-20', 80.00, 200.00, 240.00),
('DEV003', '磨床Grinding-003', '磨床', 1, 'A车间-3号生产线', '2023-03-10', 75.00, 200.00, 240.00),
('DEV004', '钻床Drill-004', '钻床', 0, 'B车间-1号生产线', '2023-04-05', 70.00, 200.00, 240.00),
('DEV005', '工业机器人Robot-005', '工业机器人', 1, 'B车间-2号生产线', '2023-05-18', 90.00, 200.00, 240.00),
('DEV006', '传送带Conveyor-006', '传送带', 1, 'B车间-3号生产线', '2023-06-22', 60.00, 200.00, 240.00),
('DEV007', '注塑机Injection-007', '注塑机', 1, 'C车间-1号生产线', '2023-07-30', 95.00, 200.00, 240.00),
('DEV008', '冲压机Press-008', '冲压机', 2, 'C车间-2号生产线', '2023-08-15', 85.00, 200.00, 240.00);

INSERT INTO operator (username, password, real_name, phone, email, role, status) VALUES
('admin', '123456', '管理员', '13800138000', 'admin@company.com', 2, 1),
('operator1', '123456', '张三', '13800138001', 'zhangsan@company.com', 1, 1),
('operator2', '123456', '李四', '13800138002', 'lisi@company.com', 1, 1),
('operator3', '123456', '王五', '13800138003', 'wangwu@company.com', 1, 1);
