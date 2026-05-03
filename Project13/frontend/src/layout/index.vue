<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <div class="logo">
          <h2>物流追踪系统</h2>
        </div>
        
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/track">
          <el-icon><Search /></el-icon>
          <span>物流追踪</span>
        </el-menu-item>
        
        <el-menu-item index="/order/create">
          <el-icon><Plus /></el-icon>
          <span>快递下单</span>
        </el-menu-item>
        
        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        
        <el-sub-menu index="admin" v-if="isAdmin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>管理员后台</span>
          </template>
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon>
            <span>订单审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/logistics">
            <el-icon><Van /></el-icon>
            <span>物流管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentPageTitle">{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon class="user-icon"><UserFilled /></el-icon>
              {{ userInfo.username }}
              <el-icon class="user-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

userStore.initUser()

const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  return route.meta.title
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.clearUser()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  
  .el-menu {
    border-right: none;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-bottom: 1px solid #3a4a5b;
      
      h2 {
        color: #fff;
        font-size: 18px;
        margin: 0;
      }
    }
    
    .el-menu-item, .el-sub-menu__title {
      &:hover {
        background-color: #263445 !important;
      }
    }
  }
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .header-left {
    .el-breadcrumb {
      font-size: 14px;
    }
  }
  
  .header-right {
    .user-info {
      cursor: pointer;
      display: flex;
      align-items: center;
      color: #606266;
      
      .user-icon {
        margin: 0 5px;
      }
    }
  }
}
</style>
