<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="user-info">
            <el-avatar :size="100">
              <img v-if="userInfo.avatar" :src="userInfo.avatar" />
              <el-icon v-else :size="50"><UserFilled /></el-icon>
            </el-avatar>
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <p class="role">
              <el-tag :type="getRoleType(userInfo.role)">{{ getRoleText(userInfo.role) }}</el-tag>
            </p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span>个人信息</span>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form
                ref="infoFormRef"
                :model="infoForm"
                :rules="infoRules"
                label-width="100px"
              >
                <el-form-item label="用户名">
                  <el-input :value="userInfo.username" disabled />
                </el-form-item>
                <el-form-item label="角色">
                  <el-tag :type="getRoleType(userInfo.role)">{{ getRoleText(userInfo.role) }}</el-tag>
                </el-form-item>
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="infoForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="头像">
                  <el-upload
                    class="avatar-uploader"
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                  >
                    <el-avatar :size="80">
                      <img v-if="infoForm.avatar" :src="infoForm.avatar" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-avatar>
                  </el-upload>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="saving" @click="saveInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="changingPassword" @click="changePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const activeTab = ref('info')
const userInfo = ref({})
const saving = ref(false)
const changingPassword = ref(false)

const infoFormRef = ref(null)
const passwordFormRef = ref(null)

const infoForm = reactive({
  realName: '',
  phone: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
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
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const uploadUrl = '/api/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

onMounted(() => {
  loadUserInfo()
})

async function loadUserInfo() {
  try {
    const res = await api.get('/api/user/profile')
    if (res.data.code === 200) {
      userInfo.value = res.data.data
      infoForm.realName = res.data.data.realName || ''
      infoForm.phone = res.data.data.phone || ''
      infoForm.avatar = res.data.data.avatar || ''
    }
  } catch (e) {
    console.error(e)
  }
}

function getRoleType(role) {
  const map = {
    'USER': '',
    'WORKER': 'primary',
    'ADMIN': 'danger'
  }
  return map[role] || ''
}

function getRoleText(role) {
  const map = {
    'USER': '普通用户',
    'WORKER': '服务师傅',
    'ADMIN': '管理员'
  }
  return map[role] || role
}

async function saveInfo() {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    const res = await api.put('/api/user/profile', infoForm)
    if (res.data.code === 200) {
      ElMessage.success('保存成功')
      userStore.setUserInfo({
        ...userStore.userInfo,
        realName: infoForm.realName,
        avatar: infoForm.avatar
      })
      loadUserInfo()
    } else {
      ElMessage.error(res.data.message || '保存失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  changingPassword.value = true
  try {
    const res = await api.put('/api/user/password', passwordForm)
    if (res.data.code === 200) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(res.data.message || '修改失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    changingPassword.value = false
  }
}

function handleAvatarSuccess(response) {
  if (response.code === 200) {
    infoForm.avatar = response.data
  } else {
    ElMessage.error('上传失败')
  }
}

function beforeAvatarUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.user-info {
  text-align: center;
  padding: 20px 0;
}

.user-info h3 {
  margin: 15px 0 10px;
  font-size: 20px;
  color: #303133;
}

.role {
  margin: 0;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
}
</style>
