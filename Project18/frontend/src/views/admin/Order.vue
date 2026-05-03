<template>
  <div class="order-admin-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Document /></el-icon>
        订单管理
      </h2>
    </div>

    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input
            v-model="searchForm.orderNo"
            placeholder="请输入订单号"
            clearable
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table
        :data="orderList"
        style="width: 100%"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="scope"> ¥{{ scope.row.unitPrice }} </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">
            <span class="total-amount">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="consignee" label="收货人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleDetail(scope.row)">
              详情
            </el-button>
            <el-button
              type="success"
              link
              v-if="scope.row.status === 1"
              @click="handleShip(scope.row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrderList"
          @current-change="fetchOrderList"
        />
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <div class="order-detail" v-if="currentOrder">
        <div class="detail-section">
          <h4 class="section-title">订单信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">订单号:</span>
              <span class="detail-value">{{ currentOrder.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">订单状态:</span>
              <el-tag :type="getStatusType(currentOrder.status)" size="small">
                {{ getStatusText(currentOrder.status) }}
              </el-tag>
            </div>
            <div class="detail-item">
              <span class="detail-label">下单时间:</span>
              <span class="detail-value">{{
                formatDateTime(currentOrder.createTime)
              }}</span>
            </div>
            <div class="detail-item" v-if="currentOrder.paymentTime">
              <span class="detail-label">支付时间:</span>
              <span class="detail-value">{{
                formatDateTime(currentOrder.paymentTime)
              }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">商品信息</h4>
          <div class="product-info">
            <el-image
              :src="currentOrder.productImage || '/placeholder.png'"
              fit="cover"
              class="product-image"
            />
            <div class="product-detail">
              <h4 class="product-name">{{ currentOrder.productName }}</h4>
              <p class="product-price">
                单价: ¥{{ currentOrder.unitPrice }} x
                {{ currentOrder.quantity }}
              </p>
              <p class="product-amount">
                订单金额:
                <span class="amount">¥{{ currentOrder.totalAmount }}</span>
              </p>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">收货信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">收货人:</span>
              <span class="detail-value">{{ currentOrder.consignee }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系电话:</span>
              <span class="detail-value">{{ currentOrder.phone }}</span>
            </div>
            <div class="detail-item" style="grid-column: span 2">
              <span class="detail-label">收货地址:</span>
              <span class="detail-value">{{ currentOrder.address }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="currentOrder.remark">
          <h4 class="section-title">订单备注</h4>
          <p class="remark-text">{{ currentOrder.remark }}</p>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="shipVisible" title="订单发货" width="500px">
      <el-form
        ref="shipFormRef"
        :model="shipForm"
        :rules="shipRules"
        label-width="100px"
      >
        <el-form-item label="物流公司" prop="company">
          <el-select
            v-model="shipForm.company"
            placeholder="请选择物流公司"
            style="width: 100%"
          >
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="EMS" value="EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="trackingNo">
          <el-input
            v-model="shipForm.trackingNo"
            placeholder="请输入物流单号"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="shipLoading"
          @click="handleConfirmShip"
        >
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import { getOrderPage, shipOrder } from "@/api/order";

const loading = ref(false);
const shipLoading = ref(false);
const detailVisible = ref(false);
const shipVisible = ref(false);
const shipFormRef = ref(null);

const searchForm = reactive({
  orderNo: "",
  status: null,
});

const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const orderList = ref([]);
const currentOrder = ref(null);

const shipForm = reactive({
  orderId: null,
  company: "",
  trackingNo: "",
});

const shipRules = {
  company: [{ required: true, message: "请选择物流公司", trigger: "change" }],
  trackingNo: [{ required: true, message: "请输入物流单号", trigger: "blur" }],
};

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const getStatusType = (status) => {
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

const getStatusText = (status) => {
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

const fetchOrderList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm,
    };
    const res = await getOrderPage(params);
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

const handleSearch = () => {
  pageNum.value = 1;
  fetchOrderList();
};

const handleReset = () => {
  searchForm.orderNo = "";
  searchForm.status = null;
  pageNum.value = 1;
  fetchOrderList();
};

const handleDetail = (row) => {
  currentOrder.value = row;
  detailVisible.value = true;
};

const handleShip = (row) => {
  shipForm.orderId = row.id;
  shipForm.company = "";
  shipForm.trackingNo = "";
  shipVisible.value = true;
};

const handleConfirmShip = async () => {
  const valid = await shipFormRef.value.validate().catch(() => false);
  if (!valid) return;

  shipLoading.value = true;
  try {
    await shipOrder(shipForm.orderId, shipForm);
    ElMessage.success("发货成功");
    shipVisible.value = false;
    fetchOrderList();
  } catch (e) {
    console.error("发货失败:", e);
  } finally {
    shipLoading.value = false;
  }
};

onMounted(() => {
  fetchOrderList();
});
</script>

<style lang="scss" scoped>
.order-admin-container {
  .page-header {
    margin-bottom: 20px;

    .page-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: bold;
      color: $text-primary;
      margin: 0;
    }
  }
}

.search-bar {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;

  .search-form {
    margin-bottom: 0;
  }
}

.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;

  .total-amount {
    font-weight: bold;
    color: $seckill-color;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

.order-detail {
  .detail-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      font-size: 15px;
      font-weight: bold;
      color: $text-primary;
      margin: 0 0 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid $border-color-lighter;
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;

      .detail-item {
        display: flex;
        align-items: center;

        .detail-label {
          width: 80px;
          font-size: 14px;
          color: $text-secondary;
          flex-shrink: 0;
        }

        .detail-value {
          font-size: 14px;
          color: $text-primary;
        }
      }
    }

    .product-info {
      display: flex;
      gap: 15px;

      .product-image {
        width: 100px;
        height: 100px;
        border-radius: 8px;
        overflow: hidden;
        border: 1px solid $border-color-lighter;
        flex-shrink: 0;
      }

      .product-detail {
        flex: 1;

        .product-name {
          font-size: 14px;
          font-weight: bold;
          color: $text-primary;
          margin: 0 0 8px;
        }

        .product-price {
          font-size: 13px;
          color: $text-secondary;
          margin: 0 0 8px;
        }

        .product-amount {
          font-size: 14px;
          color: $text-secondary;
          margin: 0;

          .amount {
            font-size: 18px;
            font-weight: bold;
            color: $seckill-color;
          }
        }
      }
    }

    .remark-text {
      font-size: 14px;
      color: $text-regular;
      margin: 0;
      line-height: 1.6;
    }
  }
}
</style>
