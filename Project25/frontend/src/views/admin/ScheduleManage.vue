<template>
  <div class="schedule-manage">
    <div class="page-header">
      <h2>场次管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        添加场次
      </el-button>
    </div>

    <el-card>
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="电影">
          <el-select v-model="filters.movie_id" placeholder="选择电影" clearable @change="fetchSchedules">
            <el-option
              v-for="movie in movies"
              :key="movie.id"
              :label="movie.title"
              :value="movie.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影院">
          <el-select v-model="filters.cinema_id" placeholder="选择影院" clearable @change="fetchSchedules">
            <el-option
              v-for="cinema in cinemas"
              :key="cinema.id"
              :label="cinema.name"
              :value="cinema.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="schedules" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="movie_title" label="电影" min-width="150" />
        <el-table-column prop="cinema_name" label="影院" min-width="150" />
        <el-table-column prop="hall_name" label="放映厅" width="120" />
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.start_time) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.end_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑场次' : '添加场次'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="电影" prop="movie_id">
          <el-select v-model="form.movie_id" placeholder="请选择电影" style="width: 100%;">
            <el-option
              v-for="movie in movies"
              :key="movie.id"
              :label="movie.title"
              :value="movie.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影院" prop="cinema_id">
          <el-select 
            v-model="form.cinema_id" 
            placeholder="请选择影院" 
            style="width: 100%;"
            @change="handleCinemaChange"
          >
            <el-option
              v-for="cinema in cinemas"
              :key="cinema.id"
              :label="cinema.name"
              :value="cinema.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="放映厅" prop="hall_id">
          <el-select v-model="form.hall_id" placeholder="请选择放映厅" style="width: 100%;">
            <el-option
              v-for="hall in halls"
              :key="hall.id"
              :label="hall.name"
              :value="hall.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="start_time">
          <el-date-picker
            v-model="form.start_time"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_time">
          <el-date-picker
            v-model="form.end_time"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number v-model="form.price" :min="1" :max="500" style="width: 100%;" />
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
const schedules = ref([])
const movies = ref([])
const cinemas = ref([])
const halls = ref([])

const filters = reactive({
  movie_id: null,
  cinema_id: null
})

const defaultForm = {
  id: null,
  movie_id: null,
  cinema_id: null,
  hall_id: null,
  start_time: '',
  end_time: '',
  price: 50
}

const form = reactive({ ...defaultForm })

const rules = {
  movie_id: [{ required: true, message: '请选择电影', trigger: 'change' }],
  hall_id: [{ required: true, message: '请选择放映厅', trigger: 'change' }],
  start_time: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  end_time: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }]
}

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchSchedules = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.movie_id) {
      params.movie_id = filters.movie_id
    }
    if (filters.cinema_id) {
      params.cinema_id = filters.cinema_id
    }
    const response = await api.get('/admin/schedules', { params })
    schedules.value = response.data
  } catch (error) {
    console.error('Failed to fetch schedules:', error)
  } finally {
    loading.value = false
  }
}

const fetchMovies = async () => {
  try {
    const response = await api.get('/movies')
    movies.value = response.data
  } catch (error) {
    console.error('Failed to fetch movies:', error)
  }
}

const fetchCinemas = async () => {
  try {
    const response = await api.get('/admin/cinemas')
    cinemas.value = response.data
  } catch (error) {
    console.error('Failed to fetch cinemas:', error)
  }
}

const fetchHalls = async (cinemaId = null) => {
  try {
    const params = {}
    if (cinemaId) {
      params.cinema_id = cinemaId
    }
    const response = await api.get('/admin/halls', { params })
    halls.value = response.data
  } catch (error) {
    console.error('Failed to fetch halls:', error)
  }
}

const handleCinemaChange = (cinemaId) => {
  form.hall_id = null
  fetchHalls(cinemaId)
}

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, {
      id: row.id,
      movie_id: row.movie_id,
      cinema_id: row.cinema_id,
      hall_id: row.hall_id,
      start_time: row.start_time,
      end_time: row.end_time,
      price: row.price
    })
    if (row.cinema_id) {
      fetchHalls(row.cinema_id)
    }
  } else {
    Object.assign(form, defaultForm)
    halls.value = []
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
          await api.put(`/admin/schedules/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/admin/schedules', form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchSchedules()
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
      `确定要删除该场次吗？`,
      '确认删除',
      { type: 'warning' }
    )
    await api.delete(`/admin/schedules/${row.id}`)
    ElMessage.success('删除成功')
    fetchSchedules()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

onMounted(() => {
  fetchSchedules()
  fetchMovies()
  fetchCinemas()
})
</script>

<style scoped>
.schedule-manage {
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

.filter-form {
  margin-bottom: 20px;
}
</style>
