<template>
  <div class="page-container">
    <div class="page-header">
      <h2>工单管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增工单
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="filterForm.priority" placeholder="请选择" clearable>
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
            <el-option label="紧急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="工单编号" width="180" />
      <el-table-column prop="title" label="工单标题" min-width="200" />
      <el-table-column prop="orderType" label="类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ getOrderTypeText(row.orderType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="{ row }">
          <el-tag :type="getPriorityType(row.priority)">
            {{ getPriorityText(row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button 
            type="primary" 
            link 
            v-if="row.status === 0 || row.status === 1"
            @click="handleEditStatus(row)"
          >
            更新状态
          </el-button>
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
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="工单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择" style="width: 100%">
            <el-option label="日常维护" :value="1" />
            <el-option label="故障处理" :value="2" />
            <el-option label="设备检修" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备" prop="deviceId">
          <el-select v-model="form.deviceId" placeholder="请选择设备" style="width: 100%">
            <el-option v-for="device in deviceList" :key="device.id" :label="device.deviceName" :value="device.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio :value="1">低</el-radio>
            <el-radio :value="2">中</el-radio>
            <el-radio :value="3">高</el-radio>
            <el-radio :value="4">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="指派给" prop="assignTo">
          <el-select v-model="form.assignTo" placeholder="请选择运维人员" style="width: 100%" clearable>
            <el-option v-for="op in operatorList" :key="op.id" :label="op.realName" :value="op.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" v-if="!isView">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusDialogVisible" title="更新工单状态" width="500px">
      <el-form label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(currentOrder?.status)">
            {{ getStatusText(currentOrder?.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="更新为">
          <el-select v-model="newStatus" placeholder="请选择" style="width: 100%">
            <el-option label="处理中" :value="1" v-if="currentOrder?.status === 0" />
            <el-option label="已完成" :value="2" v-if="currentOrder?.status === 1" />
            <el-option label="已关闭" :value="3" v-if="currentOrder?.status === 2" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果" v-if="newStatus === 2">
          <el-input 
            v-model="resultRemark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { workOrderApi, deviceApi, operatorApi } from '../utils/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const isEdit = ref(false)
const isView = ref(false)
const formRef = ref(null)
const deviceList = ref([])
const operatorList = ref([])
const currentOrder = ref(null)
const newStatus = ref(null)
const resultRemark = ref('')

const filterForm = reactive({
  status: null,
  priority: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  title: '',
  orderType: 1,
  deviceId: null,
  priority: 2,
  assignTo: null,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  orderType: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }]
}

const dialogTitle = ref('新增工单')

const getOrderTypeText = (type) => {
  const map = { 1: '日常维护', 2: '故障处理', 3: '设备检修' }
  return map[type] || '未知'
}

const getPriorityType = (priority) => {
  const map = { 1: 'info', 2: '', 3: 'warning', 4: 'danger' }
  return map[priority] || ''
}

const getPriorityText = (priority) => {
  const map = { 1: '低', 2: '中', 3: '高', 4: '紧急' }
  return map[priority] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭' }
  return map[status] || '未知'
}

const loadDevices = async () => {
  try {
    const res = await deviceApi.list()
    deviceList.value = res.data
  } catch (e) {
    console.error('加载设备失败:', e)
  }
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
      priority: filterForm.priority
    }
    const res = await workOrderApi.page(params)
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
  filterForm.priority = null
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  isView.value = false
  dialogTitle.value = '新增工单'
  Object.assign(form, {
    id: null,
    title: '',
    orderType: 1,
    deviceId: null,
    priority: 2,
    assignTo: null,
    description: ''
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  isEdit.value = true
  isView.value = true
  dialogTitle.value = '工单详情'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleEditStatus = (row) => {
  currentOrder.value = row
  newStatus.value = null
  resultRemark.value = ''
  statusDialogVisible.value = true
}

const submitForm = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await workOrderApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await workOrderApi.save(form)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('提交失败:', e)
  }
}

const submitStatus = async () => {
  if (newStatus.value === null) {
    ElMessage.warning('请选择状态')
    return
  }
  try {
    await workOrderApi.updateStatus(currentOrder.value.id, newStatus.value, resultRemark.value)
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('更新失败:', e)
  }
}

onMounted(() => {
  loadDevices()
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
</style>
