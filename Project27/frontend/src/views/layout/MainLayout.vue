<template>
  <el-container class="main-container">
    <el-header class="header">
      <div class="logo" @click="goHome">
        <el-icon :size="28"><HomeFilled /></el-icon>
        <span class="logo-text">家政服务平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="nav-menu"
        mode="horizontal"
        background-color="#409EFF"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/home" @click="$router.push('/home')">
          <el-icon><HomeFilled /></el-icon>
          首页
        </el-menu-item>
        <el-menu-item index="/services" @click="$router.push('/services')">
          <el-icon><Service /></el-icon>
          服务列表
        </el-menu-item>
        <el-menu-item index="/workers" @click="$router.push('/workers')">
          <el-icon><User /></el-icon>
          师傅列表
        </el-menu-item>
        <template v-if="userStore.isLoggedIn">
          <el-menu-item index="/orders" @click="$router.push('/orders')">
            <el-icon><List /></el-icon>
            我的订单
          </el-menu-item>
        </template>
      </el-menu>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32">
                <img v-if="userStore.avatar" :src="userStore.avatar" />
                <el-icon v-else><UserFilled /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.realName || userStore.username }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="workerApply" v-if="userStore.role !== 'WORKER' && userStore.role !== 'ADMIN'">
                  师傅入驻
                </el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.role === 'ADMIN'">
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
    <el-footer class="footer">
      <p>家政服务预约管理系统 © 2024</p>
    </el-footer>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  HomeFilled,
  Service,
  User,
  List,
  UserFilled,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function goHome() {
  router.push('/home')
}

function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'workerApply':
      router.push('/worker-apply')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      }).catch(() => {})
      break
  }
}
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  color: #fff;
  cursor: pointer;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  margin-left: 10px;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
  margin-left: 30px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  color: #fff;
  cursor: pointer;
}

.user-name {
  margin-left: 8px;
  margin-right: 5px;
}

.dropdown-icon {
  font-size: 12px;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}

.footer {
  background-color: #303133;
  color: #fff;
  text-align: center;
  padding: 20px;
  font-size: 14px;
}
</style>
