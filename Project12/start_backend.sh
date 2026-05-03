#!/bin/bash

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "========================================"
echo "社区健康管理系统 - 启动脚本"
echo "========================================"

if [ ! -d "venv" ]; then
    echo "错误: Python 虚拟环境不存在，请先运行 setup.sh"
    exit 1
fi

source venv/bin/activate

echo ""
echo "[1/3] 确保 Docker 服务运行中..."
docker-compose up -d postgres redis 2>/dev/null || true
sleep 3

echo ""
echo "[2/3] 应用数据库迁移..."
python manage.py migrate --noinput

echo ""
echo "[3/3] 启动后端服务..."
echo "后端服务将在 http://localhost:8000 运行"
echo "Admin后台: http://localhost:8000/admin"
echo "API文档: http://localhost:8000/swagger"
echo ""
echo "按 Ctrl+C 停止服务"
echo "========================================"
echo ""

python manage.py runserver 0.0.0.0:8000
