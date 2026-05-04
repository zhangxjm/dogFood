<template>
  <div class="banners-container">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增轮播图
      </el-button>
    </div>

    <el-card>
      <el-table :data="banners" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="图片" width="200">
          <template #default="scope">
            <el-image
              :src="scope.row.image_url"
              :preview-src-list="[scope.row.image_url]"
              fit="cover"
              style="width: 180px; height: 80px;"
            />
          </template>
        </el-table-column>
        <el-table-column prop="link_url" label="链接" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.link_url">{{ scope.row.link_url }}</span>
            <span v-else class="text-muted">无链接</span>
          </template>
        </el-table-column>
        <el-table-column prop="order" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.is_active"
              @change="handleToggleActive(scope.row)"
              active-text="启用"
              inactive-text="禁用"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button text type="primary" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-popconfirm
              title="确定要删除这个轮播图吗？"
              @confirm="handleDelete(scope.row.id)"
            >
              <template #reference>
                <el-button text type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="banners.length === 0 && !loading" description="暂无轮播图" />
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑轮播图' : '新增轮播图'"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="image_url">
          <el-input v-model="formData.image_url" placeholder="请输入图片URL" />
          <div class="image-preview" v-if="formData.image_url">
            <el-image
              :src="formData.image_url"
              fit="cover"
              style="width: 300px; height: 150px;"
            />
          </div>
        </el-form-item>
        <el-form-item label="链接URL">
          <el-input v-model="formData.link_url" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.order" :min="0" :max="100" />
          <el-text size="small" type="info" style="margin-left: 10px;">
            数字越小排序越靠前
          </el-text>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.is_active" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBanners, getAllBanners, createBanner, updateBanner, deleteBanner } from '@/api/banners'
import { ElMessage } from 'element-plus'

const banners = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const currentId = ref(null)

const formData = reactive({
  title: '',
  image_url: '',
  link_url: '',
  order: 0,
  is_active: true
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  image_url: [{ required: true, message: '请输入图片URL', trigger: 'blur' }]
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const fetchBanners = async () => {
  loading.value = true
  try {
    const res = await getAllBanners()
    banners.value = res.data
  } catch (error) {
    ElMessage.error('获取轮播图列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  formData.title = ''
  formData.image_url = ''
  formData.link_url = ''
  formData.order = 0
  formData.is_active = true
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  formData.title = row.title
  formData.image_url = row.image_url
  formData.link_url = row.link_url || ''
  formData.order = row.order
  formData.is_active = row.is_active
  dialogVisible.value = true
}

const handleToggleActive = async (row) => {
  try {
    await updateBanner(row.id, { is_active: row.is_active })
    ElMessage.success(row.is_active ? '已启用' : '已禁用')
  } catch (error) {
    row.is_active = !row.is_active
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await deleteBanner(id)
    ElMessage.success('删除成功')
    fetchBanners()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateBanner(currentId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createBanner(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchBanners()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchBanners()
})
</script>

<style scoped>
.banners-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.image-preview {
  margin-top: 10px;
}

.text-muted {
  color: #909399;
  font-size: 13px;
}
</style>
