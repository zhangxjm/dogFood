<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="user-info">
            <div class="avatar-section">
              <el-avatar :size="80" class="avatar">
                <el-icon :size="40"><UserFilled /></el-icon>
              </el-avatar>
              <div class="user-role">
                <el-tag :type="userInfo.role === 'admin' ? 'success' : 'primary'">
                  {{ userInfo.role_display || '读者' }}
                </el-tag>
              </div>
            </div>
            <el-divider />
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ userInfo.first_name || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">电话：</span>
              <span class="value">{{ userInfo.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ userInfo.gender_display || '未知' }}</span>
            </div>
            <el-divider />
            <div class="borrow-info">
              <div class="info-item">
                <span class="label">最大可借：</span>
                <span class="value">{{ userInfo.max_borrow_count }} 本</span>
              </div>
              <div class="info-item">
                <span class="label">借阅天数：</span>
                <span class="value">{{ userInfo.borrow_days }} 天</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>编辑信息</span>
              <el-button type="primary" @click="handleSave" :loading="loading">
                保存
              </el-button>
            </div>
          </template>
          
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
            style="max-width: 500px"
          >
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="姓名" prop="first_name">
              <el-input v-model="profileForm.first_name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入电话" />
            </el-form-item>
            <el-form-item label="地址" prop="address">
              <el-input
                v-model="profileForm.address"
                type="textarea"
                :rows="3"
                placeholder="请输入地址"
              />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio value="male">男</el-radio>
                <el-radio value="female">女</el-radio>
                <el-radio value="unknown">未知</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>
          
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="120px"
            style="max-width: 500px"
          >
            <el-form-item label="原密码" prop="old_password">
              <el-input
                v-model="passwordForm.old_password"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="new_password">
              <el-input
                v-model="passwordForm.new_password"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="new_password2">
              <el-input
                v-model="passwordForm.new_password2"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateUserInfo, changePassword } from '@/api/user'

const userStore = useUserStore()
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const passwordLoading = ref(false)

const userInfo = computed(() => userStore.userInfo || {})

const profileForm = reactive({
  email: '',
  first_name: '',
  phone: '',
  address: '',
  gender: 'unknown',
})

const profileRules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
}

const passwordForm = reactive({
  old_password: '',
  new_password: '',
  new_password2: '',
})

const validatePassword2 = (rule, value, callback) => {
  if (value !== passwordForm.new_password) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  old_password: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
  ],
  new_password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  new_password2: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validatePassword2, trigger: 'blur' },
  ],
}

const initForm = () => {
  if (userInfo.value) {
    profileForm.email = userInfo.value.email || ''
    profileForm.first_name = userInfo.value.first_name || ''
    profileForm.phone = userInfo.value.phone || ''
    profileForm.address = userInfo.value.address || ''
    profileForm.gender = userInfo.value.gender || 'unknown'
  }
}

const handleSave = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await updateUserInfo(profileForm)
        userStore.updateUserInfo(profileForm)
        ElMessage.success('保存成功')
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await changePassword(passwordForm)
        ElMessage.success('密码修改成功')
        passwordForm.old_password = ''
        passwordForm.new_password = ''
        passwordForm.new_password2 = ''
      } catch (error) {
        console.error('密码修改失败:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

onMounted(() => {
  initForm()
})
</script>

<style scoped>
.profile-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar {
  margin-bottom: 10px;
  background-color: #409eff;
}

.info-item {
  display: flex;
  padding: 8px 0;
}

.info-item .label {
  width: 80px;
  color: #909399;
}

.info-item .value {
  flex: 1;
  color: #303133;
}

.borrow-info {
  background-color: #f5f7fa;
  padding: 10px 15px;
  border-radius: 4px;
}
</style>
