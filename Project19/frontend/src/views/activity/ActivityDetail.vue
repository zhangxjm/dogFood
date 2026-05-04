<template>
  <div class="activity-detail">
    <el-page-header @back="goBack">
      <template #content>
        <span class="title">{{ activity.name }}</span>
      </template>
    </el-page-header>

    <el-card class="detail-card" v-loading="loading">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="活动标题" :span="3" class="title-item">
          <el-tag :type="getStatusType(activity.status)" size="large" class="status-tag">
            {{ getStatusText(activity.status) }}
          </el-tag>
          <span class="activity-title">{{ activity.name }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="所属社团">{{ activity.clubName }}</el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ activity.location }}</el-descriptions-item>
        <el-descriptions-item label="参与人数">
          {{ activity.currentParticipants }}/{{ activity.maxParticipants }}
        </el-descriptions-item>
        <el-descriptions-item label="开始时间" :span="3">{{ activity.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间" :span="3">{{ activity.endTime }}</el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="3">{{ activity.createTime }}</el-descriptions-item>
        <el-descriptions-item label="活动描述" :span="3">
          <div class="description">{{ activity.description }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <div class="action-area" v-if="activity.status === 1">
        <template v-if="!isRegistered">
          <el-button type="primary" size="large" :loading="registering" @click="handleRegister">
            <el-icon><Edit /></el-icon>
            立即报名
          </el-button>
        </template>
        <template v-else>
          <el-button type="danger" size="large" :loading="canceling" @click="handleCancel">
            <el-icon><Close /></el-icon>
            取消报名
          </el-button>
        </template>
      </div>
    </el-card>

    <el-card class="register-card" v-if="isPresident || userInfo.role === 3">
      <template #header>
        <span>报名列表</span>
      </template>
      <el-table :data="registrationList" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="createTime" label="报名时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已确认' : '已报名' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityDetail, registerActivity, cancelRegistration, getActivityRegistrations } from '@/api/activity'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const registering = ref(false)
const canceling = ref(false)
const activity = ref({})
const registrationList = ref([])

const isRegistered = computed(() => {
  return registrationList.value.some(item => item.userId === userInfo.value.id)
})

const isPresident = computed(() => {
  return activity.value.presidentId === userInfo.value.id
})

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '已取消',
    1: '报名中',
    2: '进行中',
    3: '已结束'
  }
  return texts[status] || '未知'
}

const goBack = () => {
  router.back()
}

const loadActivityDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('活动ID不能为空')
    return
  }

  loading.value = true
  try {
    const res = await getActivityDetail(id)
    activity.value = res.data || {}
    loadRegistrations()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadRegistrations = async () => {
  try {
    const res = await getActivityRegistrations(route.params.id)
    registrationList.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleRegister = async () => {
  try {
    await ElMessageBox.confirm('确定要报名参加该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    registering.value = true
    await registerActivity(route.params.id)
    ElMessage.success('报名成功')
    loadRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  } finally {
    registering.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    canceling.value = true
    await cancelRegistration(route.params.id)
    ElMessage.success('取消成功')
    loadRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  } finally {
    canceling.value = false
  }
}

onMounted(() => {
  loadActivityDetail()
})
</script>

<style scoped lang="scss">
.activity-detail {
  .title {
    font-size: 18px;
    font-weight: bold;
  }

  .detail-card {
    margin-top: 20px;

    .title-item {
      display: flex;
      align-items: center;

      .status-tag {
        margin-right: 12px;
      }

      .activity-title {
        font-size: 18px;
        font-weight: bold;
      }
    }

    .description {
      white-space: pre-wrap;
      line-height: 1.8;
    }

    .action-area {
      margin-top: 20px;
      text-align: center;
    }
  }

  .register-card {
    margin-top: 20px;
  }
}
</style>
