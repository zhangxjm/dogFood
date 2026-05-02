<template>
  <view class="suggestions-page">
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
        @click="goToDetail(suggestion.id)"
      >
        <view class="suggestion-header">
          <view class="suggestion-type-tag" :class="suggestion.type">
            {{ suggestion.type === 'complaint' ? '投诉' : '建议' }}
          </view>
          <view class="suggestion-status" :class="getStatusClass(suggestion.status)">
            {{ getStatusText(suggestion.status) }}
          </view>
        </view>
        <view class="suggestion-title">{{ suggestion.title }}</view>
        <view class="suggestion-desc">{{ suggestion.content.substring(0, 60) }}...</view>
        <view class="suggestion-footer">
          <view class="suggestion-time">{{ formatDate(suggestion.created_at) }}</view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredSuggestions.length === 0">
        <view class="empty-icon">💬</view>
        <view class="empty-text">暂无投诉建议记录</view>
      </view>
    </view>
    
    <view class="fab-btn" @click="goToApply">
      <text class="fab-icon">+</text>
      <text class="fab-text">提交建议</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const suggestions = ref<any[]>([])

const filteredSuggestions = computed(() => {
  if (activeTab.value === 'all') {
    return suggestions.value
  }
  return suggestions.value.filter(suggestion => suggestion.status === activeTab.value)
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
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function goToDetail(id: number) {
  uni.navigateTo({
    url: `/pages/suggestion-detail/suggestion-detail?id=${id}`
  })
}

function goToApply() {
  uni.navigateTo({
    url: '/pages/suggestion-apply/suggestion-apply'
  })
}

async function loadSuggestions() {
  try {
    const res = await request.get('/api/suggestions')
    if (Array.isArray(res)) {
      suggestions.value = res
    }
  } catch (error) {
    console.error('加载建议记录失败', error)
  }
}

onMounted(() => {
  loadSuggestions()
})
</script>

<style scoped>
.suggestions-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
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

.suggestion-type-tag {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.suggestion-type-tag.complaint {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.suggestion-type-tag.suggestion {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
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

.suggestion-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.suggestion-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.suggestion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.suggestion-time {
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

.fab-btn {
  position: fixed;
  bottom: 40rpx;
  right: 30rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  padding: 24rpx 36rpx;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 122, 255, 0.3);
  z-index: 100;
}

.fab-icon {
  font-size: 36rpx;
  font-weight: 300;
}

.fab-text {
  font-size: 28rpx;
  font-weight: 500;
}
</style>
