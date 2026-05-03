<template>
  <div class="seckill-detail-container">
    <Header />

    <main class="main-content">
      <div class="container" v-loading="loading">
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ name: 'Home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ name: 'SeckillList' }"
            >限时秒杀</el-breadcrumb-item
          >
          <el-breadcrumb-item>{{ activity?.activityName }}</el-breadcrumb-item>
        </el-breadcrumb>

        <div class="seckill-detail-card" v-if="activity && activity.product">
          <div class="seckill-header">
            <div class="activity-status" :class="activityStatusClass">
              <el-icon :size="18"><Activity /></el-icon>
              <span>{{ activityStatusText }}</span>
            </div>

            <div class="countdown-wrapper" v-if="activity.status === 1">
              <span class="countdown-label">距离结束</span>
              <div class="countdown-box">
                <span class="time-item">{{ countdown.hours }}</span>
                <span class="separator">:</span>
                <span class="time-item">{{ countdown.minutes }}</span>
                <span class="separator">:</span>
                <span class="time-item">{{ countdown.seconds }}</span>
              </div>
            </div>

            <div
              class="countdown-wrapper coming"
              v-else-if="activity.status === 0"
            >
              <span class="countdown-label">距离开始</span>
              <div class="countdown-box">
                <span class="time-item">{{ countdown.hours }}</span>
                <span class="separator">:</span>
                <span class="time-item">{{ countdown.minutes }}</span>
                <span class="separator">:</span>
                <span class="time-item">{{ countdown.seconds }}</span>
              </div>
            </div>
          </div>

          <div class="seckill-content">
            <div class="product-gallery">
              <el-image
                :src="activity.product.mainImage || '/placeholder.png'"
                :alt="activity.product.productName"
                fit="cover"
                class="main-image"
                :preview-src-list="[activity.product.mainImage]"
              />
            </div>

            <div class="product-info">
              <h1 class="activity-name">{{ activity.activityName }}</h1>
              <p class="product-title">{{ activity.product.productTitle }}</p>

              <div class="price-section">
                <div class="price-row seckill-row">
                  <span class="price-label">秒杀价</span>
                  <span class="seckill-price"
                    >¥{{ activity.seckillPrice }}</span
                  >
                  <span class="original-price"
                    >¥{{ activity.product.price }}</span
                  >
                  <span class="discount-badge">{{ discount }}折</span>
                </div>
              </div>

              <div class="info-section">
                <div class="info-row">
                  <span class="info-label">限购数量</span>
                  <span class="info-value highlight"
                    >每人限购 {{ activity.seckillLimit }} 件</span
                  >
                </div>
                <div class="info-row stock-row">
                  <span class="info-label">剩余库存</span>
                  <span class="info-value stock-value">
                    <el-progress
                      :percentage="stockPercentage"
                      :color="stockColor"
                      :stroke-width="10"
                      :show-text="false"
                      style="width: 200px; margin-left: 10px"
                    />
                    <span class="stock-text"
                      >{{ activity.stock }} / {{ activity.seckillCount }}</span
                    >
                  </span>
                </div>
                <div class="info-row">
                  <span class="info-label">活动时间</span>
                  <span class="info-value"
                    >{{ formatDateTime(activity.startTime) }} -
                    {{ formatDateTime(activity.endTime) }}</span
                  >
                </div>
              </div>

              <div
                class="quantity-section"
                v-if="activity.status === 1 && activity.stock > 0"
              >
                <span class="info-label">购买数量</span>
                <el-input-number
                  v-model="quantity"
                  :min="1"
                  :max="Math.min(activity.stock, activity.seckillLimit)"
                  size="large"
                />
              </div>

              <div class="action-section">
                <template v-if="activity.status === 1">
                  <el-button
                    type="danger"
                    size="large"
                    class="btn-seckill"
                    :loading="seckillLoading"
                    :disabled="activity.stock <= 0"
                    @click="handleSeckill"
                  >
                    <template v-if="activity.stock > 0">
                      <el-icon><Lightning /></el-icon>
                      立即秒杀
                    </template>
                    <template v-else>
                      <el-icon><Close /></el-icon>
                      已抢完
                    </template>
                  </el-button>
                  <el-button
                    type="primary"
                    size="large"
                    plain
                    @click="handleAddToCart"
                  >
                    <el-icon><ShoppingCart /></el-icon>
                    加入购物车
                  </el-button>
                </template>

                <template v-else-if="activity.status === 0">
                  <el-button
                    type="warning"
                    size="large"
                    class="btn-seckill"
                    disabled
                  >
                    <el-icon><Timer /></el-icon>
                    即将开始
                  </el-button>
                </template>

                <template v-else>
                  <el-button
                    type="info"
                    size="large"
                    class="btn-seckill"
                    disabled
                  >
                    <el-icon><Close /></el-icon>
                    活动已结束
                  </el-button>
                </template>
              </div>

              <div class="tips-section" v-if="activity.status === 1">
                <el-alert
                  title="温馨提示"
                  type="info"
                  :closable="false"
                  show-icon
                >
                  <template #default>
                    <div class="tips-content">
                      <p>• 秒杀商品数量有限，先到先得</p>
                      <p>• 每个用户限购 {{ activity.seckillLimit }} 件</p>
                      <p>• 订单创建后请在15分钟内完成支付</p>
                    </div>
                  </template>
                </el-alert>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <el-dialog
      v-model="resultDialogVisible"
      :title="resultDialogTitle"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="result-content" :class="resultStatus">
        <el-icon class="result-icon" :size="80">
          <SuccessFilled v-if="resultStatus === 'success'" />
          <WarningFilled v-else-if="resultStatus === 'pending'" />
          <CircleCloseFilled v-else />
        </el-icon>
        <p class="result-message">{{ resultMessage }}</p>
        <div v-if="resultOrderNo" class="result-order">
          订单号: <span class="order-no">{{ resultOrderNo }}</span>
        </div>
      </div>
      <template #footer>
        <el-button
          @click="resultDialogVisible = false"
          v-if="resultStatus === 'pending'"
        >
          关闭
        </el-button>
        <el-button
          type="primary"
          @click="goToOrder"
          v-else-if="resultStatus === 'success'"
        >
          去支付
        </el-button>
        <el-button @click="resultDialogVisible = false" v-else>
          知道了
        </el-button>
      </template>
    </el-dialog>

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {
  getSeckillDetail,
  executeSeckill,
  getSeckillResult,
} from "@/api/seckill";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";
import { formatCountdown, debounce } from "@/utils";

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();

const loading = ref(false);
const seckillLoading = ref(false);
const activity = ref(null);
const quantity = ref(1);
const countdown = ref({ hours: "00", minutes: "00", seconds: "00" });
const countdownTimer = ref(null);

const resultDialogVisible = ref(false);
const resultStatus = ref("success");
const resultMessage = ref("");
const resultOrderNo = ref("");
const resultPollingTimer = ref(null);

const activityStatusClass = computed(() => {
  const status = activity.value?.status;
  switch (status) {
    case 0:
      return "status-coming";
    case 1:
      return "status-active";
    case 2:
      return "status-ended";
    default:
      return "status-closed";
  }
});

const activityStatusText = computed(() => {
  const status = activity.value?.status;
  switch (status) {
    case 0:
      return "即将开始";
    case 1:
      return "进行中";
    case 2:
      return "已结束";
    default:
      return "已关闭";
  }
});

const discount = computed(() => {
  if (!activity.value) return "0";
  const percent =
    (activity.value.seckillPrice / activity.value.product.price) * 10;
  return percent.toFixed(1);
});

const stockPercentage = computed(() => {
  if (!activity.value) return 0;
  return (
    ((activity.value.seckillCount - activity.value.stock) /
      activity.value.seckillCount) *
    100
  );
});

const stockColor = computed(() => {
  if (stockPercentage.value >= 80) return "#f56c6c";
  if (stockPercentage.value >= 50) return "#e6a23c";
  return "#67c23a";
});

const resultDialogTitle = computed(() => {
  switch (resultStatus.value) {
    case "success":
      return "秒杀成功";
    case "pending":
      return "秒杀处理中";
    default:
      return "秒杀失败";
  }
});

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("MM-DD HH:mm");
};

const updateCountdown = () => {
  if (!activity.value) return;

  const now = dayjs();
  let targetTime;

  if (activity.value.status === 1) {
    targetTime = dayjs(activity.value.endTime);
  } else if (activity.value.status === 0) {
    targetTime = dayjs(activity.value.startTime);
  } else {
    countdown.value = { hours: "00", minutes: "00", seconds: "00" };
    return;
  }

  const diff = targetTime.diff(now);
  if (diff <= 0) {
    countdown.value = { hours: "00", minutes: "00", seconds: "00" };
    if (countdownTimer.value) {
      clearInterval(countdownTimer.value);
    }
    fetchSeckillDetail();
    return;
  }

  countdown.value = formatCountdown(diff);
};

const fetchSeckillDetail = async () => {
  const id = route.params.id;
  if (!id) return;

  loading.value = true;
  try {
    const res = await getSeckillDetail(id);
    activity.value = res.data;
    quantity.value = 1;
    updateCountdown();
  } catch (e) {
    console.error("获取秒杀详情失败:", e);
    ElMessage.error("获取秒杀详情失败");
  } finally {
    loading.value = false;
  }
};

const pollSeckillResult = async (activityId) => {
  let pollCount = 0;
  const maxPollCount = 10;

  const poll = async () => {
    try {
      const res = await getSeckillResult(activityId);
      if (res.data && res.data.status !== "pending") {
        if (resultPollingTimer.value) {
          clearInterval(resultPollingTimer.value);
          resultPollingTimer.value = null;
        }

        if (res.data.status === "success") {
          resultStatus.value = "success";
          resultMessage.value = "恭喜您，秒杀成功！";
          resultOrderNo.value = res.data.orderNo;
        } else {
          resultStatus.value = "fail";
          resultMessage.value = res.data.message || "秒杀失败，请稍后再试";
          resultOrderNo.value = "";
        }
      } else {
        pollCount++;
        if (pollCount >= maxPollCount) {
          if (resultPollingTimer.value) {
            clearInterval(resultPollingTimer.value);
            resultPollingTimer.value = null;
          }
          resultStatus.value = "pending";
          resultMessage.value = "系统正在处理中，请稍后查看订单";
          resultOrderNo.value = "";
        }
      }
    } catch (e) {
      console.error("轮询秒杀结果失败:", e);
    }
  };

  resultPollingTimer.value = setInterval(poll, 1000);
  poll();
};

const handleSeckill = debounce(
  async () => {
    if (!userStore.isLoggedIn) {
      ElMessage.warning("请先登录");
      router.push({ name: "Login", query: { redirect: route.fullPath } });
      return;
    }

    if (!activity.value || activity.value.status !== 1) {
      ElMessage.warning("活动未开始或已结束");
      return;
    }

    if (activity.value.stock <= 0) {
      ElMessage.warning("库存不足");
      return;
    }

    try {
      await ElMessageBox.confirm(
        `确定要秒杀该商品吗？\n秒杀价格: ¥${activity.value.seckillPrice}`,
        "确认秒杀",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        },
      );
    } catch {
      return;
    }

    seckillLoading.value = true;
    try {
      const res = await executeSeckill({
        activityId: activity.value.id,
        quantity: quantity.value,
      });

      if (res.data && res.data.status === "success") {
        resultStatus.value = "success";
        resultMessage.value = "恭喜您，秒杀成功！";
        resultOrderNo.value = res.data.orderNo;
        resultDialogVisible.value = true;
        fetchSeckillDetail();
      } else if (res.data && res.data.status === "pending") {
        resultStatus.value = "pending";
        resultMessage.value = "系统正在处理中，请稍候...";
        resultDialogVisible.value = true;
        pollSeckillResult(activity.value.id);
      } else {
        resultStatus.value = "fail";
        resultMessage.value = res.data?.message || "秒杀失败，请稍后再试";
        resultDialogVisible.value = true;
      }
    } catch (e) {
      console.error("秒杀失败:", e);
      resultStatus.value = "fail";
      resultMessage.value = e.message || "秒杀失败，请稍后再试";
      resultDialogVisible.value = true;
    } finally {
      seckillLoading.value = false;
    }
  },
  1000,
  { leading: true, trailing: false },
);

const handleAddToCart = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    router.push({ name: "Login", query: { redirect: route.fullPath } });
    return;
  }

  if (activity.value?.product) {
    cartStore.addItem({
      ...activity.value.product,
      quantity: quantity.value,
    });
    ElMessage.success("已加入购物车");
  }
};

const goToOrder = () => {
  resultDialogVisible.value = false;
  if (resultOrderNo.value) {
    router.push({ name: "OrderDetail", params: { id: resultOrderNo.value } });
  } else {
    router.push({ name: "OrderList" });
  }
};

onMounted(() => {
  fetchSeckillDetail();
  countdownTimer.value = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
  if (resultPollingTimer.value) {
    clearInterval(resultPollingTimer.value);
  }
});
</script>

<style lang="scss" scoped>
.seckill-detail-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.breadcrumb {
  margin-bottom: 20px;
}

.seckill-detail-card {
  background: $bg-color-white;
  border-radius: 8px;
  overflow: hidden;
}

.seckill-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background: linear-gradient(135deg, $seckill-color, #ff6b6b);

  .activity-status {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 16px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    color: #fff;
    font-weight: bold;

    &.status-coming {
      background: rgba($warning-color, 0.2);
      color: $warning-color;
    }

    &.status-active {
      background: rgba($success-color, 0.2);
      color: $success-color;
    }

    &.status-ended,
    &.status-closed {
      background: rgba($info-color, 0.2);
      color: $info-color;
    }
  }

  .countdown-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #fff;

    &.coming {
      color: $warning-color;

      .time-item {
        background: $warning-color;
      }
    }

    .countdown-label {
      font-size: 14px;
    }

    .countdown-box {
      display: flex;
      align-items: center;
      gap: 4px;

      .time-item {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        background: #fff;
        color: $seckill-color;
        border-radius: 4px;
        font-size: 20px;
        font-weight: bold;
      }

      .separator {
        font-size: 20px;
        font-weight: bold;
      }
    }
  }
}

.seckill-content {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.product-gallery {
  width: 400px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid $border-color-lighter;
}

.product-info {
  flex: 1;
}

.activity-name {
  font-size: 22px;
  font-weight: bold;
  color: $text-primary;
  margin: 0 0 8px;
}

.product-title {
  font-size: 14px;
  color: $text-secondary;
  margin: 0 0 20px;
}

.price-section {
  background: $seckill-bg;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;

  .seckill-row {
    display: flex;
    align-items: baseline;
    gap: 12px;

    .price-label {
      font-size: 14px;
      color: $text-secondary;
      width: 60px;
    }

    .seckill-price {
      font-size: 36px;
      font-weight: bold;
      color: $seckill-color;
    }

    .original-price {
      font-size: 18px;
      color: $text-secondary;
      text-decoration: line-through;
    }

    .discount-badge {
      background: linear-gradient(135deg, $seckill-color, #ff6b6b);
      color: #fff;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

.info-section {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;

  .info-label {
    font-size: 14px;
    color: $text-secondary;
    width: 80px;
    flex-shrink: 0;
  }

  .info-value {
    font-size: 14px;
    color: $text-primary;

    &.highlight {
      color: $seckill-color;
      font-weight: bold;
    }

    &.stock-value {
      display: flex;
      align-items: center;

      .stock-text {
        margin-left: 10px;
        color: $text-secondary;
      }
    }
  }
}

.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.action-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.btn-seckill {
  background: linear-gradient(135deg, $seckill-color, #ff6b6b);
  border: none;
  font-size: 16px;
  font-weight: bold;
  height: 50px;
  min-width: 160px;
  border-radius: 25px;

  &:hover {
    background: linear-gradient(135deg, #ff3333, #ff5252);
  }

  &.is-disabled {
    background: #ccc;
    cursor: not-allowed;
  }
}

.tips-section {
  .tips-content {
    p {
      margin: 5px 0;
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.result-content {
  text-align: center;
  padding: 20px 0;

  .result-icon {
    margin-bottom: 15px;

    &.success {
      color: $success-color;
    }

    &.pending {
      color: $warning-color;
    }

    &.fail {
      color: $danger-color;
    }
  }

  .result-message {
    font-size: 16px;
    color: $text-primary;
    margin: 0 0 15px;
  }

  .result-order {
    font-size: 14px;
    color: $text-secondary;

    .order-no {
      color: $primary-color;
      font-weight: bold;
    }
  }
}

@media (max-width: 768px) {
  .seckill-content {
    flex-direction: column;
    gap: 20px;
  }

  .product-gallery {
    width: 100%;
  }

  .main-image {
    height: 300px;
  }

  .seckill-header {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
