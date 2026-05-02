<template>
  <view class="repair-apply-page">
    <view class="form-section">
      <view class="section-title">报修信息</view>
      
      <view class="input-group">
        <view class="input-item">
          <label>报修类型</label>
          <picker :value="typeIndex" :range="repairTypes" @change="onTypeChange">
            <view class="picker-value">
              {{ repairTypes[typeIndex] || '请选择' }}
            </view>
          </picker>
        </view>
        
        <view class="input-item">
          <label>标题</label>
          <input 
            type="text" 
            v-model="form.title" 
            placeholder="请输入报修标题"
          />
        </view>
        
        <view class="input-item textarea-item">
          <label>问题描述</label>
          <textarea 
            v-model="form.description" 
            placeholder="请详细描述问题"
            :maxlength="500"
          />
        </view>
        
        <view class="input-item">
          <label>位置</label>
          <input 
            type="text" 
            v-model="form.location" 
            placeholder="如：1栋1单元101室"
          />
        </view>
      </view>
      
      <view class="image-section">
        <view class="section-title">上传图片（可选）</view>
        <view class="image-list">
          <view 
            class="image-item" 
            v-for="(img, index) in images" 
            :key="index"
            @click="previewImage(index)"
          >
            <image :src="img" mode="aspectFill" class="preview-img" />
            <view class="delete-btn" @click.stop="deleteImage(index)">
              <text>×</text>
            </view>
          </view>
          <view 
            class="add-btn" 
            v-if="images.length < 9"
            @click="chooseImage"
          >
            <text class="add-icon">+</text>
            <text class="add-text">添加图片</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="bottom-actions">
      <view class="submit-btn" @click="handleSubmit">
        提交报修
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { request } from '@/utils/request'

const repairTypes = ['水电', '家电', '土建', '其他']
const typeIndex = ref(0)
const images = ref<string[]>([])

const form = ref({
  title: '',
  description: '',
  location: ''
})

function onTypeChange(e: any) {
  typeIndex.value = e.detail.value
}

function chooseImage() {
  uni.chooseImage({
    count: 9 - images.value.length,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      images.value = [...images.value, ...res.tempFilePaths]
    }
  })
}

function previewImage(index: number) {
  uni.previewImage({
    urls: images.value,
    current: images.value[index]
  })
}

function deleteImage(index: number) {
  images.value.splice(index, 1)
}

async function handleSubmit() {
  if (!form.value.title.trim()) {
    uni.showToast({
      title: '请输入报修标题',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.description.trim()) {
    uni.showToast({
      title: '请输入问题描述',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.location.trim()) {
    uni.showToast({
      title: '请输入位置',
      icon: 'none'
    })
    return
  }
  
  try {
    await request.post('/api/repairs', {
      repair_type: repairTypes[typeIndex.value],
      title: form.value.title,
      description: form.value.description,
      location: form.value.location,
      images: images.value
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
.repair-apply-page {
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

.image-section {
  margin-top: 40rpx;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.preview-img {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.delete-btn {
  position: absolute;
  top: -16rpx;
  right: -16rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-btn text {
  color: #fff;
  font-size: 28rpx;
}

.add-btn {
  width: 200rpx;
  height: 200rpx;
  background-color: #fff;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2rpx dashed #ddd;
}

.add-icon {
  font-size: 60rpx;
  color: #ccc;
  line-height: 1;
}

.add-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
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
