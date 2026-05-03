# 文创作品平台

一个完整的全栈文创/艺术作品展示和交易平台，支持搜索引擎收录和响应式设计。

## 技术栈

### 前端
- **Nuxt 3** - SSR 服务端渲染框架，优化 SEO
- **Vue 3** - 渐进式 JavaScript 框架
- **TailwindCSS** - 实用优先的 CSS 框架
- **Pinia** - Vue 状态管理

### 后端
- **FastAPI** - 高性能 Python Web 框架
- **MongoDB** - 非关系型数据库
- **Motor** - MongoDB 异步驱动
- **Pydantic** - 数据验证

## 项目结构

```
Project16/
├── backend/                    # 后端代码
│   ├── app/
│   │   ├── __init__.py
│   │   ├── main.py            # FastAPI 应用入口
│   │   ├── config.py          # 配置文件
│   │   ├── database.py        # 数据库连接
│   │   ├── auth.py            # 认证模块
│   │   ├── models.py          # Pydantic 模型
│   │   └── routers/           # API 路由
│   │       ├── __init__.py
│   │       ├── auth.py        # 认证路由
│   │       ├── categories.py  # 分类路由
│   │       ├── works.py       # 作品路由
│   │       ├── interactions.py # 交互路由 (点赞/收藏/评论)
│   │       ├── orders.py      # 订单路由
│   │       └── upload.py      # 文件上传路由
│   ├── requirements.txt       # Python 依赖
│   └── .env.example           # 环境变量示例
│
├── frontend/                   # 前端代码
│   ├── pages/                 # 页面组件
│   │   ├── index.vue          # 首页
│   │   ├── login.vue          # 登录页
│   │   ├── register.vue       # 注册页
│   │   ├── works/
│   │   │   ├── index.vue      # 作品列表页
│   │   │   ├── [id].vue       # 作品详情页
│   │   │   └── upload.vue     # 作品上传页
│   │   └── users/
│   │       └── [id].vue       # 用户主页
│   ├── components/            # 通用组件
│   │   ├── AppHeader.vue
│   │   ├── AppFooter.vue
│   │   └── WorkCard.vue
│   ├── stores/                # Pinia 状态管理
│   │   └── auth.ts
│   ├── types/                 # TypeScript 类型
│   │   └── index.ts
│   ├── assets/css/
│   │   └── main.css           # Tailwind 全局样式
│   ├── nuxt.config.ts         # Nuxt 配置
│   ├── tailwind.config.js     # Tailwind 配置
│   ├── tsconfig.json
│   └── package.json
│
├── docker-compose.yml         # Docker 服务配置
└── README.md
```

## 核心功能

### 1. 用户系统
- 用户注册/登录
- JWT 令牌认证
- 设计师身份认证
- 个人资料管理

### 2. 作品系统
- 作品上传/编辑/删除
- 作品分类管理
- 作品搜索和筛选
- 作品详情展示
- 标签系统

### 3. 交互功能
- 作品点赞/取消点赞
- 作品收藏/取消收藏
- 作品评论
- 浏览次数统计

### 4. 交易系统
- 订单创建
- 订单管理
- 支付状态跟踪

### 5. 文件系统
- 图片上传 (支持多图)
- 图片预览
- 文件大小限制

## 快速开始

### 1. 环境要求
- Node.js >= 18
- Python >= 3.10
- Docker (用于 MongoDB)

### 2. 启动数据库

```bash
# 启动 MongoDB 和 Mongo Express
docker-compose up -d
```

MongoDB 将运行在 `mongodb://localhost:27017`
Mongo Express (Web 管理界面) 将运行在 `http://localhost:8081`

### 3. 启动后端

```bash
cd backend

# 创建虚拟环境 (推荐)
python3 -m venv venv
source venv/bin/activate  # Mac/Linux
# 或
# venv\Scripts\activate  # Windows

# 安装依赖
pip install -r requirements.txt

# 复制环境变量文件
cp .env.example .env

# 启动开发服务器
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

后端 API 将运行在 `http://localhost:8000`
API 文档: `http://localhost:8000/docs`
API Redoc: `http://localhost:8000/redoc`

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将运行在 `http://localhost:3000`

## API 接口

### 认证接口
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录 (表单格式)
- `POST /api/auth/login-json` - 登录 (JSON 格式)
- `GET /api/auth/me` - 获取当前用户
- `PUT /api/auth/me` - 更新当前用户

### 作品接口
- `GET /api/works` - 获取作品列表
- `GET /api/works/featured` - 获取精选作品
- `GET /api/works/{id}` - 获取作品详情
- `POST /api/works` - 创建作品 (需设计师权限)
- `PUT /api/works/{id}` - 更新作品
- `DELETE /api/works/{id}` - 删除作品

### 分类接口
- `GET /api/categories` - 获取分类列表
- `GET /api/categories/{name}` - 获取分类详情

### 交互接口
- `GET /api/works/{id}/comments` - 获取作品评论
- `POST /api/comments` - 发表评论
- `POST /api/works/{id}/like` - 点赞/取消点赞
- `GET /api/works/{id}/liked` - 检查是否已点赞
- `POST /api/works/{id}/favorite` - 收藏/取消收藏
- `GET /api/works/{id}/favorited` - 检查是否已收藏

### 订单接口
- `GET /api/orders` - 获取我的订单
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `POST /api/orders/{id}/pay` - 支付订单
- `POST /api/orders/{id}/cancel` - 取消订单

### 上传接口
- `POST /api/upload/image` - 上传单张图片
- `POST /api/upload/images` - 上传多张图片

## 部署指南

### 构建前端

```bash
cd frontend

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

### 构建后端

```bash
cd backend

# 使用 Gunicorn 运行
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker app.main:app --bind 0.0.0.0:8000
```

### Docker 部署

项目已包含 MongoDB 的 Docker 配置。如需部署整个应用，可创建如下的 Dockerfile:

**backend/Dockerfile:**
```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

**frontend/Dockerfile:**
```dockerfile
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

EXPOSE 3000

CMD ["npm", "run", "start"]
```

## 环境变量配置

### 后端环境变量 (.env)

```bash
# MongoDB 连接
MONGODB_URL=mongodb://localhost:27017
DATABASE_NAME=creative_platform

# JWT 认证
SECRET_KEY=your-secret-key-change-in-production
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440

# 文件上传
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=5242880

# CORS 跨域
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

### 前端环境变量

```bash
# API 地址
NUXT_PUBLIC_API_BASE=http://localhost:8000
```

## 默认分类

系统会在首次启动时自动创建以下分类:
- 插画 🎨
- 平面设计 🖼️
- 3D建模 🧊
- 摄影 📷
- 动效 🎬
- 字体 🔤
- 包装 📦
- 图标 ✨

## SEO 优化

项目已配置以下 SEO 优化:
1. **Nuxt 3 SSR** - 服务端渲染，便于搜索引擎抓取
2. **Meta 标签** - 每个页面都有独立的 title、description、keywords
3. **Open Graph** - 社交分享优化
4. **语义化 HTML** - 良好的 HTML 结构

## 响应式设计

前端使用 TailwindCSS 实现响应式布局，完美适配:
- 手机端 (< 640px)
- 平板端 (640px - 1024px)
- 桌面端 (> 1024px)

## 许可证

MIT License
