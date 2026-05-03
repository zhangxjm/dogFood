<template>
  <div class="movie-manage">
    <div class="page-header">
      <h2>电影管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        添加电影
      </el-button>
    </div>

    <el-card>
      <el-table :data="movies" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="海报" width="100">
          <template #default="{ row }">
            <el-image :src="row.poster" fit="cover" style="width: 60px; height: 80px; border-radius: 4px;" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="电影名称" min-width="150" />
        <el-table-column prop="category_name" label="分类" width="100" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'showing' ? 'success' : 'warning'">
              {{ row.status === 'showing' ? '正在热映' : '即将上映' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="热门" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.is_hot" type="danger">热门</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">
            <span>{{ row.rating || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑电影' : '添加电影'"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="电影名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入电影名称" />
        </el-form-item>
        <el-form-item label="海报链接" prop="poster">
          <el-input v-model="form.poster" placeholder="请输入海报图片链接" />
        </el-form-item>
        <el-form-item label="电影分类" prop="category_id">
          <el-select v-model="form.category_id" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="form.duration" :min="1" :max="300" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="上映日期" prop="release_date">
          <el-date-picker
            v-model="form.release_date"
            type="date"
            placeholder="选择上映日期"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="导演" prop="director">
          <el-input v-model="form.director" placeholder="请输入导演" />
        </el-form-item>
        <el-form-item label="主演" prop="actors">
          <el-input v-model="form.actors" placeholder="请输入主演" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input-number v-model="form.rating" :min="0" :max="10" :precision="1" :step="0.5" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="showing">正在热映</el-radio>
            <el-radio value="coming">即将上映</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否热门" prop="is_hot">
          <el-switch v-model="form.is_hot" />
        </el-form-item>
        <el-form-item label="剧情简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入剧情简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../utils/request'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const movies = ref([])
const categories = ref([])

const defaultForm = {
  id: null,
  title: '',
  poster: '',
  description: '',
  duration: 120,
  release_date: '',
  director: '',
  actors: '',
  category_id: null,
  status: 'showing',
  is_hot: false,
  rating: 0
}

const form = reactive({ ...defaultForm })

const rules = {
  title: [{ required: true, message: '请输入电影名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

const fetchMovies = async () => {
  loading.value = true
  try {
    const response = await api.get('/admin/movies')
    movies.value = response.data
  } catch (error) {
    console.error('Failed to fetch movies:', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await api.get('/admin/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, defaultForm)
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await api.put(`/admin/movies/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/admin/movies', form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchMovies()
      } catch (error) {
        console.error('Failed to submit:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除电影 "${row.title}" 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await api.delete(`/admin/movies/${row.id}`)
    ElMessage.success('删除成功')
    fetchMovies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

onMounted(() => {
  fetchMovies()
  fetchCategories()
})
</script>

<style scoped>
.movie-manage {
  max-width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.text-muted {
  color: #999;
}
</style>
