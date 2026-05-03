<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><DataAnalysis /></el-icon>
        数据统计
      </h2>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div
          class="stat-icon"
          style="background: linear-gradient(135deg, #667eea, #764ba2)"
        >
          <el-icon :size="32"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>

      <div class="stat-card">
        <div
          class="stat-icon"
          style="background: linear-gradient(135deg, #11998e, #38ef7d)"
        >
          <el-icon :size="32"><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalProducts || 0 }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div
          class="stat-icon"
          style="background: linear-gradient(135deg, #f093fb, #f5576c)"
        >
          <el-icon :size="32"><Timer /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalSeckills || 0 }}</div>
          <div class="stat-label">秒杀活动数</div>
        </div>
      </div>

      <div class="stat-card">
        <div
          class="stat-icon"
          style="background: linear-gradient(135deg, #4facfe, #00f2fe)"
        >
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ statistics.totalOrders || 0 }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
    </div>

    <div class="dashboard-row">
      <div class="chart-card">
        <h3 class="card-title">
          <el-icon><TrendCharts /></el-icon>
          销售趋势
        </h3>
        <div class="chart-placeholder">
          <el-empty description="销售趋势图表" />
        </div>
      </div>

      <div class="chart-card">
        <h3 class="card-title">
          <el-icon><PieChart /></el-icon>
          订单状态分布
        </h3>
        <div class="order-status-list">
          <div
            class="status-item"
            v-for="item in orderStatusList"
            :key="item.status"
          >
            <div class="status-badge" :class="item.class">{{ item.label }}</div>
            <div class="status-count">{{ item.count || 0 }} 单</div>
            <el-progress
              :percentage="item.percentage || 0"
              :color="item.color"
              :stroke-width="10"
              style="flex: 1; margin: 0 15px"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="table-card">
      <h3 class="card-title">
        <el-icon><Timer /></el-icon>
        正在进行的秒杀活动
      </h3>
      <el-table :data="activeSeckills" style="width: 100%" v-loading="loading">
        <el-table-column prop="activityName" label="活动名称" min-width="200" />
        <el-table-column prop="productName" label="商品名称" min-width="200">
          <template #default="scope">
            {{ scope.row.product?.productName }}
          </template>
        </el-table-column>
        <el-table-column prop="seckillPrice" label="秒杀价" width="120">
          <template #default="scope">
            <span class="seckill-price">¥{{ scope.row.seckillPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存进度" width="200">
          <template #default="scope">
            <div class="stock-info">
              <el-progress
                :percentage="getStockPercent(scope.row)"
                :color="getStockColor(scope.row)"
                :stroke-width="10"
              />
              <span class="stock-text"
                >{{ scope.row.stock }}/{{ scope.row.seckillCount }}</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.endTime) }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty
        v-if="activeSeckills.length === 0 && !loading"
        description="暂无进行中的秒杀活动"
      />
    </div>

    <div class="table-card">
      <h3 class="card-title">
        <el-icon><Document /></el-icon>
        最近订单
      </h3>
      <el-table :data="recentOrders" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">
            <span class="order-amount">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)" size="small">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import dayjs from "dayjs";
import { getOrderStatistics } from "@/api/order";
import { getSeckillList } from "@/api/seckill";

const loading = ref(false);

const statistics = reactive({
  totalUsers: 0,
  totalProducts: 0,
  totalSeckills: 0,
  totalOrders: 0,
});

const activeSeckills = ref([]);
const recentOrders = ref([]);

const orderStatusList = [
  {
    status: 0,
    label: "待付款",
    class: "status-pending",
    color: "#e6a23c",
    count: 0,
    percentage: 0,
  },
  {
    status: 1,
    label: "待发货",
    class: "status-paid",
    color: "#409eff",
    count: 0,
    percentage: 0,
  },
  {
    status: 2,
    label: "待收货",
    class: "status-shipped",
    color: "#909399",
    count: 0,
    percentage: 0,
  },
  {
    status: 3,
    label: "已完成",
    class: "status-completed",
    color: "#67c23a",
    count: 0,
    percentage: 0,
  },
];

const formatDateTime = (date) => {
  if (!date) return "";
  return dayjs(date).format("YYYY-MM-DD HH:mm:ss");
};

const getStockPercent = (item) => {
  if (!item || item.seckillCount <= 0) return 0;
  const sold = item.seckillCount - item.stock;
  return Math.round((sold / item.seckillCount) * 100);
};

const getStockColor = (item) => {
  const percent = getStockPercent(item);
  if (percent >= 80) return "#f56c6c";
  if (percent >= 50) return "#e6a23c";
  return "#67c23a";
};

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

const fetchDashboardData = async () => {
  loading.value = true;
  try {
    const seckillRes = await getSeckillList({ status: 1, pageSize: 100 });
    activeSeckills.value = seckillRes.data || [];
    statistics.totalSeckills = activeSeckills.value.length;
  } catch (e) {
    console.error("获取仪表盘数据失败:", e);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchDashboardData();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: $text-primary;
    }

    .stat-label {
      font-size: 14px;
      color: $text-secondary;
    }
  }
}

.dashboard-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: bold;
    color: $text-primary;
    margin: 0 0 20px;
  }

  .chart-placeholder {
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.order-status-list {
  .status-item {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    .status-badge {
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
      min-width: 60px;
      text-align: center;

      &.status-pending {
        background: #fdf6ec;
        color: #e6a23c;
      }

      &.status-paid {
        background: #ecf5ff;
        color: #409eff;
      }

      &.status-shipped {
        background: #f4f4f5;
        color: #909399;
      }

      &.status-completed {
        background: #f0f9eb;
        color: #67c23a;
      }
    }

    .status-count {
      width: 60px;
      text-align: right;
      font-size: 14px;
      color: $text-secondary;
    }
  }
}

.table-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: bold;
    color: $text-primary;
    margin: 0 0 20px;
  }

  .seckill-price {
    font-size: 14px;
    font-weight: bold;
    color: $seckill-color;
  }

  .order-amount {
    font-size: 14px;
    font-weight: bold;
    color: $seckill-color;
  }

  .stock-info {
    display: flex;
    align-items: center;
    gap: 10px;

    :deep(.el-progress) {
      flex: 1;
      margin-right: 0;
    }

    .stock-text {
      font-size: 12px;
      color: $text-secondary;
      white-space: nowrap;
    }
  }
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .dashboard-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
