<template>
  <div class="page-container">
    <div class="page-header">
      <h2>报警管理</h2>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="filterForm.alarmLevel" placeholder="请选择" clearable>
            <el-option label="一般" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="alarmMessage" label="报警信息" min-width="250" />
      <el-table-column prop="alarmType" label="报警类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getAlarmType(row.alarmType)">
            {{ getAlarmTypeText(row.alarmType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alarmLevel" label="级别" width="100">
        <template #default="{ row }">
          <el-tag :type="getLevelType(row.alarmLevel)">
            {{ getLevelText(row.alarmLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="currentValue" label="当前值" width="100" />
      <el-table-column prop="thresholdValue" label="阈值" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'danger' : 'success'">
            {{ row.status === 0 ? '未处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handleUser" label="处理人" width="100" />
      <el-table-column prop="createTime" label="报警时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            link 
            v-if="row.status === 0"
            @click="handleAlarm(row)"
          >
            处理
          </el-button>
          <span v-else class="text-gray">-</span>
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

    <el-dialog v-model="handleDialogVisible" title="处理报警" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="处理人">
          <el-select v-model="handleForm.handleUser" placeholder="请选择处理人" style="width: 100%">
            <el-option v-for="op in operatorList" :key="op.id" :label="op.realName" :value="op.realName" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input 
            v-model="handleForm.handleRemark" 
            type="textarea" 
            :rows="4"
            placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { alarmApi, operatorApi } from '../utils/api'

const loading = ref(false)
const tableData = ref([])
const handleDialogVisible = ref(false)
const operatorList = ref([])
const currentAlarmId = ref(null)

const filterForm = reactive({
  status: null,
  alarmLevel: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const handleForm = reactive({
  handleUser: '',
  handleRemark: ''
})

const getAlarmType = (type) => {
  const map = {
    'TEMPERATURE': 'danger',
    'VOLTAGE_LOW': 'warning',
    'VOLTAGE_HIGH': 'warning',
    'DEVICE_FAULT': 'danger'
  }
  return map[type] || 'info'
}

const getAlarmTypeText = (type) => {
  const map = {
    'TEMPERATURE': '温度',
    'VOLTAGE_LOW': '低压',
    'VOLTAGE_HIGH': '高压',
    'DEVICE_FAULT': '故障'
  }
  return map[type] || type
}

const getLevelType = (level) => {
  const map = { 1: '', 2: 'warning', 3: 'danger' }
  return map[level] || ''
}

const getLevelText = (level) => {
  const map = { 1: '一般', 2: '重要', 3: '紧急' }
  return map[level] || '未知'
}

const loadOperators = async () => {
  try {
    const res = await operatorApi.list()
    operatorList.value = res.data
  } catch (e) {
    console.error('加载运维人员失败:', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      status: filterForm.status,
      alarmLevel: filterForm.alarmLevel
    }
    const res = await alarmApi.page(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total
  } catch (e) {
    console.error('加载数据失败:', e)
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.status = null
  filterForm.alarmLevel = null
  pagination.page = 1
  loadData()
}

const handleAlarm = (row) => {
  currentAlarmId.value = row.id
  handleForm.handleUser = ''
  handleForm.handleRemark = ''
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.handleUser) {
    ElMessage.warning('请选择处理人')
    return
  }
  try {
    await alarmApi.handle(currentAlarmId.value, handleForm.handleUser, handleForm.handleRemark)
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('处理失败:', e)
  }
}

onMounted(() => {
  loadOperators()
  loadData()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header h2 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #1f2937;
}

.filter-card {
  margin-bottom: 20px;
}

.text-gray {
  color: #9ca3af;
}
</style>
