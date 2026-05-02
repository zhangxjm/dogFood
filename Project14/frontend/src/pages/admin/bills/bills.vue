<template>
  <view class="admin-bills-page">
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
    
    <view class="create-btn" @click="showCreateModal = true">
      <text class="create-icon">+</text>
      <text>新建账单</text>
    </view>
    
    <view class="bill-list">
      <view 
        class="bill-item" 
        v-for="bill in filteredBills" 
        :key="bill.id"
      >
        <view class="bill-header">
          <view class="owner-info">
            <view class="owner-name">{{ bill.owner_name || '未知业主' }}</view>
            <view class="owner-room">{{ bill.room_number || '未知房号' }}</view>
          </view>
          <view class="bill-status" :class="getStatusClass(bill.status)">
            {{ getStatusText(bill.status) }}
          </view>
        </view>
        <view class="bill-content">
          <view class="bill-type">{{ bill.bill_type }}</view>
          <view class="bill-period">{{ bill.period }}</view>
        </view>
        <view class="bill-desc" v-if="bill.description">{{ bill.description }}</view>
        <view class="bill-footer">
          <view class="bill-date">{{ formatDate(bill.created_at) }}</view>
          <view class="bill-amount">
            <text class="amount-label">¥</text>
            <text class="amount-value">{{ bill.amount.toFixed(2) }}</text>
          </view>
        </view>
        <view class="action-section" v-if="bill.status === 'unpaid'">
          <view class="action-btn remind" @click="sendRemind(bill)">
            催缴
          </view>
        </view>
      </view>
      
      <view class="empty-state" v-if="filteredBills.length === 0">
        <view class="empty-icon">📄</view>
        <view class="empty-text">暂无账单记录</view>
      </view>
    </view>
    
    <view class="modal-mask" v-if="showCreateModal" @click="showCreateModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-title">新建账单</view>
        
        <view class="form-item">
          <view class="form-label">选择业主</view>
          <picker :value="selectedUserIndex" :range="users" range-key="name" @change="onUserChange">
            <view class="picker-value">
              {{ users[selectedUserIndex]?.name || '请选择业主' }}
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <view class="form-label">账单类型</view>
          <view class="type-selector">
            <view 
              v-for="type in billTypes" 
              :key="type"
              class="type-item"
              :class="{ active: form.bill_type === type }"
              @click="form.bill_type = type"
            >
              {{ type }}
            </view>
          </view>
        </view>
        
        <view class="form-item">
          <view class="form-label">费用金额</view>
          <input 
            v-model="form.amount" 
            type="digit" 
            placeholder="请输入金额"
            class="form-input"
          />
        </view>
        
        <view class="form-item">
          <view class="form-label">计费周期</view>
          <input 
            v-model="form.period" 
            type="text" 
            placeholder="例如：2024-01"
            class="form-input"
          />
        </view>
        
        <view class="form-item">
          <view class="form-label">备注说明</view>
          <textarea 
            v-model="form.description" 
            placeholder="请输入备注说明（选填）"
            class="form-textarea"
          ></textarea>
        </view>
        
        <view class="modal-btns">
          <view class="modal-btn cancel" @click="showCreateModal = false">取消</view>
          <view class="modal-btn confirm" @click="createBill">创建</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { request } from '@/utils/request'

const activeTab = ref('all')
const bills = ref<any[]>([])
const users = ref<any[]>([])
const selectedUserIndex = ref(-1)
const showCreateModal = ref(false)

const billTypes = ['物业费', '水电费', '停车费', '其他']

const form = ref({
  bill_type: '物业费',
  amount: '',
  period: '',
  description: ''
})

const filteredBills = computed(() => {
  if (activeTab.value === 'all') {
    return bills.value
  }
  return bills.value.filter(bill => bill.status === activeTab.value)
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

function onUserChange(e: any) {
  selectedUserIndex.value = e.detail.value
}

function sendRemind(bill: any) {
  uni.showToast({
    title: '已发送催缴通知',
    icon: 'success'
  })
}

async function createBill() {
  if (selectedUserIndex.value < 0) {
    uni.showToast({ title: '请选择业主', icon: 'none' })
    return
  }
  if (!form.value.amount || parseFloat(form.value.amount) <= 0) {
    uni.showToast({ title: '请输入有效金额', icon: 'none' })
    return
  }
  if (!form.value.period) {
    uni.showToast({ title: '请输入计费周期', icon: 'none' })
    return
  }
  
  try {
    const user = users.value[selectedUserIndex.value]
    await request.post('/api/admin/bills', {
      owner_id: user.id,
      bill_type: form.value.bill_type,
      amount: parseFloat(form.value.amount),
      period: form.value.period,
      description: form.value.description
    })
    
    uni.showToast({ title: '创建成功', icon: 'success' })
    showCreateModal.value = false
    form.value = {
      bill_type: '物业费',
      amount: '',
      period: '',
      description: ''
    }
    selectedUserIndex.value = -1
    loadBills()
  } catch (error) {
    console.error('创建账单失败', error)
  }
}

async function loadBills() {
  try {
    const params: any = {}
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    
    const res = await request.get('/api/admin/bills', params)
    if (Array.isArray(res)) {
      bills.value = res
    }
  } catch (error) {
    console.error('加载账单失败', error)
  }
}

async function loadUsers() {
  try {
    const res = await request.get('/api/admin/users')
    if (Array.isArray(res)) {
      users.value = res.filter((u: any) => u.role !== 'admin')
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

watch(activeTab, () => {
  loadBills()
})

onMounted(() => {
  loadBills()
  loadUsers()
})
</script>

<style scoped>
.admin-bills-page {
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

.bill-list {
  padding: 20rpx;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.bill-type {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.bill-period {
  font-size: 24rpx;
  color: #999;
}

.bill-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.bill-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16rpx;
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

.action-section {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  display: inline-block;
  font-size: 26rpx;
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
}

.action-btn.remind {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
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

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 80%;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.picker-value {
  background-color: #f5f5f5;
  padding: 20rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
}

.type-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.type-item {
  padding: 12rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
}

.type-item.active {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.form-input {
  background-color: #f5f5f5;
  padding: 20rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
}

.form-textarea {
  width: 100%;
  height: 160rpx;
  background-color: #f5f5f5;
  padding: 20rpx;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
}

.modal-btns {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.modal-btn {
  flex: 1;
  text-align: center;
  padding: 28rpx;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 500;
}

.modal-btn.cancel {
  background-color: #f5f5f5;
  color: #666;
}

.modal-btn.confirm {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
}
</style>
