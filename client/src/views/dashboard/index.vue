<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon orders-icon">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon products-icon">
            <el-icon size="32"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalProducts }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon users-icon">
            <el-icon size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    
    <el-col :span="6">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon revenue-icon">
            <el-icon size="32"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.totalRevenue }}</div>
            <div class="stat-label">总销售额</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
  
  <el-row :gutter="20" class="mt-20">
    <el-col :span="12">
      <el-card>
      <template #header>
        <div class="card-header">
          <span>热销商品</span>
        </div>
      </template>
      <el-table :data="hotProducts" style="width: 100%">
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="price" label="售价" width="120">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="stock" label="库存" width="100" />
      </el-table>
    </el-card>
  </el-col>
  
  <el-col :span="12">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>最近订单</span>
          <el-button type="text" @click="goToOrders">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="payAmount" label="金额" width="100">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </el-col>
</el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()

const stats = ref({
  totalOrders: 0,
  totalProducts: 0,
  totalUsers: 0,
  totalRevenue: '0.00'
})

const hotProducts = ref([
  { name: 'iPhone 15 Pro Max', price: 8999, sales: 520, stock: 100 },
  { name: '华为Mate 60 Pro', price: 6999, sales: 380, stock: 80 },
  { name: 'MacBook Pro 14英寸', price: 14999, sales: 120, stock: 50 },
  { name: 'Nike Air Jordan 1', price: 1299, sales: 650, stock: 200 },
  { name: 'SK-II神仙水', price: 1290, sales: 450, stock: 150 }
])

const recentOrders = ref([
  { orderNo: '20240501100001', payAmount: 8999, status: 1, createTime: new Date() },
  { orderNo: '20240501100002', payAmount: 6999, status: 2, createTime: new Date() },
  { orderNo: '20240501100003', payAmount: 1299, status: 3, createTime: new Date() }
])

const getStatusType = (status) => {
  const types = ['warning', 'primary', 'success', 'info', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待付款', '待发货', '待收货', '已完成', '已取消']
  return texts[status] || '未知'
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const goToOrders = () => {
  router.push('/orders')
}

onMounted(() => {
  stats.value = {
    totalOrders: 1280,
    totalProducts: 120,
    totalUsers: 5680,
    totalRevenue: '1,256,800.00'
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    :deep(.el-card__body) {
      padding: 20px;
    }
  }
  
  .stat-content {
    display: flex;
    align-items: center;
  }
  
  .stat-icon {
    width: 80px;
    height: 80px;
    border-radius: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 20px;
    
    &.orders-icon {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
    }
    
    &.products-icon {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: #fff;
    }
    
    &.users-icon {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: #fff;
    }
    
    &.revenue-icon {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      color: #fff;
    }
  }
  
  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #333;
    }
    
    .stat-label {
      font-size: 14px;
      color: #999;
      margin-top: 4px;
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
  }
  
  .price-text {
    color: #FF4757;
    font-weight: bold;
  }
}
</style>
