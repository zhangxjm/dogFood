<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="admin-sidebar">
      <div class="sidebar-header">
        <h3 class="logo">
          <el-icon><ShoppingCart /></el-icon>
          <span>秒杀后台</span>
        </h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/seckill">
          <el-icon><Timer /></el-icon>
          <span>秒杀管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ name: 'Home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="28" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{
                userStore.userInfo?.nickname || userStore.userInfo?.username
              }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <router-link :to="{ name: 'Home' }">
                  <el-dropdown-item command="home">
                    <el-icon><House /></el-icon>
                    前台首页
                  </el-dropdown-item>
                </router-link>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const activeMenu = computed(() => route.path);

const pageTitleMap = {
  "/admin/dashboard": "数据统计",
  "/admin/product": "商品管理",
  "/admin/seckill": "秒杀管理",
  "/admin/order": "订单管理",
  "/admin/user": "用户管理",
};

const currentPageTitle = computed(() => {
  return pageTitleMap[route.path] || "管理后台";
});

const handleCommand = async (command) => {
  if (command === "logout") {
    try {
      await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      });
      await userStore.logout();
      router.push({ name: "Login" });
    } catch {
      // 用户取消
    }
  }
};
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
}

.admin-sidebar {
  background-color: #304156;

  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: bold;
      color: #fff;
      margin: 0;
    }
  }

  .sidebar-menu {
    border-right: none;

    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
    }
  }
}

.admin-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .user-avatar {
        background: $primary-color;
      }

      .username {
        font-size: 14px;
        color: $text-primary;
      }
    }
  }
}

.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
