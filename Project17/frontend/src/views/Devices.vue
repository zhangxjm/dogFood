<template>
  <div class="page-container">
    <div class="page-header">
      <h2>设备管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增设备
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="设备名称">
          <el-input v-model="filterForm.deviceName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="离线" :value="0" />
            <el-option label="在线" :value="1" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="deviceCode" label="设备编码" width="140" />
      <el-table-column prop="deviceName" label="设备名称" width="200" />
      <el-table-column prop="deviceType" label="设备类型" width="120" />
      <el-table-column prop="location" label="安装位置" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="installDate" label="安装日期" width="120" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="loadData"
      @size-change="loadData"
    />

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备编码" prop="deviceCode">
          <el-input v-model="form.deviceCode" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="form.deviceType" placeholder="请选择" style="width: 100%">
            <el-option label="数控车床" value="数控车床" />
            <el-option label="铣床" value="铣床" />
            <el-option label="磨床" value="磨床" />
            <el-option label="钻床" value="钻床" />
            <el-option label="工业机器人" value="工业机器人" />
            <el-option label="传送带" value="传送带" />
            <el-option label="注塑机" value="注塑机" />
            <el-option label="冲压机" value="冲压机" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">离线</el-radio>
            <el-radio :value="1">在线</el-radio>
            <el-radio :value="2">故障</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="温度阈值" prop="temperatureThreshold">
          <el-input-number v-model="form.temperatureThreshold" :min="0" :max="200" />
        </el-form-item>
        <el-form-item label="电压范围" prop="voltageMin">
          <el-col :span="10">
            <el-input-number v-model="form.voltageMin" :min="0" :max="500" placeholder="最小值" />
          </el-col>
          <el-col :span="4" class="text-center">-</el-col>
          <el-col :span="10">
            <el-input-number v-model="form.voltageMax" :min="0" :max="500" placeholder="最大值" />
          </el-col>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deviceApi } from '../utils/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const filterForm = reactive({
  deviceName: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  deviceCode: '',
  deviceName: '',
  deviceType: '',
  location: '',
  status: 1,
  temperatureThreshold: 80,
  voltageMin: 200,
  voltageMax: 240
})

const rules = {
  deviceCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }]
}

const dialogTitle = ref('新增设备')

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '离线', 1: '在线', 2: '故障' }
  return map[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...filterForm
    }
    const res = await deviceApi.page(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (e) {
    console.error('加载数据失败:', e)
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.deviceName = ''
  filterForm.status = null
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  Object.assign(form, {
    id: null,
    deviceCode: '',
    deviceName: '',
    deviceType: '',
    location: '',
    status: 1,
    temperatureThreshold: 80,
    voltageMin: 200,
    voltageMax: 240
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该设备？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deviceApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await deviceApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await deviceApi.save(form)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('提交失败:', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.filter-card {
  margin-bottom: 20px;
}

.text-center {
  text-align: center;
  line-height: 32px;
}
</style>
