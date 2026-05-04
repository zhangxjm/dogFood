<template>
  <div class="statistics-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考勤统计</span>
          <div class="header-actions">
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="员工姓名">
          <el-input v-model="queryForm.empName" placeholder="请输入员工姓名" clearable />
        </el-form-item>
        <el-form-item label="年份">
          <el-select v-model="queryForm.statisticsYear" placeholder="请选择年份" clearable style="width: 120px;">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="queryForm.statisticsMonth" placeholder="请选择月份" clearable style="width: 100px;">
            <el-option
              v-for="month in 12"
              :key="month"
              :label="month + '月'"
              :value="month"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="empName" label="员工姓名" width="120" />
        <el-table-column prop="statisticsYear" label="年份" width="80" align="center" />
        <el-table-column prop="statisticsMonth" label="月份" width="80" align="center">
          <template #default="scope">
            {{ scope.row.statisticsMonth }}月
          </template>
        </el-table-column>
        <el-table-column prop="workDays" label="应出勤天数" width="110" align="center" />
        <el-table-column prop="actualDays" label="实际出勤天数" width="110" align="center" />
        <el-table-column prop="lateDays" label="迟到天数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.lateDays > 0 ? 'warning' : 'info'" size="small">
              {{ scope.row.lateDays }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="earlyDays" label="早退天数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.earlyDays > 0 ? 'warning' : 'info'" size="small">
              {{ scope.row.earlyDays }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="absentDays" label="旷工天数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.absentDays > 0 ? 'danger' : 'info'" size="small">
              {{ scope.row.absentDays }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leaveDays" label="请假天数" width="100" align="center" />
        <el-table-column prop="overtimeHours" label="加班时长" width="100" align="center">
          <template #default="scope">
            {{ scope.row.overtimeHours }}小时
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleView(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      title="考勤统计详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="员工姓名">{{ form.empName }}</el-descriptions-item>
        <el-descriptions-item label="统计年月">{{ form.statisticsYear }}年{{ form.statisticsMonth }}月</el-descriptions-item>
        <el-descriptions-item label="应出勤天数">{{ form.workDays }}天</el-descriptions-item>
        <el-descriptions-item label="实际出勤天数">{{ form.actualDays }}天</el-descriptions-item>
        <el-descriptions-item label="迟到天数">
          <el-tag :type="form.lateDays > 0 ? 'warning' : 'info'">
            {{ form.lateDays }}天
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="早退天数">
          <el-tag :type="form.earlyDays > 0 ? 'warning' : 'info'">
            {{ form.earlyDays }}天
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="旷工天数">
          <el-tag :type="form.absentDays > 0 ? 'danger' : 'info'">
            {{ form.absentDays }}天
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ form.leaveDays }}天</el-descriptions-item>
        <el-descriptions-item label="加班时长">{{ form.overtimeHours }}小时</el-descriptions-item>
        <el-descriptions-item label="统计时间">{{ form.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">统计说明</el-divider>
      <el-alert type="info" :closable="false">
        <template #title>
          考勤统计说明
        </template>
        <ul>
          <li>迟到：上班打卡时间超过规定上班时间 30 分钟以上</li>
          <li>早退：下班打卡时间早于规定下班时间 30 分钟以上</li>
          <li>旷工：未打卡且无请假记录</li>
          <li>加班时长：审批通过的加班申请时长</li>
        </ul>
      </el-alert>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getStatisticsPage, exportStatistics } from '@/api/statistics'

const loading = ref(false)
const dialogVisible = ref(false)
const currentYear = new Date().getFullYear()

const yearOptions = computed(() => {
  const years = []
  for (let i = currentYear; i >= currentYear - 5; i--) {
    years.push(i)
  }
  return years
})

const queryForm = reactive({
  empName: '',
  statisticsYear: null,
  statisticsMonth: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const form = reactive({
  empName: '',
  statisticsYear: '',
  statisticsMonth: '',
  workDays: 0,
  actualDays: 0,
  lateDays: 0,
  earlyDays: 0,
  absentDays: 0,
  leaveDays: 0,
  overtimeHours: 0,
  createTime: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStatisticsPage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取考勤统计列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  queryForm.empName = ''
  queryForm.statisticsYear = null
  queryForm.statisticsMonth = null
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.size = size
  fetchData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchData()
}

const handleView = (row) => {
  form.empName = row.empName || '-'
  form.statisticsYear = row.statisticsYear
  form.statisticsMonth = row.statisticsMonth
  form.workDays = row.workDays
  form.actualDays = row.actualDays
  form.lateDays = row.lateDays
  form.earlyDays = row.earlyDays
  form.absentDays = row.absentDays
  form.leaveDays = row.leaveDays
  form.overtimeHours = row.overtimeHours
  form.createTime = row.createTime || '-'
  dialogVisible.value = true
}

const handleExport = () => {
  exportStatistics(queryForm)
  ElMessage.success('导出任务已开始，请稍候...')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-form {
  margin-bottom: 20px;
}
</style>