<template>
  <el-footer class="footer">
    <div class="footer-content">
      <div class="footer-info">
        <p>个人博客系统</p>
        <p>基于 Flask + Vue3 构建</p>
      </div>
      <div class="footer-links">
        <a href="#" @click.prevent="scrollToTop">返回顶部</a>
      </div>
    </div>
  </el-footer>
</template>

<script setup>
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}
</script>

<style scoped>
.footer {
  background-color: #303133;
  color: #fff;
  padding: 30px 0;
  height: auto !important;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.footer-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #909399;
}

.footer-links a {
  color: #409eff;
  text-decoration: none;
}

.footer-links a:hover {
  color: #66b1ff;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
}
</style>
