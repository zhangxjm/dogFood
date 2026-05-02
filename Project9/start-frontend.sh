#!/bin/bash

echo "========================================"
echo "  启动前端服务"
echo "========================================"

cd frontend

echo "检查 Node.js 版本..."
node --version

if [ ! -d "node_modules" ]; then
    echo "安装 npm 依赖..."
    npm install --registry=https://registry.npmmirror.com
fi

echo "启动 Vite 开发服务器..."
echo "服务地址: http://localhost:3000"
echo ""
npm run dev
