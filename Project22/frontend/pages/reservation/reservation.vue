<template>
  <view class="container">
    <view class="tab-bar">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'all' }"
        @click="switchTab('all')"
      >
        <text>全部</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'confirmed' }"
        @click="switchTab('confirmed')"
      >
        <text>已预约</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'cancelled' }"
        @click="switchTab('cancelled')"
      >
        <text>已取消</text>
      </view>
    </view>

    <view class="reservation-list">
      <view 
        v-for="reservation in reservations" 
        :key="reservation.id"
        class="reservation-card"
      >
        <view class="card-header">
          <view class="type-tag">
            <text :class="reservation.reservation_type === 'group' ? 'tag-primary' : 'tag-warning'">
              {{ reservation.reservation_type === 'group' ? '团体课程' : '私教课程' }}
            </text>
          </view>
          <view class="status-tag">
            <text 
              :class="reservation.status === 'confirmed' ? 'status-success' : 'status-cancelled'"
            >
              {{ reservation.status === 'confirmed' ? '已预约' : '已取消' }}
            </text>
          </view>
        </view>

        <view class="card-body">
          <view class="course-info">
            <text class="course-name">
              {{ reservation.course_name || `私教 - ${reservation.private_coach_name}` }}
            </text>
            <view class="course-detail">
              <text class="coach-name">
                {{ reservation.course_name ? `教练: ${reservation.coach_name}` : '' }}
              </text>
            </view>
          </view>
          <view class="time-info">
            <view class="time-item">
              <text class="time-label">日期</text>
              <text class="time-value">{{ reservation.reservation_date }}</text>
            </view>
            <view class="time-item">
              <text class="time-label">时间</text>
              <text class="time-value">{{ reservation.reservation_time }}</text>
            </view>
          </view>
        </view>

        <view class="card-footer" v-if="reservation.status === 'confirmed' && !isPastDate(reservation.reservation_date)">
          <button 
            class="btn btn-danger btn-sm cancel-btn" 
            @click="handleCancel(reservation)"
          >
            取消预约
          </button>
        </view>
      </view>

      <view class="empty" v-if="reservations.length === 0">
        <text class="empty-text">暂无预约记录</text>
      </view>
    </view>

    <view class="load-more" v-if="hasMore">
      <button class="btn btn-default btn-sm" @click="loadMore" :loading="loadingMore">
        加载更多
      </button>
    </view>
  </view>
</template>

<script>
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { request, toast, tools } from '@/utils'

export default {
  setup() {
    const activeTab = ref('all')
    const reservations = ref([])
    const page = ref(1)
    const hasMore = ref(true)
    const loadingMore = ref(false)
    const member = ref(null)

    const loadReservations = async (isLoadMore = false) => {
      if (!tools.isLoggedIn()) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }

      member.value = tools.getCurrentMember()
      
      if (!isLoadMore) {
        page.value = 1
        hasMore.value = true
        reservations.value = []
      }

      loadingMore.value = true
      
      try {
        const params = {
          page: page.value,
          per_page: 10
        }
        
        if (activeTab.value !== 'all') {
          params.status = activeTab.value
        }

        const res = await request({
          url: `/reservations/my/${member.value.id}`,
          method: 'GET',
          data: params
        })
        
        const newReservations = res.data.reservations || []
        
        if (isLoadMore) {
          reservations.value = [...reservations.value, ...newReservations]
        } else {
          reservations.value = newReservations
        }
        
        hasMore.value = newReservations.length >= 10
      } catch (e) {
        console.error('加载预约记录失败', e)
      } finally {
        loadingMore.value = false
      }
    }

    const switchTab = (tab) => {
      activeTab.value = tab
      loadReservations()
    }

    const loadMore = () => {
      if (hasMore.value && !loadingMore.value) {
        page.value += 1
        loadReservations(true)
      }
    }

    const isPastDate = (dateStr) => {
      if (!dateStr) return false
      const reservationDate = new Date(dateStr)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      return reservationDate < today
    }

    const handleCancel = async (reservation) => {
      const confirmed = await toast.confirm('取消预约', '确定要取消这个预约吗？')
      if (!confirmed) return

      try {
        await request({
          url: `/reservations/${reservation.id}/cancel`,
          method: 'POST'
        })
        
        toast.success('已取消预约')
        loadReservations()
      } catch (e) {
        console.error('取消预约失败', e)
      }
    }

    onLoad(() => {
      loadReservations()
    })

    onShow(() => {
      loadReservations()
    })

    return {
      activeTab,
      reservations,
      hasMore,
      loadingMore,
      switchTab,
      loadMore,
      isPastDate,
      handleCancel
    }
  }
}
</script>

<style scoped>
.container {
  padding: 0;
}

.tab-bar {
  display: flex;
  background: #ffffff;
  padding: 20rpx 0;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.tab-item text {
  font-size: 30rpx;
  color: #606266;
  padding: 12rpx 32rpx;
  border-radius: 32rpx;
}

.tab-item.active text {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  color: #ffffff;
  font-weight: 500;
}

.reservation-list {
  padding: 20rpx;
}

.reservation-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.tag-primary {
  display: inline-block;
  padding: 8rpx 20rpx;
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.tag-warning {
  display: inline-block;
  padding: 8rpx 20rpx;
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-success {
  display: inline-block;
  padding: 8rpx 20rpx;
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-cancelled {
  display: inline-block;
  padding: 8rpx 20rpx;
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.card-body {
  margin-bottom: 20rpx;
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
}

.time-info {
  display: flex;
  align-items: center;
}

.time-item {
  display: flex;
  align-items: center;
  margin-right: 40rpx;
}

.time-label {
  font-size: 26rpx;
  color: #909399;
  margin-right: 10rpx;
}

.time-value {
  font-size: 28rpx;
  color: #303133;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.cancel-btn {
  width: 160rpx;
  height: 64rpx;
  line-height: 64rpx;
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

.load-more {
  display: flex;
  justify-content: center;
  padding: 20rpx;
}
</style>
