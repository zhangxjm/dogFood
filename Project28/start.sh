#!/bin/bash

echo "=========================================="
echo "校园失物招领平台 - 一键启动脚本"
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"

echo ""
echo "[1] 检查环境..."
command -v python3 >/dev/null 2>&1 || { echo >&2 "请先安装 Python 3.10+"; exit 1; }
command -v pip3 >/dev/null 2>&1 || { echo >&2 "请先安装 pip3"; exit 1; }
command -v node >/dev/null 2>&1 || { echo >&2 "请先安装 Node.js 16+"; exit 1; }
command -v npm >/dev/null 2>&1 || { echo >&2 "请先安装 npm"; exit 1; }

echo ""
echo "[2] 检查数据库连接..."
echo "请确保 MySQL 已启动，并且配置了以下数据库："
echo "  数据库名: school_lost_found"
echo "  用户名: root"
echo "  密码: ld123456"
echo "  主机: 127.0.0.1:3306"
read -p "数据库已准备就绪？(y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    exit 1
fi

echo ""
echo "[3] 安装后端依赖..."
cd "$BACKEND_DIR"
pip3 install -r requirements.txt -q

echo ""
echo "[4] 执行数据库迁移..."
python3 manage.py makemigrations
python3 manage.py migrate

echo ""
echo "[5] 初始化数据..."
python3 init_data.py

echo ""
echo "[6] 安装前端依赖..."
cd "$FRONTEND_DIR"
npm install

echo ""
echo "=========================================="
echo "安装完成！"
echo "=========================================="
echo ""
echo "启动方式："
echo ""
echo "方式一：分开启动"
echo "  后端: cd backend && python3 manage.py runserver"
echo "  前端: cd frontend && npm run dev"
echo ""
echo "方式二：使用 start-dev.sh 启动"
echo ""
echo "访问地址："
echo "  前端: http://localhost:3000"
echo "  后端API: http://localhost:8000"
echo "  管理后台: http://localhost:3000/admin"
echo ""
echo "默认管理员账号："
echo "  用户名: admin"
echo "  密码: admin123456"
echo ""
