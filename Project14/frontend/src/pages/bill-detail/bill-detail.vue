<template>
  <view class="bill-detail-page" v-if="bill">
    <view class="status-header" :class="bill.status">
      <view class="status-icon">{{ getStatusIcon() }}</view>
      <view class="status-text">{{ getStatusText(bill.status) }}</view>
    </view>
    
    <view class="amount-section">
      <view class="amount-label">账单金额</view>
      <view class="amount-value">¥ {{ bill.amount.toFixed(2) }}</view>
    </view>
    
    <view class="info-section">
      <view class="section-title">账单详情</view>
      <view class="info-list">
        <view class="info-item">
          <text class="info-label">账单类型</text>
          <text class="info-value">{{ bill.bill_type }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">计费周期</text>
          <text class="info-value">{{ bill.period }}</text>
        </view>
        <view class="info-item" v-if="bill.description">
          <text class="info-label">账单说明</text>
          <text class="info-value">{{ bill.description }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">生成时间</text>
          <text class="info-value">{{ formatDate(bill.created_at) }}</text>
        </view>
        <view class="info-item" v-if="bill.paid_at">
          <text class="info-label">支付时间</text>
          <text class="info-value">{{ formatDate(bill.paid_at) }}</text>
        </view>
      </view>
    </view>
    
    <view class="bottom-actions" v-if="bill.status === 'unpaid'">
      <view class="pay-btn" @click="handlePay">
        立即支付 ¥{{ bill.amount.toFixed(2) }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { request } from '@/utils/request'

const bill = ref<any>(null)

function getStatusIcon() {
  if (bill.value?.status === 'paid') return '✅'
  return '💳'
}

function getStatusText(status: string) {
  const textMap: Record<string, string> = {
    'paid': '已支付',
    'unpaid': '待支付'
  }
  return textMap[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function handlePay() {
  uni.showModal({
    title: '确认支付',
    content: `确认支付 ¥${bill.value.amount.toFixed(2)} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await request.post(`/api/bills/${bill.value.id}/pay`)
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          })
          setTimeout(() => {
            loadBillDetail()
          }, 1500)
        } catch (error) {
          console.error('支付失败', error)
        }
      }
    }
  })
}

async function loadBillDetail() {
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
    const res = await request.get(`/api/bills/${id}`)
    bill.value = res
  } catch (error) {
    console.error('加载账单详情失败', error)
  }
}

onMounted(() => {
  loadBillDetail()
})
</script>

<style scoped>
.bill-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 180rpx;
}

.status-header {
  padding: 60rpx 30rpx;
  text-align: center;
}

.status-header.paid {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
}

.status-header.unpaid {
  background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
}

.status-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.status-text {
  font-size: 36rpx;
  font-weight: 500;
  color: #fff;
}

.amount-section {
  background-color: #fff;
  margin: -30rpx 30rpx 0;
  border-radius: 16rpx;
  padding: 40rpx;
  text-align: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.amount-label {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.amount-value {
  font-size: 64rpx;
  font-weight: bold;
  color: #ff4d4f;
}

.info-section {
  margin: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.info-list {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 28rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #333;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.pay-btn {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 500;
}
</style>
