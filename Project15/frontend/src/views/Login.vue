<template>
  <div class="login-container">
    <n-card class="login-card">
      <template #header>
        <div class="login-header">
          <n-icon size="40" color="#1890ff">
            <iosBriefcase />
          </n-icon>
          <n-text depth="1" class="login-title">求职招聘系统</n-text>
        </div>
      </template>

      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="formData.username"
            placeholder="请输入用户名"
            :disabled="loading"
          >
            <template #prefix>
              <n-icon>
                <iosPerson />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password-on="click"
            :disabled="loading"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <n-icon>
                <iosLockClosed />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item>
          <n-button
            type="primary"
            block
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </n-button>
        </n-form-item>

        <n-form-item>
          <n-space justify="space-between" style="width: 100%;">
            <n-text depth="3">还没有账号？</n-text>
            <n-button text type="primary" @click="goRegister">立即注册</n-button>
          </n-space>
        </n-form-item>
      </n-form>

      <n-divider />

      <n-text depth="3" class="hint-text">
        测试账号：admin/123456 (管理员) | hr1/123456 (企业HR) | jobseeker1/123456 (求职者)
      </n-text>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessage } from 'naive-ui'
import {
  iosBriefcase,
  iosPerson,
  iosLockClosed
} from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await userStore.login(formData)
    if (res.code === 200) {
      message.success('登录成功')
      const role = res.data.role
      if (role === 1) router.push('/user/dashboard')
      else if (role === 2) router.push('/user/company')
      else if (role === 3) router.push('/user/admin/dashboard')
    }
  } catch (e) {
    console.error('登录失败:', e)
    message.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
}

.login-header {
  text-align: center;
  margin-bottom: 8px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  margin-left: 12px;
}

.hint-text {
  display: block;
  text-align: center;
  font-size: 12px;
}
</style>
