# 校园二手交易平台项目启动文档

## 一、项目介绍

本项目是一个基于 SpringBoot3 + Vue3 的校园二手交易平台，采用前后端分离架构，实现了用户注册登录、商品发布、商品分类、商品详情、收藏商品、私聊留言咨询、交易记录、管理员审核等核心功能。

## 二、技术栈

### 后端技术
- **Spring Boot 3.2.0** - 基础框架
- **MyBatis-Plus 3.5.5** - ORM框架
- **MySQL 8.0** - 关系型数据库
- **JWT 0.12.3** - 用户认证（令牌生成和验证）
- **BCrypt** - 密码加密
- **Hutool 5.8.24** - 工具类库
- **Lombok** - 简化代码

### 前端技术
- **Vue 3.4.0** - 前端框架
- **Vue Router 4.2.5** - 路由管理
- **Element Plus 2.4.4** - UI组件库
- **Axios 1.6.2** - HTTP请求库
- **Vite 5.0.10** - 构建工具

### 容器化
- **Docker Compose** - MySQL容器管理

## 三、环境要求

### 必需环境
- JDK 17+（Spring Boot 3 要求）
- Node.js 18+
- Docker & Docker Compose（推荐）
- Maven 3.8+

### 可选环境
- MySQL 8.0（如不使用Docker）

## 四、快速启动（推荐使用Docker）

### 步骤1：启动MySQL容器

在项目根目录下执行：

```bash
cd /Users/a2109164/Projects/dogFood/Project23
docker-compose up -d
```

这将：
- 启动MySQL 8.0容器
- 自动创建数据库 `campus_trade`
- 自动执行初始化SQL脚本
- 映射端口 3306

**MySQL连接信息：**
- 主机：localhost
- 端口：3306
- 用户名：root
- 密码：ld123456
- 数据库：campus_trade

### 步骤2：启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 步骤3：启动前端服务

打开新终端窗口：

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

### 步骤4：访问应用

浏览器打开：**http://localhost:3000**

## 五、默认账号

### 重要说明
数据库中的密码使用BCrypt加密存储。如果默认账号无法登录，请按照以下步骤创建新账号：

1. 启动项目后，在注册页面注册一个新用户
2. 登录MySQL数据库，执行以下SQL将用户角色改为管理员：
```sql
UPDATE user SET role = 'admin' WHERE username = '你的用户名';
```

### 默认账号（如果BCrypt密码匹配）
- **管理员账号**：用户名 `admin`，密码 `admin123456`
- **测试用户账号**：用户名 `testuser`，密码 `123456`

### 如果默认密码不工作
请使用注册功能创建新用户，然后手动在数据库中修改角色。

## 六、项目结构

```
Project23/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/campustrade/
│   │   │   │       ├── CampusTradeApplication.java    # 启动类
│   │   │   │       ├── common/                        # 公共类
│   │   │   │       │   ├── Result.java               # 统一返回结果
│   │   │   │       │   ├── JwtUtils.java             # JWT工具类
│   │   │   │       │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   │   │       ├── config/                        # 配置类
│   │   │   │       │   ├── CorsConfig.java           # 跨域配置
│   │   │   │       │   ├── JwtInterceptor.java       # JWT拦截器
│   │   │   │       │   ├── WebMvcConfig.java         # WebMvc配置
│   │   │   │       │   └── MybatisPlusConfig.java    # MyBatis-Plus配置
│   │   │   │       ├── controller/                    # 控制器层
│   │   │   │       ├── entity/                        # 实体类
│   │   │   │       ├── mapper/                        # Mapper层
│   │   │   │       └── service/                       # 服务层
│   │   │   └── resources/
│   │   │       └── application.yml                    # 配置文件
│   └── pom.xml                                         # Maven依赖
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 公共组件
│   │   │   ├── Header.vue     # 头部导航
│   │   │   └── Footer.vue     # 底部组件
│   │   ├── router/            # 路由配置
│   │   ├── utils/             # 工具类
│   │   ├── views/             # 页面组件
│   │   │   ├── Home.vue           # 首页
│   │   │   ├── Login.vue          # 登录页
│   │   │   ├── Register.vue       # 注册页
│   │   │   ├── ProductDetail.vue  # 商品详情页
│   │   │   ├── Publish.vue        # 发布商品页
│   │   │   ├── MyProducts.vue     # 我的发布页
│   │   │   ├── MyFavorites.vue    # 我的收藏页
│   │   │   ├── MyTransactions.vue # 交易记录页
│   │   │   ├── Messages.vue       # 消息页
│   │   │   ├── Profile.vue        # 个人中心页
│   │   │   └── Admin.vue          # 管理后台页
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── sql/                        # 数据库脚本
│   └── campus_trade.sql        # 建表脚本及初始化数据
│
├── docker-compose.yml          # Docker Compose配置
└── README.md                   # 本文档
```

## 七、功能说明

### 用户模块
- 用户注册（用户名密码验证）
- 用户登录（JWT令牌认证）
- 个人信息管理（头像、昵称、手机号、邮箱）
- 密码修改
- 退出登录

### 商品模块
- 商品列表展示（分页、搜索、筛选、排序）
- 商品详情查看
- 发布商品（图片上传、信息填写）
- 商品分类浏览
- 商品搜索（关键词、价格区间、分类）
- 商品排序（时间、价格、浏览量）

### 收藏模块
- 添加收藏
- 取消收藏
- 我的收藏列表

### 消息模块
- 私聊消息发送
- 消息列表展示
- 未读消息统计
- 聊天记录查看

### 交易模块
- 交易记录查看（买家/卖家）
- 交易状态管理

### 管理后台
- 商品审核（通过/拒绝）
- 用户管理（启用/禁用/删除）
- 分类管理（增删改查）

## 八、API接口说明

### 基础路径
- 后端API：`http://localhost:8080`
- 前端代理后：`/api/*`

### 主要接口

#### 用户相关
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/info` - 获取用户信息
- `PUT /user/update` - 更新用户信息
- `POST /user/update-password` - 修改密码

#### 商品相关
- `GET /product/list` - 获取商品列表
- `GET /product/detail/{id}` - 获取商品详情
- `POST /product/publish` - 发布商品
- `GET /product/my` - 获取我的商品
- `POST /product/take-down/{id}` - 下架商品

#### 分类相关
- `GET /category/list` - 获取分类列表
- `POST /category/add` - 添加分类（管理员）
- `PUT /category/update/{id}` - 更新分类（管理员）
- `DELETE /category/delete/{id}` - 删除分类（管理员）

#### 收藏相关
- `POST /favorite/add/{productId}` - 添加收藏
- `POST /favorite/remove/{productId}` - 取消收藏
- `GET /favorite/my` - 获取我的收藏

#### 消息相关
- `POST /message/send` - 发送消息
- `GET /message/received` - 收到的消息
- `GET /message/sent` - 发送的消息
- `GET /message/chat/{otherUserId}` - 聊天记录
- `POST /message/read/{id}` - 标记已读

#### 文件上传
- `POST /file/upload` - 单文件上传
- `POST /file/upload-multiple` - 多文件上传

#### 管理员接口
- `GET /admin/pending-products` - 待审核商品
- `POST /admin/audit-product/{id}` - 审核商品
- `GET /admin/users` - 用户列表
- `POST /admin/update-user-status/{id}` - 更新用户状态
- `DELETE /admin/delete-user/{id}` - 删除用户

## 九、本地启动方式（不使用Docker）

### 步骤1：安装MySQL

安装MySQL 8.0，并创建数据库：

```sql
CREATE DATABASE campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 步骤2：执行数据库脚本

在MySQL中执行 `sql/campus_trade.sql` 文件

### 步骤3：修改数据库配置

编辑 `backend/src/main/resources/application.yml`，确保数据库连接信息正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_trade?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ld123456
```

### 步骤4：启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 步骤5：启动前端服务

```bash
cd frontend
npm install
npm run dev
```

## 十、常见问题

### Q1: Docker启动MySQL失败

检查端口3306是否被占用：

```bash
lsof -i :3306
```

如有进程占用，可修改 `docker-compose.yml` 中的端口映射。

### Q2: 后端启动失败，提示连接数据库失败

1. 确认MySQL容器已启动：`docker ps`
2. 确认数据库配置正确
3. 检查防火墙是否允许3306端口

### Q3: 前端无法访问后端API

检查Vite代理配置（`frontend/vite.config.js`）：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### Q4: 图片上传后无法访问

确认文件上传目录存在并有权限：
- 图片存储目录：`backend/images/`
- 访问URL：`http://localhost:8080/images/`

### Q5: JWT令牌过期

令牌有效期为24小时，过期后需重新登录。可在 `application.yml` 中修改有效期：

```yaml
jwt:
  expiration: 86400000  # 24小时（毫秒）
```

## 十一、开发说明

### 后端开发
1. 使用IntelliJ IDEA打开 `backend` 目录
2. 等待Maven依赖下载完成
3. 运行 `CampusTradeApplication` 主类

### 前端开发
1. 使用VS Code打开 `frontend` 目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动开发服务器

### 代码规范
- 后端代码遵循阿里巴巴Java开发规范
- 前端代码遵循Vue 3官方风格指南
- 所有接口统一返回 `Result` 对象
- 使用JWT进行身份认证
- 密码使用BCrypt加密存储

## 十二、部署建议

### 生产环境部署
1. 使用Docker容器化部署
2. 配置Nginx反向代理
3. 使用HTTPS协议
4. 配置数据库主从复制
5. 使用Redis缓存（可选扩展）
6. 配置日志收集和监控

### 安全建议
1. 修改默认JWT密钥
2. 修改数据库密码
3. 配置CORS允许的域名
4. 定期更新依赖包
5. 配置接口限流
6. 添加SQL注入防护

---

**项目已准备就绪，祝您使用愉快！**
