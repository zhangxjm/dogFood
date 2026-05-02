<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-header">
            <span class="card-title">用户总数</span>
            <el-icon class="card-icon"><User /></el-icon>
          </div>
          <div class="card-content">
            <span class="card-value">{{ stats.userCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-header">
            <span class="card-title">部门总数</span>
            <el-icon class="card-icon"><OfficeBuilding /></el-icon>
          </div>
          <div class="card-content">
            <span class="card-value">{{ stats.deptCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-header">
            <span class="card-title">菜单总数</span>
            <el-icon class="card-icon"><Menu /></el-icon>
          </div>
          <div class="card-content">
            <span class="card-value">{{ stats.menuCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="card-header">
            <span class="card-title">今日访问</span>
            <el-icon class="card-icon"><View /></el-icon>
          </div>
          <div class="card-content">
            <span class="card-value">{{ stats.visitCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>欢迎使用</span>
          </template>
          <div class="welcome-content">
            <h2>后台管理系统</h2>
            <p>技术栈：SpringBoot3 + Vue3 + MyBatis-Plus + Element Plus</p>
            <el-divider />
            <div class="feature-list">
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>登录认证、验证码、会话保持</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>用户管理、部门管理、菜单权限</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>基础CRUD、分页、条件查询</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>后端全局异常处理、跨域、统一返回结果</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-button type="primary" class="action-btn" @click="goToUser">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="success" class="action-btn" @click="goToDept">
                <el-icon><OfficeBuilding /></el-icon>
                <span>部门管理</span>
              </el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="warning" class="action-btn mt-15" @click="goToMenu">
                <el-icon><Menu /></el-icon>
                <span>菜单管理</span>
              </el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="danger" class="action-btn mt-15" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  User, OfficeBuilding, Menu, View, Check, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  userCount: 0,
  deptCount: 0,
  menuCount: 0,
  visitCount: 0
})

const goToUser = () => {
  router.push('/system/user')
}

const goToDept = () => {
  router.push('/system/dept')
}

const goToMenu = () => {
  router.push('/system/menu')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout().then(() => {
      ElMessage.success('退出成功')
      router.push('/login')
    })
  }).catch(() => {})
}
</script>

<style scoped lang="scss">
.dashboard-container {
  .box-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      
      .card-title {
        font-size: 14px;
        color: #909399;
      }
      
      .card-icon {
        font-size: 24px;
        color: #409EFF;
      }
    }
    
    .card-content {
      .card-value {
        font-size: 32px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
  
  .welcome-content {
    h2 {
      margin-bottom: 10px;
      color: #303133;
    }
    
    p {
      color: #909399;
    }
    
    .feature-list {
      .feature-item {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        color: #606266;
        
        .el-icon {
          margin-right: 10px;
          color: #67C23A;
        }
      }
    }
  }
  
  .action-btn {
    width: 100%;
    height: 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 24px;
      margin-bottom: 5px;
    }
  }
}
</style>
