<template>
  <view class="admin-coaches-container">
    <view class="search-section">
      <view class="search-input-box">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索教练姓名"
          v-model="keyword"
          @confirm="handleSearch"
        />
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
          <text>在职</text>
        </view>
        <view
          class="filter-tab"
          :class="{ active: activeFilter === 'inactive' }"
          @click="switchFilter('inactive')"
        >
          <text>离职</text>
        </view>
      </view>
    </view>

    <view class="coaches-list" v-if="coaches.length > 0">
      <view
        class="coach-card"
        v-for="(coach, index) in coaches"
        :key="coach.id"
      >
        <view class="coach-header">
          <view class="coach-avatar" :class="{ inactive: !coach.is_active }">
            <text class="avatar-text">{{ coach.name?.charAt(0) || "教" }}</text>
          </view>
          <view class="coach-info">
            <view class="info-row">
              <text class="coach-name">{{ coach.name || "未知" }}</text>
              <view
                class="coach-status"
                :class="coach.is_active ? 'active' : 'inactive'"
              >
                <text>{{ coach.is_active ? "在职" : "离职" }}</text>
              </view>
            </view>
            <view class="coach-tags">
              <text class="tag-primary" v-if="coach.specialty">{{
                coach.specialty
              }}</text>
              <text class="tag-default" v-if="coach.experience"
                >{{ coach.experience }}年经验</text
              >
            </view>
          </view>
        </view>

        <view class="coach-detail">
          <view class="detail-row" v-if="coach.phone">
            <text class="detail-label">📞 联系电话</text>
            <text class="detail-value">{{ coach.phone }}</text>
          </view>
          <view class="detail-row" v-if="coach.email">
            <text class="detail-label">📧 邮箱</text>
            <text class="detail-value">{{ coach.email }}</text>
          </view>
          <view class="detail-row" v-if="coach.bio">
            <text class="detail-label">📝 简介</text>
            <text class="detail-value">{{ coach.bio }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">📅 入职时间</text>
            <text class="detail-value">{{ formatDate(coach.created_at) }}</text>
          </view>
        </view>

        <view class="coach-actions">
          <view class="action-btn primary" @click="viewCourses(coach)">
            <text>查看课程</text>
          </view>
          <view class="action-btn warning" @click="toggleCoachStatus(coach)">
            <text>{{ coach.is_active ? "离职" : "复职" }}</text>
          </view>
          <view class="action-btn default" @click="editCoach(coach)">
            <text>编辑</text>
          </view>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">🏋️</view>
      <text class="empty-text">暂无教练数据</text>
      <text class="empty-hint">请检查搜索条件</text>
    </view>

    <view
      class="load-more"
      v-if="hasMore && coaches.length > 0"
      @click="loadMore"
    >
      <text class="load-text">加载更多</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const keyword = ref("");
const activeFilter = ref("all");
const coaches = ref([]);
const page = ref(1);
const perPage = 10;
const hasMore = ref(true);
const loading = ref(false);

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const fetchCoaches = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    coaches.value = [];
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

    const res = await request.get("/admin/coaches", { params });

    if (res.success) {
      const newCoaches = res.data.coaches || [];
      if (reset) {
        coaches.value = newCoaches;
      } else {
        coaches.value = [...coaches.value, ...newCoaches];
      }
      hasMore.value = newCoaches.length === perPage;
      page.value += 1;
    }
  } catch (e) {
    toast.error("获取教练列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  fetchCoaches(true);
};

const switchFilter = (filter) => {
  activeFilter.value = filter;
  fetchCoaches(true);
};

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchCoaches();
  }
};

const viewCourses = (coach) => {
  uni.navigateTo({
    url: `/pages/admin-courses/admin-courses?coach_id=${coach.id}`,
  });
};

const editCoach = (coach) => {
  toast.info("编辑功能开发中");
};

const toggleCoachStatus = async (coach) => {
  toast.info("状态切换功能开发中");
};

onMounted(() => {
  fetchCoaches();
});
</script>

<style scoped>
.admin-coaches-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.search-section {
  background-color: #ffffff;
  padding: 20rpx 24rpx;
}

.search-input-box {
  display: flex;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.filter-section {
  background-color: #ffffff;
  padding: 0 24rpx 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
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

.coaches-list {
  padding: 20rpx;
}

.coach-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.coach-header {
  display: flex;
  align-items: center;
}

.coach-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.coach-avatar.inactive {
  background: linear-gradient(135deg, #cccccc 0%, #e0e0e0 100%);
}

.avatar-text {
  font-size: 40rpx;
  color: #ffffff;
  font-weight: bold;
}

.coach-info {
  flex: 1;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.coach-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.coach-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
}

.coach-status.active {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.coach-status.inactive {
  color: #999999;
  background-color: #f5f5f5;
}

.coach-tags {
  display: flex;
  gap: 12rpx;
}

.tag-primary {
  padding: 4rpx 12rpx;
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
  font-size: 22rpx;
  border-radius: 6rpx;
}

.tag-default {
  padding: 4rpx 12rpx;
  background-color: #f5f5f5;
  color: #666666;
  font-size: 22rpx;
  border-radius: 6rpx;
}

.coach-detail {
  margin-top: 20rpx;
  padding: 20rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
}

.detail-row {
  display: flex;
  margin-bottom: 12rpx;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  width: 160rpx;
  font-size: 26rpx;
  color: #999999;
}

.detail-value {
  flex: 1;
  font-size: 26rpx;
  color: #333333;
}

.coach-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 20rpx;
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
</style>
