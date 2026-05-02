<template>
  <n-layout has-sider style="height: 100vh">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <n-menu
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="currentMenu"
        @update:value="handleMenuClick"
      />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered style="display: flex; justify-content: space-between; align-items: center; padding: 0 24px">
        <div style="font-size: 18px; font-weight: bold">数据管理后台</div>
        <div style="display: flex; align-items: center; gap: 16px">
          <n-avatar round>
            <template #icon>
              <n-icon :component="Person" />
            </template>
          </n-avatar>
          <span v-if="authStore.user">{{ authStore.user.username }}</span>
          <n-button quaternary @click="handleLogout">
            <template #icon>
              <n-icon :component="LogOutOutline" />
            </template>
            退出
          </n-button>
        </div>
      </n-layout-header>
      <n-layout-content content-style="padding: 24px; min-height: calc(100vh - 64px)">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { h } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { NIcon } from 'naive-ui'
import { 
  HomeOutline, 
  GridOutline, 
  LogOutOutline,
  Person
} from '@vicons/ionicons5'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

const currentMenu = computed(() => {
  console.log('[MainLayout] Current route:', route.path, 'name:', route.name)
  
  if (route.name === 'Dashboard') return 'dashboard'
  if (route.name === 'Data') return 'data'
  
  if (route.path === '/dashboard') return 'dashboard'
  if (route.path === '/data') return 'data'
  
  return 'dashboard'
})

function renderIcon(icon) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = [
  {
    label: '数据概览',
    key: 'dashboard',
    icon: renderIcon(HomeOutline)
  },
  {
    label: '数据管理',
    key: 'data',
    icon: renderIcon(GridOutline)
  }
]

function handleMenuClick(key) {
  console.log('[MainLayout] Menu clicked:', key)
  
  if (key === 'dashboard') {
    router.push({ name: 'Dashboard' })
  } else if (key === 'data') {
    router.push({ name: 'Data' })
  }
}

function handleLogout() {
  authStore.logout()
}
</script>
