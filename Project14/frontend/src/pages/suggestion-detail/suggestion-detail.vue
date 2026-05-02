<template>
  <view class="repair-detail-page">
    <view class="repair-header">
      <view class="repair-title">{{ repair.title }}</view>
      <view class="repair-status" :class="getStatusClass(repair.status)">
        {{ getStatusText(repair.status) }}
      </view>
    </view>
    
    <view class="info-section">
      <view class="info-item">
        <view class="info-label">报修类型</view>
        <view class="info-value">{{ repair.repair_type }}</view>
      </view>
      <view class="info-item">
        <view class="info-label">报修位置</view>
        <view class="info-value">{{ repair.location }}</view>
      </view>
      <view class="info-item">
        <view class="info-label">申请时间</view>
        <view class="info-value">{{ formatDate(repair.created_at) }}</view>
      </view>
      <view class="info-item" v-if="repair.assigned_to">
        <view class="info-label">维修人员</view>
        <view class="info-value">{{ repair.assigned_to }}</view>
      </view>
      <view class="info-item" v-if="repair.completed_at">
        <view class="info-label">完成时间</view>
        <view class="info-value">{{ formatDate(repair.completed_at) }}</view>
      </view>
    </view>
    
    <view class="desc-section" v-if="repair.description">
      <view class="section-title">问题描述</view>
      <view class="desc-content">{{ repair.description }}</view>
    </view>
    
    <view class="images-section" v-if="repair.images && repair.images.length > 0">
      <view class="section-title">图片说明</view>
      <view class="images-grid">
        <view class="image-item" v-for="(img, index) in repair.images" :key="index">
          <view class="image-placeholder">
            <text>📷</text>
            <text class="image-text">图片{{ index + 1 }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="progress-section" v-if="repair.progress">
      <view class="section-title">处理进度</view>
      <view class="progress-content">{{ repair.progress }}</view>
    </view>
    
    <view class="rating-section" v-if="repair.status === 'completed'">
      <view class="section-title">服务评价</view>
      <view v-if="repair.rating" class="rating-display">
        <view class="rating-stars">
          <text v-for="i in 5" :key="i" :class="i <= repair.rating ? 'star active' : 'star'">★</text>
        </view>
        <view class="rating-comment" v-if="repair.comment">{{ repair.comment }}</view>
      </view>
      <view v-else class="rating-form">
        <view class="rating-stars-select">
          <text 
            v-for="i in 5" 
            :key="i" 
            :class="i <= rating ? 'star active' : 'star'"
            @click="rating = i"
          >★</text>
        </view>
        <textarea 
          v-model="comment" 
          placeholder="请输入您的评价（选填）"
          class="comment-input"
        ></textarea>
        <view class="submit-btn" @click="submitRating">提交评价</view>
      </view>
    </view>
    
    <view class="timeline-section" v-if="timeline.length > 0">
      <view class="section-title">处理时间线</view>
      <view class="timeline-list">
        <view class="timeline-item" v-for="(item, index) in timeline" :key="index">
          <view class="timeline-dot" :class="index === 0 ? 'active' : ''"></view>
          <view class="timeline-line" v-if="index < timeline.length - 1"></view>
          <view class="timeline-content">
            <view class="timeline-title">{{ item.title }}</view>
            <view class="timeline-time">{{ item.time }}</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { request } from '@/utils/request'

const repair = ref<any>({})
const rating = ref(5)
const comment = ref('')

const timeline = computed(() => {
  const items: any[] = []
  if (repair.value.created_at) {
    items.push({
      title: '报修申请已提交',
      time: formatDate(repair.value.created_at)
    })
  }
  if (repair.value.status === 'processing' || repair.value.status === 'completed') {
    items.push({
      title: '维修人员已接单',
      time: repair.value.progress ? '处理中' : '已接单'
    })
  }
  if (repair.value.status === 'completed' && repair.value.completed_at) {
    items.push({
      title: '维修已完成',
      time: formatDate(repair.value.completed_at)
    })
  }
  if (repair.value.rating) {
    items.push({
      title: `已评价 ${repair.value.rating}星`,
      time: repair.value.comment || '感谢您的评价'
    })
  }
  return items.reverse()
})

function getStatusClass(status: string) {
  const classMap: Record<string, string> = {
    'pending': 'status-pending',
    'processing': 'status-processing',
    'completed': 'status-completed'
  }
  return classMap[status] || ''
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成'
  }
  return textMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function submitRating() {
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const id = (currentPage as any).options?.id
    
    if (!id) {
      uni.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    
    await request.post(`/api/repairs/${id}/rate`, {
      rating: rating.value,
      comment: comment.value
    })
    
    uni.showToast({ title: '评价成功', icon: 'success' })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交评价失败', error)
  }
}

async function loadRepair() {
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const id = (currentPage as any).options?.id
    
    if (!id) {
      uni.showToast({ title: '参数错误', icon: 'none' })
      return
    }
    
    const res = await request.get(`/api/repairs/${id}`)
    if (res) {
      repair.value = res
    }
  } catch (error) {
    console.error('加载报修详情失败', error)
  }
}

onMounted(() => {
  loadRepair()
})
</script>

<style scoped>
.repair-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.repair-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.repair-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
  margin-right: 20rpx;
}

.repair-status {
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
}

.repair-status.status-pending {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.repair-status.status-processing {
  background-color: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.repair-status.status-completed {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.info-section {
  background-color: #fff;
  padding: 20rpx 30rpx;
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 28rpx;
  color: #999;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.desc-section,
.images-section,
.progress-section,
.rating-section,
.timeline-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.desc-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.images-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  width: 200rpx;
  height: 200rpx;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
}

.image-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.progress-content {
  font-size: 28rpx;
  color: #1890ff;
  line-height: 1.8;
  padding: 20rpx;
  background-color: rgba(24, 144, 255, 0.05);
  border-radius: 12rpx;
  border-left: 6rpx solid #1890ff;
}

.rating-display {
  display: flex;
  flex-direction: column;
}

.rating-stars {
  font-size: 40rpx;
  margin-bottom: 16rpx;
}

.rating-stars-select {
  font-size: 48rpx;
  margin-bottom: 20rpx;
}

.star {
  color: #ddd;
  margin-right: 8rpx;
}

.star.active {
  color: #faad14;
}

.rating-comment {
  font-size: 28rpx;
  color: #666;
}

.comment-input {
  width: 100%;
  height: 160rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  margin-bottom: 20rpx;
}

.submit-btn {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 500;
}

.timeline-list {
  position: relative;
}

.timeline-item {
  display: flex;
  position: relative;
  padding-bottom: 30rpx;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background-color: #ddd;
  margin-right: 20rpx;
  position: relative;
  z-index: 1;
}

.timeline-dot.active {
  background-color: #007AFF;
}

.timeline-line {
  position: absolute;
  left: 9rpx;
  top: 24rpx;
  width: 2rpx;
  height: calc(100% - 20rpx);
  background-color: #f0f0f0;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 8rpx;
}

.timeline-time {
  font-size: 24rpx;
  color: #999;
}
</style>
