# Nuxt3 电商商城全栈项目

一个基于 Nuxt3 + Vue3 + TailwindCSS + 原生 Node.js 的电商展示全栈项目。

## 技术栈

- **前端框架**: Nuxt 3 + Vue 3
- **样式框架**: TailwindCSS
- **后端服务**: 原生 Node.js HTTP
- **数据存储**: JSON 文件模拟持久化
- **开发语言**: TypeScript

## 项目功能

### 前端功能
1. **电商首页**: 轮播展示、分类导航、热卖推荐、新品上市
2. **商品分类**: 分类列表、分类详情页
3. **商品列表**: 全部商品、分类筛选、价格筛选、多维度排序
4. **商品详情**: 商品信息、图片轮播、规格参数、加入购物车
5. **购物车**: 添加/删除商品、修改数量、清空购物车、订单摘要
6. **响应式设计**: 完美适配手机/平板/电脑端

### 后端接口
- `GET /api/products` - 获取所有商品
- `GET /api/products/:id` - 获取商品详情
- `GET /api/products/category/:id` - 按分类获取商品
- `GET /api/categories` - 获取所有分类
- `GET /api/cart` - 获取购物车
- `POST /api/cart` - 添加商品到购物车
- `PUT /api/cart/:id` - 更新购物车商品数量
- `DELETE /api/cart/:id` - 删除购物车商品

## 项目结构

```
Project4/
├── components/           # 公共组件
│   ├── TheHeader.vue    # 头部导航
│   ├── TheFooter.vue    # 底部组件
│   └── ProductCard.vue  # 商品卡片
├── composables/          # 组合式函数
│   └── useCart.ts       # 购物车状态管理
├── data/                 # JSON 模拟数据
│   ├── products.json    # 商品数据
│   ├── categories.json  # 分类数据
│   └── cart.json        # 购物车数据
├── layouts/              # 布局组件
│   └── default.vue      # 默认布局
├── pages/                # 页面路由
│   ├── index.vue        # 首页
│   ├── cart.vue         # 购物车
│   ├── categories/
│   │   ├── index.vue    # 分类列表
│   │   └── [id].vue     # 分类详情
│   └── products/
│       ├── index.vue    # 商品列表
│       └── [id].vue     # 商品详情
├── server/               # 后端服务
│   └── index.js         # Node.js HTTP 服务
├── assets/               # 静态资源
│   └── css/
│       └── main.css     # 全局样式
├── Dockerfile            # Docker 构建文件
├── docker-compose.yml    # Docker Compose 配置
├── nuxt.config.ts        # Nuxt3 配置
├── tailwind.config.js    # TailwindCSS 配置
├── tsconfig.json         # TypeScript 配置
├── package.json          # 依赖配置
└── .env                  # 环境变量
```

## 快速开始

### 方式一：本地开发环境

#### 1. 安装依赖

```bash
npm install
```

#### 2. 启动项目

**方式 A：同时启动前后端（推荐）**

```bash
npm run start
```

**方式 B：分别启动**

```bash
# 启动后端服务（端口 3001）
npm run server

# 另开终端启动前端开发服务器（端口 3000）
npm run dev
```

#### 3. 访问项目

- 前端页面: http://localhost:3000
- 后端 API: http://localhost:3001/api

### 方式二：Docker 容器运行

#### 1. 构建并运行

```bash
docker-compose up -d --build
```

#### 2. 访问项目

- 前端页面: http://localhost:3000
- 后端 API: http://localhost:3001/api

#### 3. 常用命令

```bash
# 查看日志
docker-compose logs -f

# 停止容器
docker-compose down

# 重启容器
docker-compose restart
```

## 环境变量配置

项目使用 `.env` 文件管理环境变量：

```env
# API 基础地址
NUXT_PUBLIC_API_BASE=http://localhost:3001/api

# 后端服务端口
PORT=3001

# 运行环境
NODE_ENV=development
```

## API 接口说明

### 统一返回格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 接口列表

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/products | 获取所有商品 |
| GET | /api/products/:id | 获取单个商品详情 |
| GET | /api/products/category/:id | 按分类获取商品 |
| GET | /api/categories | 获取所有分类 |
| GET | /api/cart | 获取购物车 |
| POST | /api/cart | 添加商品到购物车 |
| PUT | /api/cart/:id | 更新购物车商品数量 |
| DELETE | /api/cart/:id | 删除购物车商品 |

### 接口示例

#### 添加商品到购物车

```bash
curl -X POST http://localhost:3001/api/cart \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'
```

#### 更新购物车商品数量

```bash
curl -X PUT http://localhost:3001/api/cart/1234567890 \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}'
```

#### 删除购物车商品

```bash
curl -X DELETE http://localhost:3001/api/cart/1234567890
```

## 数据结构

### 商品数据 (products.json)

```json
{
  "id": 1,
  "name": "商品名称",
  "price": 999,
  "originalPrice": 1299,
  "categoryId": 1,
  "image": "商品主图URL",
  "images": ["轮播图URL数组"],
  "description": "商品描述",
  "stock": 100,
  "sales": 2580,
  "rating": 4.9,
  "reviews": 1256,
  "isHot": true,
  "isNew": true,
  "specs": {
    "规格名": "规格值"
  }
}
```

### 分类数据 (categories.json)

```json
{
  "id": 1,
  "name": "分类名称",
  "icon": "分类图标emoji",
  "description": "分类描述",
  "order": 1
}
```

### 购物车数据 (cart.json)

```json
{
  "items": [
    {
      "id": 1234567890,
      "productId": 1,
      "quantity": 2,
      "product": { ... }
    }
  ],
  "total": 1998
}
```

## 开发说明

### 添加新商品

编辑 `data/products.json` 文件，按照现有格式添加新商品数据。

### 添加新分类

编辑 `data/categories.json` 文件，添加新分类。

### 修改接口

后端接口代码位于 `server/index.js`，可以根据需要扩展更多功能。

### 自定义主题

修改 `tailwind.config.js` 中的颜色配置：

```javascript
theme: {
  extend: {
    colors: {
      primary: '#ff6b35',  // 主色调
      secondary: '#2c3e50',
      accent: '#3498db'
    }
  }
}
```

## 依赖清单

### 生产依赖

| 包名 | 版本 | 说明 |
|------|------|------|
| nuxt | ^3.10.3 | Nuxt3 框架 |
| vue | ^3.4.21 | Vue3 框架 |
| vue-router | ^4.3.0 | Vue 路由 |
| @nuxtjs/tailwindcss | ^6.11.4 | TailwindCSS 模块 |

### 开发依赖

| 包名 | 版本 | 说明 |
|------|------|------|
| typescript | ^5.3.3 | TypeScript |
| concurrently | ^8.2.2 | 并行运行命令 |
| @nuxt/types | ^2.17.3 | Nuxt 类型定义 |

## 常见问题

### 1. 端口被占用

修改 `.env` 文件中的端口配置：

```env
PORT=3002
NUXT_PUBLIC_API_BASE=http://localhost:3002/api
```

### 2. 跨域问题

后端已配置 CORS 允许所有来源访问。如需限制，修改 `server/index.js` 中的 `Access-Control-Allow-Origin`。

### 3. 数据持久化

项目使用 JSON 文件存储数据，购物车数据会保存到 `data/cart.json` 文件中。使用 Docker 时，data 目录已挂载为卷，数据不会丢失。

## License

MIT
