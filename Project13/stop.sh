#!/bin/bash

echo "=========================================="
echo "   物流追踪系统 - 停止脚本"
echo "=========================================="
echo ""

PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
cd "$PROJECT_ROOT"

DOCKER_COMPOSE="docker compose"
if ! command -v docker compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
fi

echo "[1/2] 停止 Docker 容器..."
$DOCKER_COMPOSE down

echo ""
echo "[2/2] 可选: 删除数据卷 (如需要彻底重置)..."
echo "   执行以下命令删除所有数据:"
echo "   $DOCKER_COMPOSE down -v"
echo ""

echo "✓ 已停止所有服务"
echo ""
echo "=========================================="
