#!/bin/bash

echo "========================================"
echo "  Vue3 + NestJS + MySQL 后台管理系统"
echo "  启动脚本"
echo "========================================"
echo ""

PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo ""
print_info "1. 检查 Node.js 版本..."
node --version
if [ $? -ne 0 ]; then
    print_error "未安装 Node.js，请先安装 Node.js 18+ 版本"
    exit 1
fi

echo ""
print_info "2. 检查 npm 版本..."
npm --version

echo ""
print_info "3. 启动 MySQL 容器..."
cd "$PROJECT_ROOT"
docker-compose up -d mysql

print_info "等待 MySQL 初始化..."
sleep 15

# 检查 MySQL 是否就绪
MAX_RETRIES=30
RETRY_COUNT=0
MYSQL_READY=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    docker exec admin_mysql mysql -uroot -pld123456 -e "SELECT 1" >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        MYSQL_READY=true
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    print_info "MySQL 正在初始化... (${RETRY_COUNT}/${MAX_RETRIES})"
    sleep 2
done

if [ "$MYSQL_READY" = false ]; then
    print_error "MySQL 初始化超时，请检查 Docker 容器状态"
    print_warning "你可以手动运行: docker logs admin_mysql 查看日志"
    exit 1
fi

print_info "MySQL 初始化完成!"

echo ""
print_info "4. 安装后端依赖..."
cd "$PROJECT_ROOT/backend"
npm install

echo ""
print_info "5. 安装前端依赖..."
cd "$PROJECT_ROOT/frontend"
npm install

echo ""
print_info "========================================"
print_info "  依赖安装完成!"
print_info "========================================"
echo ""
print_info "启动方式:"
echo ""
print_info "方式一: 分别启动 (推荐开发时使用)"
echo "  启动后端: cd backend && npm run dev"
echo "  启动前端: cd frontend && npm run dev"
echo ""
print_info "方式二: 使用 concurrently 同时启动"
echo "  cd $PROJECT_ROOT && npm run dev"
echo ""
print_info "服务地址:"
echo "  后端 API: http://localhost:3000/api"
echo "  前端页面: http://localhost:5173"
echo ""
print_info "默认账号:"
echo "  用户名: admin"
echo "  密码: admin123"
echo ""
print_info "========================================"
