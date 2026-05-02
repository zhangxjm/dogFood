<template>
  <view class="notices-page">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'all' }"
        @click="activeTab = 'all'"
      >
        全部
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'important' }"
        @click="activeTab = 'important'"
      >
        重要
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'maintenance' }"
        @click="activeTab = 'maintenance'"
      >
        维护
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'general' }"
        @click="activeTab = 'general'"
      >
        普通
      </view>
    </view>
    
    <view class="notice-list">
      <view 
        class="notice-item" 
        v-for="notice in filteredNotices" 
        :key="notice.id"
        @click="goToDetail(notice.id)"
      >
        <view class="notice-header">
          <view class="notice-tag" :class="getNoticeTagClass(notice.notice_type)">
            {{ getNoticeTypeName(notice.notice_type) }}
          </view>
          <view class="notice-priority" v-if="notice.priority === 'high'">
            置顶
          </view>
        </view>
        <view class="notice-title">{{ notice.title }}</view>
        <view class="notice-desc">{{ notice.content.substring(0, 80) }}...</view>
        <view class="notice-footer">
          <view class="notice-date">{{ formatDate(notice.publish_date) }}</view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredNotices.length === 0">
        <view class="empty-icon">📢</view>
        <view class="empty-text">暂无公告</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const notices = ref<any[]>([])

const filteredNotices = computed(() => {
  if (activeTab.value === 'all') {
    return notices.value
  }
  return notices.value.filter(notice => notice.notice_type === activeTab.value)
})

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
    'important': '重要',
    'maintenance': '维护',
    'general': '通知'
  }
  return nameMap[type] || '通知'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function goToDetail(id: number) {
  uni.navigateTo({
    url: `/pages/notice-detail/notice-detail?id=${id}`
  })
}

async function loadNotices() {
  try {
    const res = await request.get('/api/notices')
    if (Array.isArray(res)) {
      notices.value = res
    }
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notices-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
  overflow-x: auto;
  gap: 16rpx;
}

.tab-item {
  padding: 12rpx 24rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 40rpx;
  white-space: nowrap;
}

.tab-item.active {
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  font-weight: 500;
}

.notice-list {
  padding: 20rpx;
}

.notice-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.notice-header {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  gap: 16rpx;
}

.notice-tag {
  font-size: 22rpx;
  padding: 4rpx 16rpx;
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

.notice-priority {
  font-size: 20rpx;
  color: #ff4d4f;
  padding: 2rpx 12rpx;
  border: 1rpx solid #ff4d4f;
  border-radius: 4rpx;
}

.notice-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.notice-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-date {
  font-size: 24rpx;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
