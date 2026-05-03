<template>
  <div class="my-activities">
    <el-card>
      <template #header>
        <span>我的活动</span>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="activityTitle" label="活动名称" min-width="180" />
        <el-table-column prop="clubName" label="所属社团" width="150" />
        <el-table-column prop="location" label="活动地点" width="150" />
        <el-table-column label="参与人数" width="120">
          <template #default="scope">
            {{ scope.row.currentParticipants }}/{{ scope.row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="activityStatus" label="活动状态" width="100">
          <template #default="scope">
            <el-tag :type="getActivityStatusType(scope.row.activityStatus)">
              {{ getActivityStatusText(scope.row.activityStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已确认' : '已报名' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="createTime" label="报名时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="goDetail(scope.row.activityId)">查看</el-button>
            <el-button
              type="danger"
              link
              v-if="!isActivityEnded(scope.row.startTime) && scope.row.activityStatus === 1"
              @click="handleCancel(scope.row)"
            >
              取消报名
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyActivities, cancelRegistration } from '@/api/activity'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const getActivityStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return types[status] || 'info'
}

const getActivityStatusText = (status) => {
  const texts = {
    0: '已取消',
    1: '报名中',
    2: '进行中',
    3: '已结束'
  }
  return texts[status] || '未知'
}

const isActivityEnded = (startTime) => {
  if (!startTime) return false
  const now = new Date()
  const start = new Date(startTime)
  return start < now
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyActivities()
    tableData.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  router.push(`/activity/${id}`)
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelRegistration(row.activityId)
    ElMessage.success('取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.my-activities {
}
</style>
