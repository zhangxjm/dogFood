<template>
  <div class="order-detail-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>订单详情</span>
          <el-button type="primary" link @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>
      
      <el-descriptions title="基本信息" :column="3" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="预计送达">{{ formatDate(order.expectedTime) }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ order.freight }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="物品名称">{{ order.goodsName }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <el-row :gutter="40">
        <el-col :span="12">
          <el-descriptions title="寄件人信息" :column="1" border>
            <el-descriptions-item label="姓名">{{ order.senderName }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ order.senderPhone }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ order.senderAddress }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <el-descriptions title="收件人信息" :column="1" border>
            <el-descriptions-item label="姓名">{{ order.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ order.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ order.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      
      <el-divider />
      
      <el-descriptions title="物品信息" :column="3" border>
        <el-descriptions-item label="物品名称">{{ order.goodsName }}</el-descriptions-item>
        <el-descriptions-item label="重量">{{ order.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="体积">{{ order.volume }} m³</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ order.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <div class="action-buttons">
        <el-button type="primary" @click="goTrack">
          <el-icon><Search /></el-icon>
          查看物流
        </el-button>
        <el-button v-if="canReview(order.status)" type="success" @click="handleReview(true)">
          审核通过
        </el-button>
        <el-button v-if="canReview(order.status)" type="danger" @click="handleReview(false)">
          审核拒绝
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderById, reviewOrder } from '@/api/order'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const order = ref({})

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

const canReview = (status) => {
  return userStore.isAdmin && status === 0
}

const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const loadData = async () => {
  const orderId = route.params.id
  if (!orderId) return
  
  loading.value = true
  try {
    const res = await getOrderById(orderId)
    order.value = res.data || {}
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.go(-1)
}

const goTrack = () => {
  router.push({
    path: '/track',
    query: { orderNo: order.value.orderNo }
  })
}

const handleReview = async (approved) => {
  try {
    await ElMessageBox.confirm(
      `确定要${approved ? '通过' : '拒绝'}该订单吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await reviewOrder(route.params.id, approved)
    ElMessage.success(approved ? '审核通过' : '审核拒绝')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.order-detail-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .action-buttons {
    text-align: center;
    margin-top: 20px;
  }
}
</style>
