<template>
  <div class="profile-page">
    <Header />
    
    <div class="main-content">
      <div class="container">
        <div class="profile-wrapper">
          <aside class="sidebar">
            <div class="user-card">
              <el-avatar :size="64" class="avatar">
                <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" />
                <el-icon v-else :size="32"><User /></el-icon>
              </el-avatar>
              <div class="user-info">
                <h3 class="username">{{ userStore.userInfo?.username }}</h3>
                <p class="role">{{ getRoleText(userStore.userInfo?.role) }}</p>
              </div>
            </div>
            
            <el-menu
              :default-active="activeMenu"
              router
              background-color="#fff"
              text-color="#606266"
              active-text-color="#667eea"
            >
              <el-menu-item index="/profile/my-items">
                <el-icon><Document /></el-icon>
                <span>我的发布</span>
              </el-menu-item>
              <el-menu-item index="/profile/my-collections">
                <el-icon><Star /></el-icon>
                <span>我的收藏</span>
              </el-menu-item>
              <el-menu-item index="/profile/my-claims">
                <el-icon><TakeawayBox /></el-icon>
                <span>认领记录</span>
              </el-menu-item>
              <el-menu-item index="/profile/received-claims">
                <el-icon><Bell /></el-icon>
                <span>收到的认领</span>
              </el-menu-item>
              <el-menu-item index="/profile/settings">
                <el-icon><Setting /></el-icon>
                <span>个人设置</span>
              </el-menu-item>
            </el-menu>
          </aside>
          
          <main class="content">
            <router-view />
          </main>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Header from '@/components/Header.vue'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const getRoleText = (role) => {
  const roleMap = {
    student: '学生',
    teacher: '教师',
    admin: '管理员'
  }
  return roleMap[role] || '用户'
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-content {
  flex: 1;
  padding: 30px 0;
}

.profile-wrapper {
  display: flex;
  gap: 30px;
}

.sidebar {
  width: 260px;
  flex-shrink: 0;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .avatar {
    margin-bottom: 16px;
    background: #f5f7fa;
  }
  
  .user-info {
    .username {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 6px;
    }
    
    .role {
      font-size: 13px;
      color: #909399;
      margin: 0;
    }
  }
}

:deep(.el-menu) {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  
  .el-menu-item {
    height: 50px;
    line-height: 50px;
    margin: 0;
    border-bottom: 1px solid #ebeef5;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background: #f5f7fa;
    }
    
    &.is-active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      
      &::before {
        display: none;
      }
    }
  }
}

.content {
  flex: 1;
  min-height: 600px;
}
</style>
