<template>
  <div class="admin-layout">
    <el-container class="admin-container">
      <el-aside width="220px" class="admin-aside">
        <div class="admin-logo">
          <el-icon :size="24"><Management /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
        >
          <el-menu-item index="/admin/pending-items">
            <el-icon><Document /></el-icon>
            <span>待审核物品</span>
          </el-menu-item>
          <el-menu-item index="/admin/category">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/comments">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container class="admin-main-container">
        <el-header class="admin-header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-button type="primary" link @click="goHome">
              <el-icon><House /></el-icon> 返回前台
            </el-button>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  return route.path
})

const currentPageTitle = computed(() => {
  return route.meta.title || '管理后台'
})

const goHome = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.admin-layout {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.admin-container {
  height: 100%;
}

.admin-aside {
  background: #304156;
  display: flex;
  flex-direction: column;
  
  .admin-logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background: #263445;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
  }
  
  .admin-menu {
    border-right: none;
    flex: 1;
    
    .el-menu-item {
      height: 50px;
      line-height: 50px;
    }
  }
}

.admin-main-container {
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.admin-header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  
  .header-left {
    .el-breadcrumb {
      font-size: 14px;
    }
  }
  
  .header-right {
    .el-button {
      font-size: 14px;
    }
  }
}

.admin-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
