<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(item, index) in statsCards" :key="index">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="30"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近借阅</span>
          </template>
          <el-table :data="recentBorrows" style="width: 100%" v-loading="loading">
            <el-table-column prop="book_title" label="图书名称" />
            <el-table-column prop="borrow_date" label="借阅日期">
              <template #default="scope">
                {{ formatDate(scope.row.borrow_date) }}
              </template>
            </el-table-column>
            <el-table-column prop="due_date" label="应还日期">
              <template #default="scope">
                {{ formatDate(scope.row.due_date) }}
              </template>
            </el-table-column>
            <el-table-column prop="status_display" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status_display }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>我的罚款</span>
          </template>
          <el-table :data="myFines" style="width: 100%" v-loading="loading">
            <el-table-column prop="amount" label="罚款金额">
              <template #default="scope">
                ¥{{ scope.row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="overdue_days" label="逾期天数" />
            <el-table-column prop="status_display" label="状态">
              <template #default="scope">
                <el-tag :type="getFineStatusType(scope.row.status)">{{ scope.row.status_display }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="created_at" label="罚款日期">
              <template #default="scope">
                {{ formatDate(scope.row.created_at) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, markRaw } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useUserStore } from '@/store/user'
import { getMyBorrows, getMyFines, getBorrowStatistics } from '@/api/borrow'
import { Document, Reading, Warning, Money } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const recentBorrows = ref([])
const myFines = ref([])

const statsCards = reactive([
  { label: '总借阅次数', value: 0, icon: markRaw(Document), color: '#409EFF' },
  { label: '当前借阅', value: 0, icon: markRaw(Reading), color: '#67C23A' },
  { label: '逾期数量', value: 0, icon: markRaw(Warning), color: '#E6A23C' },
  { label: '未付罚款', value: '¥0', icon: markRaw(Money), color: '#F56C6C' },
])

const isAdmin = computed(() => userStore.isAdmin)

const formatDate = (date) => {
  if (!date) return '-'
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

const getFineStatusType = (status) => {
  const statusMap = {
    unpaid: 'danger',
    paid: 'success',
    waived: 'info',
  }
  return statusMap[status] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const [borrowsRes, finesRes] = await Promise.all([
      getMyBorrows({ page_size: 5 }),
      getMyFines({ page_size: 5 }),
    ])
    
    recentBorrows.value = borrowsRes.data?.results || borrowsRes.data || []
    myFines.value = finesRes.data?.results || finesRes.data || []
    
    try {
      const statsRes = await getBorrowStatistics()
      if (statsRes.data) {
        statsCards[0].value = statsRes.data.total_borrows || 0
        statsCards[1].value = statsRes.data.current_borrows || 0
        statsCards[2].value = statsRes.data.overdue_count || 0
        statsCards[3].value = `¥${statsRes.data.unpaid_fines || 0}`
      }
    } catch (e) {
      console.error('获取统计信息失败:', e)
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
