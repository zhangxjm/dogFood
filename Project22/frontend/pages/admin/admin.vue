<template>
  <view class="admin-container">
    <view class="header-section">
      <view class="header-info">
        <view class="admin-avatar">
          <text class="avatar-text">管</text>
        </view>
        <view class="admin-info">
          <text class="admin-name">管理员</text>
          <text class="admin-role">健身俱乐部管理系统</text>
        </view>
      </view>
      <text class="header-date">{{ currentDate }}</text>
    </view>

    <view class="stats-section">
      <view class="stats-title">今日数据概览</view>
      <view class="stats-grid">
        <view class="stat-card member">
          <view class="stat-header">
            <text class="stat-icon">👥</text>
            <text class="stat-label">会员</text>
          </view>
          <view class="stat-main">
            <text class="stat-value">{{ stats.members?.total || 0 }}</text>
            <text class="stat-sub">活跃 {{ stats.members?.active || 0 }}</text>
          </view>
        </view>
        <view class="stat-card coach">
          <view class="stat-header">
            <text class="stat-icon">🏋️</text>
            <text class="stat-label">教练</text>
          </view>
          <view class="stat-main">
            <text class="stat-value">{{ stats.coaches?.total || 0 }}</text>
            <text class="stat-sub">在职 {{ stats.coaches?.active || 0 }}</text>
          </view>
        </view>
        <view class="stat-card course">
          <view class="stat-header">
            <text class="stat-icon">📚</text>
            <text class="stat-label">课程</text>
          </view>
          <view class="stat-main">
            <text class="stat-value">{{ stats.courses?.total || 0 }}</text>
            <text class="stat-sub">开放 {{ stats.courses?.active || 0 }}</text>
          </view>
        </view>
        <view class="stat-card checkin">
          <view class="stat-header">
            <text class="stat-icon">✅</text>
            <text class="stat-label">今日签到</text>
          </view>
          <view class="stat-main">
            <text class="stat-value">{{ stats.checkins?.today || 0 }}</text>
            <text class="stat-sub">累计 {{ stats.checkins?.total || 0 }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="finance-section">
      <view class="section-header">
        <text class="section-title">财务概览</text>
      </view>
      <view class="finance-row">
        <view class="finance-item">
          <text class="finance-label">累计充值</text>
          <text class="finance-value recharge"
            >¥{{ stats.finance?.total_recharge || 0 }}</text
          >
        </view>
        <view class="finance-divider"></view>
        <view class="finance-item">
          <text class="finance-label">累计消费</text>
          <text class="finance-value consumption"
            >¥{{ stats.finance?.total_consumption || 0 }}</text
          >
        </view>
      </view>
    </view>

    <view class="quick-actions">
      <view class="section-header">
        <text class="section-title">快捷操作</text>
      </view>
      <view class="actions-grid">
        <view class="action-item" @click="goToMembers">
          <view class="action-icon member">
            <text>👥</text>
          </view>
          <text class="action-label">会员管理</text>
        </view>
        <view class="action-item" @click="goToCoaches">
          <view class="action-icon coach">
            <text>🏋️</text>
          </view>
          <text class="action-label">教练管理</text>
        </view>
        <view class="action-item" @click="goToCourses">
          <view class="action-icon course">
            <text>📚</text>
          </view>
          <text class="action-label">课程管理</text>
        </view>
        <view class="action-item" @click="goToReservations">
          <view class="action-icon reservation">
            <text>📅</text>
          </view>
          <text class="action-label">预约管理</text>
        </view>
      </view>
    </view>

    <view class="recent-activities" v-if="recentCheckins.length > 0">
      <view class="section-header">
        <text class="section-title">最近签到</text>
        <text class="section-more" @click="viewAllCheckins">查看全部</text>
      </view>
      <view class="activity-list">
        <view
          class="activity-item"
          v-for="(checkin, index) in recentCheckins.slice(0, 5)"
          :key="checkin.id"
        >
          <view class="activity-avatar">
            <text class="avatar-small">{{
              checkin.member_name?.charAt(0) || "会"
            }}</text>
          </view>
          <view class="activity-content">
            <text class="activity-name">{{
              checkin.member_name || "会员"
            }}</text>
            <text class="activity-time">{{
              formatTime(checkin.checkin_time)
            }}</text>
          </view>
          <view class="activity-status">
            <text class="status-success">已签到</text>
          </view>
        </view>
      </view>
    </view>

    <view class="version-info">
      <text class="version-text">健身俱乐部管理系统 v1.0.0</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const stats = ref({
  members: { total: 0, active: 0 },
  coaches: { total: 0, active: 0 },
  courses: { total: 0, active: 0 },
  reservations: { total: 0, today: 0 },
  checkins: { total: 0, today: 0 },
  finance: { total_recharge: 0, total_consumption: 0 },
});

const recentCheckins = ref([]);

const currentDate = computed(() => {
  const now = new Date();
  const weekDays = [
    "星期日",
    "星期一",
    "星期二",
    "星期三",
    "星期四",
    "星期五",
    "星期六",
  ];
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const day = now.getDate();
  return `${year}年${month}月${day}日 ${weekDays[now.getDay()]}`;
});

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${month}月${day}日 ${hours}:${minutes}`;
};

const fetchStats = async () => {
  try {
    const res = await request.get("/admin/stats");
    if (res.success) {
      stats.value = res.data;
    }
  } catch (e) {
    console.error("获取统计数据失败", e);
  }
};

const fetchRecentCheckins = async () => {
  try {
    const res = await request.get("/admin/checkins", {
      params: { per_page: 5 },
    });
    if (res.success) {
      recentCheckins.value = res.data.checkins || [];
    }
  } catch (e) {
    console.error("获取最近签到失败", e);
  }
};

const goToMembers = () => {
  uni.navigateTo({
    url: "/pages/admin-members/admin-members",
  });
};

const goToCoaches = () => {
  uni.navigateTo({
    url: "/pages/admin-coaches/admin-coaches",
  });
};

const goToCourses = () => {
  uni.navigateTo({
    url: "/pages/admin-courses/admin-courses",
  });
};

const goToReservations = () => {
  toast.info("预约管理功能开发中");
};

const viewAllCheckins = () => {
  toast.info("签到记录功能开发中");
};

onMounted(() => {
  fetchStats();
  fetchRecentCheckins();
});
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 40rpx;
}

.header-section {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  padding: 40rpx 30rpx;
  padding-bottom: 60rpx;
}

.header-info {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.admin-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.avatar-text {
  font-size: 40rpx;
  color: #ffffff;
  font-weight: bold;
}

.avatar-small {
  font-size: 28rpx;
  color: #409eff;
  font-weight: bold;
}

.admin-info {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 6rpx;
}

.admin-role {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.header-date {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
}

.stats-section {
  background-color: #ffffff;
  margin: -30rpx 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.stats-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.stat-card {
  width: calc(50% - 10rpx);
  border-radius: 12rpx;
  padding: 24rpx;
  border: 2rpx solid #f0f0f0;
}

.stat-card.member {
  background-color: #f0f7ff;
  border-color: #d0e8ff;
}

.stat-card.coach {
  background-color: #f0fff4;
  border-color: #d0ffd8;
}

.stat-card.course {
  background-color: #fffbf0;
  border-color: #ffefd0;
}

.stat-card.checkin {
  background-color: #fff0f0;
  border-color: #ffd0d0;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-bottom: 16rpx;
}

.stat-icon {
  font-size: 32rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #666666;
}

.stat-main {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 4rpx;
}

.stat-sub {
  font-size: 22rpx;
  color: #999999;
}

.finance-section {
  background-color: #ffffff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
}

.section-more {
  font-size: 24rpx;
  color: #409eff;
}

.finance-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.finance-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.finance-label {
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 8rpx;
}

.finance-value {
  font-size: 36rpx;
  font-weight: bold;
}

.finance-value.recharge {
  color: #67c23a;
}

.finance-value.consumption {
  color: #f56c6c;
}

.finance-divider {
  width: 1rpx;
  height: 60rpx;
  background-color: #e0e0e0;
}

.quick-actions {
  background-color: #ffffff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.actions-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 30rpx;
}

.action-item {
  width: calc(25% - 22rpx);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.action-icon text {
  font-size: 36rpx;
}

.action-icon.member {
  background-color: #ecf5ff;
}

.action-icon.coach {
  background-color: #f0f9eb;
}

.action-icon.course {
  background-color: #fdf6ec;
}

.action-icon.reservation {
  background-color: #fef0f0;
}

.action-label {
  font-size: 24rpx;
  color: #333333;
}

.recent-activities {
  background-color: #ffffff;
  margin: 0 20rpx 20rpx;
  border-radius: 16rpx;
  padding: 30rpx;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
}

.activity-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background-color: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-name {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 4rpx;
}

.activity-time {
  font-size: 22rpx;
  color: #999999;
}

.activity-status {
  margin-left: 16rpx;
}

.status-success {
  font-size: 22rpx;
  color: #67c23a;
  padding: 6rpx 16rpx;
  background-color: rgba(103, 194, 58, 0.1);
  border-radius: 8rpx;
}

.version-info {
  text-align: center;
  padding: 40rpx;
}

.version-text {
  font-size: 22rpx;
  color: #cccccc;
}
</style>
