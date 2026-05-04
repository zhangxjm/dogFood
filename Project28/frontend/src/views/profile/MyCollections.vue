<template>
  <div class="my-collections-page">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>
    
    <div class="items-grid" v-loading="loading">
      <div v-if="!loading && items.length === 0" class="empty-state">
        <el-empty description="暂无收藏物品" />
      </div>
      
      <div v-else class="item-card" v-for="item in items" :key="item.id">
        <div class="item-image">
          <img v-if="item.item?.image1" :src="item.item.image1" :alt="item.item.title" />
          <div v-else class="no-image">
            <el-icon :size="32"><Picture /></el-icon>
          </div>
          <el-tag :type="getItemType(item.item?.item_type)" size="small">
            {{ getItemTypeText(item.item?.item_type) }}
          </el-tag>
        </div>
        
        <div class="item-content">
          <h3 class="item-title" @click="goDetail(item.item?.id)">{{ item.item?.title }}</h3>
          <div class="item-meta">
            <span><el-icon><Location /></el-icon> {{ item.item?.location }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(item.item?.item_time) }}</span>
          </div>
          <div class="item-actions">
            <el-button type="primary" link @click="goDetail(item.item?.id)">
              <el-icon><View /></el-icon> 查看详情
            </el-button>
            <el-button type="danger" link @click="handleRemoveCollection(item)">
              <el-icon><Delete /></el-icon> 取消收藏
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { collectionApi } from '@/api/collection'

const router = useRouter()

const loading = ref(false)
const items = ref([])

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getItemType = (type) => {
  return type === 'lost' ? 'danger' : 'success'
}

const getItemTypeText = (type) => {
  return type === 'lost' ? '寻物启事' : '招领启事'
}

const fetchCollections = async () => {
  loading.value = true
  try {
    const res = await collectionApi.getList()
    if (res.code === 200) {
      items.value = res.data
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  if (id) {
    router.push(`/item/${id}`)
  }
}

const handleRemoveCollection = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏此物品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await collectionApi.delete(item.id)
    if (res.code === 200) {
      ElMessage.success('已取消收藏')
      fetchCollections()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
  }
}

onMounted(() => {
  fetchCollections()
})
</script>

<style lang="scss" scoped>
.my-collections-page {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .page-header {
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
      width: 120px;
      height: 90px;
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
        top: 6px;
        left: 6px;
      }
    }
    
    .item-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      
      .item-title {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin: 0 0 12px;
        cursor: pointer;
        
        &:hover {
          color: #409eff;
        }
      }
      
      .item-meta {
        display: flex;
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
}
</style>
