<template>
  <div class="services-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>服务管理</span>
          <div class="header-actions">
            <el-select v-model="categoryId" placeholder="服务分类" clearable @change="loadServices">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
            <el-input
              v-model="keyword"
              placeholder="搜索服务名称"
              style="width: 200px;"
              clearable
              @keyup.enter="loadServices"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增服务
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="services" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="服务名称" />
        <el-table-column prop="categoryName" label="服务分类" />
        <el-table-column prop="price" label="价格">
          <template #default="scope">
            <span style="color: #F56C6C;">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              link
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="loadServices"
        @current-change="loadServices"
      />
      
      <el-empty v-if="services.length === 0 && !loading" description="暂无服务" />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑服务' : '新增服务'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择服务分类" style="width: 100%;">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="计价单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：次、小时、天" />
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入服务描述"
          />
        </el-form-item>
        <el-form-item label="服务状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const services = ref([])
const categories = ref([])
const loading = ref(false)
const keyword = ref('')
const categoryId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  price: 0,
  unit: '次',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择服务分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入服务价格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入计价单位', trigger: 'blur' }]
}

onMounted(async () => {
  await loadCategories()
  loadServices()
})

async function loadCategories() {
  try {
    const res = await api.get('/api/public/categories')
    if (res.data.code === 200) {
      categories.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadServices() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    if (categoryId.value) {
      params.categoryId = categoryId.value
    }
    
    const res = await api.get('/api/public/services', { params })
    if (res.data.code === 200) {
      services.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    price: 0,
    unit: '次',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  
  await ElMessageBox.confirm(`确定要${action}该服务吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await api.put(`/api/admin/services/${row.id}/status?status=${newStatus}`)
    if (res.data.code === 200) {
      ElMessage.success(`${action}成功`)
      loadServices()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  
  submitting.value = true
  try {
    let res
    if (isEdit.value) {
      res = await api.put(`/api/admin/services/${form.id}`, form)
    } else {
      res = await api.post('/api/admin/services', form)
    }
    
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadServices()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.services-container {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
