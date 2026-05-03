<template>
  <div class="dashboard-container">
    <n-grid :cols="1" :x-gap="24" :y-gap="24">
      <n-gi :span="1">
        <n-card>
          <n-space align="center" style="margin-bottom: 16px;">
            <n-avatar :size="64">
              <n-icon :size="36">
                <iosPerson />
              </n-icon>
            </n-avatar>
            <n-space vertical>
              <n-text depth="1" strong style="font-size: 20px;">
                {{ userStore.userInfo?.username || '用户' }}
              </n-text>
              <n-tag size="small" type="info">
                {{ roleText }}
              </n-tag>
            </n-space>
          </n-space>
        </n-card>
      </n-gi>

      <n-gi :span="1">
        <n-grid :cols="2" :x-gap="24">
          <n-gi :span="1">
            <n-statistic-card label="我的投递" :value="stats.applicationCount || 0">
              <template #prefix>
                <n-icon :size="24" :depth="3">
                  <iosPaperPlane />
                </n-icon>
              </template>
            </n-statistic-card>
          </n-gi>
          <n-gi :span="1">
            <n-statistic-card label="我的收藏" :value="stats.favoriteCount || 0">
              <template #prefix>
                <n-icon :size="24" :depth="3">
                  <iosHeart />
                </n-icon>
              </template>
            </n-statistic-card>
          </n-gi>
        </n-grid>
      </n-gi>

      <n-gi :span="1" v-if="userStore.userInfo?.role === 2">
        <n-grid :cols="2" :x-gap="24">
          <n-gi :span="1">
            <n-statistic-card label="发布职位" :value="stats.jobCount || 0">
              <template #prefix>
                <n-icon :size="24" :depth="3">
                  <iosBriefcase />
                </n-icon>
              </template>
            </n-statistic-card>
          </n-gi>
          <n-gi :span="1">
            <n-statistic-card label="收到简历" :value="stats.resumeCount || 0">
              <template #prefix>
                <n-icon :size="24" :depth="3">
                  <iosDocumentText />
                </n-icon>
              </template>
            </n-statistic-card>
          </n-gi>
        </n-grid>
      </n-gi>

      <n-gi :span="1" v-if="recentJobs.length > 0">
        <n-card title="热门职位">
          <n-list>
            <n-list-item
              v-for="job in recentJobs"
              :key="job.id"
              style="cursor: pointer;"
              @click="goToJob(job.id)"
            >
              <n-list-item-meta>
                <template #avatar>
                  <n-avatar :size="40">
                    <n-icon>
                      <iosBriefcase />
                    </n-icon>
                  </n-avatar>
                </template>
                <template #title>
                  <n-text depth="1">{{ job.jobTitle }}</n-text>
                </template>
                <template #description>
                  <n-space>
                    <n-text depth="3">{{ job.city }}</n-text>
                    <n-text type="success">{{ job.salaryMin }}-{{ job.salaryMax }}K</n-text>
                  </n-space>
                </template>
              </n-list-item-meta>
            </n-list-item>
          </n-list>
        </n-card>
      </n-gi>
    </n-grid>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { jobApi } from '@/api/job'
import {
  iosPerson,
  iosPaperPlane,
  iosHeart,
  iosBriefcase,
  iosDocumentText
} from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()

const stats = reactive({
  applicationCount: 0,
  favoriteCount: 0,
  jobCount: 0,
  resumeCount: 0
})

const recentJobs = ref([])

const roleText = computed(() => {
  const role = userStore.userInfo?.role
  switch (role) {
    case 1: return '求职者'
    case 2: return '企业HR'
    case 3: return '管理员'
    default: return '求职者'
  }
})

const loadRecentJobs = async () => {
  try {
    const res = await jobApi.list({ page: 1, pageSize: 5 })
    if (res.code === 200) {
      recentJobs.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载热门职位失败:', e)
  }
}

const goToJob = (jobId) => {
  router.push(`/job/${jobId}`)
}

onMounted(() => {
  userStore.initStore()
  loadRecentJobs()
})
</script>

<style scoped>
.dashboard-container {
  max-width: 900px;
}
</style>
