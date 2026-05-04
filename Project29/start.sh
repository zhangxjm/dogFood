#!/bin/bash

echo "=========================================="
echo "  员工人事考勤管理系统 - 启动脚本"
echo "=========================================="
echo ""

case "$1" in
    start)
        echo "🚀 启动所有服务..."
        docker compose up -d
        echo ""
        echo "✅ 服务已启动！"
        echo ""
        echo "📋 访问地址："
        echo "   - 前端: http://localhost:3000"
        echo "   - 后端API: http://localhost:8080"
        echo "   - MySQL: localhost:3306"
        echo ""
        echo "👤 默认账号："
        echo "   - 用户名: admin"
        echo "   - 密码: admin123"
        echo ""
        ;;
        
    stop)
        echo "🛑 停止所有服务..."
        docker compose down
        echo "✅ 服务已停止！"
        ;;
        
    restart)
        echo "🔄 重启所有服务..."
        docker compose restart
        echo "✅ 服务已重启！"
        ;;
        
    logs)
        echo "📜 查看服务日志..."
        docker compose logs -f
        ;;
        
    status)
        echo "📊 查看服务状态..."
        docker compose ps
        ;;
        
    build)
        echo "🔨 重新构建镜像..."
        docker compose build
        echo "✅ 镜像构建完成！"
        ;;
        
    clean)
        echo "🧹 清理所有数据和容器..."
        docker compose down -v
        echo "✅ 清理完成！"
        ;;
        
    dev)
        echo "🛠️  开发模式启动（仅MySQL）..."
        docker compose up -d mysql redis
        echo ""
        echo "✅ MySQL和Redis已启动！"
        echo ""
        echo "📋 后端启动方式："
        echo "   cd backend && mvn spring-boot:run"
        echo ""
        echo "📋 前端启动方式："
        echo "   cd frontend && npm install && npm run dev"
        echo ""
        ;;
        
    *)
        echo "📖 用法: $0 [命令]"
        echo ""
        echo "可用命令："
        echo "   start    - 启动所有服务"
        echo "   stop     - 停止所有服务"
        echo "   restart  - 重启所有服务"
        echo "   logs     - 查看服务日志"
        echo "   status   - 查看服务状态"
        echo "   build    - 重新构建镜像"
        echo "   clean    - 清理所有数据和容器（谨慎使用）"
        echo "   dev      - 开发模式（仅启动MySQL和Redis）"
        echo ""
        ;;
esac