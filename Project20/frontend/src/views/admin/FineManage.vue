<template>
  <div class="fine-manage-container">
    <el-card>
      <template #header>
        <span>罚款管理</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.search"
            placeholder="用户名/书名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
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
      
      <el-table
        :data="fineList"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="user_name" label="读者" width="120" />
        <el-table-column prop="book_title" label="书名" min-width="200" />
        <el-table-column prop="amount" label="罚款金额" width="100">
          <template #default="scope">
            <span class="text-danger">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="overdue_days" label="逾期天数" width="100" />
        <el-table-column prop="reason" label="原因" min-width="150" />
        <el-table-column prop="status_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="罚款日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column prop="paid_at" label="支付日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.paid_at) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'unpaid'"
              type="primary"
              link
              @click="handlePay(scope.row)"
            >
              确认支付
            </el-button>
            <el-button
              v-if="scope.row.status === 'unpaid'"
              type="warning"
              link
              @click="handleWaive(scope.row)"
            >
              豁免
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getFineList as getAllFines, payFine, waiveFine } from '@/api/borrow'

const loading = ref(false)
const fineList = ref([])

const searchForm = reactive({
  search: '',
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
      search: searchForm.search || undefined,
      status: searchForm.status || undefined,
    }
    
    const res = await getAllFines(params)
    
    if (res.data?.results) {
      fineList.value = res.data.results
      pagination.total = res.data.count
    } else {
      fineList.value = res.data || []
      pagination.total = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取罚款列表失败:', error)
    ElMessage.error('获取罚款列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchFines()
}

const resetSearch = () => {
  searchForm.search = ''
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
  ElMessageBox.confirm(`确定要确认收到罚款 ¥${row.amount} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await payFine(row.id)
        ElMessage.success('确认支付成功')
        fetchFines()
      } catch (error) {
        console.error('确认支付失败:', error)
      }
    })
    .catch(() => {})
}

const handleWaive = (row) => {
  ElMessageBox.prompt('请输入豁免原因', '豁免罚款', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '请输入豁免原因',
  })
    .then(async ({ value }) => {
      try {
        await waiveFine(row.id, { reason: value })
        ElMessage.success('豁免成功')
        fetchFines()
      } catch (error) {
        console.error('豁免失败:', error)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchFines()
})
</script>

<style scoped>
.fine-manage-container {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
</style>
