<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon worker-icon">
              <el-icon :size="32"><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.workerCount }}</div>
              <div class="stat-label">师傅总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon :size="32"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon service-icon">
              <el-icon :size="32"><Service /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.serviceCount }}</div>
              <div class="stat-label">服务总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>最新订单</span>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="serviceName" label="服务名称" />
            <el-table-column prop="totalAmount" label="金额">
              <template #default="scope">
                <span style="color: #F56C6C;">¥{{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>待审核师傅</span>
          </template>
          <el-table :data="pendingWorkers" style="width: 100%">
            <el-table-column prop="realName" label="姓名" />
            <el-table-column prop="categoryName" label="服务分类" />
            <el-table-column prop="workYears" label="工作年限">
              <template #default="scope">
                {{ scope.row.workYears }} 年
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import {
  User,
  Avatar,
  List,
  Service
} from '@element-plus/icons-vue'

const stats = reactive({
  userCount: 0,
  workerCount: 0,
  orderCount: 0,
  serviceCount: 0
})

const recentOrders = ref([])
const pendingWorkers = ref([])

onMounted(() => {
  loadStats()
  loadRecentOrders()
  loadPendingWorkers()
})

async function loadStats() {
  try {
    const [usersRes, workersRes, ordersRes, servicesRes] = await Promise.all([
      api.get('/api/admin/users', { params: { current: 1, size: 1 } }),
      api.get('/api/admin/workers', { params: { current: 1, size: 1 } }),
      api.get('/api/admin/orders', { params: { current: 1, size: 1 } }),
      api.get('/api/admin/services', { params: { current: 1, size: 1 } })
    ])
    
    stats.userCount = usersRes.data.data?.total || 0
    stats.workerCount = workersRes.data.data?.total || 0
    stats.orderCount = ordersRes.data.data?.total || 0
    stats.serviceCount = servicesRes.data.data?.total || 0
  } catch (e) {
    console.error(e)
  }
}

async function loadRecentOrders() {
  try {
    const res = await api.get('/api/admin/orders', { params: { current: 1, size: 5 } })
    if (res.data.code === 200) {
      recentOrders.value = res.data.data.records || []
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadPendingWorkers() {
  try {
    const res = await api.get('/api/admin/workers', { params: { current: 1, size: 5, auditStatus: 'PENDING' } })
    if (res.data.code === 200) {
      pendingWorkers.value = res.data.data.records || []
    }
  } catch (e) {
    console.error(e)
  }
}

function getStatusType(status) {
  const map = {
    'PENDING': 'warning',
    'ACCEPTED': 'primary',
    'COMPLETED': 'info',
    'RATED': 'success',
    'CANCELLED': 'danger'
  }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = {
    'PENDING': '待接单',
    'ACCEPTED': '服务中',
    'COMPLETED': '待评价',
    'RATED': '已评价',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.user-icon {
  background-color: #409EFF;
  color: #fff;
}

.worker-icon {
  background-color: #67C23A;
  color: #fff;
}

.order-icon {
  background-color: #E6A23C;
  color: #fff;
}

.service-icon {
  background-color: #F56C6C;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>
