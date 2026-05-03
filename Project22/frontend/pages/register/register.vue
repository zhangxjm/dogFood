<template>
  <div class="register-container">
    <div class="header">
      <h1 class="title">注册账号</h1>
      <p class="subtitle">成为会员，享受更多优惠</p>
    </div>

    <div class="form-section">
      <van-form @submit="handleRegister">
        <van-cell-group inset>
          <van-field
            v-model="form.name"
            name="name"
            label="姓名"
            placeholder="请输入真实姓名"
            :rules="[{ required: true, message: '请输入姓名' }]"
          />
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
            label="设置密码"
            placeholder="请设置6-16位密码"
            :rules="[{ required: true, message: '请输入密码' }, { min: 6, message: '密码长度至少6位' }]"
          />
          <van-field
            v-model="form.confirmPassword"
            type="password"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[{ required: true, message: '请确认密码' }]"
          />
        </van-cell-group>

        <div style="margin: 24px; margin-top: 40px;">
          <van-button round block type="primary" size="large" native-type="submit" :loading="loading">
            注册
          </van-button>
        </div>
      </van-form>

      <div class="form-footer">
        <span class="footer-text">已有账号？</span>
        <span class="footer-link" @click="goToLogin">立即登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import toast from '@/utils/toast'

const router = useRouter()

const form = reactive({
  name: '',
  phone: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)

const handleRegister = async (values) => {
  if (values.password !== values.confirmPassword) {
    toast.error('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  
  try {
    const res = await request.post('/members/register', {
      name: values.name,
      phone: values.phone,
      password: values.password
    })
    
    if (res.success) {
      localStorage.setItem('member', JSON.stringify(res.data))
      localStorage.setItem('memberId', res.data.id)
      toast.success('注册成功')
      
      setTimeout(() => {
        router.push('/index')
      }, 1000)
    }
  } catch (error) {
    console.error('注册失败', error)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.back()
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  padding: 40px 24px 50px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #ffffff;
  margin: 0 0 10px 0;
}

.subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.form-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 30px 0;
  margin: -30px 16px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
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
