<template>
  <div class="orders-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-actions">
            <el-select v-model="status" placeholder="订单状态" clearable @change="loadOrders">
              <el-option label="全部" value="" />
              <el-option label="待接单" value="PENDING" />
              <el-option label="服务中" value="ACCEPTED" />
              <el-option label="待评价" value="COMPLETED" />
              <el-option label="已评价" value="RATED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
            <el-input
              v-model="keyword"
              placeholder="搜索订单号/用户名"
              style="width: 200px;"
              clearable
              @keyup.enter="loadOrders"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userName" label="下单用户" />
        <el-table-column prop="serviceName" label="服务名称" />
        <el-table-column prop="workerName" label="服务师傅">
          <template #default="scope">
            {{ scope.row.workerName || '待接单' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额">
          <template #default="scope">
            <span style="color: #F56C6C;">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
      
      <el-empty v-if="orders.length === 0 && !loading" description="暂无订单" />
    </el-card>
    
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单用户">{{ currentOrder.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="服务地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentOrder.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务师傅">{{ currentOrder.workerName || '待接单' }}</el-descriptions-item>
        <el-descriptions-item label="服务时间">{{ formatDateTime(currentOrder.serviceTime) }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDateTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="订单备注" :span="2">{{ currentOrder.remark || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'
import { Search } from '@element-plus/icons-vue'

const orders = ref([])
const loading = ref(false)
const keyword = ref('')
const status = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailDialogVisible = ref(false)
const currentOrder = ref(null)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    if (status.value) {
      params.status = status.value
    }
    
    const res = await api.get('/api/admin/orders', { params })
    if (res.data.code === 200) {
      orders.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
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

function viewDetail(row) {
  currentOrder.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped>
.orders-container {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
