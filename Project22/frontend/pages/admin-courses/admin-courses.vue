<template>
  <view class="admin-courses-container">
    <view class="week-days">
      <view
        class="day-item"
        v-for="(day, index) in weekDays"
        :key="index"
        :class="{ active: selectedDay === index }"
        @click="selectDay(index)"
      >
        <text class="day-name">{{ day.name }}</text>
        <text class="day-date" :class="{ today: day.isToday }">{{
          day.date
        }}</text>
      </view>
    </view>

    <view class="filter-section">
      <view class="filter-tabs">
        <view
          class="filter-tab"
          :class="{ active: activeFilter === 'all' }"
          @click="switchFilter('all')"
        >
          <text>全部</text>
        </view>
        <view
          class="filter-tab"
          :class="{ active: activeFilter === 'active' }"
          @click="switchFilter('active')"
        >
          <text>开放</text>
        </view>
        <view
          class="filter-tab"
          :class="{ active: activeFilter === 'inactive' }"
          @click="switchFilter('inactive')"
        >
          <text>关闭</text>
        </view>
      </view>
    </view>

    <view class="courses-list" v-if="courses.length > 0">
      <view
        class="course-card"
        v-for="(course, index) in courses"
        :key="course.id"
      >
        <view class="course-header">
          <view class="course-time-badge">
            <text class="start-time">{{ course.start_time }}</text>
            <text class="time-divider">-</text>
            <text class="end-time">{{ course.end_time }}</text>
          </view>
          <view
            class="course-status"
            :class="course.is_active ? 'active' : 'inactive'"
          >
            <text>{{ course.is_active ? "开放" : "关闭" }}</text>
          </view>
        </view>

        <view class="course-body">
          <text class="course-name">{{ course.name || "未知课程" }}</text>
          <view class="course-info">
            <view class="info-item" v-if="course.coach_name">
              <text class="info-icon">👨‍🏫</text>
              <text class="info-text">{{ course.coach_name }}</text>
            </view>
            <view class="info-item">
              <text class="info-icon">📍</text>
              <text class="info-text">{{ course.location || "待定" }}</text>
            </view>
            <view class="info-item">
              <text class="info-icon">👥</text>
              <text class="info-text"
                >{{ course.current_capacity || 0 }}/{{
                  course.max_capacity || 20
                }}人</text
              >
            </view>
            <view class="info-item">
              <text class="info-icon">💰</text>
              <text class="info-text">¥{{ course.price || 0 }}/次</text>
            </view>
          </view>
          <view class="course-progress" v-if="course.max_capacity">
            <view class="progress-bar">
              <view
                class="progress-fill"
                :style="{ width: getProgressWidth(course) + '%' }"
              ></view>
            </view>
            <text class="progress-text">{{ getProgressText(course) }}</text>
          </view>
        </view>

        <view class="course-footer">
          <view class="course-day" v-if="course.day_of_week !== null">
            <text class="day-text">{{
              getDayOfWeekText(course.day_of_week)
            }}</text>
          </view>
          <view class="course-actions">
            <view class="action-btn primary" @click="viewReservations(course)">
              <text>查看预约</text>
            </view>
            <view
              class="action-btn warning"
              @click="toggleCourseStatus(course)"
            >
              <text>{{ course.is_active ? "关闭" : "开放" }}</text>
            </view>
            <view class="action-btn default" @click="editCourse(course)">
              <text>编辑</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">📚</view>
      <text class="empty-text">暂无课程数据</text>
      <text class="empty-hint">请选择其他日期或筛选条件</text>
    </view>

    <view
      class="load-more"
      v-if="hasMore && courses.length > 0"
      @click="loadMore"
    >
      <text class="load-text">加载更多</text>
    </view>

    <view class="fab-button" @click="addCourse">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onLoad } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const selectedDay = ref(null);
const activeFilter = ref("all");
const courses = ref([]);
const page = ref(1);
const perPage = 20;
const hasMore = ref(true);
const loading = ref(false);
const coachId = ref(null);

const weekDays = computed(() => {
  const days = [];
  const today = new Date();
  const dayNames = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];

  for (let i = 0; i < 7; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + i);
    days.push({
      name: i === 0 ? "今天" : dayNames[date.getDay()],
      date: date.getDate(),
      dayOfWeek: date.getDay(),
      isToday: i === 0,
    });
  }
  return days;
});

const getDayOfWeekText = (day) => {
  const days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  return days[day] || "未知";
};

const getProgressWidth = (course) => {
  if (!course.max_capacity) return 0;
  return Math.min(
    ((course.current_capacity || 0) / course.max_capacity) * 100,
    100,
  );
};

const getProgressText = (course) => {
  const current = course.current_capacity || 0;
  const max = course.max_capacity || 20;
  const remaining = max - current;
  if (remaining <= 0) {
    return "已满";
  } else if (remaining <= 5) {
    return `剩余${remaining}个名额`;
  }
  return `剩余${remaining}个名额`;
};

const fetchCourses = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    courses.value = [];
    hasMore.value = true;
  }

  loading.value = true;
  try {
    const params = {
      page: page.value,
      per_page: perPage,
    };

    if (activeFilter.value === "active") {
      params.is_active = "true";
    } else if (activeFilter.value === "inactive") {
      params.is_active = "false";
    }

    if (coachId.value) {
      params.coach_id = coachId.value;
    }

    if (selectedDay.value !== null) {
      params.day_of_week = weekDays.value[selectedDay.value].dayOfWeek;
    }

    const res = await request.get("/admin/courses", { params });

    if (res.success) {
      const newCourses = res.data.courses || [];
      if (reset) {
        courses.value = newCourses;
      } else {
        courses.value = [...courses.value, ...newCourses];
      }
      hasMore.value = newCourses.length === perPage;
      page.value += 1;
    }
  } catch (e) {
    toast.error("获取课程列表失败");
  } finally {
    loading.value = false;
  }
};

const selectDay = (index) => {
  if (selectedDay.value === index) {
    selectedDay.value = null;
  } else {
    selectedDay.value = index;
  }
  fetchCourses(true);
};

const switchFilter = (filter) => {
  activeFilter.value = filter;
  fetchCourses(true);
};

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchCourses();
  }
};

const viewReservations = (course) => {
  toast.info("预约查看功能开发中");
};

const editCourse = (course) => {
  toast.info("编辑功能开发中");
};

const addCourse = () => {
  toast.info("添加课程功能开发中");
};

const toggleCourseStatus = (course) => {
  toast.info("状态切换功能开发中");
};

onLoad((options) => {
  if (options.coach_id) {
    coachId.value = parseInt(options.coach_id);
  }
});

onMounted(() => {
  fetchCourses();
});
</script>

<style scoped>
.admin-courses-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 120rpx;
}

.week-days {
  display: flex;
  background-color: #ffffff;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.day-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx 0;
  margin: 0 4rpx;
  border-radius: 12rpx;
  transition: all 0.3s ease;
}

.day-item.active {
  background-color: #ecf5ff;
}

.day-name {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 6rpx;
}

.day-date {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.day-date.today {
  color: #409eff;
  font-weight: bold;
}

.filter-section {
  background-color: #ffffff;
  padding: 20rpx 24rpx;
  margin-bottom: 20rpx;
}

.filter-tabs {
  display: flex;
  gap: 20rpx;
}

.filter-tab {
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666666;
  background-color: #f5f7fa;
}

.filter-tab.active {
  background-color: #409eff;
  color: #ffffff;
}

.courses-list {
  padding: 0 20rpx;
}

.course-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.course-time-badge {
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

.start-time,
.end-time {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: bold;
}

.time-divider {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 8rpx;
}

.course-status {
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
}

.course-status.active {
  background-color: rgba(103, 194, 58, 0.9);
  color: #ffffff;
}

.course-status.inactive {
  background-color: rgba(245, 108, 108, 0.9);
  color: #ffffff;
}

.course-body {
  padding: 24rpx;
}

.course-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
}

.course-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.info-icon {
  font-size: 24rpx;
}

.info-text {
  font-size: 24rpx;
  color: #666666;
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f5f5f5;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 22rpx;
  color: #999999;
  min-width: 120rpx;
  text-align: right;
}

.course-footer {
  padding: 0 24rpx 24rpx;
}

.course-day {
  margin-bottom: 16rpx;
}

.day-text {
  font-size: 22rpx;
  color: #409eff;
  padding: 4rpx 12rpx;
  background-color: #ecf5ff;
  border-radius: 6rpx;
}

.course-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  flex: 1;
  padding: 16rpx 0;
  text-align: center;
  border-radius: 8rpx;
  font-size: 26rpx;
}

.action-btn.primary {
  background-color: #ecf5ff;
  color: #409eff;
}

.action-btn.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.action-btn.default {
  background-color: #f5f5f5;
  color: #666666;
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

.fab-button {
  position: fixed;
  right: 30rpx;
  bottom: calc(30rpx + env(safe-area-inset-bottom));
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(64, 158, 255, 0.4);
  z-index: 10;
}

.fab-icon {
  font-size: 48rpx;
  color: #ffffff;
  line-height: 1;
}
</style>
