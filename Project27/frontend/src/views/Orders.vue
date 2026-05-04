<template>
  <div class="orders-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
          <el-radio-group v-model="statusFilter" size="small" @change="loadOrders">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="PENDING">待接单</el-radio-button>
            <el-radio-button value="ACCEPTED">服务中</el-radio-button>
            <el-radio-button value="COMPLETED">待评价</el-radio-button>
            <el-radio-button value="RATED">已评价</el-radio-button>
            <el-radio-button value="CANCELLED">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="serviceName" label="服务名称" width="200" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold;">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="serviceAddress" label="服务地址" show-overflow-tooltip />
        <el-table-column prop="serviceTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.serviceTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="viewOrder(scope.row)">查看</el-button>
            <el-button
              link
              type="primary"
              v-if="scope.row.status === 'PENDING'"
              @click="cancelOrder(scope.row)"
            >
              取消
            </el-button>
            <el-button
              link
              type="primary"
              v-if="userStore.role === 'WORKER' && scope.row.status === 'PENDING'"
              @click="acceptOrder(scope.row)"
            >
              接单
            </el-button>
            <el-button
              link
              type="primary"
              v-if="userStore.role === 'WORKER' && scope.row.status === 'ACCEPTED'"
              @click="completeOrder(scope.row)"
            >
              完成
            </el-button>
            <el-button
              link
              type="primary"
              v-if="userStore.role !== 'WORKER' && scope.row.status === 'COMPLETED'"
              @click="showReviewDialog(scope.row)"
            >
              评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
      
      <el-empty v-if="orders.length === 0 && !loading" description="暂无订单" />
    </el-card>
    
    <el-dialog
      v-model="reviewDialogVisible"
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
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const loading = ref(false)
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const reviewDialogVisible = ref(false)
const reviewFormRef = ref(null)
const submitting = ref(false)
const currentOrder = ref(null)

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
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    const res = await api.get('/api/orders', { params })
    if (res.data.code === 200) {
      orders.value = res.data.data.records
      total.value = res.data.data.total
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

function viewOrder(row) {
  router.push(`/order/${row.id}`)
}

async function cancelOrder(row) {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    const res = await api.put(`/api/orders/${row.id}/cancel`)
    if (res.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(res.data.message || '取消失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function acceptOrder(row) {
  try {
    const res = await api.put(`/api/orders/${row.id}/accept`)
    if (res.data.code === 200) {
      ElMessage.success('接单成功')
      loadOrders()
    } else {
      ElMessage.error(res.data.message || '接单失败')
    }
  } catch (e) {
    console.error(e)
  }
}

async function completeOrder(row) {
  try {
    const res = await api.put(`/api/orders/${row.id}/complete`)
    if (res.data.code === 200) {
      ElMessage.success('订单已完成')
      loadOrders()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    console.error(e)
  }
}

function showReviewDialog(order) {
  currentOrder.value = order
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewDialogVisible.value = true
}

async function submitReview() {
  const valid = await reviewFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    const res = await api.post(`/api/orders/${currentOrder.value.id}/review`, reviewForm)
    if (res.data.code === 200) {
      ElMessage.success('评价成功')
      reviewDialogVisible.value = false
      loadOrders()
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
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
