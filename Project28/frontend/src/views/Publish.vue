<template>
  <div class="publish-page">
    <Header />
    
    <div class="main-content container">
      <div class="publish-card">
        <div class="page-header">
          <el-icon :size="24" color="#667eea"><DocumentAdd /></el-icon>
          <h2>发布物品信息</h2>
        </div>
        
        <el-form
          ref="publishFormRef"
          :model="publishForm"
          :rules="publishRules"
          class="publish-form"
          label-position="top"
        >
          <el-form-item label="物品类型" prop="item_type">
            <el-radio-group v-model="publishForm.item_type" size="large">
              <el-radio-button value="lost">
                <el-icon><Warning /></el-icon>
                寻物启事
              </el-radio-button>
              <el-radio-button value="found">
                <el-icon><Box /></el-icon>
                招领启事
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="16">
              <el-form-item label="物品标题" prop="title">
                <el-input
                  v-model="publishForm.title"
                  placeholder="请输入物品标题（如：丢失黑色钱包）"
                  size="large"
                  :maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="物品分类" prop="category">
                <el-select
                  v-model="publishForm.category"
                  placeholder="选择分类"
                  size="large"
                  style="width: 100%"
                >
                  <el-option
                    v-for="cat in categories"
                    :key="cat.id"
                    :label="cat.name"
                    :value="cat.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="详细描述" prop="description">
            <el-input
              v-model="publishForm.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述物品的特征、丢失/发现的具体情况等"
              size="large"
              :maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="丢失/发现地点" prop="location">
                <el-input
                  v-model="publishForm.location"
                  placeholder="如：图书馆二楼、食堂门口"
                  size="large"
                  prefix-icon="Location"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="丢失/发现时间" prop="item_time">
                <el-date-picker
                  v-model="publishForm.item_time"
                  type="datetime"
                  placeholder="选择时间"
                  size="large"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人" prop="contact_name">
                <el-input
                  v-model="publishForm.contact_name"
                  placeholder="请输入您的姓名"
                  size="large"
                  prefix-icon="User"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contact_phone">
                <el-input
                  v-model="publishForm.contact_phone"
                  placeholder="请输入联系电话"
                  size="large"
                  prefix-icon="Phone"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="悬赏金额（选填）" prop="reward">
            <el-input-number
              v-model="publishForm.reward"
              :min="0"
              :precision="2"
              size="large"
              style="width: 200px"
            />
            <span class="unit">元</span>
          </el-form-item>
          
          <el-form-item label="物品图片（选填）">
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              list-type="picture-card"
              :limit="3"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">
                  最多上传3张图片，支持 jpg、png 格式
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="submit-btn"
              @click="handleSubmit"
            >
              发布
            </el-button>
            <el-button size="large" @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { itemApi } from '@/api/item'
import Header from '@/components/Header.vue'

const router = useRouter()
const userStore = useUserStore()

const publishFormRef = ref(null)
const loading = ref(false)
const categories = ref([])
const fileList = ref([])

const uploadUrl = '/api/item/items/'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const publishForm = reactive({
  item_type: 'lost',
  title: '',
  category: null,
  description: '',
  location: '',
  item_time: null,
  contact_name: '',
  contact_phone: '',
  reward: 0,
  image1: null,
  image2: null,
  image3: null
})

const publishRules = {
  item_type: [
    { required: true, message: '请选择物品类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入物品标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度为2-50个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, message: '描述至少10个字符', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入地点', trigger: 'blur' }
  ],
  item_time: [
    { required: true, message: '请选择时间', trigger: 'change' }
  ],
  contact_name: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  contact_phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const fetchCategories = async () => {
  try {
    const res = await itemApi.getCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const handleUploadSuccess = (response, file, fileList) => {
  const index = fileList.indexOf(file)
  if (index === 0) publishForm.image1 = response.data?.url || file.response?.data?.url
  else if (index === 1) publishForm.image2 = response.data?.url || file.response?.data?.url
  else if (index === 2) publishForm.image3 = response.data?.url || file.response?.data?.url
}

const handleRemove = (file, fileList) => {
  const index = fileList.indexOf(file)
  if (index === 0) publishForm.image1 = null
  else if (index === 1) publishForm.image2 = null
  else if (index === 2) publishForm.image3 = null
}

const handleSubmit = async () => {
  const valid = await publishFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const data = { ...publishForm }
    if (!data.category) delete data.category
    
    const res = await itemApi.create(data)
    if (res.code === 200) {
      ElMessage.success(res.message || '发布成功，等待管理员审核')
      router.push('/profile/my-items')
    }
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  publishFormRef.value.resetFields()
  fileList.value = []
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.publish-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  padding: 30px 0;
}

.publish-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  
  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

.publish-form {
  .el-form-item__label {
    font-weight: 500;
    color: #303133;
  }
  
  .unit {
    margin-left: 10px;
    color: #909399;
  }
  
  .submit-btn {
    width: 140px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
  }
}
</style>
