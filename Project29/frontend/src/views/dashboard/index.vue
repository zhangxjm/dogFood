<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409EFF;">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.employeeCount }}</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67C23A;">
              <el-icon size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayAttendance }}</div>
              <div class="stat-label">今日打卡</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #E6A23C;">
              <el-icon size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingLeave }}</div>
              <div class="stat-label">待审批请假</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #F56C6C;">
              <el-icon size="32"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOvertime }}</div>
              <div class="stat-label">待审批加班</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="action-card" @click="goToAttendance">
                <el-icon size="40" color="#409EFF"><Clock /></el-icon>
                <span>考勤打卡</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="action-card" @click="goToLeave">
                <el-icon size="40" color="#67C23A"><Document /></el-icon>
                <span>请假申请</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="action-card" @click="goToOvertime">
                <el-icon size="40" color="#E6A23C"><Timer /></el-icon>
                <span>加班申请</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>系统提示</span>
          </template>
          <div class="tips-content">
            <el-alert
              title="上班时间：09:00"
              type="info"
              :closable="false"
              show-icon
              style="margin-bottom: 10px;"
            />
            <el-alert
              title="下班时间：18:00"
              type="info"
              :closable="false"
              show-icon
              style="margin-bottom: 10px;"
            />
            <el-alert
              title="默认管理员账号：admin / admin123"
              type="warning"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const stats = reactive({
  employeeCount: 0,
  todayAttendance: 0,
  pendingLeave: 0,
  pendingOvertime: 0
})

const fetchData = async () => {
  stats.employeeCount = 3
  stats.todayAttendance = 2
  stats.pendingLeave = 1
  stats.pendingOvertime = 1
}

const goToAttendance = () => {
  router.push('/attendance')
}

const goToLeave = () => {
  router.push('/leave')
}

const goToOvertime = () => {
  router.push('/overtime')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.action-card:hover {
  background-color: #f5f7fa;
}

.action-card span {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.tips-content {
  padding: 10px 0;
}
</style>
