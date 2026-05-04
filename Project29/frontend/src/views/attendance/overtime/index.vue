<template>
  <div class="overtime-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>加班管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              申请加班
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="加班类型">
          <el-select v-model="queryForm.overtimeType" placeholder="请选择类型" clearable style="width: 150px;">
            <el-option label="工作日加班" :value="1" />
            <el-option label="周末加班" :value="2" />
            <el-option label="节假日加班" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="empName" label="员工姓名" width="100" />
        <el-table-column prop="overtimeType" label="加班类型" width="120" align="center">
          <template #default="scope">
            <el-tag>{{ getOvertimeTypeName(scope.row.overtimeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column prop="overtimeHours" label="加班时长(小时)" width="120" align="center" />
        <el-table-column prop="reason" label="加班原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="审批状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button type="primary" link size="small" @click="handleApprove(scope.row, 1)">
                通过
              </el-button>
              <el-button type="danger" link size="small" @click="handleApprove(scope.row, 2)">
                拒绝
              </el-button>
            </template>
            <el-button type="info" link size="small" @click="handleView(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="加班类型" prop="overtimeType">
          <el-select v-model="form.overtimeType" placeholder="请选择加班类型" style="width: 100%;">
            <el-option label="工作日加班" :value="1" />
            <el-option label="周末加班" :value="2" />
            <el-option label="节假日加班" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="加班原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入加班原因" />
        </el-form-item>
        <template v-if="isView">
          <el-form-item label="审批状态">
            <el-tag :type="getStatusType(form.status)">
              {{ getStatusName(form.status) }}
            </el-tag>
          </el-form-item>
          <el-form-item label="审批人" v-if="form.approverId">
            <el-input v-model="form.approverName" disabled />
          </el-form-item>
          <el-form-item label="审批时间" v-if="form.approveTime">
            <el-input v-model="form.approveTime" disabled />
          </el-form-item>
          <el-form-item label="审批备注" v-if="form.approveRemark">
            <el-input v-model="form.approveRemark" type="textarea" :rows="2" disabled />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="approveDialogVisible"
      title="审批"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="approveRemark" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button :type="approveStatus === 1 ? 'success' : 'danger'" @click="submitApprove">
          {{ approveStatus === 1 ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOvertimePage, addOvertime, approveOvertime } from '@/api/overtime'

const loading = ref(false)
const dialogVisible = ref(false)
const approveDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const isView = ref(false)
const dateRange = ref([])
const currentApproveRow = ref(null)
const approveStatus = ref(0)
const approveRemark = ref('')

const queryForm = reactive({
  overtimeType: null,
  status: null,
  startDate: null,
  endDate: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const dialogTitle = computed(() => {
  if (isView.value) return '查看详情'
  return isEdit.value ? '编辑申请' : '申请加班'
})

const form = reactive({
  id: null,
  overtimeType: null,
  startTime: '',
  endTime: '',
  reason: '',
  status: 0,
  approverId: null,
  approverName: '',
  approveTime: '',
  approveRemark: ''
})

const rules = {
  overtimeType: [{ required: true, message: '请选择加班类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入加班原因', trigger: 'blur' }]
}

const overtimeTypeMap = {
  1: '工作日加班',
  2: '周末加班',
  3: '节假日加班'
}

const statusMap = {
  0: '待审批',
  1: '已通过',
  2: '已拒绝'
}

const statusTypeMap = {
  0: 'warning',
  1: 'success',
  2: 'danger'
}

const getOvertimeTypeName = (type) => overtimeTypeMap[type] || '未知'
const getStatusName = (status) => statusMap[status] || '未知'
const getStatusType = (status) => statusTypeMap[status] || 'info'

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryForm.startDate = val[0]
    queryForm.endDate = val[1]
  } else {
    queryForm.startDate = null
    queryForm.endDate = null
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOvertimePage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取加班列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  queryForm.overtimeType = null
  queryForm.status = null
  queryForm.startDate = null
  queryForm.endDate = null
  dateRange.value = []
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.size = size
  fetchData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  isView.value = false
  form.id = null
  form.overtimeType = null
  form.startTime = ''
  form.endTime = ''
  form.reason = ''
  form.status = 0
  form.approverId = null
  form.approverName = ''
  form.approveTime = ''
  form.approveRemark = ''
  dialogVisible.value = true
}

const handleView = (row) => {
  isView.value = true
  isEdit.value = false
  form.id = row.id
  form.overtimeType = row.overtimeType
  form.startTime = row.startTime
  form.endTime = row.endTime
  form.reason = row.reason
  form.status = row.status
  form.approverId = row.approverId
  form.approverName = row.approverName
  form.approveTime = row.approveTime
  form.approveRemark = row.approveRemark
  dialogVisible.value = true
}

const handleApprove = (row, status) => {
  currentApproveRow.value = row
  approveStatus.value = status
  approveRemark.value = ''
  approveDialogVisible.value = true
}

const submitApprove = async () => {
  try {
    const res = await approveOvertime({
      id: currentApproveRow.value.id,
      status: approveStatus.value,
      remark: approveRemark.value
    })
    if (res.code === 200) {
      ElMessage.success('审批成功')
      approveDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '审批失败')
    }
  } catch (error) {
    console.error('审批失败:', error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    const res = await addOvertime(form)
    if (res.code === 200) {
      ElMessage.success('申请成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '申请失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}
</style>