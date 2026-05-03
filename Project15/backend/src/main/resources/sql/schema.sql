CREATE DATABASE IF NOT EXISTS job_recruitment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE job_recruitment;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-求职者，2-企业HR，3-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-待审核',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公司ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    company_name VARCHAR(100) NOT NULL COMMENT '公司名称',
    industry VARCHAR(100) COMMENT '所属行业',
    scale VARCHAR(50) COMMENT '公司规模',
    address VARCHAR(255) COMMENT '公司地址',
    description TEXT COMMENT '公司简介',
    logo VARCHAR(255) COMMENT '公司Logo',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-通过，2-拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司表';

CREATE TABLE IF NOT EXISTS job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
    company_id BIGINT NOT NULL COMMENT '公司ID',
    job_title VARCHAR(100) NOT NULL COMMENT '职位名称',
    job_type VARCHAR(50) COMMENT '职位类型：全职/兼职/实习',
    salary_min INT COMMENT '最低薪资',
    salary_max INT COMMENT '最高薪资',
    city VARCHAR(50) COMMENT '工作城市',
    address VARCHAR(255) COMMENT '具体地址',
    experience VARCHAR(50) COMMENT '经验要求',
    education VARCHAR(50) COMMENT '学历要求',
    job_description TEXT COMMENT '职位描述',
    job_requirement TEXT COMMENT '职位要求',
    welfare VARCHAR(500) COMMENT '福利待遇',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-上架，2-下架，3-拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_company_id (company_id),
    INDEX idx_job_title (job_title),
    INDEX idx_city (city),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

CREATE TABLE IF NOT EXISTS resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender TINYINT COMMENT '性别：0-女，1-男',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '电子邮箱',
    birthday DATE COMMENT '出生日期',
    city VARCHAR(50) COMMENT '所在城市',
    avatar VARCHAR(255) COMMENT '头像',
    job_intention VARCHAR(100) COMMENT '求职意向',
    expected_salary_min INT COMMENT '期望最低薪资',
    expected_salary_max INT COMMENT '期望最高薪资',
    expected_city VARCHAR(50) COMMENT '期望城市',
    education TEXT COMMENT '教育经历（JSON格式）',
    work_experience TEXT COMMENT '工作经历（JSON格式）',
    project_experience TEXT COMMENT '项目经历（JSON格式）',
    skills VARCHAR(500) COMMENT '技能标签',
    self_evaluation TEXT COMMENT '自我评价',
    status TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-公开',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_job_intention (job_intention)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

CREATE TABLE IF NOT EXISTS job_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投递记录ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    user_id BIGINT NOT NULL COMMENT '求职者ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    company_id BIGINT NOT NULL COMMENT '公司ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理，1-已查看，2-邀面试，3-不合适，4-已录用',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_job_id (job_id),
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位投递表';

CREATE TABLE IF NOT EXISTS job_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_job (user_id, job_id),
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位收藏表';

CREATE TABLE IF NOT EXISTS message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    message_type TINYINT NOT NULL DEFAULT 0 COMMENT '消息类型：0-系统消息，1-沟通消息，2-面试通知',
    content TEXT NOT NULL COMMENT '消息内容',
    related_id BIGINT COMMENT '关联ID（投递ID/面试ID等）',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

CREATE TABLE IF NOT EXISTS interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试安排ID',
    application_id BIGINT NOT NULL COMMENT '投递记录ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    user_id BIGINT NOT NULL COMMENT '求职者ID',
    company_id BIGINT NOT NULL COMMENT '公司ID',
    interview_time DATETIME COMMENT '面试时间',
    interview_type TINYINT COMMENT '面试类型：0-线下面试，1-视频面试，2-电话面试',
    interview_address VARCHAR(255) COMMENT '面试地址/视频链接',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    notes TEXT COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待确认，1-已接受，2-已拒绝，3-已完成，4-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_application_id (application_id),
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试安排表';
