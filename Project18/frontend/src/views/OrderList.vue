<template>
  <div class="order-list-container">
    <Header />

    <main class="main-content">
      <div class="container">
        <div class="page-header">
          <h1 class="page-title">
            <el-icon><Document /></el-icon>
            我的订单
          </h1>
        </div>

        <el-tabs
          v-model="activeTab"
          class="order-tabs"
          @tab-change="handleTabChange"
        >
          <el-tab-pane label="全部" name="">
            <template #label>
              <span>全部</span>
              <el-badge
                :value="orderCounts.all"
                :hidden="orderCounts.all === 0"
                class="tab-badge"
              />
            </template>
          </el-tab-pane>
          <el-tab-pane label="待付款" name="0">
            <template #label>
              <span>待付款</span>
              <el-badge
                :value="orderCounts.pending"
                :hidden="orderCounts.pending === 0"
                class="tab-badge"
                type="danger"
              />
            </template>
          </el-tab-pane>
          <el-tab-pane label="待发货" name="1">
            <template #label>
              <span>待发货</span>
              <el-badge
                :value="orderCounts.paid"
                :hidden="orderCounts.paid === 0"
                class="tab-badge"
              />
            </template>
          </el-tab-pane>
          <el-tab-pane label="待收货" name="2">
            <template #label>
              <span>待收货</span>
              <el-badge
                :value="orderCounts.shipped"
                :hidden="orderCounts.shipped === 0"
                class="tab-badge"
              />
            </template>
          </el-tab-pane>
          <el-tab-pane label="已完成" name="3">
            <template #label>
              <span>已完成</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已取消" name="4">
            <template #label>
              <span>已取消</span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <div class="order-list" v-loading="loading">
          <div class="order-card" v-for="order in orderList" :key="order.id">
            <div class="order-header">
              <span class="order-time">{{
                formatDateTime(order.createTime)
              }}</span>
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <el-tag
                :type="getOrderStatusType(order.status)"
                effect="light"
                size="small"
              >
                {{ getOrderStatusText(order.status) }}
              </el-tag>
            </div>

            <div class="order-content">
              <router-link
                :to="{ name: 'ProductDetail', params: { id: order.productId } }"
                class="product-info"
              >
                <el-image
                  :src="order.productImage || '/placeholder.png'"
                  :alt="order.productName"
                  fit="cover"
                  class="product-image"
                />
                <div class="product-detail">
                  <h4 class="product-name">{{ order.productName }}</h4>
                  <div class="product-price">
                    <span>单价: ¥{{ order.unitPrice }}</span>
                    <span>数量: x{{ order.quantity }}</span>
                  </div>
                </div>
              </router-link>

              <div class="order-amount">
                <div class="amount-label">订单金额</div>
                <div class="amount-value">¥{{ order.totalAmount }}</div>
              </div>

              <div class="order-actions">
                <template v-if="order.status === 0">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handlePay(order)"
                  >
                    去支付
                  </el-button>
                  <el-button size="small" @click="handleCancel(order)">
                    取消订单
                  </el-button>
                  <div class="countdown" v-if="order.expireTime">
                    <span>剩余: </span>
                    <span class="time">{{ getOrderCountdown(order) }}</span>
                  </div>
                </template>

                <template v-else-if="order.status === 2">
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleConfirm(order)"
                  >
                    确认收货
                  </el-button>
                </template>

                <template v-else>
                  <el-button
                    type="primary"
                    size="small"
                    plain
                    @click="goToDetail(order)"
                  >
                    查看详情
                  </el-button>
                </template>
              </div>
            </div>
          </div>

          <el-empty
            v-if="orderList.length === 0 && !loading"
            description="暂无订单"
          />

          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchOrderList"
              @current-change="fetchOrderList"
            />
          </div>
        </div>
      </div>
    </main>

    <el-dialog v-model="payDialogVisible" title="订单支付" width="500px">
      <div class="pay-content" v-if="currentOrder">
        <div class="pay-info">
          <div class="info-row">
            <span class="info-label">订单号:</span>
            <span class="info-value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">商品名称:</span>
            <span class="info-value">{{ currentOrder.productName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">购买数量:</span>
            <span class="info-value">{{ currentOrder.quantity }} 件</span>
          </div>
          <div class="info-row total-row">
            <span class="info-label">应付金额:</span>
            <span class="info-value total-amount"
              >¥{{ currentOrder.totalAmount }}</span
            >
          </div>
        </div>

        <div class="pay-methods">
          <h4 class="methods-title">选择支付方式</h4>
          <div class="methods-list">
            <div
              class="method-item"
              :class="{ active: payMethod === 1 }"
              @click="payMethod = 1"
            >
              <el-icon :size="32" color="#1DA1F2"><Wallet /></el-icon>
              <span>支付宝</span>
            </div>
            <div
              class="method-item"
              :class="{ active: payMethod === 2 }"
              @click="payMethod = 2"
            >
              <el-icon :size="32" color="#07C160"><ChatDotRound /></el-icon>
              <span>微信支付</span>
            </div>
            <div
              class="method-item"
              :class="{ active: payMethod === 3 }"
              @click="payMethod = 3"
            >
              <el-icon :size="32" color="#409EFF"><CreditCard /></el-icon>
              <span>银行卡</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="payLoading"
          @click="handleConfirmPay"
        >
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {
  getOrderList,
  payOrder,
  cancelOrder,
  confirmReceive,
} from "@/api/order";
import { formatCountdown } from "@/utils";

const router = useRouter();

const loading = ref(false);
const activeTab = ref("");
const orderList = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const orderCounts = reactive({
  all: 0,
  pending: 0,
  paid: 0,
  shipped: 0,
});

const payDialogVisible = ref(false);
const currentOrder = ref(null);
const payMethod = ref(1);
const payLoading = ref(false);
const countdownTimer = ref(null);

const getOrderStatusType = (status) => {
  const types = {
    0: "warning",
    1: "primary",
    2: "info",
    3: "success",
    4: "info",
    5: "danger",
  };
  return types[status] || "info";
};

const getOrderStatusText = (status) => {
  const texts = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
    5: "已退款",
  };
  return texts[status] || "未知";
};

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const getOrderCountdown = (order) => {
  if (!order.expireTime) return "--:--:--";
  const now = dayjs();
  const diff = dayjs(order.expireTime).diff(now);
  if (diff <= 0) return "00:00:00";
  const countdown = formatCountdown(diff);
  return `${countdown.hours}:${countdown.minutes}:${countdown.seconds}`;
};

const handleTabChange = () => {
  pageNum.value = 1;
  fetchOrderList();
};

const fetchOrderList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };
    if (activeTab.value !== "") {
      params.status = parseInt(activeTab.value);
    }
    const res = await getOrderList(params);
    if (res.data) {
      orderList.value = res.data.list || [];
      total.value = res.data.total || 0;
    }
  } catch (e) {
    console.error("获取订单列表失败:", e);
  } finally {
    loading.value = false;
  }
};

const handlePay = (order) => {
  currentOrder.value = order;
  payMethod.value = 1;
  payDialogVisible.value = true;
};

const handleConfirmPay = async () => {
  if (!currentOrder.value) return;

  payLoading.value = true;
  try {
    await payOrder(currentOrder.value.id, payMethod.value);
    ElMessage.success("支付成功");
    payDialogVisible.value = false;
    fetchOrderList();
  } catch (e) {
    console.error("支付失败:", e);
  } finally {
    payLoading.value = false;
  }
};

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "确认取消", {
      confirmButtonText: "确定",
      cancelButtonText: "再想想",
      type: "warning",
    });

    await cancelOrder(order.id, "用户主动取消");
    ElMessage.success("订单已取消");
    fetchOrderList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("取消订单失败:", e);
    }
  }
};

const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm("确认已收到商品吗？", "确认收货", {
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      type: "warning",
    });

    await confirmReceive(order.id);
    ElMessage.success("已确认收货");
    fetchOrderList();
  } catch (e) {
    if (e !== "cancel") {
      console.error("确认收货失败:", e);
    }
  }
};

const goToDetail = (order) => {
  router.push({ name: "OrderDetail", params: { id: order.id } });
};

onMounted(() => {
  fetchOrderList();
  countdownTimer.value = setInterval(fetchOrderList, 30000);
});

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
});
</script>

<style lang="scss" scoped>
.order-list-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 20px;
    font-weight: bold;
    color: $text-primary;
    margin: 0;
  }
}

.order-tabs {
  background: $bg-color-white;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: $bg-color;

    .el-tabs__item {
      height: 50px;
      line-height: 50px;
      padding: 0 20px;
      position: relative;

      .tab-badge {
        margin-left: 4px;
      }
    }
  }
}

.order-list {
  .order-card {
    background: $bg-color-white;
    border-radius: 8px;
    margin-bottom: 15px;
    overflow: hidden;
  }

  .order-header {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    background: $bg-color-page;
    font-size: 14px;

    .order-time {
      color: $text-secondary;
      margin-right: 20px;
    }

    .order-no {
      color: $text-regular;
      margin-right: auto;
    }
  }

  .order-content {
    display: flex;
    align-items: center;
    padding: 20px;
    gap: 20px;

    .product-info {
      display: flex;
      align-items: center;
      gap: 15px;
      flex: 1;
      color: inherit;

      .product-image {
        width: 100px;
        height: 100px;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid $border-color-lighter;
      }

      .product-detail {
        .product-name {
          font-size: 14px;
          color: $text-primary;
          margin: 0 0 8px;
          height: 40px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .product-price {
          display: flex;
          gap: 20px;
          font-size: 13px;
          color: $text-secondary;
        }
      }
    }

    .order-amount {
      width: 120px;
      text-align: center;

      .amount-label {
        font-size: 12px;
        color: $text-secondary;
        margin-bottom: 4px;
      }

      .amount-value {
        font-size: 16px;
        font-weight: bold;
        color: $text-primary;
      }
    }

    .order-actions {
      width: 150px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;

      .countdown {
        font-size: 12px;
        color: $danger-color;

        .time {
          font-weight: bold;
        }
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.pay-content {
  .pay-info {
    background: $bg-color;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;

    .info-row {
      display: flex;
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }

      &.total-row {
        padding-top: 12px;
        border-top: 1px solid $border-color-lighter;
      }

      .info-label {
        width: 80px;
        color: $text-secondary;
      }

      .info-value {
        color: $text-primary;

        &.total-amount {
          font-size: 20px;
          font-weight: bold;
          color: $seckill-color;
        }
      }
    }
  }

  .pay-methods {
    .methods-title {
      font-size: 15px;
      font-weight: bold;
      color: $text-primary;
      margin: 0 0 15px;
    }

    .methods-list {
      display: flex;
      gap: 15px;

      .method-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        padding: 20px 30px;
        border: 2px solid $border-color;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: $primary-color;
        }

        &.active {
          border-color: $primary-color;
          background: rgba($primary-color, 0.05);
        }

        span {
          font-size: 13px;
          color: $text-regular;
        }
      }
    }
  }
}
</style>
