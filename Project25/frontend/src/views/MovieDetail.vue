<template>
  <div class="movie-detail" v-loading="loading">
    <div class="movie-header" v-if="movie">
      <el-row :gutter="24">
        <el-col :span="6">
          <el-image :src="movie.poster" fit="cover" class="movie-poster" />
        </el-col>
        <el-col :span="18">
          <div class="movie-info">
            <h1 class="movie-title">{{ movie.title }}</h1>
            <div class="movie-meta">
              <el-tag type="primary" size="small">{{ movie.category_name || '未分类' }}</el-tag>
              <span class="duration">{{ movie.duration }}分钟</span>
              <span class="divider">|</span>
              <span class="rating">
                <el-rate :model-value="movie.rating / 2" disabled />
                <span class="rating-text">{{ movie.rating || '暂无评分' }}</span>
              </span>
            </div>
            <div class="movie-detail-info">
              <p><strong>导演：</strong>{{ movie.director || '待定' }}</p>
              <p><strong>主演：</strong>{{ movie.actors || '待定' }}</p>
              <p><strong>上映日期：</strong>{{ movie.release_date ? new Date(movie.release_date).toLocaleDateString('zh-CN') : '待定' }}</p>
              <p><strong>状态：</strong>{{ movie.status === 'showing' ? '正在热映' : '即将上映' }}</p>
            </div>
            <div class="movie-actions">
              <el-button type="primary" size="large" v-if="movie.status === 'showing'" @click="scrollToSchedule">
                立即购票
              </el-button>
              <el-button type="warning" size="large" v-else disabled>
                敬请期待
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="movie-description" v-if="movie">
      <h3>剧情简介</h3>
      <p>{{ movie.description || '暂无简介' }}</p>
    </div>

    <div class="schedule-section" id="schedule-section" v-if="movie?.status === 'showing'">
      <h3>排片场次</h3>
      <el-collapse v-model="activeCinema" accordion>
        <el-collapse-item
          v-for="cinemaGroup in scheduleByCinema"
          :key="cinemaGroup.cinema.id"
          :name="cinemaGroup.cinema.id"
        >
          <template #title>
            <span class="cinema-name">{{ cinemaGroup.cinema.name }}</span>
            <span class="cinema-address">{{ cinemaGroup.cinema.address }}</span>
          </template>
          <template #arrow>
            <el-icon v-if="!isExpanded(cinemaGroup.cinema.id)"><ArrowDown /></el-icon>
            <el-icon v-else><ArrowUp /></el-icon>
          </template>
          <div class="schedule-list">
            <el-card
              v-for="schedule in cinemaGroup.schedules"
              :key="schedule.id"
              class="schedule-card"
            >
              <el-row :gutter="16" align="middle">
                <el-col :span="6">
                  <div class="schedule-time">
                    <div class="start-time">{{ formatTime(schedule.start_time) }}</div>
                    <div class="time-range">
                      {{ formatTime(schedule.start_time) }} - {{ formatTime(schedule.end_time) }}
                    </div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="schedule-info">
                    <p class="hall-name">{{ schedule.hall_name }}</p>
                    <p class="language">国语 2D</p>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="schedule-price">
                    <span class="price">¥{{ schedule.price }}</span>
                    <span class="original-price" v-if="schedule.price < 80">原价 ¥80</span>
                  </div>
                </el-col>
                <el-col :span="4">
                  <el-button 
                    type="primary" 
                    @click="goToSeatSelection(schedule.id)"
                  >
                    选座购票
                  </el-button>
                </el-col>
              </el-row>
            </el-card>
          </div>
        </el-collapse-item>
      </el-collapse>
      <el-empty v-if="scheduleByCinema.length === 0" description="暂无排片" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../utils/request'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const movie = ref(null)
const schedules = ref([])
const loading = ref(false)
const activeCinema = ref([])

const scheduleByCinema = computed(() => {
  const groups = {}
  schedules.value.forEach(schedule => {
    const cinemaId = schedule.cinema_id
    if (!groups[cinemaId]) {
      groups[cinemaId] = {
        cinema: {
          id: cinemaId,
          name: schedule.cinema_name,
          address: schedule.cinema_name ? ` - ${schedule.hall_name.slice(0, 2)}...` : ''
        },
        schedules: []
      }
    }
    groups[cinemaId].schedules.push(schedule)
  })
  return Object.values(groups)
})

const fetchMovie = async () => {
  loading.value = true
  try {
    const movieId = route.params.id
    const response = await api.get(`/movies/${movieId}`)
    movie.value = response.data
  } catch (error) {
    console.error('Failed to fetch movie:', error)
    ElMessage.error('获取电影详情失败')
  } finally {
    loading.value = false
  }
}

const fetchSchedules = async () => {
  try {
    const movieId = route.params.id
    const response = await api.get(`/schedules/movie/${movieId}`)
    schedules.value = response.data.flatMap(g => g.schedules)
    if (schedules.value.length > 0) {
      activeCinema.value = schedules.value[0].cinema_id
    }
  } catch (error) {
    console.error('Failed to fetch schedules:', error)
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const scrollToSchedule = () => {
  const element = document.getElementById('schedule-section')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const isExpanded = (cinemaId) => {
  return activeCinema.value === cinemaId
}

const goToSeatSelection = (scheduleId) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: `/schedule/${scheduleId}` } })
    return
  }
  router.push(`/schedule/${scheduleId}`)
}

onMounted(() => {
  fetchMovie()
  fetchSchedules()
})
</script>

<style scoped>
.movie-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.movie-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 24px;
  color: #fff;
}

.movie-poster {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.movie-info {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.movie-title {
  font-size: 32px;
  margin: 0 0 16px 0;
  font-weight: bold;
}

.movie-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.duration {
  font-size: 14px;
  color: #aaa;
}

.divider {
  color: #666;
}

.rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-text {
  color: #ffd700;
  font-weight: bold;
  font-size: 18px;
}

.movie-detail-info {
  margin-bottom: 24px;
}

.movie-detail-info p {
  margin: 8px 0;
  color: #ccc;
  font-size: 14px;
}

.movie-detail-info strong {
  color: #fff;
}

.movie-actions {
  margin-top: 16px;
}

.movie-description {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.movie-description h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.movie-description p {
  line-height: 1.8;
  color: #666;
  text-indent: 2em;
}

.schedule-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.schedule-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.cinema-name {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.cinema-address {
  margin-left: 16px;
  font-size: 13px;
  color: #999;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.schedule-card {
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.schedule-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.schedule-time {
  text-align: center;
}

.start-time {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.time-range {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.schedule-info {
  padding: 0 16px;
}

.hall-name {
  font-weight: bold;
  margin: 0 0 4px 0;
}

.language {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.schedule-price {
  text-align: right;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}
</style>
