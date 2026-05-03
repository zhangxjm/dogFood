<template>
  <div class="track-container">
    <el-card shadow="never">
      <template #header>
        <span>物流追踪</span>
      </template>
      
      <el-form :inline="true" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" style="width: 300px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="searching">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleRefresh" v-if="orderData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-row :gutter="20" style="margin-top: 20px;" v-if="orderData">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>物流轨迹</span>
              <el-tag :type="getStatusType(orderData.status)">{{ getStatusText(orderData.status) }}</el-tag>
            </div>
          </template>
          
          <el-timeline v-if="trackList.length > 0">
            <el-timeline-item
              v-for="(track, index) in trackList"
              :key="track.id"
              :timestamp="formatDate(track.createTime)"
              placement="top"
              :type="index === trackList.length - 1 ? 'primary' : ''"
            >
              <el-card shadow="never" class="track-card">
                <div class="track-content">
                  <div class="track-title">{{ track.description }}</div>
                  <div class="track-location" v-if="track.location">
                    <el-icon><Location /></el-icon>
                    {{ track.location }}
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          
          <el-empty v-else description="暂无物流轨迹" />
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span>订单信息</span>
          </template>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单编号">{{ orderData.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="寄件人">{{ orderData.senderName }}</el-descriptions-item>
            <el-descriptions-item label="收件人">{{ orderData.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="物品">{{ orderData.goodsName }}</el-descriptions-item>
            <el-descriptions-item label="运费">¥{{ orderData.freight }}</el-descriptions-item>
          </el-descriptions>
          
          <el-divider />
          
          <div class="map-placeholder">
            <el-icon :size="60" color="#409eff"><MapLocation /></el-icon>
            <div class="map-text">高德地图API</div>
            <div class="map-tip">
              <p>使用高德地图可以：</p>
              <p>1. 显示寄件人/收件人位置</p>
              <p>2. 实时显示包裹位置</p>
              <p>3. 规划配送路线</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card shadow="never" style="margin-top: 20px;" v-else-if="!orderData && hasSearched">
      <el-empty description="未找到订单，请检查订单号" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderByOrderNo } from '@/api/order'
import { getLogisticsTracks } from '@/api/logistics'
import dayjs from 'dayjs'

const route = useRoute()

const searching = ref(false)
const hasSearched = ref(false)
const orderData = ref(null)
const trackList = ref([])

const searchForm = reactive({
  orderNo: ''
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

const loadTracks = async (orderId) => {
  try {
    const res = await getLogisticsTracks(orderId)
    trackList.value = (res.data || []).reverse()
  } catch (error) {
    console.error('加载物流轨迹失败:', error)
  }
}

const handleSearch = async () => {
  if (!searchForm.orderNo.trim()) {
    ElMessage.warning('请输入订单号')
    return
  }
  
  searching.value = true
  hasSearched.value = true
  
  try {
    const res = await getOrderByOrderNo(searchForm.orderNo.trim())
    if (res.data) {
      orderData.value = res.data
      loadTracks(res.data.id)
    } else {
      orderData.value = null
      trackList.value = []
    }
  } catch (error) {
    orderData.value = null
    trackList.value = []
  } finally {
    searching.value = false
  }
}

const handleRefresh = () => {
  handleSearch()
}

onMounted(() => {
  const orderNo = route.query.orderNo
  if (orderNo) {
    searchForm.orderNo = orderNo
    handleSearch()
  }
})
</script>

<style lang="scss" scoped>
.track-container {
  .search-form {
    margin-bottom: 0;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .track-card {
    margin-bottom: 10px;
    
    .track-content {
      .track-title {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
      }
      
      .track-location {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
  
  .map-placeholder {
    text-align: center;
    padding: 30px 0;
    
    .map-text {
      font-size: 14px;
      color: #409eff;
      margin-top: 10px;
    }
    
    .map-tip {
      margin-top: 15px;
      text-align: left;
      padding: 15px;
      background: #ecf5ff;
      border-radius: 4px;
      
      p {
        margin: 5px 0;
        font-size: 12px;
        color: #606266;
      }
    }
  }
}
</style>
