#!/bin/bash

echo "=================================="
echo "  启动全栈企业级后台管理系统"
echo "=================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 进入项目目录
cd "$(dirname "$0")"

# 停止现有容器
echo "🔄 停止现有容器..."
docker-compose down

# 构建并启动服务
echo "🏗️  构建并启动服务..."
docker-compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查容器状态
echo "📊 检查容器状态..."
docker-compose ps

echo "=================================="
echo "✅ 服务启动完成！"
echo "=================================="
echo ""
echo "🌐 访问地址："
echo "   前端: http://localhost"
echo "   后端: http://localhost:3000"
echo "   API 文档: http://localhost:3000/api-docs"
echo ""
echo "👤 默认账号："
echo "   用户名: admin"
echo "   密码: 123456"
echo ""
echo "📋 常用命令："
echo "   查看日志: docker-compose logs -f"
echo "   停止服务: docker-compose down"
echo "   重启服务: docker-compose restart"
echo "=================================="
