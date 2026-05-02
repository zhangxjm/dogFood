<template>
  <view class="admin-users-page">
    <view class="search-section">
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <input 
          v-model="searchKeyword" 
          placeholder="搜索业主姓名、房号"
          class="search-input"
        />
      </view>
    </view>
    
    <view class="user-list">
      <view 
        class="user-item" 
        v-for="user in filteredUsers" 
        :key="user.id"
      >
        <view class="user-header">
          <view class="user-avatar">
            <text>{{ user.name.charAt(0) }}</text>
          </view>
          <view class="user-info">
            <view class="user-name">{{ user.name }}</view>
            <view class="user-room">
              {{ user.building }}栋{{ user.unit }}单元{{ user.room_number }}
            </view>
          </view>
          <view class="user-role" v-if="user.role === 'admin'">
            管理员
          </view>
        </view>
        <view class="user-detail">
          <view class="detail-item">
            <view class="detail-label">用户名</view>
            <view class="detail-value">{{ user.username }}</view>
          </view>
          <view class="detail-item">
            <view class="detail-label">联系电话</view>
            <view class="detail-value">{{ user.phone }}</view>
          </view>
        </view>
        <view class="user-stats" v-if="user.role !== 'admin'">
          <view class="stat-item">
            <view class="stat-value">{{ user.billCount || 0 }}</view>
            <view class="stat-label">账单数</view>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <view class="stat-value">{{ user.repairCount || 0 }}</view>
            <view class="stat-label">报修数</view>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <view class="stat-value">{{ user.suggestionCount || 0 }}</view>
            <view class="stat-label">建议数</view>
          </view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredUsers.length === 0">
        <view class="empty-icon">👥</view>
        <view class="empty-text">暂无业主信息</view>
      </view>
    </view>
    
    <view class="stats-card">
      <view class="stats-title">数据统计</view>
      <view class="stats-grid">
        <view class="stat-item">
          <view class="stat-value">{{ totalUsers }}</view>
          <view class="stat-label">业主总数</view>
        </view>
        <view class="stat-item">
          <view class="stat-value">{{ adminCount }}</view>
          <view class="stat-label">管理员</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { request } from '@/utils/request'

const searchKeyword = ref('')
const users = ref<any[]>([])

const filteredUsers = computed(() => {
  if (!searchKeyword.value) {
    return users.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return users.value.filter(user => 
    user.name.toLowerCase().includes(keyword) ||
    user.room_number.includes(keyword) ||
    user.phone.includes(keyword)
  )
})

const totalUsers = computed(() => {
  return users.value.filter(u => u.role !== 'admin').length
})

const adminCount = computed(() => {
  return users.value.filter(u => u.role === 'admin').length
})

async function loadUsers() {
  try {
    const res = await request.get('/api/admin/users')
    if (Array.isArray(res)) {
      users.value = res
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.search-section {
  padding: 20rpx 30rpx;
  background-color: #fff;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  padding: 20rpx 24rpx;
  border-radius: 40rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.user-list {
  padding: 20rpx;
}

.user-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.user-avatar {
  width: 96rpx;
  height: 96rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.user-avatar text {
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 8rpx;
}

.user-room {
  font-size: 26rpx;
  color: #666;
}

.user-role {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.user-detail {
  padding: 20rpx 0;
  border-top: 1rpx solid #f0f0f0;
  border-bottom: 1rpx solid #f0f0f0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
}

.detail-label {
  font-size: 26rpx;
  color: #999;
}

.detail-value {
  font-size: 26rpx;
  color: #333;
}

.user-stats {
  display: flex;
  padding-top: 20rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #007AFF;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.stat-divider {
  width: 1rpx;
  background-color: #f0f0f0;
  margin: 0 20rpx;
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

.stats-card {
  margin: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.stats-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.stats-grid {
  display: flex;
  gap: 20rpx;
}

.stats-grid .stat-item {
  flex: 1;
  padding: 20rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
}
</style>
