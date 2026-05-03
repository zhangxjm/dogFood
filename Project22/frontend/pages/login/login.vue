<template>
  <div class="login-container">
    <div class="logo-section">
      <div class="logo">
        <span class="logo-text">🏋️</span>
      </div>
      <h1 class="app-title">健身俱乐部</h1>
      <p class="app-desc">健康生活，从这里开始</p>
    </div>

    <div class="form-section">
      <van-form @submit="handleLogin">
        <van-cell-group inset>
          <van-field
            v-model="form.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
            maxlength="11"
            :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
          />
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </van-cell-group>

        <div style="margin: 24px; margin-top: 40px;">
          <van-button round block type="primary" size="large" native-type="submit" :loading="loading">
            登录
          </van-button>
        </div>
      </van-form>

      <div class="form-footer">
        <span class="footer-text">还没有账号？</span>
        <span class="footer-link" @click="goToRegister">立即注册</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import toast from '@/utils/toast'

const router = useRouter()

const form = ref({
  phone: '',
  password: ''
})
const loading = ref(false)

const handleLogin = async (values) => {
  loading.value = true
  
  try {
    const res = await request.post('/members/login', {
      phone: values.phone,
      password: values.password
    })
    
    if (res.success) {
      localStorage.setItem('member', JSON.stringify(res.data))
      localStorage.setItem('memberId', res.data.id)
      toast.success('登录成功')
      
      setTimeout(() => {
        router.push('/index')
      }, 1000)
    }
  } catch (error) {
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #409EFF 0%, #67C23A 100%);
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0 40px;
}

.logo {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-text {
  font-size: 50px;
}

.app-title {
  font-size: 28px;
  font-weight: bold;
  color: #ffffff;
  margin: 0 0 10px 0;
}

.app-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.form-section {
  background: #ffffff;
  border-radius: 24px 24px 0 0;
  padding: 40px 0;
  min-height: calc(100vh - 250px);
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
}

.footer-text {
  font-size: 14px;
  color: #909399;
}

.footer-link {
  font-size: 14px;
  color: #409EFF;
  margin-left: 6px;
  cursor: pointer;
}
</style>
