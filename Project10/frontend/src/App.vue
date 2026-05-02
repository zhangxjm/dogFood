<template>
  <div class="app-container">
    <el-container>
      <el-header v-if="showHeader" class="app-header">
        <div class="header-content">
          <div class="logo" @click="router.push('/courses')">
            <el-icon :size="32" color="#409EFF"><VideoCamera /></el-icon>
            <span class="logo-text">在线课程平台</span>
          </div>
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            class="nav-menu"
            router
          >
            <el-menu-item index="/courses">课程中心</el-menu-item>
            <el-menu-item index="/user/enrollments" v-if="isLoggedIn">我的课程</el-menu-item>
            <el-menu-item index="/teacher/courses" v-if="isTeacher">教师后台</el-menu-item>
          </el-menu>
          <div class="user-actions">
            <template v-if="isLoggedIn">
              <el-dropdown @command="handleCommand">
                <span class="user-dropdown">
                  <el-avatar :size="36" :src="userInfo?.avatar">
                    {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <span class="username">{{ userInfo?.username }}</span>
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="router.push('/login')">登录</el-button>
              <el-button @click="router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isTeacher = computed(() => userStore.isTeacher)
const userInfo = computed(() => userStore.userInfo)

const showHeader = computed(() => !['/login', '/register'].includes(route.path))

const activeMenu = computed(() => {
  if (route.path.startsWith('/user')) return '/user/enrollments'
  if (route.path.startsWith('/teacher')) return '/teacher/courses'
  if (route.path.startsWith('/courses')) return '/courses'
  return route.path
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/courses')
      }).catch(() => {})
      break
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.app-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-left: 10px;
}

.nav-menu {
  border-bottom: none;
  flex: 1;
  margin-left: 40px;
}

.nav-menu :deep(.el-menu-item) {
  height: 64px;
  line-height: 64px;
  border-bottom: none;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #606266;
  font-size: 14px;
}

.app-main {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
}
</style>
