<template>
  <div class="order-detail">
    <el-card class="status-card" v-if="order">
      <div class="status-header">
        <el-icon :size="40" :color="getStatusColor()">
          <component :is="getStatusIcon()" />
        </el-icon>
        <div class="status-info">
          <h2>{{ getStatusText() }}</h2>
          <p v-if="order.status === 0 && order.expireTime">
            请在 {{ formatTime(order.expireTime) }} 前完成支付，否则订单将自动取消
          </p>
        </div>
      </div>
      <div class="status-actions" v-if="order.status === 0">
        <el-button type="primary" size="large" :loading="paying" @click="handlePay">
          立即支付
        </el-button>
        <el-button size="large" @click="handleCancel">取消订单</el-button>
      </div>
      <div class="status-actions" v-else-if="order.status === 2">
        <el-button type="primary" size="large" @click="handleConfirm">
          确认收货
        </el-button>
      </div>
    </el-card>

    <el-card class="info-card">
      <template #header>
        <span>订单信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusTagType()">{{ getStatusText() }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">
          {{ order.paymentType === 1 ? '支付宝' : order.paymentType === 2 ? '微信支付' : '其他' }}
        </el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="order.paymentTime">
          {{ formatTime(order.paymentTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="发货时间" v-if="order.shipTime">
          {{ formatTime(order.shipTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="product-card">
      <template #header>
        <span>商品信息</span>
      </template>
      <div class="product-item">
        <div class="product-image">
          <img :src="order.productImage || 'https://picsum.photos/80/80'" alt="" />
        </div>
        <div class="product-info">
          <h3>{{ order.productName }}</h3>
          <p class="price">¥{{ order.unitPrice }} × {{ order.quantity }}</p>
        </div>
        <div class="product-total">
          <span class="total-amount">¥{{ order.totalAmount }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="address-card">
      <template #header>
        <span>收货信息</span>
      </template>
      <div class="address-info">
        <p><strong>收货人：</strong>{{ order.consignee }} {{ order.phone }}</p>
        <p><strong>收货地址：</strong>{{ order.address }}</p>
      </div>
    </el-card>

    <el-card class="amount-card">
      <template #header>
        <span>订单金额</span>
      </template>
      <div class="amount-list">
        <div class="amount-row">
          <span class="label">商品金额</span>
          <span class="value">¥{{ order.totalAmount }}</span>
        </div>
        <div class="amount-row">
          <span class="label">运费</span>
          <span class="value">¥0.00</span>
        </div>
        <div class="amount-row total">
          <span class="label">实付金额</span>
          <span class="value">¥{{ order.payAmount || order.totalAmount }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Timer,
  CircleCheckFilled,
  Truck,
  Coin,
  Close,
} from "@element-plus/icons-vue";
import orderApi from "@/api/order";

const route = useRoute();
const router = useRouter();

const order = ref(null);
const paying = ref(false);

const getStatusColor = () => {
  switch (order.value?.status) {
    case 0:
      return "#E6A23C";
    case 1:
      return "#67C23A";
    case 2:
      return "#409EFF";
    case 3:
      return "#67C23A";
    case 4:
      return "#909399";
    default:
      return "#909399";
  }
};

const getStatusIcon = () => {
  switch (order.value?.status) {
    case 0:
      return "Timer";
    case 1:
      return "Coin";
    case 2:
      return "Truck";
    case 3:
      return "CircleCheckFilled";
    case 4:
      return "Close";
    default:
      return "CircleCheckFilled";
  }
};

const getStatusText = () => {
  switch (order.value?.status) {
    case 0:
      return "待支付";
    case 1:
      return "待发货";
    case 2:
      return "待收货";
    case 3:
      return "已完成";
    case 4:
      return "已取消";
    default:
      return "未知状态";
  }
};

const getStatusTagType = () => {
  switch (order.value?.status) {
    case 0:
      return "warning";
    case 1:
      return "success";
    case 2:
      return "primary";
    case 3:
      return "success";
    case 4:
      return "info";
    default:
      return "info";
  }
};

const formatTime = (time) => {
  if (!time) return "";
  const date = new Date(time);
  return date.toLocaleString("zh-CN");
};

const getOrderDetail = async () => {
  try {
    const res = await orderApi.getDetail(route.params.id);
    order.value = res.data;
  } catch (error) {
    ElMessage.error("获取订单详情失败");
    router.push("/order");
  }
};

const handlePay = async () => {
  try {
    await ElMessageBox.confirm("确定支付该订单？", "确认支付", {
      confirmButtonText: "确认支付",
      cancelButtonText: "取消",
      type: "info",
    });

    paying.value = true;
    await orderApi.pay(order.value.id, 1);
    ElMessage.success("支付成功");
    getOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "支付失败");
    }
  } finally {
    paying.value = false;
  }
};

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm("确定取消该订单？", "确认取消", {
      confirmButtonText: "确认取消",
      cancelButtonText: "不取消",
      type: "warning",
    });

    await orderApi.cancel(order.value.id);
    ElMessage.success("订单已取消");
    router.push("/order");
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "取消失败");
    }
  }
};

const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm("确认已收到商品？", "确认收货", {
      confirmButtonText: "确认收货",
      cancelButtonText: "取消",
      type: "warning",
    });

    await orderApi.confirm(order.value.id);
    ElMessage.success("确认收货成功");
    getOrderDetail();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "确认失败");
    }
  }
};

onMounted(() => {
  getOrderDetail();
});
</script>

<style lang="scss" scoped>
.order-detail {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.status-card {
  margin-bottom: 20px;

  .status-header {
    display: flex;
    align-items: center;
    gap: 20px;

    .status-info {
      h2 {
        margin: 0 0 10px 0;
        font-size: 24px;
      }

      p {
        margin: 0;
        color: #909399;
      }
    }
  }

  .status-actions {
    margin-top: 20px;
    display: flex;
    gap: 10px;
  }
}

.info-card,
.product-card,
.address-card,
.amount-card {
  margin-bottom: 20px;

  :deep(.el-card__header) {
    font-weight: bold;
    font-size: 16px;
  }
}

.product-item {
  display: flex;
  align-items: center;
  gap: 15px;

  .product-image {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    overflow: hidden;
    background: #f5f5f5;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .product-info {
    flex: 1;

    h3 {
      margin: 0 0 10px 0;
      font-size: 16px;
    }

    .price {
      margin: 0;
      color: #909399;
    }
  }

  .product-total {
    text-align: right;

    .total-amount {
      font-size: 18px;
      font-weight: bold;
      color: #f56c6c;
    }
  }
}

.address-info {
  p {
    margin: 10px 0;
  }
}

.amount-list {
  .amount-row {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;

    &.total {
      padding-top: 15px;
      border-top: 1px solid #ebeef5;
      margin-top: 10px;

      .label,
      .value {
        font-size: 18px;
        font-weight: bold;
      }

      .value {
        color: #f56c6c;
      }
    }
  }
}
</style>
