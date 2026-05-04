# 家政服务预约管理系统

一个完整的Java全栈家政服务预约管理系统，采用前后端分离架构设计。

## 技术栈

### 后端技术
- **Spring Boot 3.2.0** - 核心框架
- **Spring Security 6** - 安全框架
- **MyBatis-Plus 3.5.5** - ORM框架
- **JWT (jjwt 0.12.3)** - Token认证
- **MySQL 8.0** - 关系型数据库
- **Lombok** - 代码简化工具
- **Hutool** - 工具类库

### 前端技术
- **Vue 3.4** - 渐进式JavaScript框架
- **Vue Router 4** - 路由管理
- **Pinia 2** - 状态管理
- **Element Plus 2** - UI组件库
- **Axios** - HTTP客户端
- **Vite 5** - 构建工具

## 项目结构

```
Project27/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── homemaking/
│   │   │   │           ├── common/          # 通用类
│   │   │   │           ├── config/          # 配置类
│   │   │   │           ├── controller/      # 控制器
│   │   │   │           ├── entity/          # 实体类
│   │   │   │           ├── exception/       # 异常处理
│   │   │   │           ├── mapper/          # 数据访问层
│   │   │   │           ├── security/        # 安全模块
│   │   │   │           └── service/         # 服务层
│   │   │   └── resources/
│   │   │       ├── application.yml          # 应用配置
│   │   │       └── sql/
│   │   │           └── init.sql             # 数据库初始化脚本
│   └── pom.xml
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia状态管理
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面组件
│   │       ├── admin/          # 后台管理页面
│   │       └── layout/         # 布局组件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml          # Docker配置
└── README.md
```

## 核心功能

### 1. 服务分类管理
- 保洁、维修、保姆、搬家四大类服务
- 支持新增、编辑、删除分类
- 支持分类排序和状态管理

### 2. 师傅入驻与审核
- 师傅在线提交入驻申请
- 管理员审核师傅资料
- 师傅资料管理（技能描述、工作年限、评分等）

### 3. 用户预约下单
- 浏览服务列表和服务详情
- 选择服务项目和服务师傅
- 选择预约时间和服务地址
- 订单创建和支付（模拟）

### 4. 订单状态流转
- **待接单** - 用户下单，等待师傅接单
- **服务中** - 师傅接单，正在服务
- **待评价** - 服务完成，等待用户评价
- **已评价** - 用户已评价订单
- **已取消** - 订单已取消

### 5. 评价系统
- 用户对服务进行评分（1-5星）
- 用户对服务进行文字评价
- 查看订单评价记录

### 6. 平台公告
- 管理员发布平台公告
- 支持公告排序和发布状态管理
- 用户在首页查看公告信息

### 7. 角色权限控制
- **普通用户** - 浏览服务、下单预约、订单管理、个人中心
- **服务师傅** - 接单管理、订单处理、个人资料管理
- **管理员** - 用户管理、师傅审核、服务管理、订单管理、公告管理

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

### 步骤一：启动MySQL数据库

```bash
# 在项目根目录下执行
docker-compose up -d
```

这将启动MySQL 8.0容器，自动创建`homemaking`数据库并执行初始化脚本。

**数据库连接信息：**
- 主机：localhost
- 端口：3306
- 用户名：root
- 密码：ld123456
- 数据库：homemaking

### 步骤二：启动后端服务

```bash
cd backend

# 使用Maven安装依赖
mvn clean install -DskipTests

# 启动应用
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 步骤三：启动前端服务

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 步骤四：访问系统

打开浏览器访问：
- 前台：`http://localhost:5173`
- 后台管理：`http://localhost:5173/admin`

## 默认账号

系统初始化了以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 后台管理系统权限 |
| user1 | 123456 | 普通用户 | 前台预约下单权限 |

## API接口文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/me` - 获取当前用户信息

### 公开接口
- `GET /api/public/categories` - 获取服务分类列表
- `GET /api/public/services` - 获取服务列表（分页）
- `GET /api/public/services/{id}` - 获取服务详情
- `GET /api/public/workers` - 获取师傅列表（分页）
- `GET /api/public/announcements` - 获取公告列表

### 用户接口
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/profile` - 更新用户信息
- `PUT /api/user/password` - 修改密码

### 订单接口
- `POST /api/orders` - 创建订单
- `GET /api/orders` - 获取订单列表（分页）
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders/{id}/accept` - 师傅接单
- `POST /api/orders/{id}/complete` - 完成服务
- `POST /api/orders/{id}/cancel` - 取消订单
- `POST /api/orders/{id}/review` - 评价订单

### 师傅接口
- `POST /api/worker/apply` - 申请入驻
- `GET /api/worker/profile` - 获取师傅资料
- `PUT /api/worker/profile` - 更新师傅资料

### 管理后台接口
- `GET /api/admin/users` - 用户列表（分页）
- `PUT /api/admin/users/{id}/status` - 更新用户状态
- `GET /api/admin/workers` - 师傅列表（分页）
- `PUT /api/admin/workers/{id}/audit` - 审核师傅
- `POST /api/admin/categories` - 新增分类
- `PUT /api/admin/categories/{id}` - 更新分类
- `DELETE /api/admin/categories/{id}` - 删除分类
- `POST /api/admin/services` - 新增服务
- `PUT /api/admin/services/{id}` - 更新服务
- `PUT /api/admin/services/{id}/status` - 更新服务状态
- `GET /api/admin/orders` - 订单列表（分页）
- `POST /api/admin/announcements` - 新增公告
- `PUT /api/admin/announcements/{id}` - 更新公告
- `DELETE /api/admin/announcements/{id}` - 删除公告
- `PUT /api/admin/announcements/{id}/status` - 更新公告状态

## 数据库表结构

系统包含以下核心数据表：

1. **sys_user** - 用户表
   - 存储用户基本信息（用户名、密码、手机号、角色等）

2. **service_category** - 服务分类表
   - 存储服务分类信息（保洁、维修、保姆、搬家）

3. **service_item** - 服务项目表
   - 存储具体服务项目（日常保洁、空调清洗等）

4. **worker** - 师傅表
   - 存储服务师傅信息（身份证、技能、评分、审核状态等）

5. **orders** - 订单表
   - 存储订单信息（订单号、用户ID、师傅ID、服务时间、状态等）

6. **review** - 评价表
   - 存储订单评价信息（评分、评价内容等）

7. **announcement** - 公告表
   - 存储平台公告信息

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/homemaking?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ld123456
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: homemaking-jwt-secret-key-2024-very-long-secret
  expiration: 86400000  # 24小时

file:
  upload:
    path: ./uploads/
```

### 前端配置 (vite.config.js)

```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 生产部署

### 后端打包

```bash
cd backend
mvn clean package -DskipTests
```

打包后生成 `target/homemaking-1.0.0.jar`，可直接运行：

```bash
java -jar homemaking-1.0.0.jar
```

### 前端打包

```bash
cd frontend
npm run build
```

打包后生成 `dist` 目录，可部署到Nginx等Web服务器。

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态资源
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 开发规范

### 后端代码规范
- 使用Lombok简化代码
- 统一使用Result作为API返回结果
- 使用全局异常处理
- 遵循RESTful API设计规范

### 前端代码规范
- 使用Vue 3 Composition API
- 使用Pinia进行状态管理
- 统一使用ElMessage进行消息提示
- 路由配置使用路由守卫进行权限控制

## 注意事项

1. **JWT密钥**：生产环境请修改`jwt.secret`为更长的随机字符串
2. **文件上传**：默认上传到`./uploads/`目录，生产环境建议配置独立的存储路径
3. **密码加密**：使用BCrypt加密，数据库中存储的是加密后的密码
4. **跨域配置**：后端已配置CORS，开发环境前端使用Vite代理

## 许可证

本项目仅供学习和参考使用。

## 联系方式

如有问题或建议，请提交Issue。
