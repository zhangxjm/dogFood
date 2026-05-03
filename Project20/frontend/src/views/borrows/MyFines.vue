<template>
  <div class="my-fines-container">
    <el-card>
      <template #header>
        <span>我的罚款</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="未支付" value="unpaid" />
            <el-option label="已支付" value="paid" />
            <el-option label="已豁免" value="waived" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="fineList" style="width: 100%" v-loading="loading">
        <el-table-column prop="amount" label="罚款金额" width="120">
          <template #default="scope">
            <span class="fine-amount">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="overdue_days" label="逾期天数" width="100" />
        <el-table-column prop="reason" label="罚款原因" min-width="200" />
        <el-table-column prop="status_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paid_date" label="支付日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.paid_date) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="罚款日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              v-if="scope.row.status === 'unpaid'"
              @click="handlePay(scope.row)"
            >
              支付
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <el-dialog
      v-model="payDialogVisible"
      title="确认支付"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="罚款金额">
          <el-input :value="`¥${currentFine?.amount}`" disabled />
        </el-form-item>
        <el-form-item label="逾期天数">
          <el-input :value="`${currentFine?.overdue_days} 天`" disabled />
        </el-form-item>
        <el-form-item label="罚款原因">
          <el-input :value="currentFine?.reason" type="textarea" :rows="2" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="payLoading">
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getMyFines, payFine } from '@/api/borrow'

const loading = ref(false)
const payLoading = ref(false)
const payDialogVisible = ref(false)
const currentFine = ref(null)
const fineList = ref([])

const searchForm = reactive({
  status: '',
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

const getStatusType = (status) => {
  const statusMap = {
    unpaid: 'danger',
    paid: 'success',
    waived: 'info',
  }
  return statusMap[status] || ''
}

const fetchFines = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.pageSize,
      status: searchForm.status || undefined,
    }
    
    const res = await getMyFines(params)
    
    if (res.data?.results) {
      fineList.value = res.data.results
      pagination.total = res.data.count
    } else {
      fineList.value = res.data || []
      pagination.total = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取罚款记录失败:', error)
    ElMessage.error('获取罚款记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchFines()
}

const resetSearch = () => {
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchFines()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchFines()
}

const handlePay = (row) => {
  currentFine.value = row
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!currentFine.value) return
  
  payLoading.value = true
  try {
    await payFine(currentFine.value.id)
    
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    fetchFines()
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    payLoading.value = false
  }
}

onMounted(() => {
  fetchFines()
})
</script>

<style scoped>
.my-fines-container {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}

.fine-amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}
</style>
