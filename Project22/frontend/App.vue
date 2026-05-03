<template>
  <div class="app-container">
    <router-view />
    <van-tabbar v-model="activeTab" route fixed placeholder>
      <van-tabbar-item to="/index" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/courses" icon="calendar-o">课程</van-tabbar-item>
      <van-tabbar-item to="/checkin" icon="location-o">签到</van-tabbar-item>
      <van-tabbar-item to="/member" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeTab = computed(() => {
  const path = route.path
  if (path === '/index') return 0
  if (path === '/courses') return 1
  if (path === '/checkin') return 2
  if (path === '/member') return 3
  return 0
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}

.app-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-container {
  padding-bottom: 50px;
}

.van-tabbar {
  z-index: 100;
}

:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --text-color: #333333;
  --text-color-secondary: #666666;
  --text-color-placeholder: #999999;
  --border-color: #dcdfe6;
  --background-color: #f5f7fa;
}
</style>
