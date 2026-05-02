#!/bin/bash

echo "========================================"
echo "  启动全栈项目"
echo "========================================"

BASE_DIR=$(dirname "$0")

echo "1. 启动后端服务 (端口 5000)..."
echo "   请确保后端已创建虚拟环境并安装依赖"
echo "   或运行: bash start-backend.sh"
echo ""

echo "2. 启动前端服务 (端口 3000)..."
echo "   请确保前端已安装 npm 依赖"
echo "   或运行: bash start-frontend.sh"
echo ""

echo "========================================"
echo "  快速启动命令:"
echo "========================================"
echo ""
echo "方式一: 分别启动 (推荐)"
echo "  终端1: cd backend && source venv/bin/activate && python run.py"
echo "  终端2: cd frontend && npm run dev"
echo ""
echo "方式二: 使用 Docker Compose"
echo "  docker-compose up --build"
echo ""
echo "========================================"
echo "  访问地址:"
echo "========================================"
echo "  前端: http://localhost:3000"
echo "  后端: http://localhost:5000"
echo "  API文档: http://localhost:5000/health"
echo ""
