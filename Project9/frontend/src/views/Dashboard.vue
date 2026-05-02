<template>
  <div>
    <n-page-header title="数据概览" :subtitle="`欢迎回来，${authStore.user?.username || '用户'}`" />
    
    <n-grid :cols="4" :x-gap="24" :y-gap="24" style="margin-top: 24px">
      <n-gi>
        <n-card>
          <n-statistic label="总数据量" :value="stats.total || 0">
            <template #prefix>
              <n-icon :component="FolderOutline" />
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="草稿状态" :value="stats.by_status?.draft || 0">
            <template #prefix>
              <n-icon :component="DocumentTextOutline" />
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="进行中" :value="stats.by_status?.active || 0">
            <template #prefix>
              <n-icon :component="PlayCircleOutline" />
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card>
          <n-statistic label="已完成" :value="stats.by_status?.completed || 0">
            <template #prefix>
              <n-icon :component="CheckmarkCircleOutline" />
            </template>
          </n-statistic>
        </n-card>
      </n-gi>
    </n-grid>
    
    <n-card title="快捷操作" style="margin-top: 24px">
      <n-space>
        <n-button type="primary" @click="goToData">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          数据管理
        </n-button>
        <n-button @click="goToData">
          <template #icon>
            <n-icon :component="GridOutline" />
          </template>
          查看数据列表
        </n-button>
      </n-space>
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dataApi } from '@/api/data'
import { NIcon } from 'naive-ui'
import { h } from 'vue'
import {
  FolderOutline,
  DocumentTextOutline,
  PlayCircleOutline,
  CheckmarkCircleOutline,
  AddOutline,
  GridOutline
} from '@vicons/ionicons5'

const router = useRouter()
const authStore = useAuthStore()

const stats = ref({
  total: 0,
  by_status: {}
})

async function fetchStats() {
  try {
    const response = await dataApi.getStats()
    stats.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

function goToData() {
  router.push({ name: 'Data' })
}

onMounted(() => {
  fetchStats()
})
</script>
