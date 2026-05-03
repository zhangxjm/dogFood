<template>
  <div class="job-detail-container">
    <n-layout>
      <n-layout-header class="page-header">
        <div class="header-content">
          <n-space align="center">
            <n-button quaternary @click="goBack">
              <template #icon>
                <n-icon>
                  <iosArrowBack />
                </n-icon>
              </template>
              返回
            </n-button>
          </n-space>
          <n-space>
            <n-button
              :type="isFavorited ? 'error' : 'default'"
              @click="toggleFavorite"
            >
              <template #icon>
                <n-icon>
                  <component :is="isFavorited ? iosHeart : iosHeartOutline" />
                </n-icon>
              </template>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </n-button>
            <n-button
              type="primary"
              v-if="userStore.userInfo?.role === 1"
              :loading="applyLoading"
              @click="applyJob"
            >
              投递简历
            </n-button>
          </n-space>
        </div>
      </n-layout-header>

      <n-layout-content class="page-content">
        <n-spin :show="loading">
          <div v-if="job.id" class="job-content">
            <n-card class="main-card">
              <n-space vertical style="width: 100%;">
                <div class="job-header">
                  <n-text depth="1" class="job-title">{{ job.jobTitle }}</n-text>
                  <n-text class="salary">{{ job.salaryMin }}-{{ job.salaryMax }}K</n-text>
                </div>

                <n-space>
                  <n-tag size="small" v-if="job.city">{{ job.city }}</n-tag>
                  <n-tag size="small" v-if="job.experience">{{ job.experience }}</n-tag>
                  <n-tag size="small" v-if="job.education">{{ job.education }}</n-tag>
                  <n-tag size="small" v-if="job.jobType">{{ job.jobType }}</n-tag>
                </n-space>

                <n-divider />

                <n-space vertical>
                  <n-text depth="2" class="section-title">职位描述</n-text>
                  <n-text depth="3">{{ job.jobDescription || '暂无描述' }}</n-text>
                </n-space>

                <n-divider />

                <n-space vertical>
                  <n-text depth="2" class="section-title">职位要求</n-text>
                  <n-text depth="3">{{ job.jobRequirement || '暂无要求' }}</n-text>
                </n-space>

                <n-divider v-if="job.welfare" />

                <n-space vertical v-if="job.welfare">
                  <n-text depth="2" class="section-title">福利待遇</n-text>
                  <n-text depth="3">{{ job.welfare }}</n-text>
                </n-space>
              </n-space>
            </n-card>

            <n-card class="company-card" v-if="job.companyName">
              <n-space align="center">
                <n-avatar size="large">
                  {{ job.companyName?.charAt(0) }}
                </n-avatar>
                <n-space vertical>
                  <n-text depth="1" class="company-name">{{ job.companyName }}</n-text>
                  <n-space>
                    <n-tag size="small" v-if="job.companyIndustry">{{ job.companyIndustry }}</n-tag>
                    <n-tag size="small" v-if="job.companyScale">{{ job.companyScale }}</n-tag>
                  </n-space>
                </n-space>
              </n-space>
            </n-card>
          </div>
        </n-spin>
      </n-layout-content>
    </n-layout>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessage } from 'naive-ui'
import {
  iosArrowBack,
  iosHeart,
  iosHeartOutline
} from '@vicons/ionicons5'
import { jobApi } from '@/api/job'
import { favoriteApi } from '@/api/favorite'
import { applicationApi } from '@/api/application'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const applyLoading = ref(false)
const isFavorited = ref(false)
const job = reactive({
  id: null,
  jobTitle: '',
  salaryMin: 0,
  salaryMax: 0,
  city: '',
  experience: '',
  education: '',
  jobType: '',
  jobDescription: '',
  jobRequirement: '',
  welfare: '',
  companyName: '',
  companyIndustry: '',
  companyScale: ''
})

const loadJob = async () => {
  loading.value = true
  try {
    const res = await jobApi.getById(route.params.id)
    if (res.code === 200) {
      Object.assign(job, res.data)
    }
  } catch (e) {
    console.error('加载职位详情失败:', e)
    message.error('加载职位详情失败')
  } finally {
    loading.value = false
  }
}

const checkFavorite = async () => {
  if (!userStore.token) return
  try {
    const res = await favoriteApi.check(route.params.id)
    if (res.code === 200) {
      isFavorited.value = res.data
    }
  } catch (e) {
    console.error('检查收藏状态失败:', e)
  }
}

const toggleFavorite = async () => {
  if (!userStore.token) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await favoriteApi.toggle(route.params.id)
    if (res.code === 200) {
      isFavorited.value = !isFavorited.value
      message.success(isFavorited.value ? '收藏成功' : '已取消收藏')
    }
  } catch (e) {
    console.error('收藏操作失败:', e)
    message.error('操作失败')
  }
}

const applyJob = async () => {
  if (!userStore.token) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  applyLoading.value = true
  try {
    const res = await applicationApi.apply(route.params.id)
    if (res.code === 200) {
      message.success('投递成功')
    }
  } catch (e) {
    console.error('投递失败:', e)
    message.error(e.message || '投递失败')
  } finally {
    applyLoading.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  userStore.initStore()
  loadJob()
  checkFavorite()
})
</script>

<style scoped>
.job-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.job-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.main-card, .company-card {
  margin-bottom: 24px;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.job-title {
  font-size: 24px;
  font-weight: bold;
}

.salary {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
}

.section-title {
  font-weight: bold;
  font-size: 16px;
}

.company-name {
  font-size: 18px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .job-content {
    grid-template-columns: 1fr;
  }
}
</style>
