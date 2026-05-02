<template>
  <view class="bills-page">
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
        :class="{ active: activeTab === 'unpaid' }"
        @click="activeTab = 'unpaid'"
      >
        待支付
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 'paid' }"
        @click="activeTab = 'paid'"
      >
        已支付
      </view>
    </view>
    
    <view class="summary-card" v-if="activeTab === 'unpaid' && unpaidAmount > 0">
      <view class="summary-title">待缴金额</view>
      <view class="summary-amount">¥ {{ unpaidAmount.toFixed(2) }}</view>
      <view class="summary-count">共 {{ unpaidCount }} 笔账单</view>
    </view>
    
    <view class="bill-list">
      <view 
        class="bill-item" 
        v-for="bill in filteredBills" 
        :key="bill.id"
        @click="goToDetail(bill.id)"
      >
        <view class="bill-header">
          <view class="bill-type">{{ bill.bill_type }}</view>
          <view class="bill-status" :class="getStatusClass(bill.status)">
            {{ getStatusText(bill.status) }}
          </view>
        </view>
        <view class="bill-content">
          <view class="bill-desc">{{ bill.description || bill.period }}</view>
          <view class="bill-period">{{ bill.period }}</view>
        </view>
        <view class="bill-footer">
          <view class="bill-date">{{ formatDate(bill.created_at) }}</view>
          <view class="bill-amount">
            <text class="amount-label">¥</text>
            <text class="amount-value">{{ bill.amount.toFixed(2) }}</text>
          </view>
        </view>
        <view class="pay-btn" v-if="bill.status === 'unpaid'" @click.stop="handlePay(bill)">
          立即支付
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredBills.length === 0">
        <view class="empty-icon">📄</view>
        <view class="empty-text">暂无账单记录</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const bills = ref<any[]>([])

const filteredBills = computed(() => {
  if (activeTab.value === 'all') {
    return bills.value
  }
  return bills.value.filter(bill => bill.status === activeTab.value)
})

const unpaidAmount = computed(() => {
  return bills.value
    .filter(bill => bill.status === 'unpaid')
    .reduce((sum, bill) => sum + bill.amount, 0)
})

const unpaidCount = computed(() => {
  return bills.value.filter(bill => bill.status === 'unpaid').length
})

function getStatusClass(status: string) {
  const classMap: Record<string, string> = {
    'paid': 'status-paid',
    'unpaid': 'status-unpaid'
  }
  return classMap[status] || ''
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
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function goToDetail(id: number) {
  uni.navigateTo({
    url: `/pages/bill-detail/bill-detail?id=${id}`
  })
}

async function handlePay(bill: any) {
  uni.showModal({
    title: '确认支付',
    content: `确认支付 ¥${bill.amount.toFixed(2)} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await request.post(`/api/bills/${bill.id}/pay`)
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          })
          loadBills()
        } catch (error) {
          console.error('支付失败', error)
        }
      }
    }
  })
}

async function loadBills() {
  try {
    const res = await request.get('/api/bills')
    if (Array.isArray(res)) {
      bills.value = res
    }
  } catch (error) {
    console.error('加载账单失败', error)
  }
}

onMounted(() => {
  loadBills()
})

watch(activeTab, () => {
  // Tab切换时不需要重新加载数据，只是前端过滤
})
</script>

<style scoped>
.bills-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

.tabs {
  display: flex;
  background-color: #fff;
  padding: 20rpx 30rpx;
  gap: 30rpx;
}

.tab-item {
  padding: 16rpx 32rpx;
  font-size: 28rpx;
  color: #666;
  border-radius: 40rpx;
  position: relative;
}

.tab-item.active {
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  font-weight: 500;
}

.summary-card {
  margin: 20rpx 30rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  color: #fff;
}

.summary-title {
  font-size: 26rpx;
  opacity: 0.8;
  margin-bottom: 10rpx;
}

.summary-amount {
  font-size: 56rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}

.summary-count {
  font-size: 24rpx;
  opacity: 0.8;
}

.bill-list {
  padding: 0 30rpx;
  margin-top: 20rpx;
}

.bill-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.bill-type {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.bill-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.bill-status.status-paid {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.bill-status.status-unpaid {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.bill-content {
  margin-bottom: 20rpx;
}

.bill-desc {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.bill-period {
  font-size: 24rpx;
  color: #999;
}

.bill-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.bill-date {
  font-size: 24rpx;
  color: #999;
}

.bill-amount {
  display: flex;
  align-items: baseline;
}

.amount-label {
  font-size: 24rpx;
  color: #ff4d4f;
  margin-right: 4rpx;
}

.amount-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff4d4f;
}

.pay-btn {
  margin-top: 20rpx;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  text-align: center;
  padding: 20rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
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
