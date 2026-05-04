<template>
  <div class="order-detail-container">
    <el-card shadow="never" v-loading="loading">
      <template #header v-if="order.id">
        <div class="card-header">
          <span>订单详情</span>
          <el-tag :type="getStatusType(order.status)" size="large">{{ getStatusText(order.status) }}</el-tag>
        </div>
      </template>
      
      <el-descriptions v-if="order.id" :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ order.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务数量">{{ order.serviceQuantity }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span style="color: #F56C6C; font-weight: bold; font-size: 18px;">¥{{ order.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(order.serviceTime) }}</el-descriptions-item>
        <el-descriptions-item label="服务地址" :span="2">{{ order.serviceAddress }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ order.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务师傅">
          {{ order.workerName || '暂无' }}
          <span v-if="order.workerPhone" style="color: #909399; margin-left: 10px;">({{ order.workerPhone }})</span>
        </el-descriptions-item>
        <el-descriptions-item label="下单用户">
          {{ order.userName || '暂无' }}
          <span v-if="order.userPhone" style="color: #909399; margin-left: 10px;">({{ order.userPhone }})</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ order.remark || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="order-actions" v-if="order.id">
        <el-button type="primary" @click="goBack">返回列表</el-button>
        <el-button
          type="primary"
          v-if="order.status === 'PENDING'"
          @click="cancelOrder"
        >
          取消订单
        </el-button>
        <el-button
          type="primary"
          v-if="userStore.role === 'WORKER' && order.status === 'PENDING'"
          @click="acceptOrder"
        >
          接单
        </el-button>
        <el-button
          type="primary"
          v-if="userStore.role === 'WORKER' && order.status === 'ACCEPTED'"
          @click="completeOrder"
        >
          完成订单
        </el-button>
        <el-button
          type="primary"
          v-if="userStore.role !== 'WORKER' && order.status === 'COMPLETED'"
          @click="showReviewDialog = true"
        >
          评价订单
        </el-button>
      </div>
      
      <el-empty v-if="!order.id && !loading" description="订单不存在" />
    </el-card>
    
    <el-dialog
      v-model="showReviewDialog"
      title="评价订单"
      width="500px"
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewRules"
        label-width="80px"
      >
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text :texts="['极差', '失望', '一般', '满意', '惊喜']" />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref({})
const loading = ref(false)
const showReviewDialog = ref(false)
const reviewFormRef = ref(null)
const submitting = ref(false)

const reviewForm = reactive({
  rating: 5,
  content: ''
})

const reviewRules = {
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadOrderDetail()
})

async function loadOrderDetail() {
  loading.value = true
  try {
    const res = await api.get(`/api/orders/${route.params.id}`)
    if (res.data.code === 200) {
      order.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getStatusType(status) {
  const map = {
    'PENDING': 'warning',
    'ACCEPTED': 'primary',
    'COMPLETED': 'info',
    'RATED': 'success',
    'CANCELLED': 'danger'
  }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = {
    'PENDING': '待接单',
    'ACCEPTED': '服务中',
    'COMPLETED': '待评价',
    'RATED': '已评价',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function goBack() {
  router.back()
}

async function cancelOrder() {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await api.put(`/api/orders/${order.value.id}/cancel`)
    if (res.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrderDetail()
    } else {
      ElMessage.error(res.data.message || '取消失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function acceptOrder() {
  try {
    const res = await api.put(`/api/orders/${order.value.id}/accept`)
    if (res.data.code === 200) {
      ElMessage.success('接单成功')
      loadOrderDetail()
    } else {
      ElMessage.error(res.data.message || '接单失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function completeOrder() {
  try {
    const res = await api.put(`/api/orders/${order.value.id}/complete`)
    if (res.data.code === 200) {
      ElMessage.success('订单已完成')
      loadOrderDetail()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function submitReview() {
  const valid = await reviewFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    const res = await api.post(`/api/orders/${order.value.id}/review`, reviewForm)
    if (res.data.code === 200) {
      ElMessage.success('评价成功')
      showReviewDialog.value = false
      loadOrderDetail()
    } else {
      ElMessage.error(res.data.message || '评价失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order-detail-container {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-actions {
  margin-top: 20px;
  text-align: center;
}
</style>
