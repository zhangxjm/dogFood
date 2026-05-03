<template>
  <div class="admin-logistics-container">
    <el-card shadow="never">
      <template #header>
        <span>物流管理</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.keyword" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已取件" :value="2" />
            <el-option label="运输中" :value="3" />
            <el-option label="派送中" :value="4" />
            <el-option label="已签收" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="logisticsList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="220" />
        <el-table-column prop="currentLocation" label="当前位置" />
        <el-table-column prop="courierName" label="快递员" width="100" />
        <el-table-column prop="courierPhone" label="快递员电话" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goTrack(scope.row)">
              查看轨迹
            </el-button>
            <el-button type="primary" link @click="handleUpdate(scope.row)">
              更新状态
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
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      title="更新物流状态"
      width="600px"
    >
      <el-form
        ref="updateFormRef"
        :model="updateForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="订单编号">
          <el-input v-model="updateForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="当前位置" prop="location">
          <el-input v-model="updateForm.location" placeholder="请输入当前位置" />
        </el-form-item>
        <el-form-item label="状态描述" prop="description">
          <el-input
            v-model="updateForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入状态描述"
          />
        </el-form-item>
        <el-form-item label="物流状态" prop="status">
          <el-select v-model="updateForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="已审核" :value="1" />
            <el-option label="已取件" :value="2" />
            <el-option label="运输中" :value="3" />
            <el-option label="派送中" :value="4" />
            <el-option label="已签收" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递员姓名">
          <el-input v-model="updateForm.courierName" placeholder="请输入快递员姓名（选填）" />
        </el-form-item>
        <el-form-item label="快递员电话">
          <el-input v-model="updateForm.courierPhone" placeholder="请输入快递员电话（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLogisticsPage, updateLogistics } from '@/api/logistics'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const logisticsList = ref([])
const dialogVisible = ref(false)
const updateFormRef = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const updateForm = reactive({
  orderId: null,
  orderNo: '',
  location: '',
  description: '',
  status: null,
  courierName: '',
  courierPhone: ''
})

const rules = {
  location: [{ required: true, message: '请输入当前位置', trigger: 'blur' }],
  description: [{ required: true, message: '请输入状态描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '已审核',
    2: '已取件',
    3: '运输中',
    4: '派送中',
    5: '已签收'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'success'
  }
  return map[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    }
    
    const res = await getLogisticsPage(params)
    logisticsList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('加载物流数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.current = 1
  loadData()
}

const goTrack = (row) => {
  router.push({
    path: '/track',
    query: { orderNo: row.orderNo }
  })
}

const handleUpdate = (row) => {
  updateForm.orderId = row.orderId
  updateForm.orderNo = row.orderNo
  updateForm.location = row.currentLocation || ''
  updateForm.description = ''
  updateForm.status = row.status
  updateForm.courierName = row.courierName || ''
  updateForm.courierPhone = row.courierPhone || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!updateFormRef.value) return
  
  await updateFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await updateLogistics({
          orderId: updateForm.orderId,
          orderNo: updateForm.orderNo,
          location: updateForm.location,
          description: updateForm.description,
          status: updateForm.status,
          courierName: updateForm.courierName || undefined,
          courierPhone: updateForm.courierPhone || undefined
        })
        ElMessage.success('物流状态更新成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('更新物流状态失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.admin-logistics-container {
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
