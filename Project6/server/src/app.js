require("dotenv").config();
const express = require("express");
const cors = require("cors");
const mongoose = require("./config/database");

// 导入路由
const userRoutes = require("./routes/userRoutes");
const itemRoutes = require("./routes/itemRoutes");

// 导入中间件
const errorHandler = require("./middleware/errorHandler");

// 初始化Express应用
const app = express();

// 中间件配置
app.use(
  cors({
    origin: process.env.CORS_ORIGIN || "*",
    credentials: true,
  }),
);

app.use(express.json({ limit: "10mb" }));
app.use(express.urlencoded({ extended: true, limit: "10mb" }));

// 路由配置
app.use("/api/users", userRoutes);
app.use("/api/items", itemRoutes);

// 健康检查路由
app.get("/api/health", (req, res) => {
  res.json({
    success: true,
    message: "服务器运行正常",
    timestamp: new Date().toISOString(),
  });
});

// 404处理
app.use("*", (req, res) => {
  res.status(404).json({
    success: false,
    message: "请求的资源不存在",
  });
});

// 全局错误处理
app.use(errorHandler);

// 启动服务器
const PORT = process.env.PORT || 5000;

app.listen(PORT, () => {
  console.log(`🚀 服务器运行在端口 ${PORT}`);
  console.log(`📍 API地址: http://localhost:${PORT}/api`);
  console.log(`🛠️  环境: ${process.env.NODE_ENV || "development"}`);
});

module.exports = app;
