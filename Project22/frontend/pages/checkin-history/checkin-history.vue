<template>
  <view class="checkin-history-container">
    <view class="filter-section">
      <view class="filter-row">
        <view class="filter-item" @click="showDatePicker = true">
          <text class="filter-label">日期范围</text>
          <text class="filter-value">{{ startDate }} 至 {{ endDate }}</text>
          <text class="filter-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="stats-summary" v-if="checkins.length > 0">
      <view class="summary-item">
        <text class="summary-value">{{ totalCheckins }}</text>
        <text class="summary-label">签到次数</text>
      </view>
      <view class="summary-item">
        <text class="summary-value">{{ weekCheckins }}</text>
        <text class="summary-label">本周</text>
      </view>
      <view class="summary-item">
        <text class="summary-value">{{ monthCheckins }}</text>
        <text class="summary-label">本月</text>
      </view>
    </view>

    <view class="checkin-list" v-if="checkins.length > 0">
      <view class="list-header">
        <text class="list-title">签到记录</text>
      </view>
      <view
        class="checkin-item"
        v-for="(checkin, index) in checkins"
        :key="checkin.id"
      >
        <view class="checkin-time-badge">
          <text class="badge-date">{{ formatDate(checkin.checkin_time) }}</text>
          <text class="badge-time">{{ formatTime(checkin.checkin_time) }}</text>
        </view>
        <view class="checkin-info">
          <view class="checkin-type">
            <text class="type-icon">{{
              getTypeIcon(checkin.checkin_type)
            }}</text>
            <text class="type-text">{{
              getTypeText(checkin.checkin_type)
            }}</text>
          </view>
          <text class="checkin-location" v-if="checkin.location"
            >📍 {{ checkin.location }}</text
          >
          <text class="checkin-notes" v-if="checkin.notes">{{
            checkin.notes
          }}</text>
        </view>
        <view class="checkin-status">
          <text class="status-success">已签到</text>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">📋</view>
      <text class="empty-text">暂无签到记录</text>
      <text class="empty-hint">去健身吧，完成第一次签到</text>
    </view>

    <view
      class="load-more"
      v-if="hasMore && checkins.length > 0"
      @click="loadMore"
    >
      <text class="load-text">加载更多</text>
    </view>

    <view
      class="date-picker-mask"
      v-if="showDatePicker"
      @click="showDatePicker = false"
    ></view>
    <view class="date-picker" :class="{ show: showDatePicker }">
      <view class="picker-header">
        <text class="picker-cancel" @click="showDatePicker = false">取消</text>
        <text class="picker-title">选择日期范围</text>
        <text class="picker-confirm" @click="confirmDate">确定</text>
      </view>
      <view class="picker-body">
        <view class="picker-section">
          <text class="picker-label">开始日期</text>
          <picker
            mode="date"
            :value="startDate"
            @change="(e) => (startDate = e.detail.value)"
          >
            <view class="picker-value">{{ startDate }}</view>
          </picker>
        </view>
        <view class="picker-section">
          <text class="picker-label">结束日期</text>
          <picker
            mode="date"
            :value="endDate"
            @change="(e) => (endDate = e.detail.value)"
          >
            <view class="picker-value">{{ endDate }}</view>
          </picker>
        </view>
        <view class="quick-select">
          <text class="quick-item" @click="setQuickRange(7)">近7天</text>
          <text class="quick-item" @click="setQuickRange(30)">近30天</text>
          <text class="quick-item" @click="setQuickRange(90)">近90天</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const checkins = ref([]);
const page = ref(1);
const perPage = 10;
const hasMore = ref(true);
const loading = ref(false);

const showDatePicker = ref(false);
const today = new Date();
const startDate = ref(
  formatDateString(new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)),
);
const endDate = ref(formatDateString(today));

const memberId = uni.getStorageSync("memberId") || 1;

const totalCheckins = computed(() => checkins.value.length);
const weekCheckins = computed(() => {
  const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
  return checkins.value.filter((c) => new Date(c.checkin_time) >= weekAgo)
    .length;
});
const monthCheckins = computed(() => {
  const monthAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);
  return checkins.value.filter((c) => new Date(c.checkin_time) >= monthAgo)
    .length;
});

function formatDateString(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

const formatDate = (time) => {
  if (!time) return "";
  const date = new Date(time);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekDays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  return `${month}月${day}日 ${weekDays[date.getDay()]}`;
};

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return `${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const getTypeIcon = (type) => {
  const icons = {
    normal: "🏃",
    course: "🎯",
    private: "💪",
    other: "📌",
  };
  return icons[type] || "🏃";
};

const getTypeText = (type) => {
  const texts = {
    normal: "日常签到",
    course: "课程签到",
    private: "私教签到",
    other: "其他",
  };
  return texts[type] || "日常签到";
};

const fetchCheckins = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    checkins.value = [];
    hasMore.value = true;
  }

  loading.value = true;
  try {
    const res = await request.get(`/checkins/my/${memberId}`, {
      params: {
        page: page.value,
        per_page: perPage,
        start_date: startDate.value,
        end_date: endDate.value,
      },
    });

    if (res.success) {
      const newCheckins = res.data.checkins || [];
      if (reset) {
        checkins.value = newCheckins;
      } else {
        checkins.value = [...checkins.value, ...newCheckins];
      }
      hasMore.value = newCheckins.length === perPage;
      page.value += 1;
    } else {
      toast.error(res.message || "获取签到记录失败");
    }
  } catch (e) {
    toast.error("网络错误，请重试");
  } finally {
    loading.value = false;
  }
};

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchCheckins();
  }
};

const setQuickRange = (days) => {
  const end = new Date();
  const start = new Date(end.getTime() - days * 24 * 60 * 60 * 1000);
  startDate.value = formatDateString(start);
  endDate.value = formatDateString(end);
};

const confirmDate = () => {
  showDatePicker.value = false;
  fetchCheckins(true);
};

onMounted(() => {
  fetchCheckins();
});
</script>

<style scoped>
.checkin-history-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.filter-section {
  background-color: #ffffff;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.filter-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background-color: #f5f7fa;
  border-radius: 12rpx;
}

.filter-label {
  font-size: 28rpx;
  color: #666666;
}

.filter-value {
  font-size: 28rpx;
  color: #333333;
  flex: 1;
  text-align: center;
}

.filter-arrow {
  font-size: 32rpx;
  color: #999999;
}

.stats-summary {
  background-color: #ffffff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-around;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-value {
  font-size: 44rpx;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8rpx;
}

.summary-label {
  font-size: 24rpx;
  color: #999999;
}

.checkin-list {
  background-color: #ffffff;
  padding: 0 24rpx;
}

.list-header {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.list-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
}

.checkin-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.checkin-time-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f0f7ff;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  margin-right: 20rpx;
  min-width: 120rpx;
}

.badge-date {
  font-size: 22rpx;
  color: #409eff;
  margin-bottom: 4rpx;
}

.badge-time {
  font-size: 28rpx;
  font-weight: bold;
  color: #409eff;
}

.checkin-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.checkin-type {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.type-icon {
  font-size: 28rpx;
}

.type-text {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.checkin-location,
.checkin-notes {
  font-size: 24rpx;
  color: #999999;
}

.checkin-status {
  margin-left: 16rpx;
}

.status-success {
  font-size: 24rpx;
  color: #67c23a;
  padding: 6rpx 16rpx;
  background-color: rgba(103, 194, 58, 0.1);
  border-radius: 8rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #333333;
  margin-bottom: 12rpx;
}

.empty-hint {
  font-size: 26rpx;
  color: #999999;
}

.load-more {
  padding: 30rpx;
  text-align: center;
}

.load-text {
  font-size: 26rpx;
  color: #409eff;
}

.date-picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}

.date-picker {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  z-index: 101;
  border-radius: 24rpx 24rpx 0 0;
}

.date-picker.show {
  transform: translateY(0);
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.picker-cancel {
  font-size: 28rpx;
  color: #999999;
}

.picker-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
}

.picker-confirm {
  font-size: 28rpx;
  color: #409eff;
}

.picker-body {
  padding: 30rpx 24rpx;
  padding-bottom: calc(30rpx + env(safe-area-inset-bottom));
}

.picker-section {
  margin-bottom: 24rpx;
}

.picker-label {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 12rpx;
  display: block;
}

.picker-value {
  padding: 24rpx;
  background-color: #f5f7fa;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
  text-align: center;
}

.quick-select {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.quick-item {
  flex: 1;
  padding: 20rpx;
  background-color: #f5f7fa;
  border-radius: 12rpx;
  text-align: center;
  font-size: 26rpx;
  color: #666666;
}
</style>
