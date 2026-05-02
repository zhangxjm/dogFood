<template>
  <view class="admin-page">
    <view class="header">
      <view class="header-top">
        <view class="user-info">
          <view class="avatar">
            <text>{{ userStore.userInfo?.name?.charAt(0) || '管' }}</text>
          </view>
          <view class="user-detail">
            <view class="user-name">{{ userStore.userInfo?.name || '管理员' }}</view>
            <view class="user-role">物业管理员</view>
          </view>
        </view>
        <view class="logout-btn" @click="handleLogout">
          <text>退出</text>
        </view>
      </view>
    </view>
    
    <view class="stats-section">
      <view class="stats-grid">
        <view class="stat-item" @click="goToPage('/pages/admin/users/users')">
          <view class="stat-value">{{ stats.total_users || 0 }}</view>
          <view class="stat-label">业主总数</view>
        </view>
        <view class="stat-item" @click="goToPage('/pages/admin/bills/bills')">
          <view class="stat-value">{{ stats.unpaid_bills || 0 }}</view>
          <view class="stat-label">待缴账单</view>
        </view>
        <view class="stat-item" @click="goToPage('/pages/admin/repairs/repairs')">
          <view class="stat-value">{{ stats.pending_repairs || 0 }}</view>
          <view class="stat-label">待处理报修</view>
        </view>
        <view class="stat-item" @click="goToPage('/pages/admin/suggestions/suggestions')">
          <view class="stat-value">{{ stats.pending_suggestions || 0 }}</view>
          <view class="stat-label">待回复建议</view>
        </view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToPage('/pages/admin/bills/bills')">
        <view class="menu-icon bill">💳</view>
        <view class="menu-content">
          <view class="menu-title">缴费管理</view>
          <view class="menu-desc">管理业主账单</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/admin/repairs/repairs')">
        <view class="menu-icon repair">🔧</view>
        <view class="menu-content">
          <view class="menu-title">工单管理</view>
          <view class="menu-desc">处理业主报修</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/admin/notices/notices')">
        <view class="menu-icon notice">📢</view>
        <view class="menu-content">
          <view class="menu-title">公告管理</view>
          <view class="menu-desc">发布小区公告</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/admin/suggestions/suggestions')">
        <view class="menu-icon suggestion">💬</view>
        <view class="menu-content">
          <view class="menu-title">建议处理</view>
          <view class="menu-desc">处理业主反馈</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/admin/users/users')">
        <view class="menu-icon user">👥</view>
        <view class="menu-content">
          <view class="menu-title">业主管理</view>
          <view class="menu-desc">查看业主信息</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/admin/visitors/visitors')">
        <view class="menu-icon visitor">📋</view>
        <view class="menu-content">
          <view class="menu-title">访客记录</view>
          <view class="menu-desc">查看访客登记</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
    </view>
    
    <view class="exit-section">
      <view class="exit-btn" @click="goToClient">
        进入业主端
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { request } from '@/utils/request'

const userStore = useUserStore()

const stats = ref({
  total_users: 0,
  unpaid_bills: 0,
  pending_repairs: 0,
  pending_suggestions: 0
})

function goToPage(url: string) {
  uni.navigateTo({ url })
}

function goToClient() {
  uni.reLaunch({
    url: '/pages/index/index'
  })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    }
  })
}

async function loadStats() {
  try {
    const res = await request.get('/api/dashboard/stats')
    if (res) {
      stats.value = {
        total_users: res.total_users || 0,
        unpaid_bills: res.unpaid_bills || 0,
        pending_repairs: res.pending_repairs || 0,
        pending_suggestions: res.pending_suggestions || 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  userStore.initFromStorage()
  loadStats()
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.header {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  padding: 0 30rpx;
  padding-bottom: 80rpx;
}

.header-top {
  padding-top: 80rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.avatar text {
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
}

.user-detail {
  color: #fff;
}

.user-name {
  font-size: 34rpx;
  font-weight: bold;
  margin-bottom: 6rpx;
}

.user-role {
  font-size: 24rpx;
  opacity: 0.8;
}

.logout-btn {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
}

.logout-btn text {
  color: #fff;
  font-size: 26rpx;
}

.stats-section {
  background-color: #fff;
  margin: -40rpx 30rpx 30rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
}

.stat-item {
  width: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.stat-item:nth-child(1),
.stat-item:nth-child(2) {
  border-bottom: 1rpx solid #f0f0f0;
}

.stat-item:nth-child(1),
.stat-item:nth-child(3) {
  border-right: 1rpx solid #f0f0f0;
}

.stat-item:nth-child(3),
.stat-item:nth-child(4) {
  border-bottom: none;
}

.stat-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #007AFF;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 26rpx;
  color: #999;
}

.menu-section {
  background-color: #fff;
  margin: 0 30rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  margin-right: 24rpx;
}

.menu-icon.bill {
  background-color: #FFF2E8;
}

.menu-icon.repair {
  background-color: #E6F7FF;
}

.menu-icon.notice {
  background-color: #FFF7E6;
}

.menu-icon.suggestion {
  background-color: #FFF1F0;
}

.menu-icon.user {
  background-color: #F6FFED;
}

.menu-icon.visitor {
  background-color: #F9F0FF;
}

.menu-content {
  flex: 1;
}

.menu-title {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.menu-desc {
  font-size: 24rpx;
  color: #999;
}

.menu-arrow {
  font-size: 32rpx;
  color: #ccc;
}

.exit-section {
  padding: 40rpx 30rpx;
  margin-top: 40rpx;
}

.exit-btn {
  background-color: #fff;
  color: #007AFF;
  text-align: center;
  padding: 28rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
  font-weight: 500;
}
</style>
