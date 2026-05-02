<template>
  <div class="register-container">
    <n-card title="用户注册" style="width: 400px">
      <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="form.username"
            placeholder="请输入用户名"
            @keyup.enter="handleRegister"
          />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input
            v-model:value="form.email"
            placeholder="请输入邮箱（可选）"
          />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            placeholder="请输入密码"
            show-password-on="click"
            @keyup.enter="handleRegister"
          />
        </n-form-item>
        <n-form-item label="确认密码" path="confirmPassword">
          <n-input
            v-model:value="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password-on="click"
            @keyup.enter="handleRegister"
          />
        </n-form-item>
        <n-form-item>
          <n-space justify="space-between" style="width: 100%">
            <n-text tag="a" @click="goToLogin">已有账号？去登录</n-text>
            <n-button type="primary" :loading="loading" @click="handleRegister">
              注册
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const message = useMessage()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value) => {
  if (value !== form.password) {
    return new Error('两次输入的密码不一致')
  }
  return true
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 80, message: '用户名长度为3-80个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 128, message: '密码长度为6-128个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function handleRegister() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  
  loading.value = true
  try {
    const registerData = {
      username: form.username,
      password: form.password
    }
    if (form.email) {
      registerData.email = form.email
    }
    
    await authStore.register(registerData)
    message.success('注册成功，请登录')
    router.push({ name: 'Login' })
  } catch (error) {
    const errMsg = error.response?.data?.message || '注册失败'
    message.error(errMsg)
  } finally {
    loading.value = false
  }
}

function goToLogin() {
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>
