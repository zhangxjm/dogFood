# ============================================
# React Native + Express 全栈项目
# ============================================
#
# 项目结构：
# ├── client/           # React Native (Expo) 前端
# ├── server/           # Express 后端
# ├── docker/           # Docker 配置文件
# ├── docker-compose.yml # Docker 编排文件
# └── package.json      # 根目录配置
#
# 技术栈：
# - 前端: React Native (Expo) + React Navigation
# - 后端: Node.js + Express + MongoDB
# - 认证: JWT (JSON Web Token)
# - 容器: Docker + Docker Compose

# ============================================
# 快速开始
# ============================================

# 1. 安装所有依赖
npm run install:all

# 2. 启动 Docker 服务（MongoDB）
docker-compose up -d mongodb

# 3. 启动后端服务
npm run start:server-dev

# 4. 启动前端（新终端窗口）
npm run start:client

# ============================================
# 详细步骤
# ============================================

# 一、环境准备
# 确保已安装：
# - Node.js (v18+)
# - npm 或 yarn
# - Docker & Docker Compose
# - Expo CLI (npm install -g expo-cli)
# - Android Studio / Xcode (模拟器/真机调试)

# 二、安装依赖

# 安装后端依赖
cd server
npm install
cd ..

# 安装前端依赖
cd client
npm install
cd ..

# 或者一键安装所有依赖
npm run install:all

# 三、启动数据库 (Docker)

# 启动 MongoDB 容器
docker-compose up -d mongodb

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f mongodb

# 停止容器
docker-compose down

# 四、启动后端服务

# 开发模式（带热重载）
cd server
npm run dev

# 生产模式
cd server
npm start

# 后端 API 地址: http://localhost:5000
# 健康检查: http://localhost:5000/api/health

# 五、启动前端

cd client
npm start

# 这会打开 Expo Developer Tools
# 可以选择：
# - 在 iOS 模拟器运行
# - 在 Android 模拟器运行
# - 在浏览器运行
# - 扫码在真机运行

# ============================================
# API 接口文档
# ============================================

# 基础URL: http://localhost:5000/api

# 用户相关接口:
# POST   /users/register      # 用户注册
# POST   /users/login         # 用户登录
# GET    /users/profile       # 获取当前用户信息（需登录）
# PUT    /users/profile       # 更新用户信息（需登录）
# PUT    /users/password      # 修改密码（需登录）

# 数据相关接口:
# GET    /items               # 获取列表（支持分页、搜索、分类）
# GET    /items/categories    # 获取分类列表
# GET    /items/my            # 获取我的列表（需登录）
# GET    /items/:id           # 获取单个详情
# POST   /items               # 创建新项目（需登录）
# PUT    /items/:id           # 更新项目（需登录）
# DELETE /items/:id           # 删除项目（需登录）

# 认证方式:
# 在请求头中添加:
# Authorization: Bearer <token>

# ============================================
# 真机调试配置
# ============================================

# 1. 确保手机和电脑连接同一个 WiFi
# 2. 查看电脑 IP 地址:
#    macOS: ifconfig | grep "inet " | grep -v 127.0.0.1
#    Windows: ipconfig
# 3. 修改 client/src/config/api.js 中的 API_BASE_URL
#    例如: 'http://192.168.1.100:5000/api'
# 4. 确保后端服务允许跨域（已配置）

# Android 模拟器特殊配置:
# - 使用 10.0.2.2 访问宿主机 localhost
# - 例如: http://10.0.2.2:5000/api

# ============================================
# Docker 完整部署
# ============================================

# 启动所有服务（MongoDB + 后端）
docker-compose up -d

# 只启动 MongoDB
docker-compose up -d mongodb

# 启动 MySQL（可选）
docker-compose --profile mysql up -d

# 启动 PostgreSQL（可选）
docker-compose --profile postgres up -d

# 停止所有服务
docker-compose down

# 停止并删除数据卷（慎用！会删除所有数据）
docker-compose down -v

# ============================================
# 数据库信息
# ============================================

# MongoDB (Docker):
#   主机: localhost
#   端口: 27017
#   用户名: root
#   密码: ld123456
#   数据库: rnapp

# MySQL (Docker, 可选):
#   主机: localhost
#   端口: 3306
#   用户名: root
#   密码: ld123456
#   数据库: rnapp

# PostgreSQL (Docker, 可选):
#   主机: localhost
#   端口: 5432
#   用户名: admin
#   密码: admin123
#   数据库: rnapp

# ============================================
# 常见问题
# ============================================

# Q: 前端无法连接后端？
# A: 1. 确保后端服务已启动
#    2. 检查防火墙设置
#    3. 真机调试时使用正确的 IP 地址
#    4. 检查后端 CORS 配置

# Q: Docker 容器无法启动？
# A: 1. 检查端口是否被占用
#    2. 确保 Docker 已启动
#    3. 查看日志: docker-compose logs

# Q: Expo 无法连接？
# A: 1. 确保手机和电脑在同一 WiFi
#    2. 检查防火墙设置
#    3. 尝试使用隧道模式: npx expo start --tunnel

# ============================================
# 测试账号（需要先注册）
# ============================================

# 首次使用需要注册账号:
# POST /api/users/register
# {
#   "username": "testuser",
#   "email": "test@example.com",
#   "password": "123456"
# }

# ============================================
# 项目命令速查
# ============================================

# npm scripts:
# npm run install:all      # 安装所有依赖
# npm run install:server   # 安装后端依赖
# npm run install:client   # 安装前端依赖
# npm run start:server     # 启动后端
# npm run start:server-dev # 启动后端（开发模式）
# npm run start:client     # 启动前端
# npm run docker:up        # Docker 启动所有服务
# npm run docker:down      # Docker 停止所有服务
# npm run docker:logs      # 查看 Docker 日志
# npm run clean            # 清除 node_modules

# ============================================
# 技术支持
# ============================================
# 如有问题，请检查:
# 1. Node.js 版本 (v18+)
# 2. npm 版本
# 3. Docker 是否正常运行
# 4. 端口是否被占用
