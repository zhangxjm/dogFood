<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

onLaunch(() => {
  console.log('App Launch')
  const token = uni.getStorageSync('token')
  if (token) {
    userStore.setToken(token)
  }
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-size: 28rpx;
  color: #333;
}

.container {
  padding: 20rpx;
}

.card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.btn-primary {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #fff;
  border-radius: 44rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}

.btn-primary:active {
  opacity: 0.85;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #666;
  border-radius: 44rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
}

.text-primary {
  color: #007AFF;
}

.text-success {
  color: #52c41a;
}

.text-warning {
  color: #faad14;
}

.text-danger {
  color: #ff4d4f;
}

.text-secondary {
  color: #999;
}

.text-muted {
  color: #ccc;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.flex-1 {
  flex: 1;
}

.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

.mt-10 {
  margin-top: 10rpx;
}

.mt-20 {
  margin-top: 20rpx;
}

.mt-30 {
  margin-top: 30rpx;
}

.mb-10 {
  margin-bottom: 10rpx;
}

.mb-20 {
  margin-bottom: 20rpx;
}

.p-20 {
  padding: 20rpx;
}

.p-30 {
  padding: 30rpx;
}

.border-bottom {
  border-bottom: 1rpx solid #eee;
}

.tag {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.tag-primary {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.tag-success {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.tag-warning {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.tag-danger {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.input-group {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 0 24rpx;
  margin-bottom: 20rpx;
}

.input-item {
  display: flex;
  align-items: center;
  height: 100rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.input-item:last-child {
  border-bottom: none;
}

.input-item label {
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
}

.input-item input {
  flex: 1;
  height: 100rpx;
  font-size: 28rpx;
}

.input-item textarea {
  flex: 1;
  min-height: 200rpx;
  padding: 20rpx 0;
  font-size: 28rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-state .empty-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.empty-state .empty-text {
  font-size: 28rpx;
}
</style>
