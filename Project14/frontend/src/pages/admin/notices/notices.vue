<template>
  <view class="admin-notices-page">
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
        :class="{ active: activeTab === 'active' }"
        @click="activeTab = 'active'"
      >
        已发布
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'inactive' }"
        @click="activeTab = 'inactive'"
      >
        已下架
      </view>
    </view>
    
    <view class="create-btn" @click="goToEdit">
      <text class="create-icon">+</text>
      <text>发布公告</text>
    </view>
    
    <view class="notice-list">
      <view 
        class="notice-item" 
        v-for="notice in filteredNotices" 
        :key="notice.id"
        @click="goToEdit(notice.id)"
      >
        <view class="notice-header">
          <view class="notice-type-tag" :class="notice.notice_type">
            {{ getNoticeTypeName(notice.notice_type) }}
          </view>
          <view class="notice-status" :class="notice.is_active ? 'active' : 'inactive'">
            {{ notice.is_active ? '已发布' : '已下架' }}
          </view>
        </view>
        <view class="notice-title">{{ notice.title }}</view>
        <view class="notice-content">{{ notice.content.substring(0, 60) }}...</view>
        <view class="notice-footer">
          <view class="notice-time">发布时间: {{ formatDate(notice.publish_date) }}</view>
          <view class="priority-tag" :class="notice.priority">
            {{ getPriorityName(notice.priority) }}
          </view>
        </view>
        <view class="action-section">
          <view 
            class="action-btn" 
            :class="notice.is_active ? 'offline' : 'online'"
            @click.stop="toggleStatus(notice)"
          >
            {{ notice.is_active ? '下架' : '发布' }}
          </view>
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
import { ref, computed, onMounted, watch } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const notices = ref<any[]>([])

const filteredNotices = computed(() => {
  if (activeTab.value === 'all') {
    return notices.value
  }
  if (activeTab.value === 'active') {
    return notices.value.filter(n => n.is_active)
  }
  return notices.value.filter(n => !n.is_active)
})

function getNoticeTypeName(type: string) {
  const nameMap: Record<string, string> = {
    'important': '重要',
    'maintenance': '维护',
    'general': '通知'
  }
  return nameMap[type] || '通知'
}

function getPriorityName(priority: string) {
  const nameMap: Record<string, string> = {
    'high': '紧急',
    'normal': '普通',
    'low': '普通'
  }
  return nameMap[priority] || '普通'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function goToEdit(id?: number) {
  if (id) {
    uni.navigateTo({
      url: `/pages/admin/notice-edit/notice-edit?id=${id}`
    })
  } else {
    uni.navigateTo({
      url: '/pages/admin/notice-edit/notice-edit'
    })
  }
}

async function toggleStatus(notice: any) {
  const action = notice.is_active ? '下架' : '发布'
  uni.showModal({
    title: '确认操作',
    content: `确定要${action}该公告吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await request.put(`/api/admin/notices/${notice.id}`, {
            is_active: !notice.is_active
          })
          uni.showToast({
            title: `${action}成功`,
            icon: 'success'
          })
          loadNotices()
        } catch (error) {
          console.error('操作失败', error)
        }
      }
    }
  })
}

async function loadNotices() {
  try {
    const res = await request.get('/api/admin/notices')
    if (Array.isArray(res)) {
      notices.value = res
    }
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

watch(activeTab, () => {
  // Tab切换时前端过滤，无需重新加载
})

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.admin-notices-page {
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

.create-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  margin: 20rpx 30rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: 500;
}

.create-icon {
  font-size: 36rpx;
  font-weight: 300;
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.notice-type-tag {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.notice-type-tag.important {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.notice-type-tag.maintenance {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.notice-type-tag.general {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.notice-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.notice-status.active {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.notice-status.inactive {
  background-color: rgba(153, 153, 153, 0.1);
  color: #999;
}

.notice-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 12rpx;
}

.notice-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16rpx;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.notice-time {
  font-size: 24rpx;
  color: #999;
}

.priority-tag {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.priority-tag.high {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.priority-tag.normal,
.priority-tag.low {
  background-color: rgba(0, 0, 0, 0.05);
  color: #666;
}

.action-section {
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  display: inline-block;
  font-size: 26rpx;
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
}

.action-btn.online {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.action-btn.offline {
  background-color: rgba(153, 153, 153, 0.1);
  color: #666;
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
