<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span class="trend up">
              <el-icon><TrendCharts /></el-icon>
              +12%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.roleCount }}</div>
              <div class="stat-label">角色总数</div>
            </div>
            <div class="stat-icon role-icon">
              <el-icon><Avatar /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span class="trend up">
              <el-icon><TrendCharts /></el-icon>
              +8%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.permissionCount }}</div>
              <div class="stat-label">权限总数</div>
            </div>
            <div class="stat-icon perm-icon">
              <el-icon><Lock /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span class="trend up">
              <el-icon><TrendCharts /></el-icon>
              +5%
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ stats.onlineCount }}</div>
              <div class="stat-label">在线用户</div>
            </div>
            <div class="stat-icon online-icon">
              <el-icon><Connection /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span>较昨日</span>
            <span class="trend down">
              <el-icon><TrendCharts /></el-icon>
              -3%
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统名称"
              >后台管理系统</el-descriptions-item
            >
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="前端框架"
              >Vue 3 + Vite</el-descriptions-item
            >
            <el-descriptions-item label="UI框架"
              >Element Plus</el-descriptions-item
            >
            <el-descriptions-item label="后端框架">NestJS</el-descriptions-item>
            <el-descriptions-item label="数据库"
              >MySQL 8.0</el-descriptions-item
            >
            <el-descriptions-item label="开发语言"
              >TypeScript</el-descriptions-item
            >
            <el-descriptions-item label="运行环境"
              >Node.js</el-descriptions-item
            >
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/system/role')">
              <el-icon><Avatar /></el-icon>
              角色管理
            </el-button>
            <el-button
              type="warning"
              @click="$router.push('/system/permission')"
            >
              <el-icon><Lock /></el-icon>
              权限管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";

const stats = reactive({
  userCount: 128,
  roleCount: 12,
  permissionCount: 64,
  onlineCount: 35,
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  .mt-20 {
    margin-top: 20px;
  }

  .stat-card {
    height: 150px;

    .stat-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .stat-info {
        .stat-value {
          font-size: 32px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 30px;
        color: #fff;

        &.user-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.role-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.perm-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.online-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }
    }

    .stat-footer {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      color: #909399;
      font-size: 12px;

      .trend {
        display: flex;
        align-items: center;
        gap: 3px;

        &.up {
          color: #67c23a;
        }

        &.down {
          color: #f56c6c;
        }
      }
    }
  }

  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .el-button {
      height: 50px;
      font-size: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
  }
}
</style>
