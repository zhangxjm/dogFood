<template>
  <div class="page-container">
    <div class="page-header">
      <h2>历史数据查询</h2>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="设备">
          <el-select v-model="filterForm.deviceId" placeholder="请选择设备" clearable style="width: 200px">
            <el-option v-for="device in deviceList" :key="device.id" :label="device.deviceName" :value="device.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" v-loading="loading" stripe max-height="600">
      <el-table-column prop="deviceCode" label="设备编码" width="140" />
      <el-table-column prop="deviceName" label="设备名称" width="200" />
      <el-table-column prop="temperature" label="温度(°C)" width="120">
        <template #default="{ row }">
          <span :class="{'text-danger': row.temperature > 80}">{{ row.temperature }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="voltage" label="电压(V)" width="100" />
      <el-table-column prop="current" label="电流(A)" width="100" />
      <el-table-column prop="power" label="功率(W)" width="120" />
      <el-table-column prop="runtime" label="运行时长(分)" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="记录时间" width="180" />
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="loadData"
      @size-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { deviceApi, deviceDataApi } from '../utils/api'

const loading = ref(false)
const tableData = ref([])
const deviceList = ref([])
const dateRange = ref([])

const filterForm = reactive({
  deviceId: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '离线', 1: '在线', 2: '故障' }
  return map[status] || '未知'
}

const loadDevices = async () => {
  try {
    const res = await deviceApi.list()
    deviceList.value = res.data
  } catch (e) {
    console.error('加载设备列表失败:', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      deviceId: filterForm.deviceId
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await deviceDataApi.page(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total
  } catch (e) {
    console.error('加载数据失败:', e)
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.deviceId = null
  dateRange.value = []
  pagination.page = 1
  loadData()
}

onMounted(() => {
  loadDevices()
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

.text-danger {
  color: #ef4444;
  font-weight: bold;
}
</style>
