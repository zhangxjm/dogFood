<template>
  <el-footer class="footer">
    <div class="footer-content">
      <div class="footer-info">
        <p>© 2024 校园二手交易平台</p>
        <p class="footer-desc">专注于校园闲置物品交易，让闲置变价值</p>
      </div>
      <div class="footer-links">
        <el-link type="info" @click="goHome">首页</el-link>
        <el-link type="info" @click="goPublish">发布商品</el-link>
        <el-link type="info">关于我们</el-link>
        <el-link type="info">联系我们</el-link>
      </div>
    </div>
  </el-footer>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const goHome = () => router.push("/home");
const goPublish = () => {
  const token = localStorage.getItem("token");
  if (token) {
    router.push("/publish");
  } else {
    router.push("/login");
  }
};
</script>

<style scoped>
.footer {
  background-color: #f5f7fa;
  padding: 20px 40px;
  border-top: 1px solid #e4e7ed;
  height: auto;
  min-height: 80px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.footer-desc {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.footer-links {
  display: flex;
  gap: 20px;
}

.footer-links .el-link {
  font-size: 14px;
}
</style>
