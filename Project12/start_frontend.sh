#!/bin/bash

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR/frontend"

echo "========================================"
echo "社区健康管理系统 - 前端启动脚本"
echo "========================================"

if [ ! -d "node_modules" ]; then
    echo ""
    echo "[1/2] 安装前端依赖..."
    npm install
fi

echo ""
echo "[2/2] 启动前端开发服务器..."
echo "前端服务将在 http://localhost:3000 运行"
echo "API代理已配置到 http://localhost:8000"
echo ""
echo "请确保后端服务已启动"
echo "按 Ctrl+C 停止服务"
echo "========================================"
echo ""

npm run dev
