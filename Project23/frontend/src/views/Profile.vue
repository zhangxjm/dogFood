<template>
  <el-container>
    <Header />
    <el-main>
      <div class="container">
        <el-row :gutter="30">
          <el-col :span="8">
            <el-card>
              <div class="user-card">
                <el-avatar
                  :size="100"
                  :src="userInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                />
                <h2 class="user-name">{{ userInfo.nickname || userInfo.username }}</h2>
                <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'primary'">
                  {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
                </el-tag>
                <div class="user-stats">
                  <div class="stat-item">
                    <span class="stat-value">{{ stats.productCount || 0 }}</span>
                    <span class="stat-label">发布商品</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">{{ stats.favoriteCount || 0 }}</span>
                    <span class="stat-label">收藏商品</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">{{ stats.transactionCount || 0 }}</span>
                    <span class="stat-label">交易记录</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="16">
            <el-card>
              <template #header>
                <div class="tab-header">
                  <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                    <el-tab-pane label="基本信息" name="info" />
                    <el-tab-pane label="修改密码" name="password" />
                    <el-tab-pane label="账户设置" name="settings" />
                  </el-tabs>
                </div>
              </template>
              
              <div v-if="activeTab === 'info'" class="tab-content">
                <el-form
                  ref="infoFormRef"
                  :model="infoForm"
                  :rules="infoRules"
                  label-width="100px"
                  style="max-width: 500px"
                >
                  <el-form-item label="头像">
                    <el-upload
                      :action="uploadAction"
                      :headers="uploadHeaders"
                      :on-success="handleAvatarSuccess"
                      :show-file-list="false"
                    >
                      <el-avatar
                        :size="80"
                        :src="infoForm.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                        class="upload-avatar"
                      >
                        <el-icon><Plus /></el-icon>
                      </el-avatar>
                    </el-upload>
                  </el-form-item>
                  <el-form-item label="用户名" prop="username">
                    <el-input v-model="infoForm.username" disabled />
                  </el-form-item>
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
                  </el-form-item>
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                  </el-form-item>
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="saveInfo">保存修改</el-button>
                    <el-button @click="resetInfoForm">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
              
              <div v-if="activeTab === 'password'" class="tab-content">
                <el-form
                  ref="passwordFormRef"
                  :model="passwordForm"
                  :rules="passwordRules"
                  label-width="120px"
                  style="max-width: 500px"
                >
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input
                      v-model="passwordForm.oldPassword"
                      type="password"
                      placeholder="请输入原密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="请输入新密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="请确认新密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="updatePassword">修改密码</el-button>
                    <el-button @click="resetPasswordForm">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
              
              <div v-if="activeTab === 'settings'" class="tab-content">
                <el-descriptions title="账户信息" :column="1" border>
                  <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
                  <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
                  <el-descriptions-item label="角色">
                    <el-tag :type="userInfo.role === 'admin' ? 'danger' : 'primary'">
                      {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="账户状态">
                    <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                      {{ userInfo.status === 1 ? '正常' : '禁用' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="注册时间">
                    {{ formatTime(userInfo.createTime) }}
                  </el-descriptions-item>
                </el-descriptions>
                
                <el-divider />
                
                <div class="danger-zone">
                  <h3>危险操作</h3>
                  <el-button type="danger" @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUser, updatePassword } from '@/api/user'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const activeTab = ref('info')
const infoFormRef = ref(null)
const passwordFormRef = ref(null)

const uploadAction = '/api/file/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}

const userInfo = ref({})
const stats = ref({
  productCount: 0,
  favoriteCount: 0,
  transactionCount: 0
})

const infoForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度为 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data || {}
    
    infoForm.username = userInfo.value.username || ''
    infoForm.nickname = userInfo.value.nickname || ''
    infoForm.phone = userInfo.value.phone || ''
    infoForm.email = userInfo.value.email || ''
    infoForm.avatar = userInfo.value.avatar || ''
  } catch (e) {
    ElMessage.error('获取用户信息失败')
  }
}

onMounted(() => {
  fetchUserInfo()
})

const handleTabChange = (tab) => {
  activeTab.value = tab
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200 && response.data) {
    infoForm.avatar = response.data
    ElMessage.success('头像上传成功')
  }
}

const saveInfo = async () => {
  if (!infoFormRef.value) return
  
  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateUser({
          nickname: infoForm.nickname,
          phone: infoForm.phone || undefined,
          email: infoForm.email || undefined,
          avatar: infoForm.avatar || undefined
        })
        
        ElMessage.success('信息更新成功')
        fetchUserInfo()
      } catch (e) {
        console.error('更新失败', e)
      }
    }
  })
}

const resetInfoForm = () => {
  infoForm.nickname = userInfo.value.nickname || ''
  infoForm.phone = userInfo.value.phone || ''
  infoForm.email = userInfo.value.email || ''
  infoForm.avatar = userInfo.value.avatar || ''
}

const updatePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功，请重新登录')
        handleLogout()
      } catch (e) {
        console.error('密码修改失败', e)
      }
    }
  })
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userInfo')
  ElMessage.success('退出成功')
  router.push('/home')
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.user-name {
  margin: 15px 0 10px 0;
  color: #303133;
}

.user-stats {
  display: flex;
  width: 100%;
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.tab-header {
  margin: -20px -20px 0 -20px;
  padding: 0 20px;
}

.tab-content {
  padding-top: 20px;
}

.upload-avatar {
  cursor: pointer;
  transition: all 0.3s;
}

.upload-avatar:hover {
  opacity: 0.8;
}

.danger-zone {
  margin-top: 20px;
}

.danger-zone h3 {
  margin-bottom: 15px;
  color: #f56c6c;
}
</style>
