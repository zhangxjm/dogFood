<template>
  <div class="course-detail-container">
    <van-nav-bar
      title="课程详情"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />
    
    <div v-if="course" class="content">
      <div class="course-header">
        <h2 class="course-name">{{ course.name }}</h2>
        <div class="course-tags">
          <van-tag v-if="course.is_active" type="success">可预约</van-tag>
          <van-tag v-else type="danger">已关闭</van-tag>
        </div>
      </div>
      
      <van-cell-group inset>
        <van-cell title="教练" :value="course.coach_name || '待定'" />
        <van-cell title="时间" :value="`${course.start_time} - ${course.end_time}`" />
        <van-cell title="时长" :value="`${course.duration}分钟`" />
        <van-cell title="地点" :value="course.location || '待定'" />
        <van-cell title="价格" :value="`¥${course.price}/次`" />
        <van-cell title="剩余名额" :value="`${(course.max_capacity || 20) - (course.current_count || 0)}人`" />
      </van-cell-group>
      
      <div class="description-section" v-if="course.description">
        <h3 class="section-title">课程介绍</h3>
        <p class="description">{{ course.description }}</p>
      </div>
      
      <div class="action-section">
        <van-button
          round
          block
          type="primary"
          size="large"
          :disabled="!course.is_active || (course.current_count >= course.max_capacity)"
          @click="handleReserve"
        >
          {{ !course.is_active ? '课程已关闭' : (course.current_count >= course.max_capacity ? '名额已满' : '立即预约') }}
        </van-button>
      </div>
    </div>
    
    <van-empty v-else description="课程不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'
import toast from '@/utils/toast'

const router = useRouter()
const route = useRoute()

const course = ref(null)

const loadCourse = async () => {
  const courseId = route.params.id
  if (!courseId) return
  
  try {
    const res = await request.get(`/courses/${courseId}`)
    if (res.success) {
      course.value = res.data
    }
  } catch (e) {
    console.error('加载课程失败', e)
    toast.error('加载课程失败')
  }
}

const goBack = () => {
  router.back()
}

const handleReserve = async () => {
  const memberId = localStorage.getItem('memberId')
  if (!memberId) {
    toast.error('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const res = await request.post('/reservations/', {
      member_id: parseInt(memberId),
      course_id: parseInt(route.params.id),
      reservation_type: 'group'
    })
    
    if (res.success) {
      toast.success('预约成功')
      setTimeout(() => {
        router.push('/reservation')
      }, 1000)
    }
  } catch (e) {
    console.error('预约失败', e)
    toast.error('预约失败')
  }
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
.course-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 80px;
}

.content {
  padding: 16px;
}

.course-header {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
}

.course-name {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 12px 0;
}

.course-tags {
  display: flex;
  gap: 8px;
}

.description-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin: 16px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 12px 0;
}

.description {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin: 0;
}

.action-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #ffffff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}
</style>
