# 校园社团管理系统

## 项目简介

这是一个基于 **Spring Boot 3 + Vue 3** 技术栈开发的全栈校园社团管理系统，采用前后端分离架构。

## 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.x | 后端核心框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 关系型数据库 |
| Spring Security | 6.x | 安全框架(密码加密) |
| JWT (jjwt) | 0.12.3 | 身份认证 |
| Hutool | 5.8.24 | 工具类库 |
| Lombok | - | 代码简化 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.x | 前端框架 |
| Element Plus | 2.5.x | UI组件库 |
| Vue Router | 4.2.x | 路由管理 |
| Pinia | 2.1.x | 状态管理 |
| Axios | 1.6.x | HTTP请求库 |
| Vite | 5.0.x | 构建工具 |

## 项目结构

```
Project19/
├── backend/                         # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/campus/
│   │   │   │       ├── CampusClubApplication.java    # 启动类
│   │   │   │       ├── common/                       # 通用类
│   │   │   │       │   ├── Result.java               # 统一返回结果
│   │   │   │       │   ├── ResultCode.java           # 响应码枚举
│   │   │   │       │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   │       │   └── CorsConfig.java           # 跨域配置
│   │   │   │       ├── config/                       # 配置类
│   │   │   │       │   ├── SecurityConfig.java       # Spring Security配置
│   │   │   │       │   └── MybatisPlusConfig.java    # MyBatis-Plus配置
│   │   │   │       ├── controller/                   # 控制层(RESTful API)
│   │   │   │       │   ├── UserController.java
│   │   │   │       │   ├── ClubController.java
│   │   │   │       │   ├── ActivityController.java
│   │   │   │       │   └── CategoryController.java
│   │   │   │       ├── entity/                       # 实体类
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── Club.java
│   │   │   │       │   ├── ClubCategory.java
│   │   │   │       │   ├── ClubMember.java
│   │   │   │       │   ├── Activity.java
│   │   │   │       │   └── ActivityRegistration.java
│   │   │   │       ├── mapper/                       # Mapper接口
│   │   │   │       │   ├── UserMapper.java
│   │   │   │       │   ├── ClubMapper.java
│   │   │   │       │   ├── ClubCategoryMapper.java
│   │   │   │       │   ├── ClubMemberMapper.java
│   │   │   │       │   ├── ActivityMapper.java
│   │   │   │       │   └── ActivityRegistrationMapper.java
│   │   │   │       ├── service/                      # 业务层
│   │   │   │       │   ├── UserService.java
│   │   │   │       │   ├── ClubService.java
│   │   │   │       │   ├── ActivityService.java
│   │   │   │       │   ├── CategoryService.java
│   │   │   │       │   └── impl/
│   │   │   │       └── util/                         # 工具类
│   │   │   │           └── JwtUtil.java              # JWT工具类
│   │   │   └── resources/
│   │   │       └── application.yml                    # 应用配置
│   │   └── test/
│   ├── pom.xml                                        # Maven依赖配置
│   └── init.sql                                       # 数据库初始化脚本
│
├── frontend/                                          # 前端项目
│   ├── src/
│   │   ├── api/                                       # API接口封装
│   │   │   ├── index.js                               # Axios封装
│   │   │   ├── user.js
│   │   │   ├── club.js
│   │   │   ├── activity.js
│   │   │   └── category.js
│   │   ├── router/                                    # 路由配置
│   │   │   └── index.js
│   │   ├── stores/                                    # 状态管理(Pinia)
│   │   │   └── user.js
│   │   ├── views/                                     # 页面组件
│   │   │   ├── login/                                 # 登录注册
│   │   │   │   ├── Login.vue
│   │   │   │   └── Register.vue
│   │   │   ├── home/                                  # 首页
│   │   │   │   └── Home.vue
│   │   │   ├── layout/                                # 布局组件
│   │   │   │   └── Layout.vue
│   │   │   ├── club/                                  # 社团相关
│   │   │   │   ├── ClubList.vue
│   │   │   │   ├── ClubDetail.vue
│   │   │   │   ├── MyClubs.vue
│   │   │   │   └── ClubMemberManage.vue
│   │   │   ├── activity/                              # 活动相关
│   │   │   │   ├── ActivityList.vue
│   │   │   │   ├── ActivityDetail.vue
│   │   │   │   ├── MyActivities.vue
│   │   │   │   └── CreateActivity.vue
│   │   │   └── admin/                                 # 管理员后台
│   │   │       ├── UserManage.vue
│   │   │       ├── ClubManage.vue
│   │   │       ├── ActivityManage.vue
│   │   │       └── CategoryManage.vue
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## 功能模块

### 1. 用户管理
- 用户登录/注册
- 角色权限：学生(1)、社长(2)、系统管理员(3)
- 个人信息管理
- 管理员用户管理

### 2. 社团管理
- 社团注册申请
- 社团列表展示（搜索、分页）
- 社团详情查看
- 社团分类管理
- 社团审核（管理员）
- 我的社团

### 3. 成员管理
- 学生申请加入社团
- 社长审批加入申请
- 成员退出社团
- 成员列表管理
- 成员角色设置

### 4. 活动管理
- 社团活动发布（社长）
- 活动报名功能
- 活动记录查询
- 活动管理（管理员）
- 我的活动

### 5. 管理员后台
- 用户管理（启用/禁用、角色修改、信息编辑）
- 社团审核（通过/拒绝）
- 活动管理（取消活动）
- 分类管理（增删改查）

## 快速开始

### 环境要求

| 工具 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | Java开发环境 |
| Node.js | 16+ | 前端运行环境 |
| Maven | 3.8+ | Java项目构建 |
| MySQL | 8.0+ | 数据库 |
| Docker | 可选 | 容器化部署 |

### 一、数据库准备

#### 方式一：使用Docker启动MySQL

```bash
# 拉取MySQL镜像
docker pull mysql:8.0

# 启动MySQL容器（用户名root，密码ld123456）
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=ld123456 \
  -e MYSQL_DATABASE=campus_club \
  mysql:8.0
```

#### 方式二：使用本地MySQL

确保本地MySQL已启动，然后执行以下操作：

1. 创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS campus_club DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 执行初始化脚本

```bash
# 进入后端目录
cd backend

# 执行SQL脚本（需要MySQL命令行客户端）
mysql -u root -p < init.sql
# 输入密码：ld123456
```

或者使用MySQL客户端工具（如Navicat、DataGrip）连接数据库，执行 `backend/init.sql` 文件。

### 二、后端启动

#### 1. 修改配置（如需要）

编辑 `backend/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_club?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: ld123456  # 如果密码不同，请修改为你的MySQL密码
```

#### 2. 编译并启动

```bash
# 进入后端目录
cd backend

# 编译项目
mvn clean package -DskipTests

# 启动应用
mvn spring-boot:run

# 或者使用编译后的jar包启动
java -jar target/campus-club-0.0.1-SNAPSHOT.jar
```

后端服务启动后，访问地址：`http://localhost:8080`

### 三、前端启动

#### 1. 安装依赖

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 如果网络慢，可以使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

#### 2. 修改配置（如需要）

编辑 `frontend/vite.config.js`：

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端地址
        changeOrigin: true
      }
    }
  }
})
```

#### 3. 启动开发服务器

```bash
# 启动开发服务器
npm run dev
```

前端服务启动后，访问地址：`http://localhost:5173`

#### 4. 生产构建（可选）

```bash
# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

### 四、访问系统

1. 打开浏览器，访问：`http://localhost:5173`
2. 使用测试账号登录

## 默认测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 系统管理员 | admin | 123456 | 拥有所有管理权限 |
| 社长 | president | 123456 | 计算机协会/篮球社/书法协会社长 |
| 学生 | student | 123456 | 普通学生用户 |
| 学生 | user1 | 123456 | 普通学生用户 |
| 学生 | user2 | 123456 | 普通学生用户 |

## API接口说明

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 返回数据
  }
}
```

### 主要接口列表

#### 用户相关
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/info` - 获取当前用户信息
- `GET /api/user/list` - 用户列表（管理员）
- `POST /api/user/update` - 更新用户信息
- `POST /api/user/updateStatus/{id}` - 更新用户状态
- `POST /api/user/updateRole/{id}` - 更新用户角色

#### 社团相关
- `GET /api/club/list` - 社团列表
- `GET /api/club/{id}` - 社团详情
- `POST /api/club/create` - 创建社团（申请）
- `POST /api/club/audit/{id}` - 审核社团（管理员）
- `POST /api/club/applyJoin/{clubId}` - 申请加入社团
- `POST /api/club/auditJoin/{memberId}` - 审核加入申请（社长）
- `POST /api/club/quit/{clubId}` - 退出社团
- `GET /api/club/members/{clubId}` - 社团成员列表
- `GET /api/club/myClubs` - 我的社团
- `GET /api/club/pendingApprovals/{clubId}` - 待审核成员列表

#### 活动相关
- `GET /api/activity/list` - 活动列表
- `GET /api/activity/{id}` - 活动详情
- `POST /api/activity/create` - 发布活动（社长）
- `POST /api/activity/update` - 更新活动
- `DELETE /api/activity/{id}` - 删除活动
- `POST /api/activity/register/{activityId}` - 报名活动
- `POST /api/activity/cancel/{activityId}` - 取消报名
- `GET /api/activity/registrations/{activityId}` - 活动报名列表
- `GET /api/activity/myActivities` - 我的活动
- `GET /api/activity/clubActivities/{clubId}` - 社团活动列表

#### 分类相关
- `GET /api/category/list` - 分类列表
- `POST /api/category/create` - 创建分类
- `POST /api/category/update` - 更新分类
- `DELETE /api/category/{id}` - 删除分类

## 开发说明

### 后端代码规范

1. **三层架构**：Controller -> Service -> Mapper
2. **统一返回格式**：使用 `Result<T>` 封装响应
3. **全局异常处理**：`GlobalExceptionHandler` 统一处理异常
4. **跨域配置**：`CorsConfig` 配置跨域
5. **JWT认证**：使用JWT进行无状态身份认证

### 前端代码规范

1. **Vue 3 Composition API**：使用 `<script setup>` 语法
2. **Pinia状态管理**：使用Pinia进行全局状态管理
3. **Element Plus**：使用Element Plus UI组件库
4. **Axios封装**：统一请求/响应拦截器
5. **路由守卫**：登录认证和权限控制

## 常见问题

### Q1: 数据库连接失败

1. 检查MySQL服务是否启动
2. 确认数据库用户名和密码是否正确
3. 检查数据库端口是否被防火墙拦截
4. 确认 `application.yml` 中的数据库连接配置

### Q2: 登录失败

1. 确认数据库初始化脚本已执行
2. 检查用户名和密码是否正确
3. 查看后端控制台是否有错误日志

### Q3: 前端无法调用后端API

1. 确认后端服务已启动（端口8080）
2. 检查 `vite.config.js` 中的代理配置
3. 查看浏览器控制台的网络请求

### Q4: 密码加密说明

系统使用 BCrypt 进行密码加密，初始化脚本中的密码哈希值是 `123456` 的加密结果。如需修改测试账号密码，可以修改 `init.sql` 文件中的密码哈希值，或者登录后修改。

## 注意事项

1. 首次运行必须先执行数据库初始化脚本 `init.sql`
2. 后端默认端口：8080，前端默认端口：5173
3. 建议使用 Node.js 16+ 和 JDK 17+
4. 生产环境请修改JWT密钥和数据库密码

## 许可证

MIT License
