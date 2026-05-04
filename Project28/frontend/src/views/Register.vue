<template>
  <div class="register-page">
    <Header />
    
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <el-icon :size="48" color="#667eea"><UserPlus /></el-icon>
          <h2>加入我们</h2>
          <p>注册校园失物招领平台</p>
        </div>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  size="large"
                  prefix-icon="User"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="email">
                <el-input
                  v-model="registerForm.email"
                  placeholder="邮箱"
                  size="large"
                  prefix-icon="Message"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="password2">
                <el-input
                  v-model="registerForm.password2"
                  type="password"
                  placeholder="确认密码"
                  size="large"
                  prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="phone">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="手机号"
                  size="large"
                  prefix-icon="Phone"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="student_id">
                <el-input
                  v-model="registerForm.student_id"
                  placeholder="学号/工号"
                  size="large"
                  prefix-icon="Postcard"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item prop="gender">
            <el-radio-group v-model="registerForm.gender" size="large">
              <el-radio value="male">男</el-radio>
              <el-radio value="female">女</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item prop="department">
            <el-input
              v-model="registerForm.department"
              placeholder="院系/部门（选填）"
              size="large"
              prefix-icon="OfficeBuilding"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="register-btn"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import Header from '@/components/Header.vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  password2: '',
  phone: '',
  student_id: '',
  gender: 'male',
  department: ''
})

const validatePassword2 = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  password2: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePassword2, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  const userData = { ...registerForm }
  delete userData.password2
  
  const success = await userStore.register(userData)
  loading.value = false
  
  if (success) {
    ElMessage.success('注册成功')
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.register-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.register-card {
  width: 100%;
  max-width: 560px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 36px;
  
  h2 {
    font-size: 28px;
    color: #303133;
    margin: 16px 0 8px;
    font-weight: 600;
  }
  
  p {
    color: #909399;
    font-size: 14px;
  }
}

.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .register-btn {
    width: 100%;
    font-size: 16px;
    height: 48px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    
    &:hover {
      opacity: 0.9;
    }
  }
}

.register-footer {
  text-align: center;
  margin-top: 24px;
  color: #909399;
  font-size: 14px;
  
  .link {
    color: #409eff;
    margin-left: 4px;
    
    &:hover {
      color: #667eea;
    }
  }
}
</style>
