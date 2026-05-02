<template>
  <view class="visitor-register-page">
    <view class="form-section">
      <view class="section-title">访客信息</view>
      
      <view class="input-group">
        <view class="input-item">
          <label>访客姓名</label>
          <input 
            type="text" 
            v-model="form.visitor_name" 
            placeholder="请输入访客姓名"
          />
        </view>
        
        <view class="input-item">
          <label>联系电话</label>
          <input 
            type="number" 
            v-model="form.visitor_phone" 
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </view>
        
        <view class="input-item">
          <label>身份证号</label>
          <input 
            type="text" 
            v-model="form.visitor_id_card" 
            placeholder="请输入身份证号（选填）"
          />
        </view>
        
        <view class="input-item">
          <label>访问日期</label>
          <picker mode="date" :value="form.visit_date" @change="onDateChange">
            <view class="picker-value">
              {{ form.visit_date || '请选择' }}
            </view>
          </picker>
        </view>
        
        <view class="input-item">
          <label>访问时间</label>
          <picker mode="time" :value="form.visit_time" @change="onTimeChange">
            <view class="picker-value">
              {{ form.visit_time || '请选择' }}
            </view>
          </picker>
        </view>
        
        <view class="input-item textarea-item">
          <label>来访事由</label>
          <textarea 
            v-model="form.purpose" 
            placeholder="请输入来访事由（选填）"
            :maxlength="200"
          />
        </view>
      </view>
    </view>
    
    <view class="bottom-actions">
      <view class="submit-btn" @click="handleSubmit">
        提交登记
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { request } from '@/utils/request'

const form = ref({
  visitor_name: '',
  visitor_phone: '',
  visitor_id_card: '',
  visit_date: '',
  visit_time: '',
  purpose: ''
})

function onDateChange(e: any) {
  form.value.visit_date = e.detail.value
}

function onTimeChange(e: any) {
  form.value.visit_time = e.detail.value
}

async function handleSubmit() {
  if (!form.value.visitor_name.trim()) {
    uni.showToast({
      title: '请输入访客姓名',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.visitor_phone.trim()) {
    uni.showToast({
      title: '请输入联系电话',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.visit_date) {
    uni.showToast({
      title: '请选择访问日期',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.visit_time) {
    uni.showToast({
      title: '请选择访问时间',
      icon: 'none'
    })
    return
  }
  
  try {
    await request.post('/api/visitors', {
      visitor_name: form.value.visitor_name,
      visitor_phone: form.value.visitor_phone,
      visitor_id_card: form.value.visitor_id_card,
      visit_date: form.value.visit_date,
      visit_time: form.value.visit_time,
      purpose: form.value.purpose
    })
    
    uni.showToast({
      title: '登记成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('登记失败', error)
  }
}
</script>

<style scoped>
.visitor-register-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 180rpx;
}

.form-section {
  padding: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 20rpx;
}

.input-group {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.input-item {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.input-item:last-child {
  border-bottom: none;
}

.input-item label {
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
  flex-shrink: 0;
}

.input-item input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.input-item.textarea-item {
  flex-direction: column;
  align-items: flex-start;
  padding-top: 20rpx;
}

.input-item.textarea-item label {
  width: 100%;
  margin-bottom: 16rpx;
}

.input-item textarea {
  width: 100%;
  min-height: 160rpx;
  font-size: 28rpx;
  color: #333;
}

.picker-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  text-align: right;
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

.submit-btn {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  text-align: center;
  padding: 28rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: 500;
}
</style>
