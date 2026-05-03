<template>
  <div id="app">
    <el-container>
      <el-header class="app-header">
        <div class="header-content">
          <router-link to="/" class="logo">
            <el-icon><VideoCamera /></el-icon>
            <span>在线影院</span>
          </router-link>
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            background-color="transparent"
            text-color="#fff"
            active-text-color="#409eff"
            router
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/movies">电影</el-menu-item>
            <el-menu-item index="/orders" v-if="userStore.isLoggedIn">我的订单</el-menu-item>
            <el-menu-item index="/admin/movies" v-if="userStore.isAdmin">管理后台</el-menu-item>
          </el-menu>
          <div class="user-actions">
            <template v-if="userStore.isLoggedIn">
              <span class="username">{{ userStore.username }}</span>
              <el-button type="text" @click="handleLogout" style="color: #fff;">
                退出登录
              </el-button>
            </template>
            <template v-else>
              <el-button type="text" @click="$router.push('/login')" style="color: #fff;">
                登录
              </el-button>
              <el-button type="primary" size="small" @click="$router.push('/register')">
                注册
              </el-button>
            </template>
          </div>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
      <el-footer class="app-footer">
        <p>在线影院售票系统 © 2024</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from './stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
}

.el-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 0;
  height: 60px;
  line-height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  text-decoration: none;
}

.logo .el-icon {
  font-size: 28px;
  color: #409eff;
}

.el-menu--horizontal {
  border-bottom: none;
  flex: 1;
  margin: 0 40px;
}

.el-menu--horizontal > .el-menu-item {
  border-bottom: none;
  height: 60px;
  line-height: 60px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: #fff;
  font-size: 14px;
}

.app-main {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}

.app-footer {
  background-color: #1a1a2e;
  color: #999;
  text-align: center;
  padding: 20px;
  font-size: 14px;
}
</style>
