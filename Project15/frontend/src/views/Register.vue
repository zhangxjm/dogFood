<template>
  <div class="register-container">
    <n-card class="register-card">
      <template #header>
        <div class="register-header">
          <n-icon size="40" color="#1890ff">
            <iosBriefcase />
          </n-icon>
          <n-text depth="1" class="register-title">用户注册</n-text>
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
            placeholder="请输入密码（至少6位）"
            show-password-on="click"
            :disabled="loading"
          >
            <template #prefix>
              <n-icon>
                <iosLockClosed />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item label="确认密码" path="confirmPassword">
          <n-input
            v-model:value="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password-on="click"
            :disabled="loading"
          >
            <template #prefix>
              <n-icon>
                <iosLockClosed />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item label="角色" path="role">
          <n-radio-group v-model:value="formData.role">
            <n-radio :value="1">求职者</n-radio>
            <n-radio :value="2">企业HR</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-form-item>
          <n-button
            type="primary"
            block
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </n-button>
        </n-form-item>

        <n-form-item>
          <n-space justify="space-between" style="width: 100%;">
            <n-text depth="3">已有账号？</n-text>
            <n-button text type="primary" @click="goLogin">立即登录</n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import {
  iosBriefcase,
  iosPerson,
  iosLockClosed
} from '@vicons/ionicons5'
import { userApi } from '@/api/user'

const router = useRouter()
const message = useMessage()

const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: 1
})

const validateConfirmPassword = (rule, value) => {
  if (value !== formData.password) {
    return new Error('两次输入的密码不一致')
  }
  return true
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    const res = await userApi.register({
      username: formData.username,
      password: formData.password,
      role: formData.role
    })
    if (res.code === 200) {
      message.success('注册成功，请登录')
      router.push('/login')
    }
  } catch (e) {
    console.error('注册失败:', e)
    message.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 450px;
}

.register-header {
  text-align: center;
  margin-bottom: 8px;
}

.register-title {
  font-size: 24px;
  font-weight: bold;
  margin-left: 12px;
}
</style>
