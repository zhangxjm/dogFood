<template>
  <div class="order-list">
    <h2 class="page-title">我的订单</h2>
    
    <el-card v-if="orders.length > 0" class="order-card" v-for="order in orders" :key="order.id">
      <el-row :gutter="16">
        <el-col :span="4">
          <img :src="getMoviePoster(order)" class="movie-poster" />
        </el-col>
        <el-col :span="14">
          <div class="order-info">
            <h3>{{ order.movie_title }}</h3>
            <div class="order-detail">
              <p><el-icon><Location /></el-icon> {{ order.cinema_name }} - {{ order.hall_name }}</p>
              <p><el-icon><Clock /></el-icon> {{ formatDateTime(order.start_time) }}</p>
              <p><el-icon><Ticket /></el-icon> 座位：{{ formatSeats(order.seats) }}</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6" class="order-status-col">
          <div class="order-status">
            <el-tag :type="order.status === 'paid' ? 'success' : 'info'" size="large">
              {{ order.status === 'paid' ? '已支付' : '已取消' }}
            </el-tag>
            <p class="order-no">订单号：{{ order.order_no }}</p>
            <p class="total-price">¥{{ order.total_price }}</p>
            <p class="order-time">{{ formatOrderTime(order.created_at) }}</p>
            <el-button 
              v-if="order.status === 'paid'"
              type="danger" 
              size="small" 
              :loading="cancelling[order.id]"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    
    <div v-if="loading" class="loading-container">
      <i class="el-icon-loading"></i>
      <span>加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const cancelling = ref({})

const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await api.get('/orders')
    orders.value = response.data
  } catch (error) {
    console.error('Failed to fetch orders:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDay = weekDays[date.getDay()]
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}月${day}日 ${weekDay} ${hours}:${minutes}`
}

const formatOrderTime = (timeStr) => {
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

const getMoviePoster = (order) => {
  return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=movie%20poster%20cinema%20entertainment&image_size=portrait_4_3'
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该订单吗？',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    cancelling.value[order.id] = true
    await api.post(`/orders/${order.id}/cancel`)
    
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel order:', error)
    }
  } finally {
    cancelling.value[order.id] = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 24px 0;
  font-size: 24px;
  color: #333;
}

.order-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.movie-poster {
  width: 100%;
  height: 130px;
  object-fit: cover;
  border-radius: 4px;
}

.order-info h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.order-detail p {
  margin: 6px 0;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.order-status-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
}

.order-status {
  text-align: right;
}

.order-no {
  margin: 8px 0 4px 0;
  font-size: 12px;
  color: #999;
}

.total-price {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.order-time {
  margin: 4px 0 12px 0;
  font-size: 12px;
  color: #999;
}

.loading-container {
  text-align: center;
  padding: 60px;
  color: #999;
}
</style>
