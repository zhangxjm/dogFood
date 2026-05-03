<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>工业设备监控大屏</h1>
      <div class="header-right">
        <span class="current-time">{{ currentTime }}</span>
        <el-tag :type="wsConnected ? 'success' : 'danger'" size="large">
          {{ wsConnected ? '已连接' : '未连接' }}
        </el-tag>
      </div>
    </div>

    <div class="stats-row">
      <div class="stat-card" :class="{'pulse': stats.faultDevices > 0}">
        <div class="stat-icon" style="background: linear-gradient(135deg, #ef4444, #dc2626)">
          <el-icon :size="28"><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.faultDevices }}</div>
          <div class="stat-label">故障设备</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #3b82f6, #2563eb)">
          <el-icon :size="28"><Cpu /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalDevices }}</div>
          <div class="stat-label">设备总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #10b981, #059669)">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.onlineDevices }}</div>
          <div class="stat-label">在线设备</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706)">
          <el-icon :size="28"><Bell /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.unhandledAlarms }}</div>
          <div class="stat-label">未处理报警</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #8b5cf6, #7c3aed)">
          <el-icon :size="28"><List /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingOrders }}</div>
          <div class="stat-label">待处理工单</div>
        </div>
      </div>
    </div>

    <div class="charts-container">
      <div class="chart-section left-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>设备状态分布</h3>
          </div>
          <div ref="statusChartRef" class="chart-content"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>温度趋势 (最近50条)</h3>
          </div>
          <div ref="tempChartRef" class="chart-content"></div>
        </div>
      </div>

      <div class="chart-section center-section">
        <div class="device-list-card">
          <div class="chart-header">
            <h3>设备实时状态</h3>
            <el-tag v-if="selectedDevice">
              {{ selectedDevice.deviceName }}
            </el-tag>
          </div>
          <div class="device-grid">
            <div
              v-for="device in deviceDataList"
              :key="device.deviceId"
              class="device-item"
              :class="{'active': selectedDevice?.deviceId === device.deviceId, 'fault': device.status === 2, 'offline': device.status === 0}"
              @click="selectDevice(device)"
            >
              <div class="device-status" :class="'status-' + device.status">
                <el-icon v-if="device.status === 1"><Cpu /></el-icon>
                <el-icon v-else-if="device.status === 2"><Warning /></el-icon>
                <el-icon v-else><Close /></el-icon>
              </div>
              <div class="device-info">
                <div class="device-name">{{ device.deviceName }}</div>
                <div class="device-code">{{ device.deviceCode }}</div>
              </div>
              <div class="device-metrics">
                <div class="metric">
                  <span class="metric-label">温度</span>
                  <span class="metric-value" :class="{'warning': device.temperature > 80}">
                    {{ device.temperature }}°C
                  </span>
                </div>
                <div class="metric">
                  <span class="metric-label">电压</span>
                  <span class="metric-value">{{ device.voltage }}V</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="chart-section right-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>电压趋势 (最近50条)</h3>
          </div>
          <div ref="voltageChartRef" class="chart-content"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>实时报警</h3>
          </div>
          <div class="alarm-list">
            <div
              v-for="alarm in alarmList"
              :key="alarm.id"
              class="alarm-item"
              :class="'level-' + alarm.alarmLevel"
            >
              <div class="alarm-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="alarm-content">
                <div class="alarm-message">{{ alarm.alarmMessage }}</div>
                <div class="alarm-time">{{ formatTime(alarm.createTime) }}</div>
              </div>
              <el-tag v-if="alarm.status === 0" type="danger" size="small">未处理</el-tag>
              <el-tag v-else type="success" size="small">已处理</el-tag>
            </div>
            <el-empty v-if="alarmList.length === 0" description="暂无报警" :image-size="60" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dashboardApi, alarmApi } from '../utils/api'
import wsClient from '../utils/websocket'

const currentTime = ref('')
const wsConnected = ref(false)

const stats = reactive({
  totalDevices: 0,
  onlineDevices: 0,
  offlineDevices: 0,
  faultDevices: 0,
  unhandledAlarms: 0,
  pendingOrders: 0,
  processingOrders: 0
})

const deviceDataList = ref([])
const selectedDevice = ref(null)
const alarmList = ref([])

const statusChartRef = ref(null)
const tempChartRef = ref(null)
const voltageChartRef = ref(null)

let statusChart = null
let tempChart = null
let voltageChart = null

const temperatureHistory = reactive({
  times: [],
  values: []
})

const voltageHistory = reactive({
  times: [],
  values: []
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN')
}

const loadStats = async () => {
  try {
    const res = await dashboardApi.stats()
    Object.assign(stats, res.data)
    updateStatusChart()
  } catch (e) {
    console.error('加载统计数据失败:', e)
  }
}

const loadRecentAlarms = async () => {
  try {
    const res = await alarmApi.page({ page: 1, size: 5, status: 0 })
    alarmList.value = res.data.records || []
  } catch (e) {
    console.error('加载报警数据失败:', e)
  }
}

const initStatusChart = () => {
  statusChart = echarts.init(statusChartRef.value)
  updateStatusChart()
}

const updateStatusChart = () => {
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#0f172a', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: [
        { value: stats.onlineDevices, name: '在线', itemStyle: { color: '#10b981' } },
        { value: stats.offlineDevices, name: '离线', itemStyle: { color: '#6b7280' } },
        { value: stats.faultDevices, name: '故障', itemStyle: { color: '#ef4444' } }
      ]
    }]
  }
  statusChart?.setOption(option)
}

const initTempChart = () => {
  tempChart = echarts.init(tempChartRef.value)
  const option = {
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: temperatureHistory.times },
    yAxis: { type: 'value', max: 100, splitLine: { lineStyle: { color: '#333' } } },
    tooltip: { trigger: 'axis' },
    series: [{
      data: temperatureHistory.values,
      type: 'line',
      smooth: true,
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(239, 68, 68, 0.3)' },
        { offset: 1, color: 'rgba(239, 68, 68, 0.05)' }
      ]) },
      lineStyle: { color: '#ef4444', width: 2 },
      itemStyle: { color: '#ef4444' }
    }]
  }
  tempChart.setOption(option)
}

const initVoltageChart = () => {
  voltageChart = echarts.init(voltageChartRef.value)
  const option = {
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: voltageHistory.times },
    yAxis: { type: 'value', min: 150, max: 260, splitLine: { lineStyle: { color: '#333' } } },
    tooltip: { trigger: 'axis' },
    series: [{
      data: voltageHistory.values,
      type: 'line',
      smooth: true,
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
        { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
      ]) },
      lineStyle: { color: '#3b82f6', width: 2 },
      itemStyle: { color: '#3b82f6' }
    }]
  }
  voltageChart.setOption(option)
}

const handleDeviceData = (data) => {
  if (!data || !Array.isArray(data)) return
  
  deviceDataList.value = data
  
  if (data.length > 0) {
    const now = new Date()
    const timeStr = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
    
    const firstDevice = data[0]
    temperatureHistory.times.push(timeStr)
    temperatureHistory.values.push(firstDevice.temperature)
    
    voltageHistory.times.push(timeStr)
    voltageHistory.values.push(firstDevice.voltage)
    
    if (temperatureHistory.times.length > 50) {
      temperatureHistory.times.shift()
      temperatureHistory.values.shift()
      voltageHistory.times.shift()
      voltageHistory.values.shift()
    }
    
    updateCharts()
  }
}

const handleAlarmData = (data) => {
  if (!data) return
  alarmList.value.unshift(data)
  if (alarmList.value.length > 10) {
    alarmList.value.pop()
  }
  stats.unhandledAlarms++
}

const updateCharts = () => {
  if (tempChart) {
    tempChart.setOption({
      xAxis: { data: temperatureHistory.times },
      series: [{ data: temperatureHistory.values }]
    })
  }
  if (voltageChart) {
    voltageChart.setOption({
      xAxis: { data: voltageHistory.times },
      series: [{ data: voltageHistory.values }]
    })
  }
}

const selectDevice = (device) => {
  selectedDevice.value = device
}

const handleResize = () => {
  statusChart?.resize()
  tempChart?.resize()
  voltageChart?.resize()
}

let timer = null
let statsTimer = null

onMounted(() => {
  updateCurrentTime()
  timer = setInterval(updateCurrentTime, 1000)
  
  loadStats()
  loadRecentAlarms()
  
  nextTick(() => {
    initStatusChart()
    initTempChart()
    initVoltageChart()
  })
  
  window.addEventListener('resize', handleResize)
  
  wsClient.connect()
  wsClient.on('DEVICE_DATA', handleDeviceData)
  wsClient.on('ALARM', handleAlarmData)
  wsClient.on('CONNECTION_CHANGE', (isConnected) => {
    wsConnected.value = isConnected
  })
  
  wsConnected.value = wsClient.isConnected
  
  statsTimer = setInterval(loadStats, 10000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (statsTimer) clearInterval(statsTimer)
  window.removeEventListener('resize', handleResize)
  wsClient.off('DEVICE_DATA', handleDeviceData)
  wsClient.off('ALARM', handleAlarmData)
  statusChart?.dispose()
  tempChart?.dispose()
  voltageChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  height: calc(100vh - 100px);
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  padding: 0 20px 20px;
  overflow: auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #334155;
  margin-bottom: 20px;
}

.dashboard-header h1 {
  color: #f1f5f9;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-time {
  color: #94a3b8;
  font-size: 14px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border: 1px solid #334155;
}

.stat-card.pulse {
  animation: pulse-animation 2s infinite;
  border-color: #ef4444;
}

@keyframes pulse-animation {
  0%, 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4); }
  50% { box-shadow: 0 0 0 10px rgba(239, 68, 68, 0); }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
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
  font-weight: 700;
  color: #f1f5f9;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 4px;
}

.charts-container {
  display: grid;
  grid-template-columns: 350px 1fr 350px;
  gap: 15px;
  height: calc(100% - 200px);
  min-height: 500px;
}

.chart-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.center-section {
  display: flex;
  flex-direction: column;
}

.chart-card {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 12px;
  border: 1px solid #334155;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chart-header {
  padding: 15px 20px;
  border-bottom: 1px solid #334155;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header h3 {
  color: #f1f5f9;
  font-size: 15px;
  font-weight: 600;
  margin: 0;
}

.chart-content {
  flex: 1;
  min-height: 200px;
}

.device-list-card {
  background: rgba(30, 41, 59, 0.8);
  border-radius: 12px;
  border: 1px solid #334155;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 15px;
  overflow-y: auto;
  flex: 1;
}

.device-item {
  background: rgba(15, 23, 42, 0.6);
  border-radius: 10px;
  padding: 15px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.device-item:hover {
  background: rgba(15, 23, 42, 0.9);
  border-color: #3b82f6;
}

.device-item.active {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
}

.device-item.fault {
  border-color: #ef4444;
}

.device-item.offline {
  opacity: 0.5;
}

.device-status {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  margin-bottom: 10px;
}

.device-status.status-2 {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.device-status.status-0 {
  background: rgba(107, 114, 128, 0.2);
  color: #6b7280;
}

.device-info {
  margin-bottom: 10px;
}

.device-name {
  color: #f1f5f9;
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.device-code {
  color: #64748b;
  font-size: 12px;
  margin-top: 2px;
}

.device-metrics {
  display: flex;
  gap: 15px;
}

.metric {
  display: flex;
  flex-direction: column;
}

.metric-label {
  color: #64748b;
  font-size: 11px;
}

.metric-value {
  color: #f1f5f9;
  font-size: 14px;
  font-weight: 600;
}

.metric-value.warning {
  color: #f59e0b;
}

.alarm-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.alarm-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: rgba(15, 23, 42, 0.6);
  border-radius: 8px;
  margin-bottom: 8px;
  border-left: 3px solid #6b7280;
}

.alarm-item.level-1 {
  border-left-color: #f59e0b;
}

.alarm-item.level-2 {
  border-left-color: #ef4444;
}

.alarm-item.level-3 {
  border-left-color: #dc2626;
  animation: alarm-pulse 1s infinite;
}

@keyframes alarm-pulse {
  0%, 100% { background: rgba(220, 38, 38, 0.2); }
  50% { background: rgba(220, 38, 38, 0.4); }
}

.alarm-icon {
  color: #ef4444;
}

.alarm-content {
  flex: 1;
  min-width: 0;
}

.alarm-message {
  color: #f1f5f9;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alarm-time {
  color: #64748b;
  font-size: 11px;
  margin-top: 3px;
}

:deep(.el-empty__description) {
  color: #64748b;
}
</style>
