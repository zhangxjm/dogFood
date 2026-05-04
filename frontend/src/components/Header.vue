<template>
  <el-header class="header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <el-icon size="28"><Document /></el-icon>
        <span class="logo-text">个人博客</span>
      </div>
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        class="nav-menu"
        router
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/articles">文章</el-menu-item>
        <el-menu-item index="/archive">归档</el-menu-item>
      </el-menu>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章..."
          style="width: 200px"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button v-if="!userStore.isLoggedIn" type="primary" link @click="$router.push('/login')">
          登录
        </el-button>
        <template v-else>
          <el-button type="primary" link @click="$router.push('/admin')">
            后台管理
          </el-button>
          <el-button type="danger" link @click="handleLogout">
            退出
          </el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')

const activeMenu = computed(() => {
  return route.path
})

const handleSearch = () => {
  if (route.path !== '/articles') {
    router.push('/articles')
  }
  router.push({ path: '/articles', query: { keyword: searchKeyword.value } })
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/')
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px !important;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #409eff;
}

.logo-text {
  margin-left: 10px;
  font-size: 20px;
  font-weight: bold;
}

.nav-menu {
  border-bottom: none;
}

.nav-menu .el-menu-item {
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }

  .nav-menu {
    display: none;
  }

  .header-right .el-input {
    width: 150px !important;
  }
}
</style>
