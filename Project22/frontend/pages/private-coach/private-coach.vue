<template>
  <view class="container">
    <view class="coach-list">
      <view 
        v-for="coach in coaches" 
        :key="coach.id"
        class="coach-card"
        @click="selectCoach(coach)"
      >
        <view class="coach-header">
          <view class="coach-avatar">
            <text class="avatar-text">{{ coach.name.charAt(0) }}</text>
          </view>
          <view class="coach-info">
            <text class="coach-name">{{ coach.name }}</text>
            <view class="coach-tags">
              <text class="tag-primary" v-if="coach.specialty">{{ coach.specialty }}</text>
              <text class="tag-default" v-if="coach.experience">{{ coach.experience }}年经验</text>
            </view>
          </view>
        </view>
        <view class="coach-desc" v-if="coach.description">
          <text>{{ coach.description }}</text>
        </view>
        <view class="coach-footer">
          <text class="price-text">私教价格: ¥100/小时</text>
          <button class="btn btn-primary btn-sm reserve-btn">预约</button>
        </view>
      </view>

      <view class="empty" v-if="coaches.length === 0 && !loading">
        <text class="empty-text">暂无教练信息</text>
      </view>
    </view>

    <view class="reservation-modal" v-if="showModal" @click="closeModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">预约私教</text>
          <view class="close-btn" @click="closeModal">
            <text>✕</text>
          </view>
        </view>

        <view class="modal-body">
          <view class="selected-coach" v-if="selectedCoach">
            <view class="coach-avatar small">
              <text class="avatar-text small">{{ selectedCoach.name.charAt(0) }}</text>
            </view>
            <view class="coach-info">
              <text class="coach-name">{{ selectedCoach.name }}</text>
              <text class="coach-specialty">{{ selectedCoach.specialty }}</text>
            </view>
          </view>

          <view class="form-item">
            <text class="form-label">预约日期</text>
            <picker 
              mode="date" 
              :value="reservationDate"
              :start="minDate"
              :end="maxDate"
              @change="onDateChange"
            >
              <view class="picker-input">
                <text class="picker-text">{{ reservationDate || '请选择日期' }}</text>
                <view class="picker-arrow"></view>
              </view>
            </picker>
          </view>

          <view class="form-item">
            <text class="form-label">预约时间</text>
            <picker 
              mode="selector" 
              :range="timeSlots"
              :value="timeIndex"
              @change="onTimeChange"
            >
              <view class="picker-input">
                <text class="picker-text">{{ timeSlots[timeIndex] || '请选择时间' }}</text>
                <view class="picker-arrow"></view>
              </view>
            </picker>
          </view>

          <view class="price-info">
            <text class="price-label">预约费用</text>
            <text class="price-value">¥100.00</text>
          </view>

          <view class="balance-info">
            <text class="balance-label">账户余额</text>
            <text class="balance-value" :class="{ insufficient: member?.balance < 100 }">
              ¥{{ tools.formatMoney(member?.balance || 0) }}
            </text>
          </view>
        </view>

        <view class="modal-footer">
          <button class="btn btn-default cancel-btn" @click="closeModal">取消</button>
          <button 
            class="btn btn-primary confirm-btn" 
            @click="handleConfirmReserve"
            :loading="loading"
          >
            确认预约
          </button>
        </view>
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
    const coaches = ref([])
    const showModal = ref(false)
    const selectedCoach = ref(null)
    const reservationDate = ref('')
    const timeIndex = ref(0)
    const loading = ref(false)
    const member = ref(null)

    const timeSlots = [
      '09:00:00', '10:00:00', '11:00:00',
      '14:00:00', '15:00:00', '16:00:00', '17:00:00',
      '19:00:00', '20:00:00'
    ]

    const minDate = computed(() => {
      return tools.formatDate(new Date())
    })

    const maxDate = computed(() => {
      const date = new Date()
      date.setDate(date.getDate() + 30)
      return tools.formatDate(date)
    })

    const loadCoaches = async () => {
      try {
        const res = await request({
          url: '/coaches',
          method: 'GET',
          data: {
            is_active: true,
            per_page: 20
          }
        })
        coaches.value = res.data.coaches || []
      } catch (e) {
        console.error('加载教练列表失败', e)
      }
    }

    const selectCoach = (coach) => {
      if (!tools.isLoggedIn()) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }

      member.value = tools.getCurrentMember()
      selectedCoach.value = coach
      reservationDate.value = ''
      timeIndex.value = 0
      showModal.value = true
    }

    const onDateChange = (e) => {
      reservationDate.value = e.detail.value
    }

    const onTimeChange = (e) => {
      timeIndex.value = e.detail.value
    }

    const closeModal = () => {
      showModal.value = false
      selectedCoach.value = null
    }

    const handleConfirmReserve = async () => {
      if (!reservationDate.value) {
        toast.error('请选择预约日期')
        return
      }

      if (member.value.balance < 100) {
        toast.error('余额不足，请先充值')
        return
      }

      loading.value = true
      
      try {
        const res = await request({
          url: '/reservations/private',
          method: 'POST',
          data: {
            member_id: member.value.id,
            private_coach_id: selectedCoach.value.id,
            reservation_date: reservationDate.value,
            reservation_time: timeSlots[timeIndex.value],
            price: 100
          }
        })
        
        toast.success('预约成功')
        
        member.value.balance = res.data.balance
        tools.setCurrentMember(member.value)
        
        closeModal()
      } catch (e) {
        console.error('预约失败', e)
      } finally {
        loading.value = false
      }
    }

    onLoad(() => {
      loadCoaches()
    })

    onShow(() => {
      loadCoaches()
    })

    return {
      coaches,
      showModal,
      selectedCoach,
      reservationDate,
      timeIndex,
      timeSlots,
      loading,
      member,
      minDate,
      maxDate,
      tools,
      selectCoach,
      onDateChange,
      onTimeChange,
      closeModal,
      handleConfirmReserve
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20rpx;
}

.coach-list {
  padding-bottom: 20rpx;
}

.coach-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.coach-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.coach-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.coach-avatar.small {
  width: 80rpx;
  height: 80rpx;
}

.avatar-text {
  font-size: 40rpx;
  color: #ffffff;
  font-weight: bold;
}

.avatar-text.small {
  font-size: 32rpx;
}

.coach-info {
  flex: 1;
}

.coach-name {
  display: block;
  font-size: 32rpx;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12rpx;
}

.coach-tags {
  display: flex;
  align-items: center;
}

.tag-primary {
  display: inline-block;
  padding: 6rpx 16rpx;
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
  border-radius: 8rpx;
  font-size: 24rpx;
  margin-right: 16rpx;
}

.tag-default {
  display: inline-block;
  padding: 6rpx 16rpx;
  background: #f5f7fa;
  color: #909399;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.coach-desc {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.coach-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.price-text {
  font-size: 28rpx;
  color: #606266;
}

.reserve-btn {
  width: 140rpx;
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

.reservation-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.modal-content {
  width: 100%;
  background: #ffffff;
  border-radius: 30rpx 30rpx 0 0;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 500;
  color: #303133;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn text {
  font-size: 36rpx;
  color: #909399;
}

.modal-body {
  padding: 30rpx;
}

.selected-coach {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
}

.coach-specialty {
  font-size: 24rpx;
  color: #909399;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #606266;
  margin-bottom: 16rpx;
}

.picker-input {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
}

.picker-text {
  font-size: 30rpx;
  color: #303133;
}

.picker-arrow {
  width: 16rpx;
  height: 16rpx;
  border-top: 3rpx solid #c0c4cc;
  border-right: 3rpx solid #c0c4cc;
  transform: rotate(135deg);
}

.price-info,
.balance-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
  border-top: 1rpx solid #f0f0f0;
}

.price-label,
.balance-label {
  font-size: 28rpx;
  color: #606266;
}

.price-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #F56C6C;
}

.balance-value {
  font-size: 30rpx;
  color: #303133;
}

.balance-value.insufficient {
  color: #F56C6C;
}

.modal-footer {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-top: 1rpx solid #f0f0f0;
  gap: 20rpx;
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
}
</style>
