# 使用 Node.js 官方镜像
FROM node:20-alpine

# 设置工作目录
WORKDIR /app

# 安装系统依赖（用于编译原生模块）
RUN apk add --no-cache python3 make g++

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 暴露端口
EXPOSE 5000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:5000/api/health || exit 1

# 启动命令
CMD ["npm", "start"]
