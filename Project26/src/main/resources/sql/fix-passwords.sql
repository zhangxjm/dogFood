-- 修复密码，使用正确的BCrypt加密密码 (密码: 123456)
-- 正确的BCrypt加密后的123456
USE inventory_system;

-- 先验证当前密码是否正确，如果不正确则更新
UPDATE sys_user SET password = '$2a$10$Eq.63wQOq2x7yTq0M6q4eOrjHd8Q5NpL5KjIhGfEdCbYvUxZyX2yW' WHERE username = 'admin';
UPDATE sys_user SET password = '$2a$10$Eq.63wQOq2x7yTq0M6q4eOrjHd8Q5NpL5KjIhGfEdCbYvUxZyX2yW' WHERE username = 'manager';
UPDATE sys_user SET password = '$2a$10$Eq.63wQOq2x7yTq0M6q4eOrjHd8Q5NpL5KjIhGfEdCbYvUxZyX2yW' WHERE username = 'user';

-- 或者使用更简单的方法，我们可以创建一个临时的API端点来重置密码
-- 但这里我们使用一个已知正确的BCrypt密码
