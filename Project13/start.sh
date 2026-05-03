#!/bin/bash

set -e

echo "=========================================="
echo "   物流追踪系统 - 一键启动脚本"
echo "=========================================="
echo ""

PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
cd "$PROJECT_ROOT"

DOCKER_COMPOSE="docker compose"
if ! command -v docker compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
fi

echo "[1/6] 检查 Docker 环境..."
if ! command -v docker &> /dev/null; then
    echo "❌ 错误: 未找到 Docker，请先安装 Docker Desktop"
    exit 1
fi

echo "✓ Docker 已安装"

echo ""
echo "[2/6] 启动 Docker 容器 (MySQL, Nacos)..."
$DOCKER_COMPOSE up -d mysql nacos

echo ""
echo "[3/6] 等待 MySQL 就绪..."
for i in {1..30}; do
    if docker exec logistics-mysql mysqladmin ping -h localhost -u root -pld123456 --silent 2>/dev/null; then
        echo "✓ MySQL 已就绪"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ MySQL 启动超时，请检查 Docker 日志"
        exit 1
    fi
    echo -n "."
    sleep 2
done

echo ""
echo "[4/6] 等待 Nacos 就绪 (这可能需要 30-60 秒)..."
for i in {1..40}; do
    if curl -s http://localhost:8848/nacos/ > /dev/null 2>&1; then
        echo "✓ Nacos 已就绪"
        break
    fi
    if [ $i -eq 40 ]; then
        echo "⚠️  Nacos 可能还在启动中，将继续后续步骤"
        echo "   你可以稍后在浏览器中访问: http://localhost:8848/nacos/"
        break
    fi
    echo -n "."
    sleep 3
done

echo ""
echo "[5/6] 编译后端项目..."
if command -v mvn &> /dev/null; then
    mvn clean install -DskipTests
else
    echo "⚠️  未找到 Maven 命令"
    echo "   请手动执行: mvn clean install -DskipTests"
    echo "   或使用 IDE 编译项目"
fi

echo ""
echo "=========================================="
echo "   启动服务说明"
echo "=========================================="
echo ""
echo "🔧 Docker 容器:"
echo "   - MySQL:     localhost:3306"
echo "     用户: root, 密码: ld123456, 数据库: logistics"
echo "   - Nacos:     http://localhost:8848/nacos/"
echo ""
echo "📦 后端服务启动顺序 (每个服务需在新终端运行):"
echo ""
echo "   1. 用户服务 (端口 8081):"
echo "      cd $PROJECT_ROOT/user-service && mvn spring-boot:run"
echo ""
echo "   2. 订单服务 (端口 8082):"
echo "      cd $PROJECT_ROOT/order-service && mvn spring-boot:run"
echo ""
echo "   3. 物流服务 (端口 8083):"
echo "      cd $PROJECT_ROOT/logistics-service && mvn spring-boot:run"
echo ""
echo "   4. 网关服务 (端口 8080):"
echo "      cd $PROJECT_ROOT/gateway && mvn spring-boot:run"
echo ""
echo "🌐 前端 (端口 3000):"
echo "   cd $PROJECT_ROOT/frontend && npm install && npm run dev"
echo ""
echo "👤 测试账号:"
echo "   - 管理员: 用户名 admin, 密码 admin123"
echo "   - 普通用户: 用户名 user1, 密码 123456"
echo ""
echo "=========================================="
