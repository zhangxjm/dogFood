<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOrders }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon transit-icon">
              <el-icon><Van /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.inTransit }}</div>
              <div class="stat-label">运输中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon delivered-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.delivered }}</div>
              <div class="stat-label">已签收</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="quick-action" @click="goCreateOrder">
                <el-icon class="action-icon" :size="40"><Plus /></el-icon>
                <div class="action-text">快递下单</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="quick-action" @click="goTrack">
                <el-icon class="action-icon" :size="40"><Search /></el-icon>
                <div class="action-text">物流追踪</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="quick-action" @click="goOrders">
                <el-icon class="action-icon" :size="40"><List /></el-icon>
                <div class="action-text">我的订单</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <el-card shadow="never" style="margin-top: 20px;">
          <template #header>
            <span>最近订单</span>
            <el-button type="primary" link @click="goOrders">查看全部</el-button>
          </template>
          <el-table :data="recentOrders" style="width: 100%" v-loading="loading">
            <el-table-column prop="orderNo" label="订单编号" width="220" />
            <el-table-column prop="goodsName" label="物品名称" />
            <el-table-column prop="receiverName" label="收件人" width="100" />
            <el-table-column prop="freight" label="运费" width="100">
              <template #default="scope">
                ¥{{ scope.row.freight }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div class="status-distribution">
            <div class="status-item" v-for="(item, index) in statusDistribution" :key="index">
              <div class="status-name">{{ item.name }}</div>
              <el-progress :percentage="item.percentage" :color="item.color" />
              <div class="status-count">{{ item.count }} 单</div>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="never" style="margin-top: 20px;">
          <template #header>
            <span>系统提示</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :timestamp="activity.timestamp"
              placement="top"
            >
              <el-card>
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderPage } from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const recentOrders = ref([])

const stats = reactive({
  totalOrders: 0,
  inTransit: 0,
  pending: 0,
  delivered: 0
})

const statusDistribution = ref([
  { name: '待审核', count: 0, percentage: 0, color: '#909399' },
  { name: '已审核', count: 0, percentage: 0, color: '#409EFF' },
  { name: '运输中', count: 0, percentage: 0, color: '#67C23A' },
  { name: '已签收', count: 0, percentage: 0, color: '#67C23A' }
])

const activities = ref([
  {
    title: '欢迎使用物流追踪系统',
    content: '您可以创建快递订单，实时追踪物流状态。',
    timestamp: '刚刚'
  },
  {
    title: '测试账号',
    content: '管理员: admin/admin123，普通用户: user1/123456',
    timestamp: '1天前'
  }
])

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '已审核',
    2: '已取件',
    3: '运输中',
    4: '派送中',
    5: '已签收',
    6: '已取消'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'success',
    6: 'info'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = userStore.isAdmin ? {} : { userId: userStore.userInfo?.id }
    const res = await getOrderPage({ current: 1, size: 10, ...params })
    const orders = res.data?.records || []
    recentOrders.value = orders.slice(0, 5)
    
    stats.totalOrders = res.data?.total || 0
    stats.inTransit = orders.filter(o => o.status === 3 || o.status === 4).length
    stats.pending = orders.filter(o => o.status === 0).length
    stats.delivered = orders.filter(o => o.status === 5).length
    
    if (stats.totalOrders > 0) {
      statusDistribution.value[0].count = orders.filter(o => o.status === 0).length
      statusDistribution.value[1].count = orders.filter(o => o.status === 1).length
      statusDistribution.value[2].count = orders.filter(o => o.status === 2 || o.status === 3 || o.status === 4).length
      statusDistribution.value[3].count = orders.filter(o => o.status === 5).length
      
      statusDistribution.value.forEach(item => {
        item.percentage = Math.round((item.count / stats.totalOrders) * 100)
      })
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const goCreateOrder = () => router.push('/order/create')
const goTrack = () => router.push('/track')
const goOrders = () => router.push('/orders')

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 30px;
        color: #fff;
        
        &.order-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        &.transit-icon {
          background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
        }
        &.pending-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        &.delivered-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
      }
      
      .stat-info {
        margin-left: 20px;
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
  
  .quick-action {
    text-align: center;
    padding: 20px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s;
    
    &:hover {
      background: #f4f4f5;
    }
    
    .action-icon {
      color: #409eff;
    }
    
    .action-text {
      margin-top: 10px;
      font-size: 14px;
      color: #606266;
    }
  }
  
  .status-distribution {
    .status-item {
      margin-bottom: 20px;
      
      .status-name {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }
      
      .status-count {
        font-size: 12px;
        color: #909399;
        text-align: right;
        margin-top: 4px;
      }
    }
  }
}
</style>
