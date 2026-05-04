<template>
  <div class="announcement-detail-page">
    <el-card v-loading="loading" class="detail-card">
      <template #header>
        <div class="card-header">
          <div class="back-btn">
            <el-button type="primary" link @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
          </div>
          <div class="title-actions">
            <el-button 
              v-if="canEdit" 
              type="primary" 
              link
              @click="goEdit"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="announcement" class="content-wrapper">
        <div class="announcement-header">
          <div class="title-section">
            <el-tag v-if="announcement.isTop === 1" type="danger" effect="dark">置顶</el-tag>
            <el-tag 
              :type="announcement.type === 1 ? 'primary' : 'warning'"
            >
              {{ announcement.type === 1 ? '社团通知' : '活动公告' }}
            </el-tag>
            <h1 class="title">{{ announcement.title }}</h1>
          </div>
          <div class="meta-section">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>发布者: {{ announcement.publisherName || '未知' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>发布时间: {{ formatTime(announcement.createTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>阅读: {{ announcement.readCount || 0 }} 次</span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="announcement-content">
          <div v-if="announcement.content" class="content-text">
            {{ announcement.content }}
          </div>
          <el-empty v-else description="暂无内容" />
        </div>
      </div>

      <el-empty v-else description="公告不存在或已被删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Clock, View, Edit } from '@element-plus/icons-vue'
import { getAnnouncementDetail } from '@/api/announcement'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const announcement = ref(null)
const loading = ref(false)

const canEdit = computed(() => {
  return userStore.isPresident || userStore.isAdmin
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/club')
  }
}

const goEdit = () => {
  router.push(`/club/${route.params.clubId}/announcements`)
}

const fetchAnnouncement = async () => {
  if (!route.params.id) {
    ElMessage.error('公告ID不存在')
    return
  }

  loading.value = true
  try {
    const res = await getAnnouncementDetail(route.params.id)
    announcement.value = res.data
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchAnnouncement()
  }
})
</script>

<style scoped lang="scss">
.announcement-detail-page {
  max-width: 900px;
  margin: 0 auto;
  
  .detail-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .content-wrapper {
      padding: 10px;
      
      .announcement-header {
        .title-section {
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          gap: 12px;
          margin-bottom: 20px;
          
          .title {
            margin: 0;
            font-size: 22px;
            color: #303133;
            font-weight: 600;
          }
        }
        
        .meta-section {
          display: flex;
          flex-wrap: wrap;
          gap: 24px;
          
          .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #909399;
          }
        }
      }
      
      .announcement-content {
        padding: 20px 0;
        
        .content-text {
          font-size: 16px;
          line-height: 2;
          color: #303133;
          white-space: pre-wrap;
          word-break: break-word;
        }
      }
    }
  }
}
</style>
