<template>
  <div class="my-items-page">
    <div class="page-header">
      <h2>我的发布</h2>
      <el-radio-group v-model="statusFilter" @change="fetchItems" size="large">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="pending">待审核</el-radio-button>
        <el-radio-button value="active">已发布</el-radio-button>
        <el-radio-button value="resolved">已完成</el-radio-button>
        <el-radio-button value="rejected">已驳回</el-radio-button>
      </el-radio-group>
    </div>
    
    <div class="items-list" v-loading="loading">
      <div v-if="!loading && items.length === 0" class="empty-state">
        <el-empty description="暂无发布记录" />
      </div>
      
      <div v-else class="item-card" v-for="item in items" :key="item.id">
        <div class="item-image">
          <img v-if="item.image1" :src="item.image1" :alt="item.title" />
          <div v-else class="no-image">
            <el-icon :size="32"><Picture /></el-icon>
          </div>
          <el-tag :type="getStatusType(item.status)" size="small">
            {{ getStatusText(item.status) }}
          </el-tag>
        </div>
        
        <div class="item-content">
          <div class="item-header">
            <h3 class="item-title">{{ item.title }}</h3>
            <el-tag :type="item.item_type === 'lost' ? 'danger' : 'success'" size="small">
              {{ item.item_type === 'lost' ? '寻物启事' : '招领启事' }}
            </el-tag>
          </div>
          
          <div class="item-meta">
            <span><el-icon><Location /></el-icon> {{ item.location }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(item.item_time) }}</span>
            <span><el-icon><View /></el-icon> {{ item.view_count }} 浏览</span>
            <span><el-icon><Star /></el-icon> {{ item.collect_count }} 收藏</span>
          </div>
          
          <div class="item-actions">
            <el-button type="primary" link @click="goDetail(item.id)">
              <el-icon><View /></el-icon> 查看详情
            </el-button>
            <el-button type="success" link v-if="item.status === 'active'" @click="handleMarkResolved(item)">
              <el-icon><CircleCheck /></el-icon> 标记完成
            </el-button>
            <el-button type="info" link v-if="item.status === 'rejected'" :title="item.admin_note">
              <el-icon><InfoFilled /></el-icon> 查看驳回原因
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.page_size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchItems"
        @current-change="fetchItems"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { itemApi } from '@/api/item'

const router = useRouter()

const loading = ref(false)
const items = ref([])
const total = ref(0)
const statusFilter = ref('')

const pagination = reactive({
  page: 1,
  page_size: 10
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    active: 'success',
    resolved: 'info',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    active: '已发布',
    resolved: '已完成',
    rejected: '已驳回'
  }
  return textMap[status] || '未知'
}

const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.page_size
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    const res = await itemApi.getMyItems(params)
    if (res.code === 200) {
      items.value = res.data.list || res.data
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取我的发布失败:', error)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  router.push(`/item/${id}`)
}

const handleMarkResolved = async (item) => {
  try {
    await ElMessageBox.confirm('确定要标记此物品为已完成吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await itemApi.markResolved(item.id)
    if (res.code === 200) {
      ElMessage.success('已标记为完成')
      fetchItems()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
    }
  }
}

onMounted(() => {
  fetchItems()
})
</script>

<style lang="scss" scoped>
.my-items-page {
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
  
  .item-card {
    display: flex;
    gap: 20px;
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background: #fafafa;
    }
    
    .item-image {
      position: relative;
      width: 140px;
      height: 100px;
      flex-shrink: 0;
      border-radius: 8px;
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
      
      .el-tag {
        position: absolute;
        top: 8px;
        left: 8px;
      }
    }
    
    .item-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .item-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        
        .item-title {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
          margin: 0;
          cursor: pointer;
          
          &:hover {
            color: #409eff;
          }
        }
      }
      
      .item-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        margin-bottom: auto;
        font-size: 13px;
        color: #909399;
        
        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
      
      .item-actions {
        margin-top: 12px;
        
        .el-button {
          font-size: 13px;
        }
      }
    }
  }
  
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
  }
}
</style>
