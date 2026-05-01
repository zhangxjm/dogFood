<template>
  <div class="orders-container">
    <div class="page-header">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-card>
      <el-table :data="orders" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button 
              v-if="row.status === 1" 
              type="success" 
              link 
              @click="handleDelivery(row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <div class="order-detail" v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="order-items">
          <h4>订单商品</h4>
          <el-table :data="currentOrder.items" style="width: 100%">
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="productImage" label="商品图片" width="100">
              <template #default="{ row }">
                <el-avatar :size="50" :src="row.productImage" />
              </template>
            </el-table-column>
            <el-table-column prop="productPrice" label="单价" width="100">
              <template #default="{ row }">
                ¥{{ row.productPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="小计" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getOrders, deliveryOrder } from '@/api/order'

const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)

const orders = ref([])

const searchForm = reactive({
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const types = ['warning', 'primary', 'success', 'info', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待付款', '待发货', '待收货', '已完成', '已取消']
  return texts[status] || '未知'
}

const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrders({
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    orders.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadOrders()
}

const handleView = (row) => {
  currentOrder.value = {
    ...row,
    items: [
      { productName: 'iPhone 15 Pro Max', productImage: '', productPrice: 8999, quantity: 1, totalAmount: 8999 }
    ]
  }
  detailVisible.value = true
}

const handleDelivery = (row) => {
  ElMessageBox.confirm('确定要发货吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deliveryOrder(row.id)
      ElMessage.success('发货成功')
      loadOrders()
    } catch (error) {
      console.error('发货失败:', error)
    }
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadOrders()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadOrders()
}

onMounted(() => {
  orders.value = [
    { id: 1, orderNo: '20240501100001', totalAmount: 8999, payAmount: 8999, status: 0, createTime: new Date(), payTime: null },
    { id: 2, orderNo: '20240501100002', totalAmount: 6999, payAmount: 6999, status: 1, createTime: new Date(), payTime: new Date() },
    { id: 3, orderNo: '20240501100003', totalAmount: 1299, payAmount: 1299, status: 2, createTime: new Date(), payTime: new Date() },
    { id: 4, orderNo: '20240501100004', totalAmount: 14999, payAmount: 14999, status: 3, createTime: new Date(), payTime: new Date() },
    { id: 5, orderNo: '20240501100005', totalAmount: 99, payAmount: 99, status: 4, createTime: new Date(), payTime: null }
  ]
  pagination.total = 1280
})
</script>

<style lang="scss" scoped>
.orders-container {
  .page-header {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .price {
    color: #FF4757;
    font-weight: bold;
  }
  
  .order-detail {
    .order-items {
      margin-top: 20px;
      
      h4 {
        margin-bottom: 10px;
        color: #333;
      }
    }
  }
}
</style>
