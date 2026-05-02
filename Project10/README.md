# 在线课程学习平台

一个基于 Vue3 + FastAPI 的全栈在线课程学习平台，支持学生/教师双角色，提供课程管理、视频学习、进度跟踪等功能。

## 技术栈

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Pinia** - Vue 3 状态管理
- **Axios** - HTTP 客户端
- **Vue Router** - 官方路由管理器

### 后端
- **FastAPI** - 现代、快速的 Web 框架
- **SQLAlchemy** - Python SQL 工具包和 ORM
- **SQLite** - 轻量级数据库（可替换为 MySQL/PostgreSQL）
- **JWT** - JSON Web Token 认证
- **Pydantic** - 数据验证
- **Uvicorn** - ASGI 服务器

## 项目结构

```
Project10/
├── backend/                    # 后端项目
│   ├── app/
│   │   ├── models/            # 数据库模型
│   │   │   ├── __init__.py
│   │   │   └── models.py
│   │   ├── routers/           # API 路由
│   │   │   ├── __init__.py
│   │   │   ├── auth.py        # 认证相关
│   │   │   ├── courses.py     # 课程相关
│   │   │   ├── teacher.py     # 教师后台
│   │   │   └── users.py       # 用户相关
│   │   ├── schemas/           # Pydantic 模型
│   │   │   ├── __init__.py
│   │   │   └── schemas.py
│   │   ├── utils/             # 工具函数
│   │   │   ├── __init__.py
│   │   │   └── auth.py        # 认证工具
│   │   ├── __init__.py
│   │   ├── config.py          # 配置文件
│   │   ├── database.py        # 数据库连接
│   │   └── main.py            # 应用入口
│   ├── .env                   # 环境变量
│   └── requirements.txt       # Python 依赖
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   │   ├── auth.js
│   │   │   ├── courses.js
│   │   │   └── teacher.js
│   │   ├── router/            # 路由配置
│   │   │   └── index.js
│   │   ├── stores/            # 状态管理
│   │   │   ├── index.js
│   │   │   └── user.js
│   │   ├── styles/            # 全局样式
│   │   │   └── index.css
│   │   ├── utils/             # 工具函数
│   │   │   └── request.js
│   │   ├── views/             # 视图组件
│   │   │   ├── Login.vue              # 登录
│   │   │   ├── Register.vue           # 注册
│   │   │   ├── Courses.vue            # 课程列表
│   │   │   ├── CourseDetail.vue       # 课程详情
│   │   │   ├── CourseLearn.vue        # 视频学习
│   │   │   ├── UserCenter.vue         # 个人中心布局
│   │   │   ├── user/
│   │   │   │   ├── MyEnrollments.vue  # 我的课程
│   │   │   │   ├── MyFavorites.vue    # 我的收藏
│   │   │   │   └── Profile.vue        # 个人信息
│   │   │   ├── TeacherCenter.vue      # 教师后台布局
│   │   │   └── teacher/
│   │   │       ├── CourseList.vue     # 课程管理
│   │   │       ├── CourseForm.vue     # 创建/编辑课程
│   │   │       ├── ChapterManage.vue  # 章节管理
│   │   │       └── StudentManage.vue  # 学员管理
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 核心功能

### 用户系统
- **注册/登录** - 支持学生/教师双角色注册
- **JWT 认证** - 无状态 Token 认证机制
- **个人中心** - 查看和修改个人信息
- **权限控制** - 基于角色的访问控制

### 课程系统
- **课程列表** - 浏览、搜索、筛选课程
- **课程详情** - 查看课程信息、章节列表、评论
- **视频播放** - 支持视频学习、进度跟踪
- **课程报名** - 学生报名课程
- **课程收藏** - 收藏喜欢的课程

### 教师后台
- **课程管理** - 创建、编辑、删除、发布课程
- **章节管理** - 添加、编辑、删除章节，支持拖拽排序
- **学员管理** - 查看课程学员及学习进度

### 学习功能
- **进度跟踪** - 自动记录学习进度
- **章节完成** - 标记章节学习完成
- **评论系统** - 对课程进行评论和评分

## 快速开始

### 环境要求
- Python 3.8+
- Node.js 16+
- npm 或 yarn

### 后端启动

1. 进入后端目录
```bash
cd backend
```

2. 创建虚拟环境（可选但推荐）
```bash
python -m venv venv
source venv/bin/activate  # macOS/Linux
# 或
venv\Scripts\activate     # Windows
```

3. 安装依赖
```bash
pip install -r requirements.txt
```

4. 配置环境变量（可选）
```bash
# 编辑 .env 文件
DATABASE_URL=sqlite:///./learning_platform.db
SECRET_KEY=your-super-secret-key-change-in-production
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440
CORS_ORIGINS=["http://localhost:3000"]
```

5. 启动后端服务
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

后端服务将在 http://localhost:8000 启动

API 文档地址：
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

### 前端启动

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
# 或
yarn install
```

3. 启动开发服务器
```bash
npm run dev
# 或
yarn dev
```

前端服务将在 http://localhost:3000 启动

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 课程接口
- `GET /api/courses` - 获取课程列表（分页、筛选）
- `GET /api/courses/{id}` - 获取课程详情
- `POST /api/courses/enroll/{id}` - 报名课程
- `POST /api/courses/favorite/{id}` - 收藏/取消收藏
- `GET /api/courses/chapters/{id}` - 获取章节详情
- `POST /api/courses/progress` - 更新学习进度
- `GET /api/courses/{id}/comments` - 获取课程评论
- `POST /api/courses/comments` - 发表评论

### 用户接口
- `PUT /api/users/profile` - 更新个人信息
- `GET /api/users/enrollments` - 获取我的课程
- `GET /api/users/favorites` - 获取我的收藏

### 教师接口
- `GET /api/teacher/courses` - 获取教师课程列表
- `POST /api/teacher/courses` - 创建课程
- `GET /api/teacher/courses/{id}` - 获取课程详情
- `PUT /api/teacher/courses/{id}` - 更新课程
- `DELETE /api/teacher/courses/{id}` - 删除课程
- `POST /api/teacher/courses/{id}/publish` - 发布/下架课程
- `GET /api/teacher/courses/{id}/chapters` - 获取章节列表
- `POST /api/teacher/courses/{id}/chapters` - 创建章节
- `PUT /api/teacher/chapters/{id}` - 更新章节
- `DELETE /api/teacher/chapters/{id}` - 删除章节
- `GET /api/teacher/courses/{id}/students` - 获取学员列表

## 数据库模型

### User (用户)
- `id` - 用户ID
- `username` - 用户名（唯一）
- `email` - 邮箱（唯一）
- `hashed_password` - 加密密码
- `role` - 角色（student/teacher）
- `avatar` - 头像
- `bio` - 个人简介
- `is_active` - 是否激活
- `created_at` - 创建时间
- `updated_at` - 更新时间

### Course (课程)
- `id` - 课程ID
- `title` - 课程标题
- `description` - 课程描述
- `cover_image` - 封面图片
- `price` - 价格
- `category` - 分类
- `teacher_id` - 教师ID
- `is_published` - 是否发布
- `view_count` - 浏览次数
- `created_at` - 创建时间
- `updated_at` - 更新时间

### Chapter (章节)
- `id` - 章节ID
- `course_id` - 课程ID
- `title` - 章节标题
- `description` - 章节描述
- `video_url` - 视频地址
- `position` - 排序位置
- `is_free` - 是否免费试看
- `duration` - 视频时长（秒）
- `created_at` - 创建时间
- `updated_at` - 更新时间

### Enrollment (报名记录)
- `id` - 记录ID
- `student_id` - 学生ID
- `course_id` - 课程ID
- `enrolled_at` - 报名时间
- `progress_percent` - 学习进度百分比
- `completed_at` - 完成时间

### Favorite (收藏)
- `id` - 收藏ID
- `user_id` - 用户ID
- `course_id` - 课程ID
- `created_at` - 创建时间

### Comment (评论)
- `id` - 评论ID
- `content` - 评论内容
- `rating` - 评分
- `user_id` - 用户ID
- `course_id` - 课程ID
- `parent_id` - 父评论ID（支持回复）
- `is_approved` - 是否审核通过
- `created_at` - 创建时间
- `updated_at` - 更新时间

### Progress (学习进度)
- `id` - 进度ID
- `user_id` - 用户ID
- `chapter_id` - 章节ID
- `course_id` - 课程ID
- `is_completed` - 是否完成
- `watch_duration` - 观看时长
- `created_at` - 创建时间
- `updated_at` - 更新时间

## 统一响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 返回数据
  }
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [],
    "total": 100,
    "page": 1,
    "page_size": 10,
    "total_pages": 10
  }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

## 部署说明

### 生产环境部署

#### 后端
```bash
# 安装 gunicorn
pip install gunicorn

# 启动服务
gunicorn app.main:app -w 4 -k uvicorn.workers.UvicornWorker -b 0.0.0.0:8000
```

#### 前端
```bash
# 构建生产版本
npm run build

# 构建产物在 dist 目录，可部署到 Nginx 等静态服务器
```

### Docker 部署（可选）

用户提到可以使用 Docker 安装中间件，以下是常用数据库的 Docker 命令：

#### MySQL
```bash
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=ld123456 \
  -e MYSQL_DATABASE=learning_platform \
  -p 3306:3306 \
  mysql:8.0
```

修改后端 `.env` 配置：
```
DATABASE_URL=mysql+pymysql://root:ld123456@localhost:3306/learning_platform
```

需要安装额外依赖：
```bash
pip install pymysql cryptography
```

#### PostgreSQL
```bash
docker run -d \
  --name postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -e POSTGRES_DB=learning_platform \
  -p 5432:5432 \
  postgres:15
```

修改后端 `.env` 配置：
```
DATABASE_URL=postgresql+psycopg2://admin:admin123@localhost:5432/learning_platform
```

需要安装额外依赖：
```bash
pip install psycopg2-binary
```

## 开发说明

### 后端开发
- 数据库表会在首次启动时自动创建
- 使用 Alembic 进行数据库迁移（如需）
- API 文档会自动生成，访问 `/docs` 查看

### 前端开发
- 使用 Vue 3 Composition API
- 状态管理使用 Pinia
- 组件库使用 Element Plus
- API 请求使用 Axios，已封装在 `src/utils/request.js`

### 代码规范
- 后端：遵循 PEP 8 规范
- 前端：使用 ESLint + Prettier（可选配置）
- 所有接口遵循 RESTful 规范
- 统一的响应格式和错误处理

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
