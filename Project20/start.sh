#!/bin/bash

echo "=========================================="
echo "图书馆管理系统启动脚本"
echo "=========================================="

# 检查是否安装了docker-compose
if ! command -v docker-compose &> /dev/null; then
    echo "错误: 未安装 docker-compose"
    echo "请先安装 Docker 和 Docker Compose"
    exit 1
fi

echo "检查 Docker 服务状态..."
if ! docker info &> /dev/null; then
    echo "错误: Docker 服务未运行"
    exit 1
fi

echo "正在启动服务..."
docker compose up -d --build

echo "等待数据库初始化..."
sleep 10

echo "=========================================="
echo "服务启动完成!"
echo "=========================================="
echo ""
echo "前端访问地址: http://localhost"
echo "后端API地址: http://localhost:8000"
echo ""
echo "管理员账号: admin"
echo "管理员密码: admin123"
echo ""
echo "测试用户账号: user1"
echo "测试用户密码: user123456"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
echo "=========================================="
