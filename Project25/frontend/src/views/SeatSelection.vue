<template>
  <div class="seat-selection">
    <el-row :gutter="24">
      <el-col :span="16">
        <el-card class="seat-card">
          <template #header>
            <div class="card-header">
              <h3>选座购票</h3>
              <div class="screen-info" v-if="schedule">
                <span>{{ schedule.movie_title }}</span>
                <span class="divider">|</span>
                <span>{{ formatDateTime(schedule.start_time) }}</span>
              </div>
            </div>
          </template>
          
          <div class="screen">
            <div class="screen-line"></div>
            <span>银幕</span>
          </div>

          <div class="seat-legend">
            <div class="legend-item">
              <div class="seat-available"></div>
              <span>可选</span>
            </div>
            <div class="legend-item">
              <div class="seat-selected"></div>
              <span>已选</span>
            </div>
            <div class="legend-item">
              <div class="seat-occupied"></div>
              <span>已售</span>
            </div>
          </div>

          <div class="seat-grid" v-if="seatData">
            <div 
              v-for="(row, rowIndex) in seatData.rows" 
              :key="row.row_num"
              class="seat-row"
            >
              <div class="row-label">{{ row.row_num }}排</div>
              <div class="seat-container">
                <div
                  v-for="seat in row.seats"
                  :key="seat.id"
                  class="seat"
                  :class="{
                    'seat-available': seat.is_available && !isSelected(seat),
                    'seat-selected': isSelected(seat),
                    'seat-occupied': !seat.is_available
                  }"
                  @click="toggleSeat(seat)"
                  :title="`${row.row_num}排${seat.col_num}座`"
                >
                  <span>{{ seat.col_num }}</span>
                </div>
              </div>
            </div>
          </div>

          <el-empty v-if="!seatData" description="加载中..." />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="summary-card" v-if="schedule">
          <template #header>
            <h3>订单信息</h3>
          </template>
          
          <div class="movie-info">
            <img :src="movie?.poster" class="movie-poster" />
            <div class="movie-detail">
              <h4>{{ schedule.movie_title }}</h4>
              <p>{{ schedule.cinema_name }}</p>
              <p>{{ schedule.hall_name }}</p>
              <p>{{ formatDateTime(schedule.start_time) }}</p>
            </div>
          </div>

          <el-divider />

          <div class="selected-seats">
            <h4>已选座位</h4>
            <div class="seat-tags">
              <el-tag 
                v-for="seat in selectedSeats" 
                :key="seat.id"
                closable
                @close="toggleSeat(seat)"
              >
                {{ getSeatLabel(seat) }}
              </el-tag>
              <span v-if="selectedSeats.length === 0" class="empty-seats">请选择座位</span>
            </div>
          </div>

          <el-divider />

          <div class="price-summary">
            <div class="price-row">
              <span>票价</span>
              <span>¥{{ schedule.price }} × {{ selectedSeats.length }}张</span>
            </div>
            <div class="price-row total">
              <span>合计</span>
              <span class="total-price">¥{{ totalPrice }}</span>
            </div>
          </div>

          <el-button 
            type="primary" 
            size="large" 
            :disabled="selectedSeats.length === 0 || submitting"
            :loading="submitting"
            @click="submitOrder"
            style="width: 100%; margin-top: 20px;"
          >
            确认购票
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const schedule = ref(null)
const movie = ref(null)
const seatData = ref(null)
const selectedSeats = ref([])
const submitting = ref(false)

const totalPrice = computed(() => {
  return selectedSeats.value.length * (schedule.value?.price || 0)
})

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

const isSelected = (seat) => {
  return selectedSeats.value.some(s => s.id === seat.id)
}

const toggleSeat = (seat) => {
  if (!seat.is_available) {
    return
  }
  const index = selectedSeats.value.findIndex(s => s.id === seat.id)
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  } else {
    if (selectedSeats.value.length >= 10) {
      ElMessage.warning('最多只能选择10个座位')
      return
    }
    selectedSeats.value.push(seat)
  }
}

const getSeatLabel = (seat) => {
  const row = seatData.value.rows.find(r => r.seats.some(s => s.id === seat.id))
  if (row) {
    return `${row.row_num}排${seat.col_num}座`
  }
  return `${seat.row_num}排${seat.col_num}座`
}

const fetchSchedule = async () => {
  try {
    const scheduleId = route.params.id
    const response = await api.get(`/schedules/${scheduleId}`)
    schedule.value = response.data
    
    if (schedule.value.movie_id) {
      const movieRes = await api.get(`/movies/${schedule.value.movie_id}`)
      movie.value = movieRes.data
    }
  } catch (error) {
    console.error('Failed to fetch schedule:', error)
    ElMessage.error('获取场次信息失败')
  }
}

const fetchSeats = async () => {
  try {
    const scheduleId = route.params.id
    const response = await api.get(`/schedules/${scheduleId}/seats`)
    seatData.value = response.data
  } catch (error) {
    console.error('Failed to fetch seats:', error)
    ElMessage.error('获取座位信息失败')
  }
}

const submitOrder = async () => {
  if (selectedSeats.value.length === 0) {
    ElMessage.warning('请选择座位')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认购买 ${selectedSeats.value.length} 张电影票，总价 ¥${totalPrice.value}？`,
      '确认订单',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    submitting.value = true
    const seatLabels = selectedSeats.value.map(seat => {
      const row = seatData.value.rows.find(r => r.seats.some(s => s.id === seat.id))
      return `${row.row_num}-${seat.col_num}`
    })

    const response = await api.post('/orders/create', {
      schedule_id: route.params.id,
      seats: seatLabels.join(',')
    })

    ElMessage.success('购票成功！')
    router.push('/orders')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to create order:', error)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSchedule()
  fetchSeats()
})
</script>

<style scoped>
.seat-selection {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.screen-info {
  font-size: 13px;
  color: #999;
}

.screen-info .divider {
  margin: 0 8px;
}

.screen {
  text-align: center;
  margin-bottom: 40px;
}

.screen-line {
  width: 60%;
  height: 4px;
  background: linear-gradient(90deg, transparent, #409eff, transparent);
  margin: 0 auto 8px;
  border-radius: 2px;
}

.screen span {
  font-size: 13px;
  color: #999;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-item span {
  font-size: 13px;
  color: #666;
}

.seat-grid {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.row-label {
  width: 40px;
  text-align: right;
  font-size: 13px;
  color: #999;
}

.seat-container {
  display: flex;
  gap: 8px;
}

.seat {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px 6px 12px 12px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
}

.seat-available {
  background: #e4e7ed;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.seat-available:hover {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
  transform: scale(1.1);
}

.seat-selected {
  background: #409eff;
  color: #fff;
  border: 1px solid #409eff;
}

.seat-occupied {
  background: #909399;
  color: #c0c4cc;
  border: 1px solid #909399;
  cursor: not-allowed;
}

.summary-card {
  position: sticky;
  top: 80px;
}

.movie-info {
  display: flex;
  gap: 16px;
}

.movie-poster {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 4px;
}

.movie-detail {
  flex: 1;
}

.movie-detail h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.movie-detail p {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
}

.selected-seats h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
}

.seat-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 32px;
}

.empty-seats {
  font-size: 13px;
  color: #999;
}

.price-summary {
  padding: 8px 0;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.price-row.total {
  font-size: 16px;
  color: #333;
  font-weight: bold;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
}
</style>
