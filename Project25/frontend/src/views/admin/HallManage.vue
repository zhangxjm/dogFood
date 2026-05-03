<template>
  <div class="hall-manage">
    <div class="page-header">
      <h2>放映厅管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>
        添加放映厅
      </el-button>
    </div>

    <el-card>
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="影院">
          <el-select v-model="filters.cinema_id" placeholder="选择影院" clearable @change="fetchHalls">
            <el-option
              v-for="cinema in cinemas"
              :key="cinema.id"
              :label="cinema.name"
              :value="cinema.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="halls" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="放映厅名称" min-width="150" />
        <el-table-column prop="cinema_name" label="所属影院" min-width="200" />
        <el-table-column prop="rows" label="座位排数" width="120">
          <template #default="{ row }">
            {{ row.rows }} 排
          </template>
        </el-table-column>
        <el-table-column prop="cols" label="座位列数" width="120">
          <template #default="{ row }">
            {{ row.cols }} 列
          </template>
        </el-table-column>
        <el-table-column label="总座位数" width="120">
          <template #default="{ row }">
            {{ row.rows * row.cols }} 个
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑放映厅' : '添加放映厅'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属影院" prop="cinema_id">
          <el-select v-model="form.cinema_id" placeholder="请选择影院" style="width: 100%;">
            <el-option
              v-for="cinema in cinemas"
              :key="cinema.id"
              :label="cinema.name"
              :value="cinema.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="放映厅名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入放映厅名称" />
        </el-form-item>
        <el-form-item label="座位排数" prop="rows">
          <el-input-number v-model="form.rows" :min="1" :max="20" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="座位列数" prop="cols">
          <el-input-number v-model="form.cols" :min="1" :max="20" style="width: 100%;" />
        </el-form-item>
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          description="放映厅添加后会自动生成对应数量的座位，座位数量 = 排数 × 列数"
        />
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
import { ElMessage } from 'element-plus'
import { api } from '../../utils/request'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const halls = ref([])
const cinemas = ref([])

const filters = reactive({
  cinema_id: null
})

const defaultForm = {
  id: null,
  cinema_id: null,
  name: '',
  rows: 8,
  cols: 10
}

const form = reactive({ ...defaultForm })

const rules = {
  cinema_id: [{ required: true, message: '请选择影院', trigger: 'change' }],
  name: [{ required: true, message: '请输入放映厅名称', trigger: 'blur' }],
  rows: [{ required: true, message: '请输入座位排数', trigger: 'blur' }],
  cols: [{ required: true, message: '请输入座位列数', trigger: 'blur' }]
}

const fetchHalls = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.cinema_id) {
      params.cinema_id = filters.cinema_id
    }
    const response = await api.get('/admin/halls', { params })
    halls.value = response.data
  } catch (error) {
    console.error('Failed to fetch halls:', error)
  } finally {
    loading.value = false
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
          ElMessage.warning('放映厅信息无法编辑，如需修改请联系管理员')
        } else {
          await api.post('/admin/halls', form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchHalls()
      } catch (error) {
        console.error('Failed to submit:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  fetchHalls()
  fetchCinemas()
})
</script>

<style scoped>
.hall-manage {
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
