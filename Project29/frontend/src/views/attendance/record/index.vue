<template>
  <div class="attendance-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="clock-card">
          <div class="clock-header">
            <div class="current-time">{{ currentTime }}</div>
            <div class="current-date">{{ currentDate }}</div>
          </div>
          
          <div class="clock-status">
            <div class="status-item">
              <div class="status-label">上班打卡</div>
              <div class="status-value" :class="clockInStatus ? 'success' : 'warning'">
                {{ todayRecord?.clockInTime ? formatTime(todayRecord.clockInTime) : '--:--:--' }}
              </div>
              <div class="status-tag" v-if="todayRecord?.isLate === 1">
                <el-tag type="danger">迟到</el-tag>
              </div>
              <div class="status-tag" v-else-if="todayRecord?.clockInTime">
                <el-tag type="success">正常</el-tag>
              </div>
            </div>
            
            <div class="status-item">
              <div class="status-label">下班打卡</div>
              <div class="status-value" :class="clockOutStatus ? 'success' : 'warning'">
                {{ todayRecord?.clockOutTime ? formatTime(todayRecord.clockOutTime) : '--:--:--' }}
              </div>
              <div class="status-tag" v-if="todayRecord?.isEarly === 1">
                <el-tag type="warning">早退</el-tag>
              </div>
              <div class="status-tag" v-else-if="todayRecord?.clockOutTime">
                <el-tag type="success">正常</el-tag>
              </div>
            </div>
          </div>
          
          <div class="clock-actions">
            <el-button 
              type="primary" 
              size="large" 
              :disabled="todayRecord?.clockInTime"
              :loading="clockInLoading"
              @click="handleClockIn"
            >
              <el-icon><Sunny /></el-icon>
              上班打卡
            </el-button>
            <el-button 
              type="success" 
              size="large" 
              :disabled="!todayRecord?.clockInTime || todayRecord?.clockOutTime"
              :loading="clockOutLoading"
              @click="handleClockOut"
            >
              <el-icon><Moon /></el-icon>
              下班打卡
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>考勤记录</span>
              <el-button type="success" size="small" @click="handleExport">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
            </div>
          </template>
          
          <el-form :inline="true" :model="queryForm" class="search-form">
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="queryForm.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="queryForm.endDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </el-form-item>
          </el-form>
          
          <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe>
            <el-table-column prop="recordDate" label="日期" width="120" />
            <el-table-column prop="clockInTime" label="上班打卡" width="180">
              <template #default="scope">
                <span v-if="scope.row.clockInTime">{{ formatDateTime(scope.row.clockInTime) }}</span>
                <span v-else class="text-danger">未打卡</span>
              </template>
            </el-table-column>
            <el-table-column prop="clockOutTime" label="下班打卡" width="180">
              <template #default="scope">
                <span v-if="scope.row.clockOutTime">{{ formatDateTime(scope.row.clockOutTime) }}</span>
                <span v-else class="text-danger">未打卡</span>
              </template>
            </el-table-column>
            <el-table-column prop="workHours" label="工时(小时)" width="100" align="center" />
            <el-table-column label="状态" width="200" align="center">
              <template #default="scope">
                <div class="status-badges">
                  <el-tag v-if="scope.row.isLate === 1" type="danger" size="small" style="margin-right: 5px;">
                    迟到
                  </el-tag>
                  <el-tag v-if="scope.row.isEarly === 1" type="warning" size="small" style="margin-right: 5px;">
                    早退
                  </el-tag>
                  <el-tag v-if="scope.row.isAbsent === 1" type="danger" size="small">
                    旷工
                  </el-tag>
                  <el-tag v-else type="success" size="small">
                    正常
                  </el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; justify-content: flex-end;"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getAttendancePage, getTodayRecord, clockIn, clockOut, exportAttendance } from '@/api/attendance'
import dayjs from 'dayjs'

const userStore = useUserStore()

const loading = ref(false)
const clockInLoading = ref(false)
const clockOutLoading = ref(false)
const todayRecord = ref(null)
const tableData = ref([])

const currentTime = ref('')
const currentDate = ref('')
let timer = null

const queryForm = reactive({
  startDate: '',
  endDate: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const clockInStatus = computed(() => !!todayRecord.value?.clockInTime)
const clockOutStatus = computed(() => !!todayRecord.value?.clockOutTime)

const updateTime = () => {
  const now = dayjs()
  currentTime.value = now.format('HH:mm:ss')
  currentDate.value = now.format('YYYY年MM月DD日 dddd')
}

const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('HH:mm:ss')
}

const formatDateTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const fetchTodayRecord = async () => {
  try {
    const empId = userStore.userInfo.userId || 1
    const res = await getTodayRecord(empId)
    if (res.code === 200) {
      todayRecord.value = res.data
    }
  } catch (error) {
    console.error('获取今日打卡记录失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    }
    const res = await getAttendancePage(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取考勤记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleClockIn = async () => {
  clockInLoading.value = true
  try {
    const empId = userStore.userInfo.userId || 1
    const res = await clockIn({ empId })
    if (res.code === 200) {
      ElMessage.success('上班打卡成功')
      fetchTodayRecord()
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error) {
    console.error('上班打卡失败:', error)
  } finally {
    clockInLoading.value = false
  }
}

const handleClockOut = async () => {
  clockOutLoading.value = true
  try {
    const empId = userStore.userInfo.userId || 1
    const res = await clockOut({ empId })
    if (res.code === 200) {
      ElMessage.success('下班打卡成功')
      fetchTodayRecord()
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error) {
    console.error('下班打卡失败:', error)
  } finally {
    clockOutLoading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  fetchData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

const handleExport = () => {
  exportAttendance(queryForm)
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  fetchTodayRecord()
  fetchData()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.clock-card {
  text-align: center;
}

.clock-header {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 30px;
}

.current-time {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
}

.current-date {
  font-size: 16px;
  color: #909399;
  margin-top: 10px;
}

.clock-status {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
}

.status-item {
  text-align: center;
}

.status-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
}

.status-value.success {
  color: #67C23A;
}

.status-value.warning {
  color: #E6A23C;
}

.status-tag {
  margin-top: 10px;
}

.clock-actions {
  display: flex;
  justify-content: space-around;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.clock-actions .el-button {
  width: 140px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.text-danger {
  color: #F56C6C;
}

.status-badges {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}
</style>
