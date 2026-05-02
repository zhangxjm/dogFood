<template>
  <view class="login-page">
    <view class="login-header">
      <view class="logo">
        <text class="logo-icon">🏠</text>
      </view>
      <view class="login-title">小区物业系统</view>
      <view class="login-subtitle">便捷生活，贴心服务</view>
    </view>
    
    <view class="login-form">
      <view class="input-group">
        <view class="input-item">
          <label>账号</label>
          <input 
            type="text" 
            v-model="form.username" 
            placeholder="请输入用户名"
          />
        </view>
        <view class="input-item">
          <label>密码</label>
          <input 
            type="password" 
            v-model="form.password" 
            placeholder="请输入密码"
          />
        </view>
      </view>
      
      <view class="btn-primary" @click="handleLogin">
        登录
      </view>
      
      <view class="login-actions">
        <view class="action-item" @click="goToRegister">
          立即注册
        </view>
        <view class="action-item">
          忘记密码？
        </view>
      </view>
    </view>
    
    <view class="quick-login">
      <view class="quick-title">测试账号</view>
      <view class="quick-list">
        <view class="quick-item" @click="quickLogin('admin', 'admin123')">
          <text class="quick-icon">👑</text>
          <text class="quick-text">管理员</text>
        </view>
        <view class="quick-item" @click="quickLogin('owner1', '123456')">
          <text class="quick-icon">👤</text>
          <text class="quick-text">业主</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

function quickLogin(username: string, password: string) {
  form.value.username = username
  form.value.password = password
}

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    uni.showToast({
      title: '请输入账号和密码',
      icon: 'none'
    })
    return
  }
  
  try {
    const res = await userStore.login(form.value.username, form.value.password)
    
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      if (res.user.role === 'admin') {
        uni.reLaunch({
          url: '/pages/admin/index/index'
        })
      } else {
        uni.reLaunch({
          url: '/pages/index/index'
        })
      }
    }, 1000)
  } catch (error: any) {
    console.error('登录失败', error)
  }
}

function goToRegister() {
  uni.navigateTo({
    url: '/pages/register/register'
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #007AFF 0%, #0056CC 100%);
  padding: 0 40rpx;
}

.login-header {
  padding-top: 160rpx;
  padding-bottom: 100rpx;
  text-align: center;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 40rpx;
}

.logo-icon {
  font-size: 80rpx;
}

.login-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 16rpx;
}

.login-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.login-form {
  background-color: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}

.login-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
}

.action-item {
  font-size: 28rpx;
  color: #007AFF;
}

.quick-login {
  margin-top: 60rpx;
}

.quick-title {
  text-align: center;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 30rpx;
}

.quick-list {
  display: flex;
  justify-content: center;
  gap: 60rpx;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.quick-icon {
  font-size: 60rpx;
}

.quick-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}
</style>
