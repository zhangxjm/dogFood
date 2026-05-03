<template>
  <div class="home-container">
    <div class="header">
      <div class="welcome">
        <p class="welcome-text">欢迎使用</p>
        <h1 class="app-name">健身俱乐部</h1>
      </div>
      <div class="member-info" v-if="member">
        <p class="member-name">{{ member.name }}</p>
        <p class="member-card" v-if="member.member_card_id">卡号: {{ member.member_card_id }}</p>
      </div>
    </div>

    <div class="stats-card" v-if="stats">
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total_checkins || 0 }}</span>
          <span class="stat-label">总签到</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.total_reservations || 0 }}</span>
          <span class="stat-label">预约数</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">¥{{ stats.balance || 0 }}</span>
          <span class="stat-label">余额</span>
        </div>
      </div>
    </div>

    <div class="quick-menu">
      <h3 class="menu-title">快捷功能</h3>
      <div class="menu-grid">
        <div class="menu-item" @click="goToCheckin">
          <div class="menu-icon checkin">
            <span class="icon-text">📋</span>
          </div>
          <span class="menu-label">签到打卡</span>
        </div>
        <div class="menu-item" @click="goToCourses">
          <div class="menu-icon course">
            <span class="icon-text">🏋️</span>
          </div>
          <span class="menu-label">课程预约</span>
        </div>
        <div class="menu-item" @click="goToPrivateCoach">
          <div class="menu-icon coach">
            <span class="icon-text">👨‍🏫</span>
          </div>
          <span class="menu-label">私教预约</span>
        </div>
        <div class="menu-item" @click="goToRecharge">
          <div class="menu-icon recharge">
            <span class="icon-text">💳</span>
          </div>
          <span class="menu-label">充值续费</span>
        </div>
      </div>
    </div>

    <div class="section">
      <div class="section-header">
        <h3 class="section-title">今日课程</h3>
        <span class="section-more" @click="goToCourses">查看全部</span>
      </div>
      <div class="course-list">
        <van-card
          v-for="course in todayCourses"
          :key="course.id"
          :title="course.name"
          :desc="`${course.coach_name || ''} | ${course.start_time} | ${course.duration}分钟`"
          @click="goToCourseDetail(course.id)"
        >
          <template #tag>
            <van-tag v-if="course.current_count < course.max_capacity" type="success">可预约</van-tag>
            <van-tag v-else type="warning">已满</van-tag>
          </template>
        </van-card>
        <van-empty v-if="todayCourses.length === 0" description="今日暂无课程" />
      </div>
    </div>

    <div class="section" v-if="checkinStats">
      <div class="section-header">
        <h3 class="section-title">打卡统计</h3>
      </div>
      <div class="checkin-card">
        <div class="checkin-status" :class="{ checked: checkinStats.today_checked }">
          <div class="checkin-icon">
            <span class="icon-text">{{ checkinStats.today_checked ? '✅' : '🔘' }}</span>
          </div>
          <span class="checkin-text">{{ checkinStats.today_checked ? '今日已签到' : '今日未签到' }}</span>
        </div>
        <div class="checkin-stats">
          <div class="checkin-stat">
            <span class="checkin-num">{{ checkinStats.total_checkins || 0 }}</span>
            <span class="checkin-label">总签到</span>
          </div>
          <div class="checkin-stat">
            <span class="checkin-num">{{ checkinStats.week_checkins || 0 }}</span>
            <span class="checkin-label">本周</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import toast from '@/utils/toast'

const router = useRouter()

const member = ref(null)
const stats = ref({
  total_checkins: 0,
  total_reservations: 0,
  balance: 0
})
const todayCourses = ref([])
const checkinStats = ref(null)

const getMemberId = () => {
  return localStorage.getItem('memberId')
}

const loadData = async () => {
  const memberId = getMemberId()
  if (memberId) {
    const memberData = localStorage.getItem('member')
    if (memberData) {
      member.value = JSON.parse(memberData)
    }
    
    try {
      const [statsRes, checkinRes] = await Promise.all([
        request.get(`/members/${memberId}/stats`),
        request.get(`/checkins/my/${memberId}/stats`)
      ])
      
      if (statsRes.success) {
        stats.value = statsRes.data
      }
      if (checkinRes.success) {
        checkinStats.value = checkinRes.data
      }
    } catch (e) {
      console.error('加载统计数据失败', e)
    }
  }
  
  await loadTodayCourses()
}

const loadTodayCourses = async () => {
  try {
    const today = new Date()
    let dayOfWeek = today.getDay()
    dayOfWeek = dayOfWeek === 0 ? 7 : dayOfWeek
    
    const res = await request.get('/courses', {
      params: { day_of_week: dayOfWeek }
    })
    
    if (res.success) {
      todayCourses.value = res.data.courses || []
    }
  } catch (e) {
    console.error('加载今日课程失败', e)
  }
}

const goToCheckin = () => {
  if (!getMemberId()) {
    router.push('/login')
    return
  }
  router.push('/checkin')
}

const goToCourses = () => {
  router.push('/courses')
}

const goToPrivateCoach = () => {
  if (!getMemberId()) {
    router.push('/login')
    return
  }
  router.push('/private-coach')
}

const goToRecharge = () => {
  if (!getMemberId()) {
    router.push('/login')
    return
  }
  router.push('/recharge')
}

const goToCourseDetail = (courseId) => {
  router.push(`/course-detail/${courseId}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60px;
}

.header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  padding: 40px 24px 50px;
  border-radius: 0 0 24px 24px;
}

.welcome-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 8px 0;
}

.app-name {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
  margin: 0 0 20px 0;
}

.member-info {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 16px;
}

.member-name {
  font-size: 18px;
  font-weight: 500;
  color: #ffffff;
  margin: 0 0 6px 0;
}

.member-card {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.stats-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin: -30px 16px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stats-row {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #ebeef5;
}

.quick-menu {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin: 0 16px 20px;
}

.menu-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 20px 0;
}

.menu-grid {
  display: flex;
  flex-wrap: wrap;
}

.menu-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
  cursor: pointer;
}

.menu-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.menu-icon.checkin {
  background: linear-gradient(135deg, #67C23A 0%, #95D477 100%);
}

.menu-icon.course {
  background: linear-gradient(135deg, #409EFF 0%, #79BBFF 100%);
}

.menu-icon.coach {
  background: linear-gradient(135deg, #E6A23C 0%, #F5D76E 100%);
}

.menu-icon.recharge {
  background: linear-gradient(135deg, #F56C6C 0%, #FAB6B6 100%);
}

.icon-text {
  font-size: 26px;
}

.menu-label {
  font-size: 14px;
  color: #606266;
}

.section {
  margin: 0 16px 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}

.section-more {
  font-size: 14px;
  color: #909399;
  cursor: pointer;
}

.course-list {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
}

.checkin-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
}

.checkin-status {
  display: flex;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.checkin-status.checked .checkin-text {
  color: #67C23A;
}

.checkin-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.checkin-status.checked .checkin-icon {
  background: rgba(103, 194, 58, 0.1);
}

.checkin-text {
  font-size: 16px;
  font-weight: 500;
  color: #909399;
}

.checkin-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.checkin-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.checkin-num {
  font-size: 22px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 6px;
}

.checkin-label {
  font-size: 14px;
  color: #909399;
}
</style>
