<template>
  <view class="admin-repairs-page">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'all' }"
        @click="activeTab = 'all'"
      >
        全部
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'pending' }"
        @click="activeTab = 'pending'"
      >
        待处理
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'processing' }"
        @click="activeTab = 'processing'"
      >
        处理中
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'completed' }"
        @click="activeTab = 'completed'"
      >
        已完成
      </view>
    </view>
    
    <view class="repair-list">
      <view 
        class="repair-item" 
        v-for="repair in filteredRepairs" 
        :key="repair.id"
        @click="goToEdit(repair.id)"
      >
        <view class="repair-header">
          <view class="owner-info">
            <view class="owner-name">{{ repair.owner_name }}</view>
            <view class="owner-room">{{ repair.room_number }}</view>
          </view>
          <view class="repair-status" :class="getStatusClass(repair.status)">
            {{ getStatusText(repair.status) }}
          </view>
        </view>
        <view class="repair-type-tag" :class="repair.repair_type">
          {{ repair.repair_type }}
        </view>
        <view class="repair-title">{{ repair.title }}</view>
        <view class="repair-desc">{{ repair.description }}</view>
        <view class="repair-location">
          <text class="location-icon">📍</text>
          <text>{{ repair.location }}</text>
        </view>
        <view class="repair-footer">
          <view class="repair-time">{{ formatDate(repair.created_at) }}</view>
          <view class="action-btn" v-if="repair.status === 'pending' || repair.status === 'processing'">
            处理
          </view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredRepairs.length === 0">
        <view class="empty-icon">🔧</view>
        <view class="empty-text">暂无报修记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const repairs = ref<any[]>([])

const filteredRepairs = computed(() => {
  if (activeTab.value === 'all') {
    return repairs.value
  }
  return repairs.value.filter(repair => repair.status === activeTab.value)
})

function getStatusClass(status: string) {
  const classMap: Record<string, string> = {
    'pending': 'status-pending',
    'processing': 'status-processing',
    'completed': 'status-completed'
  }
  return classMap[status] || ''
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成'
  }
  return textMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function goToEdit(id: number) {
  uni.navigateTo({
    url: `/pages/admin/repair-edit/repair-edit?id=${id}`
  })
}

async function loadRepairs() {
  try {
    const params: any = {}
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    
    const res = await request.get('/api/admin/repairs', params)
    if (Array.isArray(res)) {
      repairs.value = res
    }
  } catch (error) {
    console.error('加载报修记录失败', error)
  }
}

watch(activeTab, () => {
  loadRepairs()
})

onMounted(() => {
  loadRepairs()
})
</script>

<style scoped>
.admin-repairs-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  overflow-x: auto;
  gap: 16rpx;
}

.tab-item {
  padding: 12rpx 24rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 40rpx;
  white-space: nowrap;
}

.tab-item.active {
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  font-weight: 500;
}

.repair-list {
  padding: 20rpx;
}

.repair-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.repair-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.owner-info {
  display: flex;
  flex-direction: column;
}

.owner-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 4rpx;
}

.owner-room {
  font-size: 24rpx;
  color: #999;
}

.repair-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.repair-status.status-pending {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.repair-status.status-processing {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.repair-status.status-completed {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.repair-type-tag {
  display: inline-block;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-bottom: 12rpx;
}

.repair-type-tag.水电 {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.repair-type-tag.家电 {
  background-color: rgba(114, 46, 209, 0.1);
  color: #722ed1;
}

.repair-type-tag.土建 {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.repair-type-tag.其他 {
  background-color: rgba(0, 0, 0, 0.05);
  color: #666;
}

.repair-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.repair-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.repair-location {
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.location-icon {
  margin-right: 8rpx;
}

.repair-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.repair-time {
  font-size: 24rpx;
  color: #999;
}

.action-btn {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  font-size: 26rpx;
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
