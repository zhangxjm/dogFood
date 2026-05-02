# React + Express + MongoDB 全栈管理系统

一个功能完整的前后端分离全栈项目，包含用户认证、角色权限、用户管理等核心功能。

## 技术栈

### 前端
- **React 18** - 前端框架
- **Ant Design 5** - UI 组件库
- **React Router 6** - 路由管理
- **Zustand** - 状态管理
- **Axios** - HTTP 客户端
- **Vite** - 构建工具

### 后端
- **Node.js** - 运行环境
- **Express** - Web 框架
- **Mongoose** - MongoDB ODM
- **JWT** - 身份认证
- **bcryptjs** - 密码加密
- **express-validator** - 参数验证
- **cors** - 跨域处理

### 数据库
- **MongoDB** - 文档数据库

## 项目结构

```
Project1/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── components/          # 公共组件
│   │   ├── layouts/             # 布局组件
│   │   │   ├── MainLayout.jsx   # 主布局
│   │   │   └── MainLayout.css
│   │   ├── pages/               # 页面组件
│   │   │   ├── Dashboard/       # 仪表板
│   │   │   ├── Login/           # 登录
│   │   │   ├── Register/        # 注册
│   │   │   └── Users/           # 用户管理
│   │   ├── router/              # 路由配置
│   │   │   ├── index.jsx
│   │   │   └── guards.jsx
│   │   ├── services/            # API 服务
│   │   │   ├── auth.js
│   │   │   └── user.js
│   │   ├── stores/              # 状态管理
│   │   │   └── userStore.js
│   │   ├── utils/               # 工具函数
│   │   │   └── request.js
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   ├── vite.config.js
│   └── .env
├── backend/                     # 后端项目
│   ├── src/
│   │   ├── config/              # 配置
│   │   │   └── database.js
│   │   ├── controllers/         # 控制器
│   │   │   ├── authController.js
│   │   │   └── userController.js
│   │   ├── middleware/          # 中间件
│   │   │   ├── auth.js
│   │   │   └── errorHandler.js
│   │   ├── models/              # 数据模型
│   │   │   └── User.js
│   │   ├── routes/              # 路由
│   │   │   ├── authRoutes.js
│   │   │   └── userRoutes.js
│   │   ├── utils/               # 工具函数
│   │   │   └── ApiResponse.js
│   │   └── app.js               # 应用入口
│   ├── Dockerfile
│   ├── package.json
│   └── .env
├── docker-compose.yml           # Docker 编排
└── README.md
```

## 功能特性

### 1. 用户认证
- 用户注册（第一个用户自动成为管理员）
- 用户登录（JWT 令牌认证）
- 退出登录
- Token 自动过期处理
- Token 持久化存储

### 2. 角色权限
- **管理员 (admin)**：拥有所有权限
- **普通用户 (user)**：基础访问权限

### 3. 用户管理（管理员功能）
- 用户列表查询（分页）
- 搜索功能（用户名、邮箱、真实姓名）
- 筛选功能（角色、状态）
- 新增用户
- 编辑用户
- 删除用户
- 启用/禁用用户

### 4. 技术特性
- **前端**
  - 路由懒加载
  - 全局请求拦截
  - 响应拦截统一处理
  - 路由守卫权限控制
  - 响应式设计
  
- **后端**
  - 分层架构（路由/控制器/模型/中间件）
  - 统一接口响应封装
  - 全局异常处理
  - CORS 跨域配置
  - 参数验证
  - 密码加密存储

## 快速开始

### 方式一：Docker 启动（推荐）

#### 环境要求
- Docker
- Docker Compose

#### 启动命令
```bash
# 进入项目根目录
cd Project1

# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 访问地址
- 前端：http://localhost:3000
- 后端 API：http://localhost:3001
- MongoDB：mongodb://localhost:27017

#### 停止服务
```bash
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v
```

### 方式二：本地启动

#### 环境要求
- Node.js >= 16
- MongoDB（本地或 Docker）

#### 启动 MongoDB
```bash
# 使用 Docker 启动 MongoDB
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  mongo:6.0
```

#### 启动后端
```bash
# 进入后端目录
cd backend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 或生产模式
npm start
```

后端服务将在 http://localhost:3001 启动

#### 启动前端
```bash
# 进入前端目录（新开终端）
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 或构建生产版本
npm run build
```

前端服务将在 http://localhost:3000 启动

## 默认账号

首次注册的用户会自动成为管理员。

示例账号：
- 用户名：`admin`
- 邮箱：`admin@example.com`
- 密码：`123456`
- 角色：管理员

## API 文档

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456",
  "realName": "测试用户"  // 可选
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 获取当前用户
```
GET /api/auth/me
Authorization: Bearer <token>
```

#### 修改密码
```
PUT /api/auth/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### 用户管理接口（需要管理员权限）

#### 获取用户列表
```
GET /api/users?page=1&pageSize=10&search=test&role=user&status=true
Authorization: Bearer <token>
```

#### 创建用户
```
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "123456",
  "realName": "新用户",
  "role": "user",
  "status": true
}
```

#### 更新用户
```
PUT /api/users/:id
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "updated@example.com",
  "status": false
}
```

#### 删除用户
```
DELETE /api/users/:id
Authorization: Bearer <token>
```

#### 批量删除用户
```
DELETE /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "ids": ["id1", "id2"]
}
```

## 环境变量配置

### 后端环境变量 (backend/.env)
```env
# 服务器配置
PORT=3001
NODE_ENV=development

# MongoDB 连接配置
MONGODB_URI=mongodb://localhost:27017/fullstack-app

# JWT 配置
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRES_IN=7d

# 盐值轮次
BCRYPT_SALT_ROUNDS=10

# 跨域配置
CORS_ORIGIN=http://localhost:3000
```

### 前端环境变量 (frontend/.env)
```env
# 后端 API 地址
VITE_API_BASE_URL=http://localhost:3001

# 应用标题
VITE_APP_TITLE=全栈管理系统
```

## 开发说明

### 后端开发
```bash
cd backend

# 安装依赖
npm install

# 开发模式（热重载）
npm run dev

# 生产模式
npm start
```

### 前端开发
```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 生产部署

### Docker 部署
```bash
# 构建并启动
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 扩容（可选）
docker-compose up -d --scale backend=3
```

### 手动部署

#### 后端部署
```bash
cd backend

# 安装生产依赖
npm install --production

# 设置环境变量
export NODE_ENV=production
export MONGODB_URI=mongodb://your-mongo-host:27017/fullstack-app
export JWT_SECRET=your-production-secret

# 启动（建议使用 PM2）
pm2 start src/app.js --name "fullstack-backend"
```

#### 前端部署
```bash
cd frontend

# 构建
npm run build

# 将 dist 目录部署到静态文件服务器（Nginx 等）
```

## 注意事项

1. **安全配置**：生产环境请务必修改 `JWT_SECRET`，使用强密码
2. **数据库连接**：确保 MongoDB 服务可用，连接字符串正确
3. **跨域配置**：生产环境请配置正确的 `CORS_ORIGIN`
4. **第一个用户**：系统中第一个注册的用户会自动成为管理员
5. **Token 过期**：Token 默认 7 天过期，过期后需重新登录

## 技术要点

### 前端
- 使用 `@` 作为 src 目录的别名
- Zustand 状态持久化到 localStorage
- Axios 拦截器统一处理请求和响应
- React Router 6 路由守卫控制页面访问权限
- 路由懒加载优化首屏加载速度

### 后端
- 分层架构：路由 -> 控制器 -> 模型
- 统一响应格式：`{ success, code, message, data }`
- 全局异常捕获，统一错误处理
- JWT 无状态认证
- Mongoose 中间件自动加密密码
- express-validator 参数验证

## 扩展建议

1. **增加 Redis**：用于 Token 黑名单、缓存
2. **文件上传**：增加头像上传功能
3. **日志系统**：集成 Winston 或 Pino
4. **接口限流**：使用 express-rate-limit
5. **单元测试**：Jest + Supertest
6. **代码规范**：ESLint + Prettier
7. **Swagger**：自动生成 API 文档
8. **邮件服务**：注册验证、密码重置

## 问题排查

### MongoDB 连接失败
- 检查 MongoDB 服务是否启动
- 检查连接字符串是否正确
- 检查防火墙和网络配置

### 前端无法访问后端 API
- 检查后端服务是否启动
- 检查 CORS 配置
- 检查前端代理配置（Vite 开发环境）

### Token 无效或过期
- 检查 JWT_SECRET 是否一致
- 检查系统时间是否正确
- 重新登录获取新 Token

## License

MIT
