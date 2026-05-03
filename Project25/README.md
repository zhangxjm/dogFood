# 在线影院售票系统

一个基于 Flask + Vue3 的全栈影院售票系统，支持电影浏览、选座购票、订单管理等完整功能。

## 技术栈

### 后端
- **Flask** - Python Web 框架
- **Flask-SQLAlchemy** - ORM 数据库操作
- **Flask-JWT-Extended** - JWT 身份认证
- **Flask-CORS** - 跨域处理
- **MySQL** - 关系型数据库

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端

## 功能特性

### 用户功能
- 🎬 **电影浏览** - 查看正在热映、即将上映的电影列表
- 🔍 **电影搜索** - 按分类、状态、关键词搜索电影
- 📋 **电影详情** - 查看电影简介、导演、演员、评分等信息
- 🎟️ **选座购票** - 可视化座位选择，实时座位状态
- 📝 **订单管理** - 查看订单历史、取消订单

### 管理员功能
- 🎬 **电影管理** - 添加、编辑、删除电影信息
- 📅 **场次管理** - 安排电影放映场次
- 🎪 **影院管理** - 管理影院信息
- 🏠 **放映厅管理** - 管理放映厅及座位布局
- 📋 **订单管理** - 查看所有订单、取消订单

## 项目结构

```
Project25/
├── backend/                    # 后端 Flask 项目
│   ├── app/
│   │   ├── routes/            # API 路由
│   │   │   ├── auth.py        # 用户认证
│   │   │   ├── movies.py      # 电影相关
│   │   │   ├── cinemas.py     # 影院相关
│   │   │   ├── schedules.py   # 场次相关
│   │   │   ├── orders.py      # 订单相关
│   │   │   └── admin.py       # 管理后台
│   │   ├── __init__.py        # 应用初始化
│   │   ├── config.py          # 配置文件
│   │   ├── models.py          # 数据库模型
│   │   └── init_data.py       # 初始化数据
│   ├── .env                    # 环境变量
│   ├── requirements.txt        # Python 依赖
│   └── run.py                  # 启动文件
├── frontend/                   # 前端 Vue 项目
│   ├── src/
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # 状态管理
│   │   ├── utils/              # 工具函数
│   │   ├── views/              # 页面组件
│   │   │   ├── admin/          # 管理后台页面
│   │   │   ├── Home.vue        # 首页
│   │   │   ├── MovieList.vue   # 电影列表
│   │   │   ├── MovieDetail.vue # 电影详情
│   │   │   ├── SeatSelection.vue # 选座页面
│   │   │   ├── OrderList.vue   # 订单列表
│   │   │   ├── Login.vue       # 登录
│   │   │   └── Register.vue    # 注册
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml           # Docker 配置
├── start.sh                     # 一键启动脚本
└── stop.sh                      # 停止脚本
```

## 快速开始

### 环境要求

- **Docker** & **Docker Compose**
- **Node.js** >= 18.0
- **Python** >= 3.9
- **pip**
- **npm** 或 **yarn**

### 一键启动

```bash
# 1. 进入项目目录
cd Project25

# 2. 给脚本添加执行权限
chmod +x start.sh
chmod +x stop.sh

# 3. 一键启动所有服务
./start.sh
```

脚本会自动完成以下操作：
1. 启动 MySQL 数据库容器
2. 安装后端 Python 依赖
3. 安装前端 Node.js 依赖
4. 启动后端服务 (端口 5000)
5. 启动前端服务 (端口 3000)
6. 初始化数据库和示例数据

### 手动启动

#### 步骤 1: 启动数据库

```bash
# 启动 MySQL 容器
docker-compose up -d mysql

# 等待数据库初始化 (约 30 秒)
sleep 30
```

#### 步骤 2: 启动后端

```bash
cd backend

# 创建虚拟环境
python3 -m venv venv

# 激活虚拟环境
source venv/bin/activate  # Linux/Mac
# 或
venv\Scripts\activate     # Windows

# 安装依赖
pip install -r requirements.txt

# 启动服务
python run.py
```

后端服务将在 http://localhost:5000 启动

#### 步骤 3: 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:3000 |
| 后端 API | http://localhost:5000 |

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 测试步骤

### 1. 环境验证

```bash
# 检查后端 API
curl http://localhost:5000/api/health

# 期望输出: {"status": "healthy"}
```

### 2. 用户注册/登录

1. 打开 http://localhost:3000
2. 点击右上角 "注册"
3. 填写用户名、密码、邮箱进行注册
4. 或使用默认账号直接登录

### 3. 浏览电影

1. 首页展示正在热映和即将上映的电影
2. 点击电影卡片进入详情页
3. 在电影详情页查看排片信息

### 4. 选座购票

1. 在电影详情页选择一个场次
2. 点击 "选座购票"
3. 在座位图中选择可用座位 (蓝色可选，灰色已售，绿色已选)
4. 点击 "确认购票" 完成订单

### 5. 查看订单

1. 登录后点击导航栏 "我的订单"
2. 查看订单列表
3. 可取消未使用的订单

### 6. 管理后台 (管理员)

1. 使用 admin 账号登录
2. 点击导航栏 "管理后台"
3. 可以进行以下操作：
   - 电影管理：添加、编辑、删除电影
   - 场次管理：安排放映场次
   - 订单管理：查看所有订单
   - 影院管理：管理影院信息
   - 放映厅管理：管理放映厅

## API 接口

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| GET | /api/auth/profile | 获取用户信息 (需登录) |
| PUT | /api/auth/profile | 更新用户信息 (需登录) |

### 电影接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/movies | 获取电影列表 |
| GET | /api/movies/:id | 获取电影详情 |
| GET | /api/movies/categories | 获取电影分类 |
| GET | /api/movies/hot | 获取热门电影 |
| GET | /api/movies/showing | 获取正在热映 |
| GET | /api/movies/coming | 获取即将上映 |

### 影院接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/cinemas | 获取影院列表 |
| GET | /api/cinemas/:id | 获取影院详情 |
| GET | /api/cinemas/:id/halls | 获取影院放映厅 |

### 场次接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/schedules | 获取场次列表 |
| GET | /api/schedules/:id | 获取场次详情 |
| GET | /api/schedules/:id/seats | 获取场次座位图 |
| GET | /api/schedules/movie/:id | 获取电影场次 |

### 订单接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/orders | 获取订单列表 (需登录) |
| GET | /api/orders/:id | 获取订单详情 (需登录) |
| POST | /api/orders/create | 创建订单 (需登录) |
| POST | /api/orders/:id/cancel | 取消订单 (需登录) |

### 管理后台接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET/POST/PUT/DELETE | /api/admin/movies | 电影管理 (需管理员) |
| GET/POST/PUT/DELETE | /api/admin/schedules | 场次管理 (需管理员) |
| GET | /api/admin/orders | 订单管理 (需管理员) |
| GET/POST | /api/admin/cinemas | 影院管理 (需管理员) |
| GET/POST | /api/admin/halls | 放映厅管理 (需管理员) |
| GET/POST | /api/admin/categories | 分类管理 (需管理员) |

## 数据库模型

### 用户 (User)
- id, username, password_hash, email, is_admin, created_at

### 电影分类 (MovieCategory)
- id, name, description

### 电影 (Movie)
- id, title, poster, description, duration, release_date, rating
- director, actors, category_id, status, is_hot, created_at

### 影院 (Cinema)
- id, name, address, phone, description, created_at

### 放映厅 (Hall)
- id, name, cinema_id, rows, cols, created_at

### 座位 (Seat)
- id, hall_id, row_num, col_num, is_available, created_at

### 场次 (Schedule)
- id, movie_id, hall_id, start_time, end_time, price, created_at

### 订单 (Order)
- id, order_no, user_id, schedule_id, total_price, status, seats, created_at

## 停止服务

```bash
# 使用停止脚本
./stop.sh

# 或手动停止
# 停止后端: kill <后端PID>
# 停止前端: kill <前端PID>
# 停止数据库: docker-compose down
```

## 常见问题

### 1. 数据库连接失败

- 确保 MySQL 容器已启动: `docker ps`
- 检查容器日志: `docker logs cinema_mysql`
- 确认数据库初始化完成 (首次启动需要约 30 秒)

### 2. 前端无法访问后端

- 确认后端服务已启动 (端口 5000)
- 检查 vite.config.js 中的代理配置
- 确认 CORS 配置正确

### 3. 登录失败

- 确认使用正确的账号密码
- 检查数据库中用户表是否有数据
- 首次运行会自动创建默认账号

## 开发说明

### 后端开发

```bash
cd backend
source venv/bin/activate
export FLASK_ENV=development
python run.py
```

### 前端开发

```bash
cd frontend
npm run dev
```

### 数据库初始化

首次运行 `python run.py` 时会自动：
1. 创建所有数据表
2. 插入默认用户 (admin/admin123, user/user123)
3. 插入示例电影分类
4. 插入示例影院和放映厅
5. 插入示例电影和场次

## License

MIT License
