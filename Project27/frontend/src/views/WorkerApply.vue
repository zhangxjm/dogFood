<template>
  <div class="worker-apply-container">
    <el-card shadow="never">
      <template #header>
        <span>师傅入驻申请</span>
      </template>
      
      <el-alert
        v-if="workerInfo && workerInfo.id"
        :title="getApplyStatusTitle()"
        :type="getApplyStatusType()"
        :description="getApplyStatusDesc()"
        show-icon
        style="margin-bottom: 20px;"
      />
      
      <el-form
        v-if="!workerInfo || !workerInfo.id"
        ref="applyFormRef"
        :model="applyForm"
        :rules="applyRules"
        label-width="120px"
        style="max-width: 600px;"
      >
        <el-form-item label="服务分类" prop="serviceCategoryId">
          <el-select v-model="applyForm.serviceCategoryId" placeholder="请选择服务分类" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="applyForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="身份证正面" prop="idCardFront">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'idCardFront')"
            :before-upload="beforeUpload"
            :limit="1"
            :show-file-list="false"
          >
            <img v-if="applyForm.idCardFront" :src="applyForm.idCardFront" class="upload-image" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
            <div class="upload-tip">点击上传身份证正面照</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="身份证反面" prop="idCardBack">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'idCardBack')"
            :before-upload="beforeUpload"
            :limit="1"
            :show-file-list="false"
          >
            <img v-if="applyForm.idCardBack" :src="applyForm.idCardBack" class="upload-image" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
            <div class="upload-tip">点击上传身份证反面照</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="工作年限" prop="workYears">
          <el-input-number v-model="applyForm.workYears" :min="0" :max="50" />
        </el-form-item>
        
        <el-form-item label="技能描述" prop="skillDesc">
          <el-input
            v-model="applyForm.skillDesc"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的技能特长和工作经验"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitApply">提交申请</el-button>
        </el-form-item>
      </el-form>
      
      <el-empty v-if="workerInfo && workerInfo.id" description="您已提交过申请" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const categories = ref([])
const workerInfo = ref(null)
const applyFormRef = ref(null)
const submitting = ref(false)

const applyForm = reactive({
  serviceCategoryId: null,
  idCard: '',
  idCardFront: '',
  idCardBack: '',
  workYears: 0,
  skillDesc: ''
})

const applyRules = {
  serviceCategoryId: [
    { required: true, message: '请选择服务分类', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  idCardFront: [
    { required: true, message: '请上传身份证正面照', trigger: 'change' }
  ],
  idCardBack: [
    { required: true, message: '请上传身份证反面照', trigger: 'change' }
  ],
  skillDesc: [
    { required: true, message: '请输入技能描述', trigger: 'blur' }
  ]
}

const uploadUrl = '/api/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

onMounted(() => {
  loadCategories()
  loadWorkerInfo()
})

async function loadCategories() {
  try {
    const res = await api.get('/api/public/categories')
    if (res.data.code === 200) {
      categories.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadWorkerInfo() {
  try {
    const res = await api.get('/api/worker/profile')
    if (res.data.code === 200) {
      workerInfo.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

function getApplyStatusTitle() {
  const map = {
    'PENDING': '申请审核中',
    'APPROVED': '申请已通过',
    'REJECTED': '申请已拒绝'
  }
  return map[workerInfo.value?.auditStatus] || '申请状态未知'
}

function getApplyStatusType() {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'error'
  }
  return map[workerInfo.value?.auditStatus] || 'info'
}

function getApplyStatusDesc() {
  if (workerInfo.value?.auditRemark) {
    return workerInfo.value.auditRemark
  }
  const map = {
    'PENDING': '请耐心等待审核，审核通过后即可接单',
    'APPROVED': '恭喜您，您已成为认证师傅，可以开始接单了',
    'REJECTED': '您的申请被拒绝，请重新提交申请'
  }
  return map[workerInfo.value?.auditStatus] || ''
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

function handleUploadSuccess(response, field) {
  if (response.code === 200) {
    applyForm[field] = response.data
  } else {
    ElMessage.error('上传失败')
  }
}

async function submitApply() {
  const valid = await applyFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    const res = await api.post('/api/worker/apply', applyForm)
    if (res.data.code === 200) {
      ElMessage.success('申请提交成功')
      loadWorkerInfo()
    } else {
      ElMessage.error(res.data.message || '提交失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.worker-apply-container {
  max-width: 800px;
  margin: 0 auto;
}

.upload-demo {
  width: 200px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.upload-demo:hover {
  border-color: #409EFF;
}

.upload-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 5px;
}

.upload-tip {
  font-size: 12px;
  color: #8c939d;
}
</style>
