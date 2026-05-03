<template>
  <view class="container">
    <view class="balance-card">
      <view class="balance-label">当前余额</view>
      <view class="balance-value">¥{{ tools.formatMoney(member?.balance || 0) }}</view>
    </view>

    <view class="section">
      <view class="section-title">选择充值金额</view>
      <view class="amount-grid">
        <view 
          v-for="(item, index) in rechargeOptions" 
          :key="index"
          class="amount-item"
          :class="{ active: selectedAmount === item.value }"
          @click="selectAmount(item.value)"
        >
          <text class="amount-value">¥{{ item.value }}</text>
          <text class="amount-gift" v-if="item.gift">赠¥{{ item.gift }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-title">自定义金额</view>
      <view class="custom-amount">
        <text class="prefix">¥</text>
        <input 
          class="amount-input" 
          type="digit" 
          placeholder="请输入金额" 
          v-model="customAmount"
          @input="onCustomAmountInput"
        />
      </view>
    </view>

    <view class="section">
      <view class="section-title">支付方式</view>
      <view class="payment-list">
        <view 
          v-for="method in paymentMethods" 
          :key="method.value"
          class="payment-item"
          :class="{ active: selectedPayment === method.value }"
          @click="selectPayment(method.value)"
        >
          <view class="payment-icon" :class="method.value">
            <text class="icon-text">{{ method.icon }}</text>
          </view>
          <view class="payment-info">
            <text class="payment-name">{{ method.name }}</text>
            <text class="payment-desc" v-if="method.desc">{{ method.desc }}</text>
          </view>
          <view class="payment-radio" :class="{ checked: selectedPayment === method.value }">
            <view class="radio-inner" v-if="selectedPayment === method.value"></view>
          </view>
        </view>
      </view>
    </view>

    <view class="recharge-summary">
      <view class="summary-row">
        <text class="summary-label">充值金额</text>
        <text class="summary-value">¥{{ tools.formatMoney(totalAmount) }}</text>
      </view>
      <view class="summary-row" v-if="giftAmount > 0">
        <text class="summary-label">赠送金额</text>
        <text class="summary-value gift">+¥{{ tools.formatMoney(giftAmount) }}</text>
      </view>
      <view class="divider"></view>
      <view class="summary-row total">
        <text class="summary-label">实付金额</text>
        <text class="summary-value">¥{{ tools.formatMoney(totalAmount) }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="total-info">
        <text class="total-label">实付:</text>
        <text class="total-amount">¥{{ tools.formatMoney(totalAmount) }}</text>
      </view>
      <button class="btn btn-primary recharge-btn" @click="handleRecharge" :loading="loading">
        立即充值
      </button>
    </view>
  </view>
</template>

<script>
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, computed } from 'vue'
import { request, toast, tools } from '@/utils'

export default {
  setup() {
    const member = ref(null)
    const selectedAmount = ref(null)
    const customAmount = ref('')
    const selectedPayment = ref('wechat')
    const loading = ref(false)

    const rechargeOptions = [
      { value: 100, gift: 10 },
      { value: 200, gift: 30 },
      { value: 500, gift: 100 },
      { value: 1000, gift: 250 },
      { value: 2000, gift: 600 },
      { value: 5000, gift: 1500 }
    ]

    const paymentMethods = [
      { value: 'wechat', name: '微信支付', icon: '💚', desc: '推荐使用' },
      { value: 'alipay', name: '支付宝', icon: '💙', desc: '' },
      { value: 'balance', name: '余额支付', icon: '💰', desc: '使用账户余额' }
    ]

    const totalAmount = computed(() => {
      if (customAmount.value && parseFloat(customAmount.value) > 0) {
        return parseFloat(customAmount.value)
      }
      return selectedAmount.value || 0
    })

    const giftAmount = computed(() => {
      if (customAmount.value && parseFloat(customAmount.value) > 0) {
        return 0
      }
      const option = rechargeOptions.find(item => item.value === selectedAmount.value)
      return option?.gift || 0
    })

    const loadData = () => {
      const currentMember = tools.getCurrentMember()
      if (currentMember) {
        member.value = currentMember
      } else {
        uni.navigateTo({ url: '/pages/login/login' })
      }
    }

    const selectAmount = (value) => {
      selectedAmount.value = value
      customAmount.value = ''
    }

    const onCustomAmountInput = (e) => {
      if (e.detail.value && parseFloat(e.detail.value) > 0) {
        selectedAmount.value = null
      }
    }

    const selectPayment = (value) => {
      selectedPayment.value = value
    }

    const handleRecharge = async () => {
      if (!totalAmount.value || totalAmount.value <= 0) {
        toast.error('请选择或输入充值金额')
        return
      }
      
      if (totalAmount.value < 1) {
        toast.error('充值金额不能小于1元')
        return
      }

      loading.value = true
      
      try {
        const res = await request({
          url: `/members/${member.value.id}/recharge`,
          method: 'POST',
          data: {
            amount: totalAmount.value,
            payment_method: selectedPayment.value
          }
        })
        
        toast.success('充值成功')
        
        member.value.balance = res.data.balance
        tools.setCurrentMember(member.value)
        
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        console.error('充值失败', e)
      } finally {
        loading.value = false
      }
    }

    onLoad(() => {
      loadData()
    })

    onShow(() => {
      loadData()
    })

    return {
      member,
      rechargeOptions,
      paymentMethods,
      selectedAmount,
      customAmount,
      selectedPayment,
      loading,
      totalAmount,
      giftAmount,
      tools,
      selectAmount,
      onCustomAmountInput,
      selectPayment,
      handleRecharge
    }
  }
}
</script>

<style scoped>
.container {
  padding-bottom: 140rpx;
}

.balance-card {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
  margin-bottom: 30rpx;
}

.balance-label {
  display: block;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 16rpx;
}

.balance-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
}

.section {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #303133;
  margin-bottom: 20rpx;
  padding-left: 10rpx;
  border-left: 6rpx solid #409EFF;
}

.amount-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.amount-item {
  width: calc(33.33% - 14rpx);
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 16rpx;
  text-align: center;
  border: 2rpx solid #e4e7ed;
  box-sizing: border-box;
}

.amount-item.active {
  border-color: #409EFF;
  background: rgba(64, 158, 255, 0.05);
}

.amount-value {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8rpx;
}

.amount-item.active .amount-value {
  color: #409EFF;
}

.amount-gift {
  font-size: 22rpx;
  color: #F56C6C;
}

.custom-amount {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 30rpx;
  border: 2rpx solid #e4e7ed;
}

.prefix {
  font-size: 32rpx;
  color: #409EFF;
  margin-right: 10rpx;
}

.amount-input {
  flex: 1;
  font-size: 32rpx;
  color: #303133;
}

.payment-list {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 0 30rpx;
}

.payment-item {
  display: flex;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.payment-item:last-child {
  border-bottom: none;
}

.payment-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.payment-icon.wechat {
  background: rgba(25, 206, 95, 0.1);
}

.payment-icon.alipay {
  background: rgba(22, 155, 213, 0.1);
}

.payment-icon.balance {
  background: rgba(64, 158, 255, 0.1);
}

.payment-info {
  flex: 1;
}

.payment-name {
  display: block;
  font-size: 30rpx;
  color: #303133;
  margin-bottom: 6rpx;
}

.payment-desc {
  font-size: 24rpx;
  color: #909399;
}

.payment-radio {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  border: 2rpx solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-radio.checked {
  border-color: #409EFF;
}

.radio-inner {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: #409EFF;
}

.recharge-summary {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-row.total {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.summary-label {
  font-size: 28rpx;
  color: #606266;
}

.summary-row.total .summary-label {
  font-size: 30rpx;
  color: #303133;
  font-weight: 500;
}

.summary-value {
  font-size: 28rpx;
  color: #303133;
}

.summary-value.gift {
  color: #67C23A;
}

.summary-row.total .summary-value {
  font-size: 32rpx;
  font-weight: bold;
  color: #F56C6C;
}

.divider {
  height: 1rpx;
  background: #f0f0f0;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}

.total-info {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 28rpx;
  color: #606266;
  margin-right: 10rpx;
}

.total-amount {
  font-size: 40rpx;
  font-weight: bold;
  color: #F56C6C;
}

.recharge-btn {
  width: 280rpx;
  height: 80rpx;
  line-height: 80rpx;
}
</style>
