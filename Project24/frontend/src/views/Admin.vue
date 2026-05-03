<template>
  <el-container class="admin-container">
    <el-aside width="200px" class="admin-aside">
      <div class="aside-header">
        <el-icon size="24"><Setting /></el-icon>
        <span>管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="aside-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/admin/polls">
          <el-icon><Document /></el-icon>
          <span>投票活动管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="(item, index) in breadcrumbs"
              :key="index"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span>欢迎，{{ user.username }}</span>
          <el-button type="primary" link @click="goHome">返回前台</el-button>
          <el-button type="danger" link @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();

const user = computed(() => {
  const userStr = localStorage.getItem("user");
  return userStr ? JSON.parse(userStr) : {};
});

const activeMenu = computed(() => {
  if (route.path === "/admin/polls" || route.path === "/admin") {
    return "/admin/polls";
  }
  if (route.path.startsWith("/admin/poll/")) {
    return "/admin/polls";
  }
  if (route.path.startsWith("/admin/stats/")) {
    return "/admin/polls";
  }
  return route.path;
});

const breadcrumbs = computed(() => {
  const crumbs = [{ title: "首页", path: "/admin/polls" }];

  if (route.path.startsWith("/admin/poll/")) {
    crumbs.push({ title: "编辑投票活动", path: "" });
  } else if (route.path.startsWith("/admin/stats/")) {
    crumbs.push({ title: "投票统计", path: "" });
  }

  return crumbs;
});

const goHome = () => {
  router.push("/");
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  ElMessage.success("已退出登录");
  router.push("/login");
};
</script>

<style scoped>
.admin-container {
  height: 100vh;
}

.admin-aside {
  background-color: #304156;
}

.aside-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5f;
}

.aside-menu {
  border-right: none;
}

.admin-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #606266;
}

.admin-main {
  background-color: #f0f2f5;
}
</style>
