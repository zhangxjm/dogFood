<template>
  <view class="transactions-container">
    <view class="summary-card">
      <view class="summary-row">
        <view class="summary-item">
          <text class="summary-label">当前余额</text>
          <text class="summary-value balance"
            >¥{{ stats.current_balance || 0 }}</text
          >
        </view>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-row">
        <view class="summary-item">
          <text class="summary-label">累计充值</text>
          <text class="summary-value recharge"
            >¥{{ stats.total_recharge || 0 }}</text
          >
        </view>
        <view class="summary-item">
          <text class="summary-label">累计消费</text>
          <text class="summary-value consumption"
            >¥{{ stats.total_consumption || 0 }}</text
          >
        </view>
        <view class="summary-item">
          <text class="summary-label">交易笔数</text>
          <text class="summary-value count">{{
            stats.total_transactions || 0
          }}</text>
        </view>
      </view>
    </view>

    <view class="filter-tabs">
      <view
        class="tab-item"
        :class="{ active: currentType === 'all' }"
        @click="switchType('all')"
      >
        <text>全部</text>
      </view>
      <view
        class="tab-item"
        :class="{ active: currentType === 'recharge' }"
        @click="switchType('recharge')"
      >
        <text>充值</text>
      </view>
      <view
        class="tab-item"
        :class="{ active: currentType === 'consumption' }"
        @click="switchType('consumption')"
      >
        <text>消费</text>
      </view>
    </view>

    <view class="transactions-list" v-if="transactions.length > 0">
      <view
        class="transaction-item"
        v-for="(transaction, index) in transactions"
        :key="transaction.id"
      >
        <view
          class="transaction-icon"
          :class="getIconClass(transaction.transaction_type)"
        >
          <text>{{ getTransactionIcon(transaction.transaction_type) }}</text>
        </view>
        <view class="transaction-info">
          <view class="info-top">
            <text class="transaction-title">{{
              getTransactionTitle(transaction)
            }}</text>
            <text
              class="transaction-amount"
              :class="getAmountClass(transaction.transaction_type)"
            >
              {{ getAmountPrefix(transaction.transaction_type) }}¥{{
                transaction.amount
              }}
            </text>
          </view>
          <view class="info-bottom">
            <text class="transaction-time">{{
              formatTime(transaction.created_at)
            }}</text>
            <text class="transaction-status" :class="transaction.status">{{
              getStatusText(transaction.status)
            }}</text>
          </view>
          <text
            class="transaction-balance"
            v-if="transaction.balance_after !== null"
          >
            余额: ¥{{ transaction.balance_after }}
          </text>
        </view>
      </view>
    </view>

    <view class="empty-state" v-else>
      <view class="empty-icon">📄</view>
      <text class="empty-text">暂无交易记录</text>
      <text class="empty-hint" v-if="currentType === 'recharge'"
        >去充值，开启健身之旅</text
      >
      <text class="empty-hint" v-else-if="currentType === 'consumption'"
        >预约课程后将产生消费记录</text
      >
      <text class="empty-hint" v-else>充值或消费后将显示交易记录</text>
    </view>

    <view
      class="load-more"
      v-if="hasMore && transactions.length > 0"
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

const stats = ref({
  current_balance: 0,
  total_recharge: 0,
  total_consumption: 0,
  total_transactions: 0,
});

const transactions = ref([]);
const currentType = ref("all");
const page = ref(1);
const perPage = 10;
const hasMore = ref(true);
const loading = ref(false);

const memberId = uni.getStorageSync("memberId") || 1;

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

const getTransactionIcon = (type) => {
  const icons = {
    recharge: "💳",
    consumption: "💰",
    refund: "↩️",
  };
  return icons[type] || "📋";
};

const getIconClass = (type) => {
  const classes = {
    recharge: "recharge",
    consumption: "consumption",
    refund: "refund",
  };
  return classes[type] || "default";
};

const getTransactionTitle = (transaction) => {
  const descriptions = {
    recharge: "账户充值",
    consumption: "消费支出",
    refund: "退款",
  };
  return (
    transaction.description ||
    descriptions[transaction.transaction_type] ||
    "交易"
  );
};

const getAmountClass = (type) => {
  return type === "recharge" || type === "refund" ? "positive" : "negative";
};

const getAmountPrefix = (type) => {
  return type === "recharge" || type === "refund" ? "+" : "-";
};

const getStatusText = (status) => {
  const statuses = {
    pending: "处理中",
    completed: "已完成",
    failed: "失败",
    cancelled: "已取消",
  };
  return statuses[status] || "未知";
};

const fetchStats = async () => {
  try {
    const res = await request.get(`/transactions/my/${memberId}/stats`);
    if (res.success) {
      stats.value = res.data;
    }
  } catch (e) {
    console.error("获取交易统计失败", e);
  }
};

const fetchTransactions = async (reset = false) => {
  if (loading.value) return;
  if (reset) {
    page.value = 1;
    transactions.value = [];
    hasMore.value = true;
  }

  loading.value = true;
  try {
    const params = {
      page: page.value,
      per_page: perPage,
    };
    if (currentType.value !== "all") {
      params.type = currentType.value;
    }

    const res = await request.get(`/transactions/my/${memberId}`, { params });

    if (res.success) {
      const newTransactions = res.data.transactions || [];
      if (reset) {
        transactions.value = newTransactions;
      } else {
        transactions.value = [...transactions.value, ...newTransactions];
      }
      hasMore.value = newTransactions.length === perPage;
      page.value += 1;
    } else {
      toast.error(res.message || "获取交易记录失败");
    }
  } catch (e) {
    toast.error("网络错误，请重试");
  } finally {
    loading.value = false;
  }
};

const switchType = (type) => {
  currentType.value = type;
  fetchTransactions(true);
};

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    fetchTransactions();
  }
};

onMounted(() => {
  fetchStats();
  fetchTransactions();
});
</script>

<style scoped>
.transactions-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.summary-card {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  padding: 30rpx;
  margin: 20rpx;
  border-radius: 16rpx;
}

.summary-row {
  display: flex;
  justify-content: space-between;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.summary-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8rpx;
}

.summary-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
}

.summary-value.balance {
  font-size: 48rpx;
}

.summary-value.recharge {
  color: #ffe066;
}

.summary-value.consumption {
  color: #ffb4a2;
}

.summary-value.count {
  color: #c8e6c9;
}

.summary-divider {
  height: 1rpx;
  background-color: rgba(255, 255, 255, 0.2);
  margin: 20rpx 0;
}

.filter-tabs {
  display: flex;
  background-color: #ffffff;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #666666;
  border-radius: 8rpx;
  transition: all 0.3s ease;
}

.tab-item.active {
  background-color: #409eff;
  color: #ffffff;
  font-weight: bold;
}

.transactions-list {
  background-color: #ffffff;
  padding: 0 24rpx;
}

.transaction-item {
  display: flex;
  align-items: flex-start;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.transaction-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  background-color: #f5f5f5;
}

.transaction-icon.recharge {
  background-color: rgba(103, 194, 58, 0.1);
}

.transaction-icon.consumption {
  background-color: rgba(245, 108, 108, 0.1);
}

.transaction-icon.refund {
  background-color: rgba(230, 162, 60, 0.1);
}

.transaction-icon text {
  font-size: 36rpx;
}

.transaction-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.info-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.transaction-title {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
}

.transaction-amount {
  font-size: 32rpx;
  font-weight: bold;
}

.transaction-amount.positive {
  color: #67c23a;
}

.transaction-amount.negative {
  color: #f56c6c;
}

.info-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.transaction-time {
  font-size: 24rpx;
  color: #999999;
}

.transaction-status {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
}

.transaction-status.pending {
  color: #e6a23c;
  background-color: rgba(230, 162, 60, 0.1);
}

.transaction-status.completed {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.transaction-status.failed,
.transaction-status.cancelled {
  color: #f56c6c;
  background-color: rgba(245, 108, 108, 0.1);
}

.transaction-balance {
  font-size: 22rpx;
  color: #999999;
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
</style>
