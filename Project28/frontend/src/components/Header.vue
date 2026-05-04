<template>
  <el-header class="site-header">
    <div class="container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="28"><Search /></el-icon>
          <span>校园失物招领</span>
        </router-link>
        <div class="nav-links">
          <router-link to="/" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link 
            v-if="userStore.isLoggedIn" 
            to="/publish" 
            :class="{ active: $route.path === '/publish' }"
          >发布物品</router-link>
        </div>
      </div>
      <div class="header-right">
        <div class="search-box" v-if="$route.path === '/'">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索物品..."
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <template v-if="userStore.isLoggedIn">
          <router-link to="/profile" class="user-info">
            <el-avatar :size="32" class="avatar">
              <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" />
              <el-icon v-else><User /></el-icon>
            </el-avatar>
            <span class="username">{{ userStore.userInfo?.username }}</span>
          </router-link>
          
          <el-dropdown @command="handleCommand">
            <el-icon class="more-icon"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        
        <template v-else>
          <router-link to="/login" class="link">登录</router-link>
          <router-link to="/register" class="link register-btn">注册</router-link>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/')
      }).catch(() => {})
      break
  }
}

const handleSearch = () => {
  if (searchKeyword.value) {
    router.push({ path: '/', query: { keyword: searchKeyword.value } })
  }
}
</script>

<style lang="scss" scoped>
.site-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 60px;
  padding: 0;
  
  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 40px;
    
    .logo {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      
      &:hover {
        opacity: 0.9;
      }
    }
    
    .nav-links {
      display: flex;
      gap: 30px;
      
      a {
        color: rgba(255, 255, 255, 0.85);
        font-size: 15px;
        transition: all 0.3s;
        
        &:hover, &.active {
          color: #fff;
          font-weight: 500;
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .search-box {
      width: 280px;
      
      :deep(.el-input__wrapper) {
        background: rgba(255, 255, 255, 0.15);
        box-shadow: none;
        border-radius: 20px;
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
        
        &.is-focus {
          background: #fff;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
      
      :deep(.el-input__inner) {
        color: #fff;
        
        &::placeholder {
          color: rgba(255, 255, 255, 0.7);
        }
      }
      
      :deep(.is-focus) {
        .el-input__inner {
          color: #303133;
          
          &::placeholder {
            color: #909399;
          }
        }
      }
    }
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      
      .avatar {
        background: rgba(255, 255, 255, 0.2);
      }
      
      .username {
        font-size: 14px;
      }
      
      &:hover {
        opacity: 0.9;
      }
    }
    
    .more-icon {
      color: #fff;
      font-size: 18px;
      cursor: pointer;
    }
    
    .link {
      color: rgba(255, 255, 255, 0.9);
      font-size: 14px;
      
      &:hover {
        color: #fff;
      }
      
      &.register-btn {
        background: #fff;
        color: #667eea;
        padding: 6px 18px;
        border-radius: 20px;
        font-weight: 500;
        
        &:hover {
          opacity: 0.95;
        }
      }
    }
  }
}
</style>
