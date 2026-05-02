<template>
  <view class="profile-page">
    <view class="profile-header">
      <view class="user-info" @click="goToEdit">
        <view class="avatar">
          <text>{{ userStore.userInfo?.name?.charAt(0) || '用' }}</text>
        </view>
        <view class="user-detail">
          <view class="user-name">{{ userStore.userInfo?.name || '用户' }}</view>
          <view class="user-room">
            {{ userStore.userInfo?.building || '--' }}栋{{ userStore.userInfo?.unit || '--' }}单元{{ userStore.userInfo?.room_number || '--' }}
          </view>
        </view>
        <view class="arrow-icon">›</view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToPage('/pages/bills/bills')">
        <view class="menu-icon bill">💳</view>
        <view class="menu-content">
          <view class="menu-title">我的账单</view>
          <view class="menu-desc">查看物业费、水电费等</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/repairs/repairs')">
        <view class="menu-icon repair">🔧</view>
        <view class="menu-content">
          <view class="menu-title">我的报修</view>
          <view class="menu-desc">查看报修记录和进度</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @click="goToPage('/pages/notices/notices')">
        <view class="menu-icon notice">📢</view>
        <view class="menu-content">
          <view class="menu-title">小区公告</view>
          <view class="menu-desc">查看最新公告信息</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/suggestions/suggestions')">
        <view class="menu-icon suggestion">💬</view>
        <view class="menu-content">
          <view class="menu-title">投诉建议</view>
          <view class="menu-desc">提出您的宝贵意见</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item" @click="goToPage('/pages/visitors/visitors')">
        <view class="menu-icon visitor">👥</view>
        <view class="menu-content">
          <view class="menu-title">访客登记</view>
          <view class="menu-desc">登记访客信息</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
    </view>
    
    <view class="menu-section" v-if="userStore.isAdmin">
      <view class="menu-item" @click="goToAdmin">
        <view class="menu-icon admin">⚙️</view>
        <view class="menu-content">
          <view class="menu-title">管理后台</view>
          <view class="menu-desc">进入物业管理后台</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item">
        <view class="menu-icon help">❓</view>
        <view class="menu-content">
          <view class="menu-title">帮助中心</view>
          <view class="menu-desc">常见问题解答</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
      
      <view class="menu-item">
        <view class="menu-icon contact">📞</view>
        <view class="menu-content">
          <view class="menu-title">联系物业</view>
          <view class="menu-desc">物业服务热线</view>
        </view>
        <view class="menu-arrow">›</view>
      </view>
    </view>
    
    <view class="logout-section">
      <view class="logout-btn" @click="handleLogout">
        退出登录
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

function goToPage(url: string) {
  uni.navigateTo({ url })
}

function goToEdit() {
  uni.navigateTo({
    url: '/pages/profile-edit/profile-edit'
  })
}

function goToAdmin() {
  uni.reLaunch({
    url: '/pages/admin/index/index'
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

onMounted(() => {
  userStore.initFromStorage()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.profile-header {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  padding: 80rpx 30rpx 40rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.avatar text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.user-detail {
  flex: 1;
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

.arrow-icon {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.6);
}

.menu-section {
  background-color: #fff;
  margin-top: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 30rpx;
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

.menu-icon.visitor {
  background-color: #F6FFED;
}

.menu-icon.admin {
  background-color: #F9F0FF;
}

.menu-icon.help {
  background-color: #E6F7FF;
}

.menu-icon.contact {
  background-color: #FFF7E6;
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

.logout-section {
  padding: 40rpx 30rpx;
  margin-top: 40rpx;
}

.logout-btn {
  background-color: #fff;
  color: #ff4d4f;
  text-align: center;
  padding: 28rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
  font-weight: 500;
}
</style>
