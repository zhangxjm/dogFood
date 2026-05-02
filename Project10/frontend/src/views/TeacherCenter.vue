<template>
  <div class="teacher-center-page">
    <el-row :gutter="20">
      <el-col :span="5">
        <el-card class="sidebar-card">
          <div class="teacher-profile">
            <el-avatar :size="80" :src="userInfo?.avatar">
              {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="teacher-info">
              <h3 class="username">{{ userInfo?.username }}</h3>
              <el-tag type="primary">教师</el-tag>
            </div>
          </div>

          <el-menu :default-active="activeMenu" router class="sidebar-menu">
            <el-menu-item index="/teacher/courses">
              <el-icon><List /></el-icon>
              <span>课程管理</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :span="19">
        <router-view />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);

const activeMenu = computed(() => {
  if (route.path.startsWith("/teacher/courses")) return "/teacher/courses";
  return route.path;
});
</script>

<style scoped>
.teacher-center-page {
  min-height: calc(100vh - 100px);
}

.sidebar-card {
  border-radius: 8px;
  position: sticky;
  top: 20px;
}

.teacher-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  margin: -20px -20px 20px;
}

.teacher-info {
  margin-top: 16px;
  text-align: center;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin-bottom: 4px;
  border-radius: 6px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #ecf5ff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
}
</style>
