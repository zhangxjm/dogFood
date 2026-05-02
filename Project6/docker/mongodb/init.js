// MongoDB 初始化脚本
// 这个脚本会在 MongoDB 容器首次启动时执行

// 切换到 rnapp 数据库
db = db.getSiblingDB('rnapp');

// 创建索引以提高查询性能
db.users.createIndex({ username: 1 }, { unique: true });
db.users.createIndex({ email: 1 }, { unique: true });

db.items.createIndex({ user: 1 });
db.items.createIndex({ category: 1 });
db.items.createIndex({ status: 1 });
db.items.createIndex({ createdAt: -1 });
db.items.createIndex({ title: 'text', description: 'text' });

// 插入一些示例数据（可选）
print('✅ MongoDB 初始化完成');
print('📁 数据库: rnapp');
print('📊 集合: users, items');
