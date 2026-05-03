<template>
  <div class="category-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增分类
          </el-button>
        </div>
      </template>
      
      <el-table
        :data="categoryList"
        style="width: 100%"
        v-loading="loading"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="scope">
            <span v-if="!scope.row.parent">📁 </span>
            <span v-else>&nbsp;&nbsp;&nbsp;&nbsp;└─ </span>
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column prop="code" label="分类编码" width="150" />
        <el-table-column prop="book_count" label="图书数量" width="100" />
        <el-table-column prop="is_active_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_active ? 'success' : 'danger'">
              {{ scope.row.is_active_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              :type="scope.row.is_active ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.is_active ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="categoryForm.code" placeholder="请输入分类编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parent">
          <el-select
            v-model="categoryForm.parent"
            placeholder="请选择父级分类（选填）"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="category in parentCategories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="is_active">
          <el-radio-group v-model="categoryForm.is_active">
            <el-radio :value="true">启用</el-radio>
            <el-radio :value="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryList, createCategory, updateCategory } from '@/api/book'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const categoryFormRef = ref(null)
const categoryList = ref([])

const categoryForm = reactive({
  id: null,
  name: '',
  code: '',
  parent: null,
  is_active: true,
  description: '',
})

const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
}

const parentCategories = computed(() => {
  return categoryList.value.filter(c => !c.parent)
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.code = ''
  categoryForm.parent = null
  categoryForm.is_active = true
  categoryForm.description = ''
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  categoryForm.id = row.id
  categoryForm.name = row.name
  categoryForm.code = row.code
  categoryForm.parent = row.parent
  categoryForm.is_active = row.is_active
  categoryForm.description = row.description || ''
  dialogVisible.value = true
}

const handleToggleStatus = (row) => {
  const action = row.is_active ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该分类吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await updateCategory(row.id, { is_active: !row.is_active })
        ElMessage.success(`${action}成功`)
        fetchCategories()
      } catch (error) {
        console.error('操作失败:', error)
      }
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  if (!categoryFormRef.value) return
  
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateCategory(categoryForm.id, categoryForm)
          ElMessage.success('更新成功')
        } else {
          await createCategory(categoryForm)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchCategories()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-manage-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
