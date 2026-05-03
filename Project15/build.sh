#!/bin/bash

echo "=========================================="
echo "      求职招聘系统 - 打包脚本"
echo "=========================================="

PROJECT_DIR=$(cd "$(dirname "$0")" && pwd)
BUILD_DIR="$PROJECT_DIR/dist"

echo "项目目录: $PROJECT_DIR"
echo ""

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

echo "1. 打包前端..."
cd "$PROJECT_DIR/frontend"
npm run build

echo "复制前端构建结果..."
cp -r "$PROJECT_DIR/frontend/dist"/* "$BUILD_DIR/"

echo ""
echo "2. 打包后端..."
cd "$PROJECT_DIR/backend"
mvn clean package -DskipTests

echo "复制后端构建结果..."
cp "$PROJECT_DIR/backend/target/job-recruitment-system-1.0.0.jar" "$BUILD_DIR/"

echo ""
echo "3. 复制配置文件..."
cp "$PROJECT_DIR/docker-compose.yml" "$BUILD_DIR/"
cp "$PROJECT_DIR/README.md" "$BUILD_DIR/" 2>/dev/null || true

echo ""
echo "=========================================="
echo "      打包完成！"
echo "=========================================="
echo ""
echo "构建输出目录: $BUILD_DIR"
echo ""
echo "部署说明:"
echo "  1. 将 dist 目录复制到服务器"
echo "  2. 启动 MySQL: docker-compose up -d mysql"
echo "  3. 启动后端: java -jar job-recruitment-system-1.0.0.jar"
echo "  4. 前端使用 Nginx 或其他静态资源服务器部署"
echo ""
