<template>
  <view class="container">
    <view class="header" v-if="member">
      <view class="member-info">
        <view class="avatar">
          <text class="avatar-text">{{ member.name.charAt(0) }}</text>
        </view>
        <view class="info-content">
          <text class="member-name">{{ member.name }}</text>
          <view class="member-tags">
            <text class="tag-primary">{{ member.member_type }}</text>
            <text class="tag-default">卡号: {{ member.member_card_id }}</text>
          </view>
        </view>
      </view>
      
      <view class="balance-card">
        <view class="balance-item">
          <text class="balance-label">账户余额</text>
          <text class="balance-value">¥{{ tools.formatMoney(member.balance) }}</text>
        </view>
        <view class="recharge-btn" @click="goToRecharge">
          <text>充值</text>
        </view>
      </view>
    </view>

    <view class="login-prompt" v-else>
      <text class="prompt-text">请先登录</text>
      <button class="btn btn-primary" @click="goToLogin">立即登录</button>
    </view>

    <view class="menu-section" v-if="member">
      <view class="menu-item" @click="goToReservation">
        <view class="menu-icon reservation">
          <text class="icon-text">📅</text>
        </view>
        <view class="menu-content">
          <text class="menu-title">我的预约</text>
          <text class="menu-desc">查看已预约的课程和私教</text>
        </view>
        <view class="menu-arrow"></view>
      </view>

      <view class="menu-item" @click="goToCheckinHistory">
        <view class="menu-icon checkin">
          <text class="icon-text">📋</text>
        </view>
        <view class="menu-content">
          <text class="menu-title">签到记录</text>
          <text class="menu-desc">查看历史签到记录</text>
        </view>
        <view class="menu-arrow"></view>
      </view>

      <view class="menu-item" @click="goToTransactions">
        <view class="menu-icon transaction">
          <text class="icon-text">💰</text>
        </view>
        <view class="menu-content">
          <text class="menu-title">消费记录</text>
          <text class="menu-desc">查看充值和消费明细</text>
        </view>
        <view class="menu-arrow"></view>
      </view>

      <view class="divider"></view>

      <view class="menu-item" @click="goToAdmin">
        <view class="menu-icon admin">
          <text class="icon-text">⚙️</text>
        </view>
        <view class="menu-content">
          <text class="menu-title">管理后台</text>
          <text class="menu-desc">会员管理、课程管理等</text>
        </view>
        <view class="menu-arrow"></view>
      </view>

      <view class="divider"></view>

      <view class="menu-item" @click="handleLogout">
        <view class="menu-icon logout">
          <text class="icon-text">🚪</text>
        </view>
        <view class="menu-content">
          <text class="menu-title logout">退出登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { toast, tools } from '@/utils'

export default {
  setup() {
    const member = ref(null)

    const loadData = () => {
      const currentMember = tools.getCurrentMember()
      member.value = currentMember
    }

    const goToLogin = () => {
      uni.navigateTo({ url: '/pages/login/login' })
    }

    const goToRecharge = () => {
      uni.navigateTo({ url: '/pages/recharge/recharge' })
    }

    const goToReservation = () => {
      uni.navigateTo({ url: '/pages/reservation/reservation' })
    }

    const goToCheckinHistory = () => {
      uni.navigateTo({ url: '/pages/checkin-history/checkin-history' })
    }

    const goToTransactions = () => {
      uni.navigateTo({ url: '/pages/transactions/transactions' })
    }

    const goToAdmin = () => {
      uni.navigateTo({ url: '/pages/admin/admin' })
    }

    const handleLogout = async () => {
      const confirmed = await toast.confirm('提示', '确定要退出登录吗？')
      if (confirmed) {
        tools.clearCurrentMember()
        member.value = null
        toast.success('已退出登录')
        uni.switchTab({ url: '/pages/index/index' })
      }
    }

    onShow(() => {
      loadData()
    })

    return {
      member,
      tools,
      goToLogin,
      goToRecharge,
      goToReservation,
      goToCheckinHistory,
      goToTransactions,
      goToAdmin,
      handleLogout
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  padding: 40rpx 30rpx 30rpx;
  margin: -20rpx -20rpx 0;
  border-radius: 0 0 30rpx 30rpx;
}

.member-info {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.avatar-text {
  font-size: 44rpx;
  color: #ffffff;
  font-weight: bold;
}

.info-content {
  flex: 1;
}

.member-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 12rpx;
}

.member-tags {
  display: flex;
  align-items: center;
}

.tag-primary {
  display: inline-block;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  border-radius: 8rpx;
  font-size: 24rpx;
  margin-right: 16rpx;
}

.tag-default {
  display: inline-block;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.9);
  border-radius: 8rpx;
  font-size: 24rpx;
}

.balance-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.balance-item {
  flex: 1;
}

.balance-label {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8rpx;
}

.balance-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
}

.recharge-btn {
  padding: 16rpx 40rpx;
  background: #ffffff;
  border-radius: 32rpx;
  font-size: 28rpx;
  color: #409EFF;
  font-weight: 500;
}

.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.prompt-text {
  font-size: 30rpx;
  color: #909399;
  margin-bottom: 40rpx;
}

.menu-section {
  background: #ffffff;
  border-radius: 20rpx;
  margin-top: 30rpx;
  padding: 0 30rpx;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
}

.menu-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.menu-icon.reservation {
  background: rgba(64, 158, 255, 0.1);
}

.menu-icon.checkin {
  background: rgba(103, 194, 58, 0.1);
}

.menu-icon.transaction {
  background: rgba(230, 162, 60, 0.1);
}

.menu-icon.admin {
  background: rgba(144, 147, 153, 0.1);
}

.menu-icon.logout {
  background: rgba(245, 108, 108, 0.1);
}

.menu-content {
  flex: 1;
}

.menu-title {
  display: block;
  font-size: 30rpx;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6rpx;
}

.menu-title.logout {
  color: #F56C6C;
}

.menu-desc {
  font-size: 24rpx;
  color: #909399;
}

.menu-arrow {
  width: 16rpx;
  height: 16rpx;
  border-top: 3rpx solid #c0c4cc;
  border-right: 3rpx solid #c0c4cc;
  transform: rotate(45deg);
}

.divider {
  height: 1rpx;
  background: #f0f0f0;
  margin: 0 -30rpx;
}
</style>
