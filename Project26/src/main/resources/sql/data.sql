-- 初始化数据脚本
USE inventory_system;

-- 初始化角色
INSERT INTO sys_role (role_code, role_name, description, permissions, status) VALUES
('ADMIN', '系统管理员', '拥有系统所有权限', '["*"]', 1),
('MANAGER', '仓库经理', '仓库管理权限', '["product:*", "inventory:*", "purchase:*", "sale:*", "transfer:*", "report:*"]', 1),
('USER', '普通用户', '基础操作权限', '["product:view", "inventory:view", "purchase:view", "sale:view", "transfer:view"]', 1);

-- 初始化管理员账号 (密码: 123456)
-- BCrypt 加密后的 123456
INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin', '$2a$10$V5AXvOwM4ValIHdEL10g3ukGvfAjluuDbnptKaKmVX4fFkKc3KTsC', '系统管理员', '13800138000', 'admin@inventory.com', 'ADMIN', 1),
('manager', '$2a$10$V5AXvOwM4ValIHdEL10g3ukGvfAjluuDbnptKaKmVX4fFkKc3KTsC', '仓库经理', '13800138001', 'manager@inventory.com', 'MANAGER', 1),
('user', '$2a$10$V5AXvOwM4ValIHdEL10g3ukGvfAjluuDbnptKaKmVX4fFkKc3KTsC', '普通用户', '13800138002', 'user@inventory.com', 'USER', 1);

-- 初始化商品分类
INSERT INTO product_category (category_name, parent_id, sort, description, status) VALUES
('电子产品', 0, 1, '电子类产品', 1),
('办公文具', 0, 2, '办公用文具', 1),
('日用品', 0, 3, '日常用品', 1),
('手机', 1, 1, '智能手机', 1),
('电脑', 1, 2, '电脑设备', 1),
('笔记本', 2, 1, '笔记本本子', 1),
('笔类', 2, 2, '书写用笔', 1);

-- 初始化供应商
INSERT INTO supplier (supplier_name, contact_person, phone, email, address, description, status) VALUES
('电子产品供应商有限公司', '张经理', '13900139001', 'contact@electronics.com', '深圳市南山区科技园1栋', '专业电子产品供应商', 1),
('办公文具批发中心', '李经理', '13900139002', 'contact@stationery.com', '广州市天河区体育西路100号', '办公文具一站式采购', 1),
('日用品贸易公司', '王经理', '13900139003', 'contact@daily.com', '杭州市西湖区文三路50号', '日用品批发零售', 1);

-- 初始化仓库
INSERT INTO warehouse (warehouse_code, warehouse_name, manager, phone, address, description, status) VALUES
('WH001', '主仓库', '张仓库', '13800138101', '北京市朝阳区建国路88号', '公司主要仓库', 1),
('WH002', '备用仓库', '李仓库', '13800138102', '北京市海淀区中关村大街100号', '备用仓储空间', 1),
('WH003', '冷链仓库', '王仓库', '13800138103', '北京市丰台区丰台路50号', '低温冷藏仓库', 1);

-- 初始化商品
INSERT INTO product (product_code, product_name, category_id, unit, specification, purchase_price, sale_price, min_stock, max_stock, description, status) VALUES
('P001', 'iPhone 15 Pro', 4, '台', '256GB', 6999.00, 7999.00, 10, 100, '苹果旗舰手机', 1),
('P002', '华为Mate 60 Pro', 4, '台', '256GB', 5999.00, 6999.00, 10, 100, '华为旗舰手机', 1),
('P003', 'MacBook Pro 14寸', 5, '台', 'M3芯片', 11999.00, 13999.00, 5, 50, '苹果笔记本电脑', 1),
('P004', '联想ThinkPad X1', 5, '台', 'i7处理器', 8999.00, 9999.00, 5, 50, '商务笔记本电脑', 1),
('P005', 'A5笔记本', 6, '本', '100页', 5.00, 8.00, 100, 1000, '普通笔记本', 1),
('P006', '晨光中性笔', 7, '支', '0.5mm黑色', 1.00, 2.00, 500, 5000, '书写中性笔', 1),
('P007', '抽纸', 3, '包', '200抽', 8.00, 12.00, 200, 2000, '生活用纸', 1),
('P008', '洗衣液', 3, '瓶', '5L', 35.00, 49.00, 50, 500, '衣物清洁液', 1);
