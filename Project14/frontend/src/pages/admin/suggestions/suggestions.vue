<template>
  <view class="admin-suggestions-page">
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
        :class="{ active: activeTab === 'pending' }"
        @click="activeTab = 'pending'"
      >
        待回复
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'replied' }"
        @click="activeTab = 'replied'"
      >
        已回复
      </view>
    </view>
    
    <view class="suggestion-list">
      <view 
        class="suggestion-item" 
        v-for="suggestion in filteredSuggestions" 
        :key="suggestion.id"
        @click="goToReply(suggestion.id)"
      >
        <view class="suggestion-header">
          <view class="owner-info">
            <view class="owner-name">{{ suggestion.owner_name || '未知业主' }}</view>
            <view class="owner-room">{{ suggestion.room_number || '未知房号' }}</view>
          </view>
          <view class="suggestion-status" :class="getStatusClass(suggestion.status)">
            {{ getStatusText(suggestion.status) }}
          </view>
        </view>
        <view class="suggestion-type-tag" :class="suggestion.type">
          {{ suggestion.type === 'complaint' ? '投诉' : '建议' }}
        </view>
        <view class="suggestion-title">{{ suggestion.title }}</view>
        <view class="suggestion-content">{{ suggestion.content.substring(0, 80) }}...</view>
        <view class="suggestion-footer">
          <view class="suggestion-time">{{ formatDate(suggestion.created_at) }}</view>
          <view class="action-btn" v-if="suggestion.status === 'pending'">
            去回复
          </view>
        </view>
        <view class="reply-section" v-if="suggestion.reply">
          <view class="reply-label">物业回复：</view>
          <view class="reply-content">{{ suggestion.reply }}</view>
          <view class="reply-time">回复时间: {{ formatDate(suggestion.replied_at) }}</view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredSuggestions.length === 0">
        <view class="empty-icon">💬</view>
        <view class="empty-text">暂无投诉建议</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const suggestions = ref<any[]>([])

const filteredSuggestions = computed(() => {
  if (activeTab.value === 'all') {
    return suggestions.value
  }
  return suggestions.value.filter(s => s.status === activeTab.value)
})

function getStatusClass(status: string) {
  const classMap: Record<string, string> = {
    'pending': 'status-pending',
    'replied': 'status-replied'
  }
  return classMap[status] || ''
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    'pending': '待回复',
    'replied': '已回复'
  }
  return textMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function goToReply(id: number) {
  uni.navigateTo({
    url: `/pages/admin/suggestion-reply/suggestion-reply?id=${id}`
  })
}

async function loadSuggestions() {
  try {
    const params: any = {}
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    
    const res = await request.get('/api/admin/suggestions', params)
    if (Array.isArray(res)) {
      suggestions.value = res
    }
  } catch (error) {
    console.error('加载建议失败', error)
  }
}

watch(activeTab, () => {
  loadSuggestions()
})

onMounted(() => {
  loadSuggestions()
})
</script>

<style scoped>
.admin-suggestions-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx;
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

.suggestion-list {
  padding: 20rpx;
}

.suggestion-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.owner-info {
  display: flex;
  flex-direction: column;
}

.owner-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 4rpx;
}

.owner-room {
  font-size: 24rpx;
  color: #999;
}

.suggestion-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.suggestion-status.status-pending {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.suggestion-status.status-replied {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.suggestion-type-tag {
  display: inline-block;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  margin-bottom: 12rpx;
}

.suggestion-type-tag.complaint {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.suggestion-type-tag.suggestion {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.suggestion-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.suggestion-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.suggestion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.suggestion-time {
  font-size: 24rpx;
  color: #999;
}

.action-btn {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  font-size: 26rpx;
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
}

.reply-section {
  margin-top: 16rpx;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
}

.reply-label {
  font-size: 26rpx;
  color: #007AFF;
  margin-bottom: 12rpx;
  font-weight: 500;
}

.reply-content {
  font-size: 26rpx;
  color: #333;
  line-height: 1.6;
  margin-bottom: 12rpx;
}

.reply-time {
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
