# Docker 网络连接问题解决方案

## 问题分析

运行 `docker run -d --name mongodb -p 27017:27017 mongo:6` 时出现以下错误：

```
Unable to find image 'mongo:6' locally
docker: Error response from daemon: Get "https://registry-1.docker.io/v2/": dialing registry-1.docker.io:443 static system has no HTTPS proxy: connecting to 199.59.148.6:443: dial tcp 199.59.148.6:443: i/o timeout.
See 'docker run --help'.
```

错误原因：
1. **镜像标签问题**：`mongo:6` 不是有效的镜像标签，已修复为 `mongo:6.0`
2. **网络连接问题**：Docker 无法连接到 Docker Hub，可能是代理配置问题或网络限制

## 解决方案

### 方案一：使用正确的镜像标签（已修复）

已更新项目文件：
- `docker-compose.yml`：将 `mongo:6` 改为 `mongo:6.0`
- `README.md`：将 `mongo:6` 改为 `mongo:6.0`

### 方案二：使用 Docker Compose（推荐）

使用 Docker Compose 启动所有服务，它会使用本地缓存的镜像或配置的镜像源：

```bash
# 进入项目根目录
cd /Users/a2109164/Projects/dogFood/Project1

# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f mongodb
```

### 方案三：配置 Docker 代理

如果您的网络需要通过代理访问外网，请配置 Docker 代理：

#### 1. 创建或编辑 Docker 服务配置文件

```bash
sudo mkdir -p /etc/systemd/system/docker.service.d
sudo nano /etc/systemd/system/docker.service.d/http-proxy.conf
```

添加以下内容（根据您的代理设置修改）：
```ini
[Service]
Environment="HTTP_PROXY=http://proxy.example.com:8080/"
Environment="HTTPS_PROXY=http://proxy.example.com:8080/"
Environment="NO_PROXY=localhost,127.0.0.1,.example.com"
```

#### 2. 重启 Docker 服务

```bash
sudo systemctl daemon-reload
sudo systemctl restart docker
```

#### 3. 验证代理配置

```bash
systemctl show --property=Environment docker
```

### 方案四：使用国内镜像源加速

已配置的镜像源（在 `~/.docker/daemon.json` 中）：
```json
"registry-mirrors": [
  "http://hub-mirror.c.163.com",
  "https://docker.mirrors.ustc.edu.cn",
  "https://registry.docker-cn.com"
]
```

如果仍然无法连接，可以尝试：

#### 1. 重启 Docker 使配置生效

```bash
# macOS
osascript -e 'quit app "Docker"'
open -a Docker

# 或使用命令行
sudo killall Docker && open -a Docker
```

#### 2. 测试镜像拉取

```bash
docker pull mongo:6.0
```

### 方案五：手动下载镜像（如果有代理问题）

#### 1. 在有网络的环境中下载镜像

```bash
docker pull mongo:6.0
docker save mongo:6.0 -o mongo-6.0.tar
```

#### 2. 将镜像文件传输到当前机器

#### 3. 加载镜像

```bash
docker load -i mongo-6.0.tar
```

#### 4. 运行容器

```bash
docker run -d --name mongodb -p 27017:27017 -v mongodb_data:/data/db mongo:6.0
```

### 方案六：检查网络连接

#### 1. 测试网络连接

```bash
# 测试是否能访问 Docker Hub
curl -I https://registry-1.docker.io/v2/

# 测试 DNS 解析
nslookup registry-1.docker.io

# 测试端口连通性
telnet registry-1.docker.io 443
```

#### 2. 检查防火墙设置

```bash
# macOS 检查防火墙
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --getglobalstate

# 临时关闭防火墙（测试用）
sudo /usr/libexec/ApplicationFirewall/socketfilterfw --setglobalstate off
```

### 方案七：使用离线模式启动项目

如果所有网络方案都失败，可以使用本地开发模式：

#### 1. 启动 MongoDB（如果已安装）

```bash
# 使用 Homebrew 安装的 MongoDB
brew services start mongodb-community

# 或直接运行
mongod --dbpath /usr/local/var/mongodb
```

#### 2. 修改后端配置

编辑 `backend/.env` 文件：
```env
MONGODB_URI=mongodb://localhost:27017/fullstack-app
```

#### 3. 启动后端服务

```bash
cd backend
npm install
npm run dev
```

#### 4. 启动前端服务

```bash
cd frontend
npm install
npm run dev
```

## 快速测试命令

### 测试 Docker 基本功能
```bash
# 检查 Docker 版本
docker --version

# 检查 Docker Compose 版本
docker-compose --version

# 检查 Docker 服务状态
docker info

# 测试运行一个简单容器
docker run hello-world
```

### 测试 MongoDB 镜像
```bash
# 尝试拉取 MongoDB 镜像（使用国内镜像源）
docker pull mongo:6.0

# 如果拉取成功，运行测试容器
docker run -d --name test-mongo -p 27017:27017 mongo:6.0 --quiet

# 检查容器状态
docker ps

# 查看容器日志
docker logs test-mongo

# 停止测试容器
docker stop test-mongo
docker rm test-mongo
```

## 常见问题排查

### 1. Docker 服务未运行
```bash
# 检查 Docker 服务状态
docker ps

# 如果报错，启动 Docker
# macOS: 打开 Docker Desktop 应用
# Linux: sudo systemctl start docker
```

### 2. 权限问题
```bash
# 将用户添加到 docker 组（Linux）
sudo usermod -aG docker $USER
newgrp docker

# macOS 通常不需要此步骤
```

### 3. 磁盘空间不足
```bash
# 清理 Docker 资源
docker system prune -a
docker volume prune
```

### 4. 镜像标签不存在
```bash
# 查看可用的 MongoDB 标签
docker search mongo --filter "is-official=true"

# 使用正确的标签
docker pull mongo:latest
docker pull mongo:6.0.14
```

## 紧急解决方案

如果以上方法都无效，可以：

1. **使用项目根目录的 Docker Compose**（已修复镜像标签问题）
2. **联系网络管理员**检查代理和防火墙设置
3. **使用 VPN** 连接外网
4. **在能访问外网的机器上构建镜像**，然后导出导入

## 项目启动命令总结

### 推荐方式：Docker Compose
```bash
cd /Users/a2109164/Projects/dogFood/Project1
docker-compose up -d --build
```

### 备用方式：本地开发
```bash
# 启动 MongoDB（使用已安装的 MongoDB 或 Docker）
# 启动后端
cd backend && npm install && npm run dev

# 启动前端（新终端）
cd frontend && npm install && npm run dev
```

## 获取帮助

如果问题仍然存在，请提供以下信息：
1. 操作系统版本
2. Docker 版本（`docker --version`）
3. 完整的错误信息
4. 网络环境（公司网络/家庭网络/VPN）
5. 是否使用代理

您可以在以下位置找到更多帮助：
- [Docker 官方文档](https://docs.docker.com/)
- [Docker Hub 状态页面](https://status.docker.com/)
- [GitHub Issues](https://github.com/docker/for-mac/issues)
