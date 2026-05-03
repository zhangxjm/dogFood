<template>
  <div class="my-borrows-container">
    <el-card>
      <template #header>
        <span>我的借阅</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="借阅中" value="borrowed" />
            <el-option label="已归还" value="returned" />
            <el-option label="已逾期" value="overdue" />
            <el-option label="已续借" value="renewed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="borrowList" style="width: 100%" v-loading="loading">
        <el-table-column prop="book_title" label="图书名称" min-width="200" />
        <el-table-column prop="book_isbn" label="ISBN" width="150" />
        <el-table-column prop="borrow_date" label="借阅日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.borrow_date) }}
          </template>
        </el-table-column>
        <el-table-column prop="due_date" label="应还日期" width="120">
          <template #default="scope">
            <span :class="{ 'overdue-text': scope.row.is_overdue }">
              {{ formatDate(scope.row.due_date) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="return_date" label="实际归还" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.return_date) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="renew_count" label="续借次数" width="80" />
        <el-table-column prop="overdue_days" label="逾期天数" width="80">
          <template #default="scope">
            <span v-if="scope.row.overdue_days > 0" class="overdue-text">
              {{ scope.row.overdue_days }}天
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="fine_amount" label="罚款金额" width="100">
          <template #default="scope">
            <span v-if="scope.row.fine_amount > 0" class="fine-text">
              ¥{{ scope.row.fine_amount }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              v-if="scope.row.status !== 'returned' && scope.row.renew_count < 2"
              @click="handleRenew(scope.row)"
            >
              续借
            </el-button>
            <el-button
              type="success"
              link
              v-if="scope.row.status !== 'returned'"
              @click="handleReturn(scope.row)"
            >
              还书
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
      v-model="returnDialogVisible"
      title="确认还书"
      width="400px"
    >
      <el-form :model="returnForm" label-width="80px">
        <el-form-item label="图书">
          <el-input :value="currentBorrow?.book_title" disabled />
        </el-form-item>
        <el-form-item label="借阅日期">
          <el-input :value="formatDate(currentBorrow?.borrow_date)" disabled />
        </el-form-item>
        <el-form-item
          v-if="currentBorrow?.overdue_days > 0"
          label="逾期罚款"
        >
          <el-input
            :value="`¥${currentBorrow?.fine_amount}`"
            disabled
            type="text"
          />
          <div style="color: #f56c6c; font-size: 12px; margin-top: 5px">
            逾期 {{ currentBorrow?.overdue_days }} 天，每天 0.5 元
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="returnForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn" :loading="returnLoading">
          确认还书
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog
      v-model="renewDialogVisible"
      title="确认续借"
      width="400px"
    >
      <el-form :model="renewForm" label-width="80px">
        <el-form-item label="图书">
          <el-input :value="currentBorrow?.book_title" disabled />
        </el-form-item>
        <el-form-item label="当前续借">
          <el-input :value="`${currentBorrow?.renew_count || 0} / 2`" disabled />
        </el-form-item>
        <el-form-item label="续借天数">
          <el-input-number
            v-model="renewForm.renew_days"
            :min="1"
            :max="30"
            :step="1"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRenew" :loading="renewLoading">
          确认续借
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getMyBorrows, returnBook, renewBook } from '@/api/borrow'

const loading = ref(false)
const returnLoading = ref(false)
const renewLoading = ref(false)
const returnDialogVisible = ref(false)
const renewDialogVisible = ref(false)
const currentBorrow = ref(null)
const borrowList = ref([])

const searchForm = reactive({
  status: '',
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const returnForm = reactive({
  remarks: '',
})

const renewForm = reactive({
  renew_days: 30,
})

const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

const getStatusType = (status) => {
  const statusMap = {
    borrowed: '',
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
      status: searchForm.status || undefined,
    }
    
    const res = await getMyBorrows(params)
    
    if (res.data?.results) {
      borrowList.value = res.data.results
      pagination.total = res.data.count
    } else {
      borrowList.value = res.data || []
      pagination.total = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取借阅记录失败:', error)
    ElMessage.error('获取借阅记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchBorrows()
}

const resetSearch = () => {
  searchForm.status = ''
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
  currentBorrow.value = row
  returnForm.remarks = ''
  returnDialogVisible.value = true
}

const handleRenew = (row) => {
  currentBorrow.value = row
  renewForm.renew_days = 30
  renewDialogVisible.value = true
}

const confirmReturn = async () => {
  if (!currentBorrow.value) return
  
  returnLoading.value = true
  try {
    await returnBook(currentBorrow.value.id, {
      remarks: returnForm.remarks,
    })
    
    ElMessage.success('还书成功')
    returnDialogVisible.value = false
    fetchBorrows()
  } catch (error) {
    console.error('还书失败:', error)
  } finally {
    returnLoading.value = false
  }
}

const confirmRenew = async () => {
  if (!currentBorrow.value) return
  
  renewLoading.value = true
  try {
    await renewBook(currentBorrow.value.id, {
      renew_days: renewForm.renew_days,
    })
    
    ElMessage.success('续借成功')
    renewDialogVisible.value = false
    fetchBorrows()
  } catch (error) {
    console.error('续借失败:', error)
  } finally {
    renewLoading.value = false
  }
}

onMounted(() => {
  fetchBorrows()
})
</script>

<style scoped>
.my-borrows-container {
  padding: 0;
}

.search-form {
  margin-bottom: 20px;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}

.fine-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
