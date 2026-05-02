<template>
  <view class="visitors-page">
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
        待访问
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'completed' }"
        @click="activeTab = 'completed'"
      >
        已访问
      </view>
    </view>
    
    <view class="visitor-list">
      <view 
        class="visitor-item" 
        v-for="visitor in filteredVisitors" 
        :key="visitor.id"
      >
        <view class="visitor-header">
          <view class="visitor-name">{{ visitor.visitor_name }}</view>
          <view class="visitor-status" :class="getStatusClass(visitor.status)">
            {{ getStatusText(visitor.status) }}
          </view>
        </view>
        <view class="visitor-info">
          <view class="info-row">
            <text class="info-label">联系电话：</text>
            <text class="info-value">{{ visitor.visitor_phone }}</text>
          </view>
          <view class="info-row" v-if="visitor.visitor_id_card">
            <text class="info-label">身份证号：</text>
            <text class="info-value">{{ visitor.visitor_id_card }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">访问日期：</text>
            <text class="info-value">{{ visitor.visit_date }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">访问时间：</text>
            <text class="info-value">{{ visitor.visit_time }}</text>
          </view>
          <view class="info-row" v-if="visitor.purpose">
            <text class="info-label">来访事由：</text>
            <text class="info-value">{{ visitor.purpose }}</text>
          </view>
        </view>
        <view class="visitor-footer">
          <view class="register-time">登记时间：{{ formatDate(visitor.created_at) }}</view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredVisitors.length === 0">
        <view class="empty-icon">👥</view>
        <view class="empty-text">暂无访客登记记录</view>
      </view>
    </view>
    
    <view class="fab-btn" @click="goToRegister">
      <text class="fab-icon">+</text>
      <text class="fab-text">登记访客</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const visitors = ref<any[]>([])

const filteredVisitors = computed(() => {
  if (activeTab.value === 'all') {
    return visitors.value
  }
  return visitors.value.filter(visitor => visitor.status === activeTab.value)
})

function getStatusClass(status: string) {
  const classMap: Record<string, string> = {
    'pending': 'status-pending',
    'completed': 'status-completed'
  }
  return classMap[status] || ''
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    'pending': '待访问',
    'completed': '已访问'
  }
  return textMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function goToRegister() {
  uni.navigateTo({
    url: '/pages/visitor-register/visitor-register'
  })
}

async function loadVisitors() {
  try {
    const res = await request.get('/api/visitors')
    if (Array.isArray(res)) {
      visitors.value = res
    }
  } catch (error) {
    console.error('加载访客记录失败', error)
  }
}

onMounted(() => {
  loadVisitors()
})
</script>

<style scoped>
.visitors-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  gap: 16rpx;
}

.tab-item {
  padding: 12rpx 24rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 40rpx;
}

.tab-item.active {
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  font-weight: 500;
}

.visitor-list {
  padding: 20rpx;
}

.visitor-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.visitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.visitor-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.visitor-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.visitor-status.status-pending {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.visitor-status.status-completed {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.visitor-info {
  margin-bottom: 16rpx;
}

.info-row {
  display: flex;
  margin-bottom: 12rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 26rpx;
  color: #999;
  width: 160rpx;
  flex-shrink: 0;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.visitor-footer {
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.register-time {
  font-size: 24rpx;
  color: #999;
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

.fab-btn {
  position: fixed;
  bottom: 40rpx;
  right: 30rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  padding: 24rpx 36rpx;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 122, 255, 0.3);
  z-index: 100;
}

.fab-icon {
  font-size: 36rpx;
  font-weight: 300;
}

.fab-text {
  font-size: 28rpx;
  font-weight: 500;
}
</style>
