<template>
  <view class="checkin-container">
    <view class="stats-card">
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-value">{{ stats.total_checkins }}</text>
          <text class="stat-label">累计签到</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.week_checkins }}</text>
          <text class="stat-label">本周签到</text>
        </view>
        <view class="stat-item">
          <text class="stat-value" :class="{ success: stats.today_checked }">
            {{ stats.today_checked ? "已签" : "未签" }}
          </text>
          <text class="stat-label">今日状态</text>
        </view>
      </view>
    </view>

    <view class="checkin-section">
      <view
        class="checkin-circle"
        :class="{ checked: stats.today_checked }"
        @click="handleCheckin"
      >
        <view class="checkin-text">
          <text class="checkin-status">{{
            stats.today_checked ? "今日已签到" : "立即签到"
          }}</text>
          <text class="checkin-time" v-if="todayCheckin">{{
            formatTime(todayCheckin.checkin_time)
          }}</text>
        </view>
      </view>
    </view>

    <view class="calendar-section">
      <view class="section-header">
        <text class="section-title">本月签到日历</text>
        <text class="section-link" @click="goToHistory">查看记录</text>
      </view>
      <view class="calendar-grid">
        <view class="calendar-weekday" v-for="day in weekdays" :key="day">
          <text>{{ day }}</text>
        </view>
        <view
          class="calendar-day"
          v-for="(day, index) in calendarDays"
          :key="index"
          :class="{
            empty: !day.day,
            today: day.isToday,
            checked: day.checked,
            future: day.isFuture,
          }"
        >
          <text v-if="day.day">{{ day.day }}</text>
        </view>
      </view>
    </view>

    <view class="tips-section">
      <view class="tips-header">
        <text class="tips-title">签到小贴士</text>
      </view>
      <view class="tips-list">
        <view class="tip-item">
          <text class="tip-icon">💪</text>
          <text class="tip-text">每天坚持签到，养成健身好习惯</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">🎁</text>
          <text class="tip-text">连续签到7天可获得会员积分</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">📊</text>
          <text class="tip-text">签到记录可在会员中心查看</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const stats = ref({
  total_checkins: 0,
  week_checkins: 0,
  today_checked: false,
});

const todayCheckin = ref(null);
const calendarDays = ref([]);
const weekdays = ["日", "一", "二", "三", "四", "五", "六"];

const memberId = uni.getStorageSync("memberId") || 1;

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return `${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const fetchStats = async () => {
  try {
    const res = await request.get(`/checkins/my/${memberId}/stats`);
    if (res.success) {
      stats.value = res.data;
    }
  } catch (e) {
    console.error("获取签到统计失败", e);
  }
};

const fetchTodayCheckin = async () => {
  try {
    const today = new Date();
    const dateStr = `${today.getFullYear()}-${(today.getMonth() + 1).toString().padStart(2, "0")}-${today.getDate().toString().padStart(2, "0")}`;
    const res = await request.get(`/checkins/my/${memberId}`, {
      params: { start_date: dateStr, end_date: dateStr, per_page: 1 },
    });
    if (res.success && res.data.checkins.length > 0) {
      todayCheckin.value = res.data.checkins[0];
    }
  } catch (e) {
    console.error("获取今日签到记录失败", e);
  }
};

const generateCalendar = async () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  const today = now.getDate();

  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const daysInMonth = lastDay.getDate();
  const firstDayOfWeek = firstDay.getDay();

  const monthDays = [];

  for (let i = 0; i < firstDayOfWeek; i++) {
    monthDays.push({ day: null });
  }

  try {
    const startDate = `${year}-${(month + 1).toString().padStart(2, "0")}-01`;
    const endDate = `${year}-${(month + 1).toString().padStart(2, "0")}-${daysInMonth}`;

    const res = await request.get(`/checkins/my/${memberId}`, {
      params: { start_date: startDate, end_date: endDate, per_page: 100 },
    });

    const checkinSet = new Set();
    if (res.success) {
      res.data.checkins.forEach((checkin) => {
        const date = new Date(checkin.checkin_time);
        checkinSet.add(date.getDate());
      });
    }

    for (let i = 1; i <= daysInMonth; i++) {
      monthDays.push({
        day: i,
        isToday: i === today,
        isFuture: i > today,
        checked: checkinSet.has(i),
      });
    }
  } catch (e) {
    console.error("生成日历失败", e);
    for (let i = 1; i <= daysInMonth; i++) {
      monthDays.push({
        day: i,
        isToday: i === today,
        isFuture: i > today,
        checked: false,
      });
    }
  }

  calendarDays.value = monthDays;
};

const handleCheckin = async () => {
  if (stats.value.today_checked) {
    toast.info("今日已签到");
    return;
  }

  try {
    const res = await request.post("/checkins/", {
      member_id: memberId,
      checkin_type: "normal",
    });

    if (res.success) {
      toast.success("签到成功！");
      stats.value.today_checked = true;
      stats.value.total_checkins += 1;
      stats.value.week_checkins += 1;
      todayCheckin.value = res.data;

      await generateCalendar();
    } else {
      toast.error(res.message || "签到失败");
    }
  } catch (e) {
    toast.error("签到失败，请重试");
  }
};

const goToHistory = () => {
  uni.navigateTo({
    url: "/pages/checkin-history/checkin-history",
  });
};

onMounted(() => {
  fetchStats();
  fetchTodayCheckin();
  generateCalendar();
});
</script>

<style scoped>
.checkin-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20rpx;
}

.stats-card {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.stats-row {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 8rpx;
}

.stat-value.success {
  color: #67c23a;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.checkin-section {
  display: flex;
  justify-content: center;
  padding: 60rpx 0;
}

.checkin-circle {
  width: 280rpx;
  height: 280rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #e0e0e0 0%, #f5f5f5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  border: 8rpx solid #ffffff;
  transition: all 0.3s ease;
}

.checkin-circle:active {
  transform: scale(0.95);
}

.checkin-circle.checked {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.checkin-text {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.checkin-status {
  font-size: 32rpx;
  font-weight: bold;
  color: #666666;
}

.checkin-circle.checked .checkin-status {
  color: #ffffff;
}

.checkin-time {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

.checkin-circle.checked .checkin-time {
  color: rgba(255, 255, 255, 0.8);
}

.calendar-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
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

.section-link {
  font-size: 26rpx;
  color: #409eff;
}

.calendar-grid {
  display: flex;
  flex-wrap: wrap;
}

.calendar-weekday {
  width: 14.28%;
  text-align: center;
  padding: 16rpx 0;
  font-size: 24rpx;
  color: #999999;
}

.calendar-day {
  width: 14.28%;
  text-align: center;
  padding: 16rpx 0;
  font-size: 26rpx;
  color: #333333;
}

.calendar-day.empty {
  visibility: hidden;
}

.calendar-day.today {
  color: #409eff;
  font-weight: bold;
}

.calendar-day.today::before {
  content: "";
  display: block;
  width: 56rpx;
  height: 56rpx;
  background-color: rgba(64, 158, 255, 0.1);
  border-radius: 50%;
  position: absolute;
  margin: -12rpx 0 0 -4rpx;
  z-index: 0;
}

.calendar-day.checked {
  color: #67c23a;
  font-weight: bold;
}

.calendar-day.checked::before {
  content: "";
  display: block;
  width: 56rpx;
  height: 56rpx;
  background-color: rgba(103, 194, 58, 0.1);
  border-radius: 50%;
  position: absolute;
  margin: -12rpx 0 0 -4rpx;
  z-index: 0;
}

.calendar-day.future {
  color: #cccccc;
}

.tips-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.tips-header {
  margin-bottom: 20rpx;
}

.tips-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.tip-icon {
  font-size: 32rpx;
}

.tip-text {
  font-size: 26rpx;
  color: #666666;
  flex: 1;
}
</style>
