<template>
  <view class="container">
    <view class="day-tabs">
      <view 
        v-for="(day, index) in weekDays" 
        :key="index"
        class="day-item"
        :class="{ active: selectedDay === index + 1 }"
        @click="selectDay(index + 1)"
      >
        <text class="day-name">{{ day.name }}</text>
        <text class="day-date">{{ day.date }}</text>
      </view>
    </view>

    <view class="course-list">
      <view 
        v-for="course in courses" 
        :key="course.id"
        class="course-card"
        @click="goToDetail(course.id)"
      >
        <view class="course-header">
          <view class="course-time">
            <text class="start-time">{{ course.start_time }}</text>
            <text class="duration">{{ course.duration }}分钟</text>
          </view>
          <view class="course-status">
            <text 
              class="status-tag" 
              :class="course.current_count < course.capacity ? 'available' : 'full'"
            >
              {{ course.current_count < course.capacity ? '可预约' : '已满' }}
            </text>
          </view>
        </view>
        
        <view class="course-info">
          <text class="course-name">{{ course.name }}</text>
          <view class="course-detail">
            <text class="coach-name">教练: {{ course.coach_name }}</text>
            <text class="capacity">剩余: {{ course.capacity - course.current_count }}人</text>
          </view>
        </view>
        
        <view class="course-footer">
          <text class="course-desc">{{ course.description }}</text>
          <view class="reserve-btn" @click.stop="handleReserve(course)">
            <text>预约</text>
          </view>
        </view>
      </view>
      
      <view class="empty" v-if="courses.length === 0">
        <text class="empty-text">暂无课程安排</text>
      </view>
    </view>
  </view>
</template>

<script>
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, computed } from 'vue'
import { request, toast, tools } from '@/utils'

export default {
  setup() {
    const selectedDay = ref(1)
    const courses = ref([])

    const weekDays = computed(() => {
      const days = []
      const today = new Date()
      const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(today)
        date.setDate(today.getDate() + i)
        const dayOfWeek = date.getDay()
        days.push({
          name: i === 0 ? '今天' : dayNames[dayOfWeek],
          date: `${date.getMonth() + 1}/${date.getDate()}`,
          dayOfWeek: dayOfWeek === 0 ? 7 : dayOfWeek
        })
      }
      return days
    })

    const selectDay = (dayIndex) => {
      const day = weekDays.value[dayIndex - 1]
      selectedDay.value = day.dayOfWeek
      loadCourses()
    }

    const loadCourses = async () => {
      try {
        const res = await request({
          url: `/courses/by-day/${selectedDay.value}`,
          method: 'GET'
        })
        courses.value = res.data.courses || []
      } catch (e) {
        console.error('加载课程失败', e)
      }
    }

    const goToDetail = (courseId) => {
      uni.navigateTo({ url: `/pages/course-detail/course-detail?id=${courseId}` })
    }

    const handleReserve = async (course) => {
      if (!tools.isLoggedIn()) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }

      if (course.current_count >= course.capacity) {
        toast.error('课程名额已满')
        return
      }

      const member = tools.getCurrentMember()
      const day = weekDays.value.find(d => d.dayOfWeek === selectedDay.value)
      const today = new Date()
      const daysToAdd = weekDays.value.findIndex(d => d.dayOfWeek === selectedDay.value)
      const targetDate = new Date(today)
      targetDate.setDate(today.getDate() + daysToAdd)
      const reservationDate = tools.formatDate(targetDate)

      const confirmed = await toast.confirm('确认预约', `确定要预约${course.name}课程吗？`)
      if (!confirmed) return

      try {
        await request({
          url: '/reservations/group',
          method: 'POST',
          data: {
            member_id: member.id,
            course_id: course.id,
            reservation_date: reservationDate
          }
        })
        
        toast.success('预约成功')
        loadCourses()
      } catch (e) {
        console.error('预约失败', e)
      }
    }

    onLoad(() => {
      const today = new Date()
      let dayOfWeek = today.getDay()
      selectedDay.value = dayOfWeek === 0 ? 7 : dayOfWeek
      loadCourses()
    })

    onShow(() => {
      loadCourses()
    })

    return {
      selectedDay,
      weekDays,
      courses,
      selectDay,
      goToDetail,
      handleReserve
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0;
}

.day-tabs {
  display: flex;
  background: #ffffff;
  padding: 20rpx 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.day-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  position: relative;
}

.day-item.active {
  background: rgba(64, 158, 255, 0.1);
}

.day-item.active::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 0;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background: #409EFF;
  border-radius: 3rpx;
}

.day-name {
  font-size: 26rpx;
  color: #606266;
  margin-bottom: 8rpx;
}

.day-item.active .day-name {
  color: #409EFF;
  font-weight: 500;
}

.day-date {
  font-size: 24rpx;
  color: #909399;
}

.course-list {
  padding: 20rpx;
}

.course-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.course-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.course-time {
  display: flex;
  align-items: baseline;
}

.start-time {
  font-size: 36rpx;
  font-weight: bold;
  color: #409EFF;
  margin-right: 16rpx;
}

.duration {
  font-size: 24rpx;
  color: #909399;
}

.status-tag {
  display: inline-block;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-tag.available {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.status-tag.full {
  background: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.course-info {
  margin-bottom: 20rpx;
}

.course-name {
  display: block;
  font-size: 32rpx;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12rpx;
}

.course-detail {
  display: flex;
  align-items: center;
}

.coach-name {
  font-size: 26rpx;
  color: #606266;
  margin-right: 30rpx;
}

.capacity {
  font-size: 26rpx;
  color: #909399;
}

.course-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.course-desc {
  flex: 1;
  font-size: 24rpx;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 20rpx;
}

.reserve-btn {
  padding: 16rpx 40rpx;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #ffffff;
  font-weight: 500;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
}
</style>
