<template>
  <div class="my-claims-page">
    <div class="page-header">
      <h2>我的认领</h2>
      <el-radio-group v-model="statusFilter" @change="fetchClaims" size="large">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="pending">待处理</el-radio-button>
        <el-radio-button value="approved">已通过</el-radio-button>
        <el-radio-button value="rejected">已拒绝</el-radio-button>
      </el-radio-group>
    </div>
    
    <div class="claims-list" v-loading="loading">
      <div v-if="!loading && claims.length === 0" class="empty-state">
        <el-empty description="暂无认领记录" />
      </div>
      
      <div v-else class="claim-card" v-for="claim in filteredClaims" :key="claim.id">
        <div class="claim-item">
          <div class="item-image">
            <img v-if="claim.item?.image1" :src="claim.item.image1" :alt="claim.item.title" />
            <div v-else class="no-image">
              <el-icon :size="24"><Picture /></el-icon>
            </div>
          </div>
          <div class="item-info">
            <h3 class="item-title" @click="goDetail(claim.item?.id)">{{ claim.item?.title }}</h3>
            <p class="item-meta">
              <el-tag :type="claim.item?.item_type === 'lost' ? 'danger' : 'success'" size="small">
                {{ claim.item?.item_type === 'lost' ? '寻物启事' : '招领启事' }}
              </el-tag>
              <span class="location"><el-icon><Location /></el-icon> {{ claim.item?.location }}</span>
              <span class="time"><el-icon><Clock /></el-icon> {{ formatDate(claim.item?.item_time) }}</span>
            </p>
          </div>
        </div>
        
        <div class="claim-content">
          <div class="claim-header">
            <span class="label">认领说明：</span>
            <el-tag :type="getStatusType(claim.status)" size="large">
              {{ getStatusText(claim.status) }}
            </el-tag>
          </div>
          <p class="claim-desc">{{ claim.description }}</p>
          <div class="claim-meta">
            <span>联系电话：{{ claim.contact_phone }}</span>
            <span>申请时间：{{ formatDate(claim.created_at) }}</span>
          </div>
          <div v-if="claim.status === 'rejected'" class="reject-info">
            <span class="reject-label">拒绝原因：</span>
            <span class="reject-reason">{{ claim.reject_reason }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { claimApi } from '@/api/claim'

const router = useRouter()

const loading = ref(false)
const claims = ref([])
const statusFilter = ref('')

const filteredClaims = computed(() => {
  if (!statusFilter.value) {
    return claims.value
  }
  return claims.value.filter(claim => claim.status === statusFilter.value)
})

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待处理',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return textMap[status] || '未知'
}

const fetchClaims = async () => {
  loading.value = true
  try {
    const res = await claimApi.getMyClaims()
    if (res.code === 200) {
      claims.value = res.data || []
    }
  } catch (error) {
    console.error('获取认领记录失败:', error)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  if (id) {
    router.push(`/item/${id}`)
  }
}

onMounted(() => {
  fetchClaims()
})
</script>

<style lang="scss" scoped>
.my-claims-page {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #ebeef5;
    
    h2 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
  
  .empty-state {
    padding: 60px 0;
  }
  
  .claim-card {
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background: #fafafa;
    }
    
    .claim-item {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px dashed #ebeef5;
      
      .item-image {
        width: 80px;
        height: 60px;
        flex-shrink: 0;
        border-radius: 6px;
        overflow: hidden;
        background: #f5f7fa;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .no-image {
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #c0c4cc;
        }
      }
      
      .item-info {
        flex: 1;
        
        .item-title {
          font-size: 15px;
          font-weight: 500;
          color: #303133;
          margin: 0 0 8px;
          cursor: pointer;
          
          &:hover {
            color: #409eff;
          }
        }
        
        .item-meta {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          align-items: center;
          font-size: 13px;
          color: #909399;
          
          .location,
          .time {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }
    
    .claim-content {
      .claim-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .label {
          font-size: 14px;
          color: #606266;
          font-weight: 500;
        }
      }
      
      .claim-desc {
        font-size: 14px;
        color: #303133;
        margin: 0 0 12px;
        line-height: 1.6;
        padding: 12px;
        background: #f5f7fa;
        border-radius: 6px;
      }
      
      .claim-meta {
        display: flex;
        gap: 24px;
        font-size: 13px;
        color: #909399;
      }
      
      .reject-info {
        margin-top: 12px;
        padding: 12px;
        background: #fef0f0;
        border-radius: 6px;
        font-size: 13px;
        
        .reject-label {
          color: #f56c6c;
          font-weight: 500;
        }
        
        .reject-reason {
          color: #606266;
        }
      }
    }
  }
}
</style>
