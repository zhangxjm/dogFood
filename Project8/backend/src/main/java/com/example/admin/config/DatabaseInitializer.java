package com.example.admin.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            initializeDatabase();
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
        }
    }

    private void initializeDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
            log.info("数据库表已存在，跳过初始化");
            insertAdminUserIfNotExists();
        } catch (Exception e) {
            log.info("数据库表不存在，开始初始化...");
            createTables();
            insertInitData();
            log.info("数据库初始化完成，默认账号: admin / 123456");
        }
    }

    private void createTables() {
        log.info("创建数据表...");
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_dept (
                dept_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                parent_id BIGINT DEFAULT 0,
                ancestors VARCHAR(500) DEFAULT '',
                dept_name VARCHAR(50) NOT NULL,
                order_num INT DEFAULT 0,
                leader VARCHAR(20) DEFAULT NULL,
                phone VARCHAR(11) DEFAULT NULL,
                email VARCHAR(50) DEFAULT NULL,
                status CHAR(1) DEFAULT '0',
                del_flag CHAR(1) DEFAULT '0',
                create_by VARCHAR(64) DEFAULT '',
                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                update_by VARCHAR(64) DEFAULT '',
                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_menu (
                menu_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                menu_name VARCHAR(50) NOT NULL,
                parent_id BIGINT DEFAULT 0,
                order_num INT DEFAULT 0,
                path VARCHAR(200) DEFAULT '',
                component VARCHAR(255) DEFAULT NULL,
                query VARCHAR(255) DEFAULT NULL,
                menu_type CHAR(1) DEFAULT '',
                visible CHAR(1) DEFAULT '0',
                perms VARCHAR(100) DEFAULT NULL,
                icon VARCHAR(100) DEFAULT '#',
                create_by VARCHAR(64) DEFAULT '',
                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                update_by VARCHAR(64) DEFAULT '',
                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                remark VARCHAR(500) DEFAULT ''
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_role (
                role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                role_name VARCHAR(30) NOT NULL,
                role_key VARCHAR(100) NOT NULL,
                role_sort INT NOT NULL,
                data_scope CHAR(1) DEFAULT '1',
                status CHAR(1) DEFAULT '0',
                del_flag CHAR(1) DEFAULT '0',
                create_by VARCHAR(64) DEFAULT '',
                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                update_by VARCHAR(64) DEFAULT '',
                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                remark VARCHAR(500) DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_user (
                user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                dept_id BIGINT DEFAULT NULL,
                user_name VARCHAR(30) NOT NULL,
                nick_name VARCHAR(30) NOT NULL,
                email VARCHAR(50) DEFAULT '',
                phonenumber VARCHAR(11) DEFAULT '',
                sex CHAR(1) DEFAULT '0',
                avatar VARCHAR(255) DEFAULT '',
                password VARCHAR(100) DEFAULT '',
                status CHAR(1) DEFAULT '0',
                del_flag CHAR(1) DEFAULT '0',
                login_ip VARCHAR(128) DEFAULT '',
                login_date DATETIME DEFAULT NULL,
                create_by VARCHAR(64) DEFAULT '',
                create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                update_by VARCHAR(64) DEFAULT '',
                update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                remark VARCHAR(500) DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_user_role (
                user_id BIGINT NOT NULL,
                role_id BIGINT NOT NULL,
                PRIMARY KEY (user_id, role_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS sys_role_menu (
                role_id BIGINT NOT NULL,
                menu_id BIGINT NOT NULL,
                PRIMARY KEY (role_id, menu_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);
        
        log.info("数据表创建完成");
    }

    private void insertInitData() {
        log.info("插入初始数据...");
        
        jdbcTemplate.update("""
            INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status) VALUES
            (1, 0, '0', '总公司', 0, '张三', '13800138000', 'admin@example.com', '0'),
            (2, 1, '0,1', '研发部门', 1, '李四', '13800138001', 'dev@example.com', '0'),
            (3, 1, '0,1', '测试部门', 2, '王五', '13800138002', 'test@example.com', '0')
        """);
        
        jdbcTemplate.update("""
            INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, perms, icon) VALUES
            (1, '系统管理', 0, 1, 'system', NULL, 'M', '0', NULL, 'Setting'),
            (2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', '0', 'system:user:list', 'User'),
            (3, '部门管理', 1, 2, 'dept', 'system/dept/index', 'C', '0', 'system:dept:list', 'OfficeBuilding'),
            (4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', '0', 'system:menu:list', 'Menu'),
            (5, '用户查询', 2, 1, '', '', 'F', '0', 'system:user:query', '#'),
            (6, '用户新增', 2, 2, '', '', 'F', '0', 'system:user:add', '#'),
            (7, '用户修改', 2, 3, '', '', 'F', '0', 'system:user:edit', '#'),
            (8, '用户删除', 2, 4, '', '', 'F', '0', 'system:user:remove', '#')
        """);
        
        jdbcTemplate.update("""
            INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, remark) VALUES
            (1, '超级管理员', 'admin', 1, '1', '0', '超级管理员角色'),
            (2, '普通角色', 'common', 2, '5', '0', '普通角色')
        """);
        
        String password = passwordEncoder.encode("123456");
        jdbcTemplate.update("""
            INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, password, status, remark) VALUES
            (1, 1, 'admin', '管理员', 'admin@example.com', '13800138000', '1', ?, '0', '超级管理员')
        """, password);
        
        jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1)");
        
        jdbcTemplate.update("""
            INSERT INTO sys_role_menu (role_id, menu_id) VALUES
            (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
            (2, 1), (2, 2), (2, 5)
        """);
        
        log.info("初始数据插入完成");
    }
    
    private void insertAdminUserIfNotExists() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE user_name = 'admin'", 
                Integer.class
            );
            
            if (count != null && count > 0) {
                return;
            }
            
            String password = passwordEncoder.encode("123456");
            jdbcTemplate.update("""
                INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, email, phonenumber, sex, password, status, remark) VALUES
                (1, 1, 'admin', '管理员', 'admin@example.com', '13800138000', '1', ?, '0', '超级管理员')
            """, password);
            
            log.info("创建默认管理员账号: admin / 123456");
        } catch (Exception e) {
            log.warn("检查或创建管理员用户失败", e);
        }
    }
}
