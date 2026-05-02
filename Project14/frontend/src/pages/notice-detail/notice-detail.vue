<template>
  <view class="notice-detail-page" v-if="notice">
    <view class="notice-header">
      <view class="notice-title">{{ notice.title }}</view>
      <view class="notice-meta">
        <view class="notice-tag" :class="getNoticeTagClass(notice.notice_type)">
          {{ getNoticeTypeName(notice.notice_type) }}
        </view>
        <view class="notice-date">{{ formatDate(notice.publish_date) }}</view>
      </view>
    </view>
    
    <view class="notice-content">
      <text>{{ notice.content }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { request } from '@/utils/request'

const notice = ref<any>(null)

function getNoticeTagClass(type: string) {
  const classMap: Record<string, string> = {
    'important': 'tag-danger',
    'maintenance': 'tag-warning',
    'general': 'tag-primary'
  }
  return classMap[type] || 'tag-primary'
}

function getNoticeTypeName(type: string) {
  const nameMap: Record<string, string> = {
    'important': '重要通知',
    'maintenance': '维护通知',
    'general': '普通通知'
  }
  return nameMap[type] || '通知'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function loadNoticeDetail() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = currentPage.options?.id
  
  if (!id) {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
    return
  }
  
  try {
    const res = await request.get(`/api/notices/${id}`)
    notice.value = res
  } catch (error) {
    console.error('加载公告详情失败', error)
  }
}

onMounted(() => {
  loadNoticeDetail()
})
</script>

<style scoped>
.notice-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.notice-header {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.notice-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.5;
  margin-bottom: 24rpx;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.notice-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.tag-danger {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.tag-warning {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.tag-primary {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.notice-date {
  font-size: 24rpx;
  color: #999;
}

.notice-content {
  background-color: #fff;
  padding: 30rpx;
  min-height: 60vh;
}

.notice-content text {
  font-size: 30rpx;
  color: #333;
  line-height: 2;
}
</style>
