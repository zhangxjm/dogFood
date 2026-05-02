<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">后台管理系统</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-input v-model="loginForm.code" placeholder="请输入验证码" style="width: 60%" @keyup.enter="handleLogin">
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
          <div class="login-code" @click="getCode">
            <img :src="codeImg" alt="验证码" class="code-img" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getCaptcha } from '@/api/auth'
import { User, Lock, Key } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const codeImg = ref('')

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  code: '',
  uuid: '',
  rememberMe: false
})

const loginRules = reactive({
  username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
  password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
})

const getCode = () => {
  getCaptcha().then(res => {
    codeImg.value = res.data.img
    loginForm.uuid = res.data.uuid
  })
}

const handleLogin = () => {
  loginFormRef.value.validate(valid => {
    if (valid) {
      loading.value = true
      userStore.login(loginForm).then(() => {
        ElMessage.success('登录成功')
        router.push('/')
      }).catch(() => {
        loading.value = false
        getCode()
      })
    }
  })
}

onMounted(() => {
  getCode()
})
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 400px;
  background: #fff;
  border-radius: 10px;
  padding: 30px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 500;
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .el-input {
    height: 45px;
    
    input {
      height: 45px;
    }
  }
}

.login-code {
  display: inline-block;
  width: 35%;
  margin-left: 5%;
  cursor: pointer;
  vertical-align: middle;
  text-align: center;
  
  .code-img {
    width: 100%;
    height: 40px;
    border-radius: 4px;
  }
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
}
</style>
