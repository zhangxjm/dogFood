<template>
  <div class="borrow-manage-container">
    <el-card>
      <template #header>
        <span>借阅管理</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.search"
            placeholder="用户名/书名/ISBN"
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
            <el-option label="借阅中" value="borrowing" />
            <el-option label="已归还" value="returned" />
            <el-option label="已逾期" value="overdue" />
            <el-option label="已续借" value="renewed" />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table
        :data="borrowList"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="user_name" label="读者" width="120" />
        <el-table-column prop="book_title" label="书名" min-width="200" />
        <el-table-column prop="book_isbn" label="ISBN" width="150" />
        <el-table-column prop="borrow_date" label="借阅日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.borrow_date) }}
          </template>
        </el-table-column>
        <el-table-column prop="due_date" label="应还日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.due_date) }}
          </template>
        </el-table-column>
        <el-table-column prop="actual_return_date" label="实际归还" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.actual_return_date) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="renew_count" label="续借次数" width="80" />
        <el-table-column prop="overdue_days" label="逾期天数" width="100">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.overdue_days > 0 }">
              {{ scope.row.overdue_days > 0 ? scope.row.overdue_days + ' 天' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'borrowing' || scope.row.status === 'overdue'"
              type="primary"
              link
              @click="handleReturn(scope.row)"
            >
              还书
            </el-button>
            <el-button
              v-else-if="scope.row.status === 'borrowing'"
              type="success"
              link
              @click="handleRenew(scope.row)"
            >
              续借
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
import { getBorrowList as getAllBorrows, returnBook, renewBook } from '@/api/borrow'

const loading = ref(false)
const borrowList = ref([])
const dateRange = ref([])

const searchForm = reactive({
  search: '',
  status: '',
  start_date: '',
  end_date: '',
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
    borrowing: 'primary',
    returned: 'success',
    overdue: 'danger',
    renewed: 'warning',
  }
  return statusMap[status] || ''
}

const fetchBorrows = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.pageSize,
      search: searchForm.search || undefined,
      status: searchForm.status || undefined,
      start_date: searchForm.start_date || undefined,
      end_date: searchForm.end_date || undefined,
    }
    
    const res = await getAllBorrows(params)
    
    if (res.data?.results) {
      borrowList.value = res.data.results
      pagination.total = res.data.count
    } else {
      borrowList.value = res.data || []
      pagination.total = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取借阅列表失败:', error)
    ElMessage.error('获取借阅列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.start_date = dateRange.value[0]
    searchForm.end_date = dateRange.value[1]
  } else {
    searchForm.start_date = ''
    searchForm.end_date = ''
  }
  pagination.page = 1
  fetchBorrows()
}

const resetSearch = () => {
  searchForm.search = ''
  searchForm.status = ''
  searchForm.start_date = ''
  searchForm.end_date = ''
  dateRange.value = []
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchBorrows()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchBorrows()
}

const handleReturn = (row) => {
  ElMessageBox.confirm('确定要还书吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await returnBook(row.id)
        ElMessage.success('还书成功')
        fetchBorrows()
      } catch (error) {
        console.error('还书失败:', error)
      }
    })
    .catch(() => {})
}

const handleRenew = (row) => {
  ElMessageBox.confirm('确定要续借吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await renewBook(row.id)
        ElMessage.success('续借成功')
        fetchBorrows()
      } catch (error) {
        console.error('续借失败:', error)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchBorrows()
})
</script>

<style scoped>
.borrow-manage-container {
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
