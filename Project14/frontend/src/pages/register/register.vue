<template>
  <view class="register-page">
    <view class="page-header">
      <text class="page-title">业主注册</text>
      <text class="page-subtitle">填写信息完成注册</text>
    </view>
    
    <view class="form-section">
      <view class="input-group">
        <view class="input-item">
          <label>用户名</label>
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
        <view class="input-item">
          <label>确认密码</label>
          <input 
            type="password" 
            v-model="form.confirmPassword" 
            placeholder="请再次输入密码"
          />
        </view>
        <view class="input-item">
          <label>姓名</label>
          <input 
            type="text" 
            v-model="form.name" 
            placeholder="请输入真实姓名"
          />
        </view>
        <view class="input-item">
          <label>手机号</label>
          <input 
            type="number" 
            v-model="form.phone" 
            placeholder="请输入手机号"
            maxlength="11"
          />
        </view>
        <view class="input-item">
          <label>楼栋</label>
          <input 
            type="text" 
            v-model="form.building" 
            placeholder="如：1栋"
          />
        </view>
        <view class="input-item">
          <label>单元</label>
          <input 
            type="text" 
            v-model="form.unit" 
            placeholder="如：1单元"
          />
        </view>
        <view class="input-item">
          <label>房号</label>
          <input 
            type="text" 
            v-model="form.room_number" 
            placeholder="如：101"
          />
        </view>
      </view>
      
      <view class="btn-primary" @click="handleRegister">
        注册
      </view>
      
      <view class="login-link">
        已有账号？<text class="link" @click="goToLogin">立即登录</text>
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
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  building: '',
  unit: '',
  room_number: ''
})

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    uni.showToast({
      title: '请填写用户名和密码',
      icon: 'none'
    })
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    uni.showToast({
      title: '两次密码输入不一致',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.name || !form.value.phone) {
    uni.showToast({
      title: '请填写姓名和手机号',
      icon: 'none'
    })
    return
  }
  
  try {
    await userStore.register({
      username: form.value.username,
      password: form.value.password,
      name: form.value.name,
      phone: form.value.phone,
      building: form.value.building,
      unit: form.value.unit,
      room_number: form.value.room_number
    })
    
    uni.showToast({
      title: '注册成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    console.error('注册失败', error)
  }
}

function goToLogin() {
  uni.navigateBack()
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60rpx;
}

.page-header {
  padding: 60rpx 40rpx 40rpx;
  text-align: center;
}

.page-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.page-subtitle {
  font-size: 28rpx;
  color: #999;
}

.form-section {
  padding: 0 30rpx;
}

.login-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 28rpx;
  color: #999;
}

.link {
  color: #007AFF;
}
</style>
