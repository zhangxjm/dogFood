<template>
  <div class="login-container">
    <n-card title="用户登录" style="width: 400px">
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
            @keyup.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            placeholder="请输入密码"
            show-password-on="click"
            @keyup.enter="handleLogin"
          />
        </n-form-item>
        <n-form-item>
          <n-space justify="space-between" style="width: 100%">
            <n-text tag="a" @click="goToRegister">没有账号？去注册</n-text>
            <n-button type="primary" :loading="loading" @click="handleLogin">
              登录
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  
  loading.value = true
  try {
    await authStore.login(form)
    message.success('登录成功')
    
    const redirect = route.query.redirect
    if (redirect && typeof redirect === 'string') {
      console.log('[Login] Redirecting to:', redirect)
      router.push(redirect)
    } else {
      router.push({ name: 'Dashboard' })
    }
  } catch (error) {
    const errMsg = error.response?.data?.message || '登录失败'
    message.error(errMsg)
  } finally {
    loading.value = false
  }
}

function goToRegister() {
  router.push({ name: 'Register' })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>
