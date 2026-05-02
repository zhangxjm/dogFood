#!/bin/bash

# Docker 连接测试脚本
# 这个脚本帮助诊断 Docker 网络连接问题

echo "=== Docker 连接测试脚本 ==="
echo ""

# 1. 检查 Docker 是否安装
echo "1. 检查 Docker 是否安装..."
if command -v docker &> /dev/null; then
    echo "   ✓ Docker 已安装"
    docker --version
else
    echo "   ✗ Docker 未安装"
    echo "   请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

echo ""

# 2. 检查 Docker 服务状态
echo "2. 检查 Docker 服务状态..."
if docker info &> /dev/null; then
    echo "   ✓ Docker 服务正在运行"
else
    echo "   ✗ Docker 服务未运行"
    echo "   请启动 Docker 服务:"
    echo "   - macOS: 打开 Docker Desktop 应用"
    echo "   - Linux: sudo systemctl start docker"
    exit 1
fi

echo ""

# 3. 检查网络连接
echo "3. 检查网络连接..."
echo "   测试连接到 Docker Hub..."
if curl -s -I https://registry-1.docker.io/v2/ | grep -q "HTTP/2 200"; then
    echo "   ✓ 可以连接到 Docker Hub"
else
    echo "   ✗ 无法连接到 Docker Hub"
    echo "   可能是网络代理或防火墙问题"
fi

echo ""

# 4. 检查 DNS 解析
echo "4. 检查 DNS 解析..."
if nslookup registry-1.docker.io &> /dev/null; then
    echo "   ✓ DNS 解析正常"
else
    echo "   ✗ DNS 解析失败"
    echo "   请检查网络设置或 DNS 配置"
fi

echo ""

# 5. 测试拉取一个小的镜像
echo "5. 测试拉取镜像..."
echo "   尝试拉取 hello-world 镜像..."
if docker pull hello-world:latest &> /dev/null; then
    echo "   ✓ 成功拉取 hello-world 镜像"
    echo "   运行测试容器..."
    if docker run --rm hello-world &> /dev/null; then
        echo "   ✓ Docker 运行正常"
    else
        echo "   ✗ Docker 运行失败"
    fi
else
    echo "   ✗ 无法拉取镜像"
    echo "   错误信息:"
    docker pull hello-world:latest 2>&1 | tail -5
fi

echo ""

# 6. 检查 Docker 配置
echo "6. 检查 Docker 配置..."
if [ -f ~/.docker/daemon.json ]; then
    echo "   ✓ 找到 daemon.json 配置文件"
    echo "   当前配置:"
    cat ~/.docker/daemon.json | python3 -m json.tool 2>/dev/null || cat ~/.docker/daemon.json
else
    echo "   ℹ 未找到 daemon.json 配置文件"
fi

echo ""

# 7. 检查代理设置
echo "7. 检查代理设置..."
if [ -n "$HTTP_PROXY" ] || [ -n "$HTTPS_PROXY" ]; then
    echo "   ℹ 检测到系统代理设置:"
    echo "   HTTP_PROXY: ${HTTP_PROXY:-未设置}"
    echo "   HTTPS_PROXY: ${HTTPS_PROXY:-未设置}"
else
    echo "   ℹ 未检测到系统代理设置"
fi

echo ""

# 8. 测试 MongoDB 镜像
echo "8. 测试 MongoDB 镜像..."
echo "   尝试拉取 mongo:6.0 镜像..."
if docker pull mongo:6.0 &> /dev/null; then
    echo "   ✓ 成功拉取 mongo:6.0 镜像"
    echo "   可以运行: docker run -d --name mongodb -p 27017:27017 mongo:6.0"
else
    echo "   ✗ 无法拉取 mongo:6.0 镜像"
    echo "   尝试拉取 mongo:latest..."
    if docker pull mongo:latest &> /dev/null; then
        echo "   ✓ 成功拉取 mongo:latest 镜像"
        echo "   可以运行: docker run -d --name mongodb -p 27017:27017 mongo:latest"
    else
        echo "   ✗ 无法拉取任何 MongoDB 镜像"
        echo "   错误信息:"
        docker pull mongo:6.0 2>&1 | tail -5
    fi
fi

echo ""

# 9. 建议
echo "=== 建议 ==="
echo ""
echo "如果测试失败，请尝试以下方法:"
echo "1. 使用 Docker Compose 启动项目:"
echo "   cd /Users/a2109164/Projects/dogFood/Project1"
echo "   docker-compose up -d --build"
echo ""
echo "2. 配置代理（如果需要）:"
echo "   编辑 ~/.docker/daemon.json 文件，修改 proxies 部分:"
echo "   \"proxies\": {"
echo "     \"http-proxy\": \"http://你的代理地址:端口\","
echo "     \"https-proxy\": \"http://你的代理地址:端口\","
echo "     \"no-proxy\": \"localhost,127.0.0.1\""
echo "   }"
echo ""
echo "3. 使用国内镜像源（已配置）:"
echo "   当前已配置以下镜像源:"
echo "   - http://hub-mirror.c.163.com"
echo "   - https://docker.mirrors.ustc.edu.cn"
echo "   - https://registry.docker-cn.com"
echo ""
echo "4. 重启 Docker 服务:"
echo "   macOS: osascript -e 'quit app \"Docker\"' && open -a Docker"
echo "   Linux: sudo systemctl restart docker"
echo ""
echo "5. 查看详细解决方案:"
echo "   请查看 DOCKER_NETWORK_SOLUTION.md 文件"

echo ""
echo "=== 测试完成 ==="