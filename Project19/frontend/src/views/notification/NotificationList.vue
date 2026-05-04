<template>
  <div class="notification-page">
    <el-card class="header-card">
      <div class="header-content">
        <h2>消息中心</h2>
        <div class="header-actions">
          <el-tag :type="unreadCount > 0 ? 'danger' : 'info'" effect="light">
            未读: {{ unreadCount }} 条
          </el-tag>
          <el-button 
            v-if="unreadCount > 0" 
            type="primary" 
            link 
            @click="handleMarkAllRead"
          >
            全部标为已读
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="filter-card">
      <el-radio-group v-model="filterType" @change="handleFilterChange">
        <el-radio-button value="all">全部消息</el-radio-button>
        <el-radio-button value="unread">未读消息</el-radio-button>
        <el-radio-button value="club">社团通知</el-radio-button>
        <el-radio-button value="activity">活动通知</el-radio-button>
        <el-radio-button value="system">系统通知</el-radio-button>
      </el-radio-group>
    </el-card>

    <el-card class="list-card">
      <el-empty v-if="notifications.length === 0 && !loading" description="暂无消息" />
      
      <div v-else class="notification-list">
        <div 
          v-for="item in notifications" 
          :key="item.id" 
          class="notification-item"
          :class="{ 'unread': item.isRead === 0 }"
          @click="handleItemClick(item)"
        >
          <div class="item-left">
            <el-avatar :size="40" :style="{ backgroundColor: getTypeColor(item.type) }">
              <el-icon :size="20">{{ getTypeIcon(item.type) }}</el-icon>
            </el-avatar>
          </div>
          <div class="item-middle">
            <div class="item-title">
              <span class="title-text" :class="{ 'bold': item.isRead === 0 }">{{ item.title }}</span>
              <span v-if="item.isRead === 0" class="unread-dot"></span>
            </div>
            <div class="item-content">{{ item.content || '暂无内容' }}</div>
            <div class="item-meta">
              <span v-if="item.clubName" class="club-name">
                <el-icon><OfficeBuilding /></el-icon>
                {{ item.clubName }}
              </span>
              <span v-if="item.senderName" class="sender">
                <el-icon><User /></el-icon>
                {{ item.senderName }}
              </span>
              <span class="time">
                <el-icon><Clock /></el-icon>
                {{ formatTime(item.createTime) }}
              </span>
            </div>
          </div>
          <div class="item-right">
            <el-dropdown @command="(command) => handleDropdownCommand(command, item)" trigger="click">
              <el-button type="primary" link>
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="item.isRead === 0" command="markRead">
                    标为已读
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>

      <el-pagination
        v-if="total > 0"
        class="pagination"
        background
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Bell, 
  OfficeBuilding, 
  Calendar, 
  Message, 
  User, 
  Clock, 
  MoreFilled 
} from '@element-plus/icons-vue'
import { getNotificationPage, markAsRead, markAllAsRead, getUnreadCount } from '@/api/notification'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const notifications = ref([])
const unreadCount = ref(0)
const loading = ref(false)
const filterType = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getTypeColor = (type) => {
  const colors = {
    1: '#409EFF',
    2: '#67C23A',
    3: '#E6A23C',
    4: '#909399'
  }
  return colors[type] || '#909399'
}

const getTypeIcon = (type) => {
  const icons = {
    1: Message,
    2: OfficeBuilding,
    3: Calendar,
    4: Bell
  }
  return icons[type] || Bell
}

const getTypeText = (type) => {
  const texts = {
    1: '系统通知',
    2: '社团通知',
    3: '活动通知',
    4: '审核通知'
  }
  return texts[type] || '其他通知'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchNotifications = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    if (filterType.value === 'unread') {
      params.unreadOnly = true
    } else if (filterType.value === 'club') {
      params.type = 2
    } else if (filterType.value === 'activity') {
      params.type = 3
    } else if (filterType.value === 'system') {
      params.type = 1
    }
    
    const res = await getNotificationPage(params)
    notifications.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取消息列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data?.count || 0
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchNotifications()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotifications()
}

const handleItemClick = async (item) => {
  if (item.isRead === 0) {
    try {
      await markAsRead(item.id)
      item.isRead = 1
      fetchUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  
  if (item.relatedType === 'club' && item.relatedId) {
    router.push(`/club/${item.relatedId}`)
  } else if (item.relatedType === 'activity' && item.relatedId) {
    router.push(`/activity/${item.relatedId}`)
  }
}

const handleDropdownCommand = async (command, item) => {
  if (command === 'markRead') {
    try {
      await markAsRead(item.id)
      item.isRead = 1
      fetchUnreadCount()
      ElMessage.success('已标记为已读')
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有消息标为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await markAllAsRead()
    ElMessage.success('已全部标为已读')
    fetchNotifications()
    fetchUnreadCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchNotifications()
    fetchUnreadCount()
  }
})
</script>

<style scoped lang="scss">
.notification-page {
  max-width: 900px;
  margin: 0 auto;
}

.header-card {
  margin-bottom: 20px;
  
  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h2 {
      margin: 0;
      font-size: 20px;
      color: #303133;
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }
}

.filter-card {
  margin-bottom: 20px;
}

.list-card {
  .notification-list {
    .notification-item {
      display: flex;
      padding: 16px;
      border-bottom: 1px solid #EBEEF5;
      cursor: pointer;
      transition: background-color 0.2s;
      
      &:hover {
        background-color: #F5F7FA;
      }
      
      &:last-child {
        border-bottom: none;
      }
      
      &.unread {
        background-color: rgba(64, 158, 255, 0.05);
      }
      
      .item-left {
        margin-right: 16px;
      }
      
      .item-middle {
        flex: 1;
        min-width: 0;
        
        .item-title {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          
          .title-text {
            font-size: 15px;
            color: #303133;
            
            &.bold {
              font-weight: 600;
            }
          }
          
          .unread-dot {
            width: 6px;
            height: 6px;
            background-color: #F56C6C;
            border-radius: 50%;
            margin-left: 8px;
          }
        }
        
        .item-content {
          font-size: 13px;
          color: #909399;
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .item-meta {
          display: flex;
          align-items: center;
          font-size: 12px;
          color: #C0C4CC;
          gap: 16px;
          
          .club-name,
          .sender,
          .time {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
      
      .item-right {
        margin-left: 16px;
      }
    }
  }
  
  .pagination {
    margin-top: 20px;
    justify-content: center;
  }
}
</style>
