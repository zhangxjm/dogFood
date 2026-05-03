<template>
  <div class="admin-dashboard">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <el-icon><VideoCamera /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/admin/movies">
            <el-icon><Film /></el-icon>
            <span>电影管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/schedules">
            <el-icon><Calendar /></el-icon>
            <span>场次管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/cinemas">
            <el-icon><OfficeBuilding /></el-icon>
            <span>影院管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/halls">
            <el-icon><Grid /></el-icon>
            <span>放映厅管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-dashboard {
  height: calc(100vh - 60px);
}

.admin-dashboard .el-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo .el-icon {
  font-size: 24px;
  color: #409eff;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

:deep(.el-menu) {
  border-right: none;
}
</style>
