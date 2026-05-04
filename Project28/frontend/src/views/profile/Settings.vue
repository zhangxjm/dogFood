<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>个人设置</h2>
    </div>
    
    <el-form
      ref="settingsFormRef"
      :model="settingsForm"
      :rules="settingsRules"
      label-width="100px"
      class="settings-form"
    >
      <el-form-item label="头像">
        <el-avatar :size="80" class="avatar-preview">
          <img v-if="settingsForm.avatar" :src="settingsForm.avatar" />
          <el-icon v-else :size="36"><User /></el-icon>
        </el-avatar>
        <el-upload
          class="avatar-uploader"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          accept="image/*"
        >
          <el-button type="primary" link>更换头像</el-button>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="用户名">
        <el-input v-model="settingsForm.username" disabled />
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="settingsForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="settingsForm.phone" placeholder="请输入手机号" />
      </el-form-item>
      
      <el-form-item label="性别">
        <el-radio-group v-model="settingsForm.gender">
          <el-radio value="male">男</el-radio>
          <el-radio value="female">女</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="学号/工号">
        <el-input v-model="settingsForm.student_id" placeholder="请输入学号或工号" />
      </el-form-item>
      
      <el-form-item label="院系">
        <el-input v-model="settingsForm.department" placeholder="请输入院系" />
      </el-form-item>
      
      <el-form-item label="个人简介">
        <el-input
          v-model="settingsForm.bio"
          type="textarea"
          :rows="3"
          placeholder="请输入个人简介"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          保存修改
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()

const settingsFormRef = ref(null)
const loading = ref(false)

const settingsForm = reactive({
  username: '',
  email: '',
  phone: '',
  gender: 'male',
  student_id: '',
  department: '',
  bio: '',
  avatar: ''
})

const uploadUrl = '/api/user/info/'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const settingsRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleAvatarSuccess = (response) => {
  settingsForm.avatar = response.data?.url || response.url
  ElMessage.success('头像上传成功')
}

const fetchUserInfo = async () => {
  if (userStore.userInfo) {
    Object.assign(settingsForm, {
      username: userStore.userInfo.username || '',
      email: userStore.userInfo.email || '',
      phone: userStore.userInfo.phone || '',
      gender: userStore.userInfo.gender || 'male',
      student_id: userStore.userInfo.student_id || '',
      department: userStore.userInfo.department || '',
      bio: userStore.userInfo.bio || '',
      avatar: userStore.userInfo.avatar || ''
    })
  }
}

const handleSubmit = async () => {
  const valid = await settingsFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const data = { ...settingsForm }
    delete data.username
    
    const success = await userStore.updateUserInfo(data)
    if (success) {
      ElMessage.success('修改成功')
    }
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style lang="scss" scoped>
.settings-page {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .page-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #ebeef5;
    
    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
  
  .settings-form {
    max-width: 500px;
    
    .avatar-preview {
      vertical-align: middle;
      background: #f5f7fa;
    }
    
    .avatar-uploader {
      margin-left: 16px;
      vertical-align: middle;
    }
  }
}
</style>
