#!/bin/bash

echo "=============================================="
echo "在线影院售票系统 - 启动脚本"
echo "=============================================="
echo ""

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker Desktop"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi

# 确定使用哪个 docker compose 命令
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

# 检查 Node.js 是否安装
if ! command -v node &> /dev/null; then
    echo "❌ Node.js 未安装，请先安装 Node.js (建议 18+)"
    exit 1
fi

# 检查 Python 是否安装
if ! command -v python3 &> /dev/null; then
    echo "❌ Python3 未安装"
    exit 1
fi

echo "✅ 环境检查完成"
echo ""

# 项目目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"

# 启动 MySQL 容器
echo "🚀 启动 MySQL 数据库容器..."
cd "$PROJECT_DIR"
$DOCKER_COMPOSE up -d mysql

echo "⏳ 等待 MySQL 初始化 (约 30 秒)..."
sleep 30

# 检查 MySQL 容器是否运行
MYSQL_CONTAINER=$(docker ps -q -f name=cinema_mysql)
if [ -z "$MYSQL_CONTAINER" ]; then
    echo "❌ MySQL 容器启动失败"
    $DOCKER_COMPOSE logs mysql
    exit 1
fi

echo "✅ MySQL 数据库已启动"
echo ""

# 安装后端依赖
echo "📦 安装后端 Python 依赖..."
cd "$BACKEND_DIR"

# 创建虚拟环境
if [ ! -d "venv" ]; then
    echo "创建虚拟环境..."
    python3 -m venv venv
fi

# 激活虚拟环境并安装依赖
source venv/bin/activate
pip install -r requirements.txt

echo "✅ 后端依赖安装完成"
echo ""

# 安装前端依赖
echo "📦 安装前端 Node.js 依赖..."
cd "$FRONTEND_DIR"

if [ ! -d "node_modules" ]; then
    npm install
fi

echo "✅ 前端依赖安装完成"
echo ""

echo "=============================================="
echo "🚀 启动后端服务 (端口 5000)..."
echo "=============================================="
cd "$BACKEND_DIR"
source venv/bin/activate

# 后台启动后端
nohup python3 run.py > backend.log 2>&1 &
BACKEND_PID=$!

echo "后端服务 PID: $BACKEND_PID"
echo "日志文件: $BACKEND_DIR/backend.log"
echo ""

# 等待后端启动
echo "⏳ 等待后端服务启动 (约 10 秒)..."
sleep 10

# 检查后端是否启动成功
if curl -s http://localhost:5000/api/health > /dev/null 2>&1; then
    echo "✅ 后端服务启动成功: http://localhost:5000"
else
    echo "⚠️  后端服务可能正在启动中，请稍后检查"
    echo "日志: $(tail -20 $BACKEND_DIR/backend.log)"
fi

echo ""
echo "=============================================="
echo "🚀 启动前端服务 (端口 3000)..."
echo "=============================================="
cd "$FRONTEND_DIR"

# 后台启动前端
nohup npm run dev > frontend.log 2>&1 &
FRONTEND_PID=$!

echo "前端服务 PID: $FRONTEND_PID"
echo "日志文件: $FRONTEND_DIR/frontend.log"
echo ""

# 等待前端启动
echo "⏳ 等待前端服务启动 (约 15 秒)..."
sleep 15

echo ""
echo "=============================================="
echo "🎉 系统启动完成！"
echo "=============================================="
echo ""
echo "📌 访问地址："
echo "   前端页面: http://localhost:3000"
echo "   后端 API: http://localhost:5000"
echo ""
echo "📌 默认账号："
echo "   管理员: admin / admin123"
echo "   普通用户: user / user123"
echo ""
echo "📌 停止服务命令："
echo "   # 停止后端"
echo "   kill $BACKEND_PID"
echo ""
echo "   # 停止前端"
echo "   kill $FRONTEND_PID"
echo ""
echo "   # 停止数据库"
echo "   cd $PROJECT_DIR && $DOCKER_COMPOSE down"
echo ""
echo "📌 查看日志："
echo "   后端日志: tail -f $BACKEND_DIR/backend.log"
echo "   前端日志: tail -f $FRONTEND_DIR/frontend.log"
echo ""

# 保存 PID 到文件
echo "$BACKEND_PID" > "$PROJECT_DIR/backend.pid"
echo "$FRONTEND_PID" > "$PROJECT_DIR/frontend.pid"
