<template>
  <view class="admin-members-container">
    <view class="search-section">
      <view class="search-input-box">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          placeholder="搜索会员姓名/手机号/卡号"
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
          <text>正常</text>
        </view>
        <view
          class="filter-tab"
          :class="{ active: activeFilter === 'inactive' }"
          @click="switchFilter('inactive')"
        >
          <text>禁用</text>
        </view>
      </view>
    </view>

    <view class="stats-bar">
      <view class="stat-item">
        <text class="stat-value">{{ totalCount }}</text>
        <text class="stat-label">会员总数</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value active">{{ activeCount }}</text>
        <text class="stat-label">活跃会员</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">{{ inactiveCount }}</text>
        <text class="stat-label">禁用会员</text>
      </view>
    </view>

    <view class="members-list" v-if="members.length > 0">
      <view
        class="member-item"
        v-for="(member, index) in members"
        :key="member.id"
      >
        <view class="member-header" @click="showMemberDetail(member)">
          <view class="member-avatar" :class="{ inactive: !member.is_active }">
            <text class="avatar-text">{{
              member.name?.charAt(0) || "会"
            }}</text>
          </view>
          <view class="member-info">
            <view class="info-row">
              <text class="member-name">{{ member.name || "未知" }}</text>
              <view
                class="member-status"
                :class="member.is_active ? 'active' : 'inactive'"
              >
                <text>{{ member.is_active ? "正常" : "禁用" }}</text>
              </view>
            </view>
            <text class="member-phone" v-if="member.phone"
              >📞 {{ member.phone }}</text
            >
            <text class="member-card" v-if="member.member_card_id">
              🎫 卡号: {{ member.member_card_id }}
            </text>
          </view>
          <text class="arrow">›</text>
        </view>

        <view class="member-extend">
          <view class="extend-row">
            <view class="extend-item">
              <text class="extend-label">会员类型</text>
              <text class="extend-value">{{
                getMemberTypeText(member.member_type)
              }}</text>
            </view>
            <view class="extend-item">
              <text class="extend-label">账户余额</text>
              <text class="extend-value balance"
                >¥{{ member.balance || 0 }}</text
              >
            </view>
          </view>
          <view class="extend-row">
            <view class="extend-item">
              <text class="extend-label">注册时间</text>
              <text class="extend-value">{{
                formatDate(member.created_at)
              }}</text>
            </view>
            <view class="extend-item" v-if="member.expiry_date">
              <text class="extend-label">有效期</text>
              <text class="extend-value">{{
                formatDate(member.expiry_date)
              }}</text>
            </view>
          </view>
        </view>

        <view class="member-actions">
          <view class="action-btn primary" @click="handleRecharge(member)">
            <text>充值</text>
          </view>
          <view class="action-btn warning" @click="toggleMemberStatus(member)">
            <text>{{ member.is_active ? "禁用" : "启用" }}</text>
          </view>
          <view class="action-btn default" @click="handleEdit(member)">
            <text>编辑</text>
          </view>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">👥</view>
      <text class="empty-text">暂无会员数据</text>
      <text class="empty-hint">请检查搜索条件</text>
    </view>

    <view
      class="load-more"
      v-if="hasMore && members.length > 0"
      @click="loadMore"
    >
      <text class="load-text">加载更多</text>
    </view>

    <view
      class="modal-mask"
      v-if="showRechargeModal"
      @click="showRechargeModal = false"
    ></view>
    <view class="modal" :class="{ show: showRechargeModal }">
      <view class="modal-header">
        <text class="modal-title">会员充值</text>
        <text class="modal-close" @click="showRechargeModal = false">✕</text>
      </view>
      <view class="modal-body">
        <view class="recharge-info">
          <text class="recharge-label">当前会员</text>
          <text class="recharge-value">{{
            selectedMember?.name || "未知"
          }}</text>
        </view>
        <view class="recharge-info">
          <text class="recharge-label">当前余额</text>
          <text class="recharge-value balance"
            >¥{{ selectedMember?.balance || 0 }}</text
          >
        </view>
        <view class="recharge-amount">
          <text class="recharge-label">充值金额</text>
          <view class="amount-input-box">
            <text class="amount-prefix">¥</text>
            <input
              class="amount-input"
              type="digit"
              placeholder="请输入金额"
              v-model="rechargeAmount"
            />
          </view>
        </view>
        <view class="quick-amounts">
          <view
            class="quick-item"
            :class="{ active: rechargeAmount === '100' }"
            @click="rechargeAmount = '100'"
          >
            <text>¥100</text>
          </view>
          <view
            class="quick-item"
            :class="{ active: rechargeAmount === '200' }"
            @click="rechargeAmount = '200'"
          >
            <text>¥200</text>
          </view>
          <view
            class="quick-item"
            :class="{ active: rechargeAmount === '500' }"
            @click="rechargeAmount = '500'"
          >
            <text>¥500</text>
          </view>
          <view
            class="quick-item"
            :class="{ active: rechargeAmount === '1000' }"
            @click="rechargeAmount = '1000'"
          >
            <text>¥1000</text>
          </view>
        </view>
      </view>
      <view class="modal-footer">
        <view class="modal-btn cancel" @click="showRechargeModal = false">
          <text>取消</text>
        </view>
        <view class="modal-btn confirm" @click="confirmRecharge">
          <text>确认充值</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { request } from "@/utils/request";
import { toast } from "@/utils/toast";

const keyword = ref("");
const activeFilter = ref("all");
const members = ref([]);
const page = ref(1);
const perPage = 10;
const hasMore = ref(true);
const loading = ref(false);
const totalCount = ref(0);
const activeCount = ref(0);
const inactiveCount = ref(0);

const showRechargeModal = ref(false);
const selectedMember = ref(null);
const rechargeAmount = ref("");

const getMemberTypeText = (type) => {
  const types = {
    regular: "普通会员",
    vip: "VIP会员",
    student: "学生会员",
    senior: "老年会员",
  };
  return types[type] || "普通会员";
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const fetchMembers = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    members.value = [];
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

    if (keyword.value) {
      params.keyword = keyword.value;
    }

    const res = await request.get("/admin/members", { params });

    if (res.success) {
      const newMembers = res.data.members || [];
      if (reset) {
        members.value = newMembers;
      } else {
        members.value = [...members.value, ...newMembers];
      }
      hasMore.value = newMembers.length === perPage;
      totalCount.value = res.data.total;
      page.value += 1;

      activeCount.value = members.value.filter((m) => m.is_active).length;
      inactiveCount.value = members.value.filter((m) => !m.is_active).length;
    }
  } catch (e) {
    toast.error("获取会员列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  fetchMembers(true);
};

const switchFilter = (filter) => {
  activeFilter.value = filter;
  fetchMembers(true);
};

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchMembers();
  }
};

const showMemberDetail = (member) => {
  toast.info("会员详情功能开发中");
};

const handleRecharge = (member) => {
  selectedMember.value = member;
  rechargeAmount.value = "";
  showRechargeModal.value = true;
};

const handleEdit = (member) => {
  toast.info("编辑功能开发中");
};

const toggleMemberStatus = async (member) => {
  try {
    const res = await request.put(`/admin/members/${member.id}`, {
      is_active: !member.is_active,
    });

    if (res.success) {
      member.is_active = !member.is_active;
      toast.success(member.is_active ? "已启用" : "已禁用");

      activeCount.value = members.value.filter((m) => m.is_active).length;
      inactiveCount.value = members.value.filter((m) => !m.is_active).length;
    } else {
      toast.error(res.message || "操作失败");
    }
  } catch (e) {
    toast.error("操作失败，请重试");
  }
};

const confirmRecharge = async () => {
  if (!rechargeAmount.value || parseFloat(rechargeAmount.value) <= 0) {
    toast.error("请输入有效的充值金额");
    return;
  }

  if (!selectedMember.value) return;

  try {
    const res = await request.post(
      `/admin/members/${selectedMember.value.id}/adjust-balance`,
      {
        amount: parseFloat(rechargeAmount.value),
        type: "add",
      },
    );

    if (res.success) {
      toast.success("充值成功");
      selectedMember.value.balance = res.data.balance;
      showRechargeModal.value = false;
      rechargeAmount.value = "";
    } else {
      toast.error(res.message || "充值失败");
    }
  } catch (e) {
    toast.error("充值失败，请重试");
  }
};

onMounted(() => {
  fetchMembers();
});
</script>

<style scoped>
.admin-members-container {
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

.stats-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background-color: #ffffff;
  padding: 30rpx 24rpx;
  margin-bottom: 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 6rpx;
}

.stat-value.active {
  color: #67c23a;
}

.stat-label {
  font-size: 22rpx;
  color: #999999;
}

.stat-divider {
  width: 1rpx;
  height: 40rpx;
  background-color: #e0e0e0;
}

.members-list {
  background-color: #ffffff;
  padding: 0 24rpx;
}

.member-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.member-header {
  display: flex;
  align-items: center;
}

.member-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.member-avatar.inactive {
  background-color: #f5f5f5;
}

.avatar-text {
  font-size: 36rpx;
  color: #409eff;
  font-weight: bold;
}

.member-avatar.inactive .avatar-text {
  color: #999999;
}

.member-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.member-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.member-status {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
}

.member-status.active {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.member-status.inactive {
  color: #f56c6c;
  background-color: rgba(245, 108, 108, 0.1);
}

.member-phone,
.member-card {
  font-size: 24rpx;
  color: #999999;
}

.arrow {
  font-size: 32rpx;
  color: #cccccc;
  margin-left: 16rpx;
}

.member-extend {
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #f9f9f9;
  border-radius: 8rpx;
}

.extend-row {
  display: flex;
  margin-bottom: 12rpx;
}

.extend-row:last-child {
  margin-bottom: 0;
}

.extend-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.extend-label {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 4rpx;
}

.extend-value {
  font-size: 26rpx;
  color: #333333;
}

.extend-value.balance {
  color: #67c23a;
  font-weight: bold;
}

.member-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
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
  background-color: #ffffff;
}

.load-text {
  font-size: 26rpx;
  color: #409eff;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}

.modal {
  position: fixed;
  left: 40rpx;
  right: 40rpx;
  top: 50%;
  transform: translateY(-50%) scale(0.9);
  background-color: #ffffff;
  border-radius: 16rpx;
  z-index: 101;
  opacity: 0;
  transition: all 0.3s ease;
}

.modal.show {
  opacity: 1;
  transform: translateY(-50%) scale(1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.modal-close {
  font-size: 32rpx;
  color: #999999;
}

.modal-body {
  padding: 30rpx 24rpx;
}

.recharge-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.recharge-label {
  font-size: 28rpx;
  color: #666666;
}

.recharge-value {
  font-size: 30rpx;
  color: #333333;
  font-weight: bold;
}

.recharge-value.balance {
  color: #67c23a;
}

.recharge-amount {
  margin-top: 24rpx;
}

.amount-input-box {
  display: flex;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-top: 12rpx;
}

.amount-prefix {
  font-size: 32rpx;
  color: #409eff;
  font-weight: bold;
  margin-right: 8rpx;
}

.amount-input {
  flex: 1;
  font-size: 32rpx;
  color: #333333;
}

.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 24rpx;
}

.quick-item {
  width: calc(50% - 8rpx);
  padding: 20rpx 0;
  text-align: center;
  background-color: #f5f7fa;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #666666;
}

.quick-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  border: 2rpx solid #409eff;
}

.modal-footer {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
}

.modal-btn {
  flex: 1;
  padding: 30rpx 0;
  text-align: center;
  font-size: 30rpx;
}

.modal-btn.cancel {
  color: #666666;
  border-right: 1rpx solid #f0f0f0;
}

.modal-btn.confirm {
  color: #409eff;
  font-weight: bold;
}
</style>
