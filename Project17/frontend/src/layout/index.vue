<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon v-if="isCollapse" :size="32"><Monitor /></el-icon>
        <span v-else class="logo-text">工业设备监控系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#1f2937"
        text-color="#9ca3af"
        active-text-color="#3b82f6"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据大屏</template>
        </el-menu-item>
        <el-menu-item index="/devices">
          <el-icon><Cpu /></el-icon>
          <template #title>设备管理</template>
        </el-menu-item>
        <el-menu-item index="/device-data">
          <el-icon><Document /></el-icon>
          <template #title>历史数据</template>
        </el-menu-item>
        <el-menu-item index="/alarms">
          <el-icon><Warning /></el-icon>
          <template #title>报警管理</template>
        </el-menu-item>
        <el-menu-item index="/work-orders">
          <el-icon><List /></el-icon>
          <template #title>工单管理</template>
        </el-menu-item>
        <el-menu-item index="/operators">
          <el-icon><User /></el-icon>
          <template #title>运维人员</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-badge :value="unhandledAlarmCount" :hidden="unhandledAlarmCount === 0" class="alarm-badge">
            <el-icon class="header-icon"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><Avatar /></el-icon>
              {{ userInfo?.realName || '用户' }}
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
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { alarmApi } from '../utils/api'
import wsClient from '../utils/websocket'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const unhandledAlarmCount = ref(0)
const userInfo = ref(null)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const matched = route.matched[route.matched.length - 1]
  return matched?.meta?.title || ''
})

const loadUnhandledCount = async () => {
  try {
    const res = await alarmApi.unhandledCount()
    unhandledAlarmCount.value = res.data
  } catch (e) {
    console.error('获取未处理报警数失败:', e)
  }
}

const handleAlarmMessage = (data) => {
  if (data && data.status === 0) {
    unhandledAlarmCount.value++
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    wsClient.close()
    router.push('/login')
  }
}

onMounted(() => {
  const userStr = localStorage.getItem('userInfo')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  
  loadUnhandledCount()
  
  wsClient.connect()
  wsClient.on('ALARM', handleAlarmMessage)
})

onUnmounted(() => {
  wsClient.off('ALARM', handleAlarmMessage)
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #1f2937;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #374151;
}

.logo-text {
  margin-left: 10px;
}

:deep(.el-menu) {
  border-right: none;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.page-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  font-size: 20px;
  cursor: pointer;
  color: #6b7280;
}

.alarm-badge {
  margin-right: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #374151;
}

.user-info .el-icon {
  margin-right: 8px;
}

.main {
  background-color: #f3f4f6;
  padding: 20px;
}
</style>
