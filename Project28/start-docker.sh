#!/bin/bash

echo "=========================================="
echo "校园失物招领平台 - Docker启动脚本"
echo "=========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

command -v docker >/dev/null 2>&1 || { echo >&2 "请先安装 Docker"; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo >&2 "请先安装 Docker Compose"; exit 1; }

echo ""
echo "[1] 构建并启动 Docker 容器..."
cd "$SCRIPT_DIR"
docker-compose up -d --build

echo ""
echo "[2] 等待 MySQL 就绪..."
sleep 15

echo ""
echo "[3] 执行数据库迁移..."
docker-compose exec -T backend python manage.py makemigrations
docker-compose exec -T backend python manage.py migrate

echo ""
echo "[4] 初始化数据..."
docker-compose exec -T backend python init_data.py

echo ""
echo "=========================================="
echo "启动完成！"
echo "=========================================="
echo ""
echo "访问地址："
echo "  后端API: http://localhost:8000"
echo "  Django Admin: http://localhost:8000/admin"
echo ""
echo "默认管理员账号："
echo "  用户名: admin"
echo "  密码: admin123456"
echo ""
echo "MySQL 连接信息："
echo "  主机: 127.0.0.1:3306"
echo "  用户名: root"
echo "  密码: ld123456"
echo "  数据库: school_lost_found"
echo ""
echo "常用命令："
echo "  查看日志: docker-compose logs -f"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart"
echo ""
