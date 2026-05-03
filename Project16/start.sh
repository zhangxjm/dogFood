#!/bin/bash

echo "=========================================="
echo "   文创作品平台 - 启动脚本"
echo "=========================================="
echo ""

PROJECT_DIR=$(pwd)

# 检查是否在正确的目录
if [ ! -d "backend" ] || [ ! -d "frontend" ]; then
    echo "❌ 请在项目根目录下运行此脚本"
    exit 1
fi

echo "📦 步骤 1: 启动 MongoDB (Docker)"
echo "------------------------------------------"

# 检查 Docker 是否运行
if ! docker info >/dev/null 2>&1; then
    echo "⚠️  Docker 未运行。请先启动 Docker Desktop 或 Docker 服务。"
    echo ""
    echo "手动启动命令:"
    echo "  docker compose up -d"
    echo ""
else
    # 启动 MongoDB
    docker compose up -d mongodb
    echo ""
    echo "⏳ 等待 MongoDB 启动..."
    sleep 3
fi

echo ""
echo "🐍 步骤 2: 启动后端服务"
echo "------------------------------------------"

cd "$PROJECT_DIR/backend"

# 检查虚拟环境是否存在
if [ -d "venv" ]; then
    echo "✅ 找到虚拟环境，激活中..."
    source venv/bin/activate
else
    echo "📝 创建虚拟环境..."
    python3 -m venv venv
    source venv/bin/activate
    echo "📦 安装依赖..."
    pip install -r requirements.txt
fi

# 检查 .env 文件
if [ ! -f ".env" ]; then
    echo "📝 创建环境变量文件..."
    cp .env.example .env
fi

echo "🚀 启动后端服务 (端口 8000)..."
echo ""
echo "API 文档地址: http://localhost:8000/docs"
echo ""

# 以后台方式启动后端
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000 &
BACKEND_PID=$!

echo "后端服务 PID: $BACKEND_PID"
echo ""

# 等待后端启动
sleep 2

echo "🌐 步骤 3: 启动前端服务"
echo "------------------------------------------"

cd "$PROJECT_DIR/frontend"

# 检查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
    echo "📦 安装依赖..."
    npm install
fi

echo "🚀 启动前端服务 (端口 3000)..."
echo ""
echo "前端地址: http://localhost:3000"
echo ""

# 启动前端（前台运行）
npm run dev &
FRONTEND_PID=$!

echo "前端服务 PID: $FRONTEND_PID"
echo ""

echo "=========================================="
echo "✅ 所有服务已启动!"
echo "=========================================="
echo ""
echo "🌐 访问地址:"
echo "   前端: http://localhost:3000"
echo "   后端 API: http://localhost:8000"
echo "   API 文档: http://localhost:8000/docs"
echo "   Mongo-Express: http://localhost:8081 (如果已启动)"
echo ""
echo "⚠️  提示:"
echo "   - 按 Ctrl+C 停止所有服务"
echo "   - 首次运行需要安装依赖，可能需要较长时间"
echo ""

# 等待用户按 Ctrl+C
trap "echo ''; echo '🛑 正在停止服务...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; docker-compose down; echo '✅ 服务已停止'; exit 0" INT

# 保持脚本运行
while true; do
    sleep 1
done
