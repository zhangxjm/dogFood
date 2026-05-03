#!/bin/bash

echo "=============================================="
echo "在线影院售票系统 - 停止脚本"
echo "=============================================="
echo ""

# 确定使用哪个 docker compose 命令
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    DOCKER_COMPOSE="docker compose"
fi

# 项目目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 停止后端服务
if [ -f "$PROJECT_DIR/backend.pid" ]; then
    BACKEND_PID=$(cat "$PROJECT_DIR/backend.pid" 2>/dev/null)
    if [ -n "$BACKEND_PID" ] && ps -p "$BACKEND_PID" > /dev/null 2>&1; then
        echo "🛑 停止后端服务 (PID: $BACKEND_PID)..."
        kill "$BACKEND_PID" 2>/dev/null
        sleep 2
        if ps -p "$BACKEND_PID" > /dev/null 2>&1; then
            kill -9 "$BACKEND_PID" 2>/dev/null
        fi
        echo "✅ 后端服务已停止"
    else
        echo "⚠️  后端服务未运行"
    fi
    rm -f "$PROJECT_DIR/backend.pid"
else
    echo "⚠️  未找到后端 PID 文件"
fi

# 停止前端服务
if [ -f "$PROJECT_DIR/frontend.pid" ]; then
    FRONTEND_PID=$(cat "$PROJECT_DIR/frontend.pid" 2>/dev/null)
    if [ -n "$FRONTEND_PID" ] && ps -p "$FRONTEND_PID" > /dev/null 2>&1; then
        echo "🛑 停止前端服务 (PID: $FRONTEND_PID)..."
        kill "$FRONTEND_PID" 2>/dev/null
        sleep 2
        if ps -p "$FRONTEND_PID" > /dev/null 2>&1; then
            kill -9 "$FRONTEND_PID" 2>/dev/null
        fi
        echo "✅ 前端服务已停止"
    else
        echo "⚠️  前端服务未运行"
    fi
    rm -f "$PROJECT_DIR/frontend.pid"
else
    echo "⚠️  未找到前端 PID 文件"
fi

# 停止 MySQL 容器（可选，询问用户）
read -p "是否停止 MySQL 容器？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🛑 停止 MySQL 容器..."
    cd "$PROJECT_DIR"
    $DOCKER_COMPOSE down mysql 2>/dev/null || true
    echo "✅ MySQL 容器已停止"
else
    echo "ℹ️  MySQL 容器保持运行"
fi

echo ""
echo "=============================================="
echo "✅ 所有服务已停止"
echo "=============================================="
