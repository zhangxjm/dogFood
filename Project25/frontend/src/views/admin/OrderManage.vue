<template>
  <div class="order-manage">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>

    <el-card>
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="订单号">
          <el-input v-model="filters.order_no" placeholder="请输入订单号" clearable @keyup.enter="fetchOrders" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="选择状态" clearable @change="fetchOrders">
            <el-option label="已支付" value="paid" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchOrders">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="order_no" label="订单号" min-width="180" />
        <el-table-column prop="movie_title" label="电影" min-width="150" />
        <el-table-column prop="cinema_name" label="影院" min-width="150" />
        <el-table-column prop="hall_name" label="放映厅" width="120" />
        <el-table-column label="座位" min-width="120">
          <template #default="{ row }">
            {{ formatSeats(row.seats) }}
          </template>
        </el-table-column>
        <el-table-column label="总价" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.total_price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'paid' ? 'success' : 'info'">
              {{ row.status === 'paid' ? '已支付' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'paid'"
              type="danger" 
              link 
              size="small" 
              @click="handleCancel(row)"
            >
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../utils/request'

const loading = ref(false)
const orders = ref([])

const filters = reactive({
  order_no: '',
  status: null
})

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatSeats = (seatsStr) => {
  if (!seatsStr) return ''
  return seatsStr.split(',').map(s => {
    const [row, col] = s.split('-')
    return `${row}排${col}座`
  }).join('、')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.order_no) {
      params.order_no = filters.order_no
    }
    if (filters.status) {
      params.status = filters.status
    }
    const response = await api.get('/admin/orders', { params })
    orders.value = response.data
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 "${row.order_no}" 吗？`,
      '确认取消',
      { type: 'warning' }
    )
    await api.post(`/orders/${row.id}/cancel`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel order:', error)
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-manage {
  max-width: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.filter-form {
  margin-bottom: 20px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
