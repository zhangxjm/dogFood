# 图书馆图书借阅管理系统

## 项目简介
这是一个基于Django4 + Django REST Framework + MySQL + Vue3 + Vite + Element Plus的全栈图书馆图书借阅管理系统。

## 技术栈
- **后端**: Django 4.x, Django REST Framework, MySQL
- **前端**: Vue 3, Vite, Element Plus
- **认证**: JWT (JSON Web Token)
- **容器化**: Docker

## 项目结构
```
Project20/
├── backend/                 # Django后端项目
│   ├── library/            # Django项目配置
│   ├── apps/               # Django应用
│   │   ├── books/          # 图书管理应用
│   │   ├── users/          # 用户管理应用
│   │   ├── borrows/        # 借阅管理应用
│   │   └── common/         # 公共工具模块
│   ├── requirements.txt    # Python依赖
│   └── manage.py           # Django管理脚本
├── frontend/                # Vue3前端项目
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   ├── api/            # API接口
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   └── utils/          # 工具函数
│   ├── package.json        # Node.js依赖
│   └── vite.config.js      # Vite配置
├── docker/                  # Docker配置
│   ├── mysql/              # MySQL配置
│   └── nginx/              # Nginx配置
├── docker-compose.yml      # Docker Compose配置
└── README.md               # 项目说明
```

## 快速开始

### 前置要求
- Docker 和 Docker Compose
- Node.js 16+
- Python 3.10+

### 使用Docker启动（推荐）
```bash
# 克隆项目
cd Project20

# 启动所有服务
docker-compose up -d

# 初始化数据库
docker-compose exec backend python manage.py migrate
docker-compose exec backend python manage.py createsuperuser

# 访问应用
# 前端: http://localhost:3000
# 后端API: http://localhost:8000/api/
# Django管理后台: http://localhost:8000/admin/
```

### 手动启动

#### 后端启动
```bash
# 进入后端目录
cd backend

# 创建虚拟环境
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 安装依赖
pip install -r requirements.txt

# 配置数据库（确保MySQL已启动）
# 修改 library/settings.py 中的数据库配置

# 运行迁移
python manage.py migrate

# 创建超级用户
python manage.py createsuperuser

# 启动开发服务器
python manage.py runserver 0.0.0.0:8000
```

#### 前端启动
```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问前端
# http://localhost:3000
```

## 核心功能

### 1. 图书管理
- 图书分类管理
- 图书入库
- 图书信息维护（增删改查）
- 条件筛选、分页查询

### 2. 读者管理
- 读者注册
- 读者信息管理
- 权限控制（普通用户/管理员）

### 3. 借阅管理
- 借书
- 还书
- 续借
- 逾期罚款计算
- 借阅记录查询
- 个人借阅中心

### 4. 管理员后台
- 图书管理
- 读者管理
- 借阅统计
- 数据列表展示

## API接口

### 认证接口
- `POST /api/token/` - 获取JWT Token
- `POST /api/token/refresh/` - 刷新Token
- `POST /api/users/register/` - 用户注册
- `GET /api/users/me/` - 获取当前用户信息

### 图书接口
- `GET /api/books/` - 获取图书列表（支持筛选、分页）
- `GET /api/books/{id}/` - 获取图书详情
- `POST /api/books/` - 新增图书
- `PUT /api/books/{id}/` - 更新图书
- `DELETE /api/books/{id}/` - 删除图书
- `GET /api/categories/` - 获取图书分类

### 借阅接口
- `GET /api/borrows/` - 获取借阅记录
- `POST /api/borrows/borrow/` - 借书
- `POST /api/borrows/{id}/return/` - 还书
- `POST /api/borrows/{id}/renew/` - 续借
- `GET /api/borrows/my/` - 获取我的借阅记录

## 数据库模型

### 核心模型
- **User**: 用户模型（扩展Django默认User）
- **Category**: 图书分类
- **Book**: 图书信息
- **BorrowRecord**: 借阅记录
- **Fine**: 罚款记录

## 技术特点

1. **前后端分离**: 单体简易架构，无复杂异步和中间件
2. **Django ORM**: 自动建表，无需手写复杂SQL
3. **JWT认证**: 基础JWT登录认证，简单权限控制
4. **统一接口格式**: 封装通用请求工具、统一接口返回格式
5. **代码简洁**: 适合入门学习
6. **生产级规范**: 配置好环境变量、跨域、依赖清单

## 环境变量

### 后端环境变量
- `DB_HOST`: 数据库主机（默认: localhost）
- `DB_PORT`: 数据库端口（默认: 3306）
- `DB_NAME`: 数据库名（默认: library）
- `DB_USER`: 数据库用户名（默认: root）
- `DB_PASSWORD`: 数据库密码（默认: ld123456）
- `JWT_SECRET_KEY`: JWT密钥
- `DEBUG`: 调试模式（默认: False）

## 开发指南

### 后端开发
1. 在 `apps/` 目录下创建新应用
2. 在 `library/settings.py` 中注册应用
3. 创建模型、序列化器、视图、路由
4. 运行迁移: `python manage.py makemigrations && python manage.py migrate`

### 前端开发
1. 在 `src/views/` 目录下创建新页面组件
2. 在 `src/router/index.js` 中配置路由
3. 在 `src/api/` 目录下添加API接口
4. 使用 Element Plus 组件进行开发

## 许可证
MIT License
