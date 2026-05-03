#!/bin/bash

set -e

echo "========================================"
echo "社区健康管理系统 - 自动部署脚本"
echo "========================================"

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose 未安装"
    exit 1
fi

echo ""
echo "[1/6] 启动 Docker 服务 (PostgreSQL, Redis)..."
docker-compose up -d postgres redis

echo ""
echo "[2/6] 等待数据库准备就绪..."
sleep 10

echo ""
echo "[3/6] 创建 Python 虚拟环境..."
if [ ! -d "venv" ]; then
    python3 -m venv venv
fi

source venv/bin/activate

echo ""
echo "[4/6] 安装 Python 依赖..."
pip install --upgrade pip
pip install -r requirements.txt

echo ""
echo "[5/6] 配置环境变量..."
if [ ! -f ".env" ]; then
    cp .env.example .env
    echo "已创建 .env 配置文件"
fi

export DJANGO_SETTINGS_MODULE=community_health.settings

echo ""
echo "[6/6] 数据库迁移和初始化..."
python manage.py makemigrations
python manage.py migrate

echo ""
echo "创建超级管理员 (admin/admin123456)..."
python manage.py shell << 'EOF'
from users.models import User
import os

if not User.objects.filter(username='admin').exists():
    User.objects.create_superuser(
        username='admin',
        email='admin@example.com',
        password='admin123456',
        role='admin',
        is_verified=True,
        is_active=True,
        is_staff=True,
        is_superuser=True,
    )
    print("超级管理员创建成功: admin / admin123456")
else:
    print("超级管理员已存在")
EOF

echo ""
echo "创建示例数据..."
python manage.py shell << 'EOF'
from communities.models import Community
from users.models import User

if not Community.objects.filter(code='COM001').exists():
    admin_user = User.objects.filter(role='admin').first()
    Community.objects.create(
        code='COM001',
        name='阳光社区卫生服务中心',
        province='北京市',
        city='北京市',
        district='朝阳区',
        address='阳光路100号',
        phone='010-12345678',
        manager=admin_user,
        status='active',
    )
    print("示例社区创建成功")
else:
    print("示例社区已存在")

if not User.objects.filter(username='doctor1').exists():
    User.objects.create_user(
        username='doctor1',
        email='doctor1@example.com',
        password='doctor123',
        role='doctor',
        first_name='张',
        last_name='医生',
        phone='13800138001',
        is_verified=True,
        is_active=True,
    )
    print("示例医生账号创建成功: doctor1 / doctor123")
else:
    print("示例医生账号已存在")

if not User.objects.filter(username='resident1').exists():
    User.objects.create_user(
        username='resident1',
        email='resident1@example.com',
        password='resident123',
        role='resident',
        first_name='李',
        last_name='居民',
        phone='13800138002',
        is_verified=True,
        is_active=True,
    )
    print("示例居民账号创建成功: resident1 / resident123")
else:
    print("示例居民账号已存在")
EOF

echo ""
echo "收集静态文件..."
python manage.py collectstatic --noinput 2>/dev/null || true

echo ""
echo "========================================"
echo "部署完成！"
echo "========================================"
echo ""
echo "默认账号信息:"
echo "  管理员: admin / admin123456"
echo "  医生: doctor1 / doctor123"
echo "  居民: resident1 / resident123"
echo ""
echo "启动命令:"
echo "  后端: source venv/bin/activate && python manage.py runserver 0.0.0.0:8000"
echo "  前端: cd frontend && npm install && npm run dev"
echo ""
echo "访问地址:"
echo "  后端API: http://localhost:8000"
echo "  Admin后台: http://localhost:8000/admin"
echo "  API文档: http://localhost:8000/swagger"
echo "  前端: http://localhost:3000"
echo ""
