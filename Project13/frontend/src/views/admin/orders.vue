<template>
  <div class="admin-orders-container">
    <el-card shadow="never">
      <template #header>
        <span>订单审核</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.keyword" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已取件" :value="2" />
            <el-option label="运输中" :value="3" />
            <el-option label="派送中" :value="4" />
            <el-option label="已签收" :value="5" />
            <el-option label="已取消" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="220" />
        <el-table-column label="寄件人" width="150">
          <template #default="scope">
            <div>{{ scope.row.senderName }}</div>
            <div class="sub-text">{{ scope.row.senderPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column label="收件人" width="150">
          <template #default="scope">
            <div>{{ scope.row.receiverName }}</div>
            <div class="sub-text">{{ scope.row.receiverPhone }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="物品名称" />
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
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.id)">
              查看详情
            </el-button>
            <el-button type="primary" link @click="goTrack(scope.row)">
              物流追踪
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              link
              @click="handleReview(scope.row.id, true)"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="danger"
              link
              @click="handleReview(scope.row.id, false)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, reviewOrder } from '@/api/order'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const orderList = ref([])

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

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

const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    }
    
    const res = await getOrderPage(params)
    orderList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.current = 1
  loadData()
}

const goDetail = (id) => {
  router.push(`/order/detail/${id}`)
}

const goTrack = (row) => {
  router.push({
    path: '/track',
    query: { orderNo: row.orderNo }
  })
}

const handleReview = async (orderId, approved) => {
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
    
    await reviewOrder(orderId, approved)
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
.admin-orders-container {
  .search-form {
    margin-bottom: 20px;
  }
  
  .sub-text {
    font-size: 12px;
    color: #909399;
  }
}
</style>
