<template>
  <view class="suggestion-apply-page">
    <view class="form-section">
      <view class="section-title">提交建议/投诉</view>
      
      <view class="input-group">
        <view class="input-item">
          <label>类型</label>
          <picker :value="typeIndex" :range="typeOptions" @change="onTypeChange">
            <view class="picker-value">
              {{ typeOptions[typeIndex] || '请选择' }}
            </view>
          </picker>
        </view>
        
        <view class="input-item">
          <label>标题</label>
          <input 
            type="text" 
            v-model="form.title" 
            placeholder="请输入标题"
          />
        </view>
        
        <view class="input-item textarea-item">
          <label>内容</label>
          <textarea 
            v-model="form.content" 
            placeholder="请详细描述您的建议或投诉"
            :maxlength="500"
          />
        </view>
      </view>
    </view>
    
    <view class="bottom-actions">
      <view class="submit-btn" @click="handleSubmit">
        提交
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { request } from '@/utils/request'

const typeOptions = ['建议', '投诉']
const typeIndex = ref(0)

const form = ref({
  title: '',
  content: ''
})

function onTypeChange(e: any) {
  typeIndex.value = e.detail.value
}

async function handleSubmit() {
  if (!form.value.title.trim()) {
    uni.showToast({
      title: '请输入标题',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.content.trim()) {
    uni.showToast({
      title: '请输入内容',
      icon: 'none'
    })
    return
  }
  
  try {
    await request.post('/api/suggestions', {
      type: typeIndex.value === 0 ? 'suggestion' : 'complaint',
      title: form.value.title,
      content: form.value.content
    })
    
    uni.showToast({
      title: '提交成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交失败', error)
  }
}
</script>

<style scoped>
.suggestion-apply-page {
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
  min-height: 200rpx;
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
