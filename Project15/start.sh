#!/bin/bash

echo "=========================================="
echo "      求职招聘系统 - 启动脚本"
echo "=========================================="

PROJECT_DIR=$(cd "$(dirname "$0")" && pwd)

echo "项目目录: $PROJECT_DIR"
echo ""

echo "1. 启动 MySQL 容器..."
cd "$PROJECT_DIR"
# 尝试使用 docker-compose（兼容旧版本）
if command -v docker-compose &> /dev/null; then
    docker-compose up -d mysql
else
    docker compose up -d mysql
fi

echo "等待 MySQL 初始化完成 (约30秒)..."
sleep 30

echo ""
echo "2. 安装前端依赖..."
cd "$PROJECT_DIR/frontend"
if [ ! -d "node_modules" ]; then
    npm install
fi

echo ""
echo "3. 安装后端依赖..."
cd "$PROJECT_DIR/backend"
mvn dependency:resolve

echo ""
echo "=========================================="
echo "      准备完成！"
echo "=========================================="
echo ""
echo "启动后端服务命令:"
echo "  cd $PROJECT_DIR/backend && mvn spring-boot:run"
echo ""
echo "启动前端服务命令:"
echo "  cd $PROJECT_DIR/frontend && npm run dev"
echo ""
echo "访问地址:"
echo "  前端: http://localhost:3000"
echo "  后端API: http://localhost:8080/api"
echo ""
echo "测试账号:"
echo "  管理员: admin / 123456"
echo "  企业HR: hr1 / 123456"
echo "  求职者: jobseeker1 / 123456"
echo ""
