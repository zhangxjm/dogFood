<template>
  <view class="index-page">
    <view class="header">
      <view class="header-top">
        <view class="user-info" @click="goToProfile">
          <view class="avatar">
            <text>{{ userStore.userInfo?.name?.charAt(0) || '用' }}</text>
          </view>
          <view class="user-detail">
            <view class="user-name">{{ userStore.userInfo?.name || '用户' }}</view>
            <view class="user-room">
              {{ userStore.userInfo?.building || '--' }}栋{{ userStore.userInfo?.unit || '--' }}单元{{ userStore.userInfo?.room_number || '--' }}
            </view>
          </view>
        </view>
        <view class="admin-entry" v-if="userStore.isAdmin" @click="goToAdmin">
          <text class="admin-icon">⚙️</text>
          <text>管理后台</text>
        </view>
      </view>
    </view>
    
    <view class="quick-actions">
      <view class="action-item" @click="goToPage('/pages/bills/bills')">
        <view class="action-icon bill">💳</view>
        <text class="action-text">缴费</text>
      </view>
      <view class="action-item" @click="goToPage('/pages/repair-apply/repair-apply')">
        <view class="action-icon repair">🔧</view>
        <text class="action-text">报修</text>
      </view>
      <view class="action-item" @click="goToPage('/pages/notices/notices')">
        <view class="action-icon notice">📢</view>
        <text class="action-text">公告</text>
      </view>
      <view class="action-item" @click="goToPage('/pages/visitor-register/visitor-register')">
        <view class="action-icon visitor">👥</view>
        <text class="action-text">访客</text>
      </view>
      <view class="action-item" @click="goToPage('/pages/suggestion-apply/suggestion-apply')">
        <view class="action-icon suggestion">💬</view>
        <text class="action-text">建议</text>
      </view>
      <view class="action-item" @click="goToPage('/pages/repairs/repairs')">
        <view class="action-icon history">📋</view>
        <text class="action-text">记录</text>
      </view>
    </view>
    
    <view class="notice-section" v-if="latestNotice">
      <view class="section-header">
        <text class="section-title">最新公告</text>
        <text class="section-more" @click="goToPage('/pages/notices/notices')">查看更多 ></text>
      </view>
      <view class="notice-card" @click="goToNoticeDetail(latestNotice.id)">
        <view class="notice-tag" :class="getNoticeTagClass(latestNotice.notice_type)">
          {{ getNoticeTypeName(latestNotice.notice_type) }}
        </view>
        <view class="notice-content">
          <view class="notice-title">{{ latestNotice.title }}</view>
          <view class="notice-desc">{{ latestNotice.content.substring(0, 50) }}...</view>
        </view>
      </view>
    </view>
    
    <view class="card-section">
      <view class="section-header">
        <text class="section-title">待办事项</text>
      </view>
      
      <view class="todo-item" v-if="unpaidCount > 0" @click="goToPage('/pages/bills/bills')">
        <view class="todo-icon warning">📄</view>
        <view class="todo-content">
          <view class="todo-title">待缴账单</view>
          <view class="todo-desc">您有 {{ unpaidCount }} 笔账单待支付</view>
        </view>
        <view class="todo-badge">{{ unpaidCount }}</view>
      </view>
      
      <view class="todo-item" v-if="pendingRepairCount > 0" @click="goToPage('/pages/repairs/repairs')">
        <view class="todo-icon info">🔧</view>
        <view class="todo-content">
          <view class="todo-title">处理中的报修</view>
          <view class="todo-desc">您有 {{ pendingRepairCount }} 个报修正在处理</view>
        </view>
        <view class="todo-badge">{{ pendingRepairCount }}</view>
      </view>
      
      <view class="empty-todo" v-if="unpaidCount === 0 && pendingRepairCount === 0">
        <text class="empty-icon">✅</text>
        <text class="empty-text">暂无待办事项</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { request } from '@/utils/request'

const userStore = useUserStore()

const latestNotice = ref<any>(null)
const unpaidCount = ref(0)
const pendingRepairCount = ref(0)

function getNoticeTagClass(type: string) {
  const classMap: Record<string, string> = {
    'important': 'tag-danger',
    'maintenance': 'tag-warning',
    'general': 'tag-primary'
  }
  return classMap[type] || 'tag-primary'
}

function getNoticeTypeName(type: string) {
  const nameMap: Record<string, string> = {
    'important': '重要',
    'maintenance': '维护',
    'general': '通知'
  }
  return nameMap[type] || '通知'
}

function goToPage(url: string) {
  uni.navigateTo({ url })
}

function goToProfile() {
  uni.switchTab({
    url: '/pages/profile/profile'
  })
}

function goToAdmin() {
  uni.reLaunch({
    url: '/pages/admin/index/index'
  })
}

function goToNoticeDetail(id: number) {
  uni.navigateTo({
    url: `/pages/notice-detail/notice-detail?id=${id}`
  })
}

async function loadData() {
  try {
    const notices = await request.get('/api/notices')
    if (Array.isArray(notices) && notices.length > 0) {
      latestNotice.value = notices[0]
    }
    
    const bills = await request.get('/api/bills', { status: 'unpaid' })
    unpaidCount.value = Array.isArray(bills) ? bills.length : 0
    
    const repairs = await request.get('/api/repairs')
    const pendingRepairs = Array.isArray(repairs) 
      ? repairs.filter((r: any) => r.status !== 'completed') 
      : []
    pendingRepairCount.value = pendingRepairs.length
  } catch (error) {
    console.error('加载数据失败', error)
  }
}

onMounted(() => {
  userStore.initFromStorage()
  if (userStore.isLoggedIn) {
    loadData()
  }
})
</script>

<style scoped>
.index-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.header {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  padding: 0 30rpx;
  padding-bottom: 60rpx;
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
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.user-room {
  font-size: 24rpx;
  opacity: 0.8;
}

.admin-entry {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background-color: rgba(255, 255, 255, 0.15);
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  color: #fff;
  font-size: 24rpx;
}

.admin-icon {
  font-size: 28rpx;
}

.quick-actions {
  background-color: #fff;
  margin: -30rpx 30rpx 30rpx;
  border-radius: 24rpx;
  padding: 40rpx 30rpx;
  display: flex;
  flex-wrap: wrap;
}

.action-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.action-item:nth-last-child(-n+3) {
  margin-bottom: 0;
}

.action-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-bottom: 16rpx;
}

.action-icon.bill {
  background-color: #FFF2E8;
}

.action-icon.repair {
  background-color: #E6F7FF;
}

.action-icon.notice {
  background-color: #FFF7E6;
}

.action-icon.visitor {
  background-color: #F6FFED;
}

.action-icon.suggestion {
  background-color: #FFF1F0;
}

.action-icon.history {
  background-color: #F9F0FF;
}

.action-text {
  font-size: 26rpx;
  color: #333;
}

.notice-section {
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #999;
}

.notice-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  gap: 20rpx;
}

.notice-tag {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  height: fit-content;
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.notice-desc {
  font-size: 26rpx;
  color: #999;
  line-height: 1.6;
}

.card-section {
  padding: 0 30rpx;
}

.todo-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.todo-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 24rpx;
}

.todo-icon.warning {
  background-color: #FFF7E6;
}

.todo-icon.info {
  background-color: #E6F7FF;
}

.todo-content {
  flex: 1;
}

.todo-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 8rpx;
}

.todo-desc {
  font-size: 24rpx;
  color: #999;
}

.todo-badge {
  background-color: #ff4d4f;
  color: #fff;
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  min-width: 40rpx;
  text-align: center;
}

.empty-todo {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  background-color: #fff;
  border-radius: 16rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
