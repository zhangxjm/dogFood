<template>
  <div class="job-list-container">
    <n-layout>
      <n-layout-header class="page-header">
        <div class="header-content">
          <n-space align="center">
            <n-icon size="32" color="#1890ff" @click="goHome">
              <iosBriefcase />
            </n-icon>
            <span class="logo-text" @click="goHome">求职招聘系统</span>
          </n-space>
          <n-space>
            <n-button v-if="!userStore.token" @click="goLogin">登录</n-button>
            <n-button v-if="userStore.token" type="primary" @click="goCenter">进入中心</n-button>
          </n-space>
        </div>
      </n-layout-header>

      <n-layout-content class="page-content">
        <div class="search-section">
          <n-space class="search-box">
            <n-input
              v-model:value="searchForm.keyword"
              placeholder="搜索职位名称、关键词..."
              style="width: 300px"
              clearable
              @clear="loadJobs"
            />
            <n-select
              v-model:value="searchForm.city"
              placeholder="选择城市"
              :options="cityOptions"
              style="width: 120px"
              clearable
              @update:value="loadJobs"
            />
            <n-select
              v-model:value="searchForm.jobType"
              placeholder="职位类型"
              :options="typeOptions"
              style="width: 120px"
              clearable
              @update:value="loadJobs"
            />
            <n-button type="primary" @click="loadJobs" :loading="loading">
              搜索
            </n-button>
          </n-space>
        </div>

        <div class="job-list-section">
          <n-grid :x-gap="16" :y-gap="16" :cols="1">
            <n-gi :span="1" v-for="job in jobs" :key="job.id">
              <n-card hoverable class="job-item" @click="goDetail(job.id)">
                <n-grid :cols="5">
                  <n-gi :span="3">
                    <n-space vertical>
                      <div class="job-title-row">
                        <n-text depth="1" class="job-title">{{ job.jobTitle }}</n-text>
                        <n-text class="salary">{{ job.salaryMin }}-{{ job.salaryMax }}K</n-text>
                      </div>
                      <n-space>
                        <n-tag size="small" v-if="job.city">{{ job.city }}</n-tag>
                        <n-tag size="small" v-if="job.experience">{{ job.experience }}</n-tag>
                        <n-tag size="small" v-if="job.education">{{ job.education }}</n-tag>
                        <n-tag size="small" v-if="job.jobType">{{ job.jobType }}</n-tag>
                      </n-space>
                      <n-text depth="3" class="company-name">
                        {{ job.companyName }}
                      </n-text>
                    </n-space>
                  </n-gi>
                  <n-gi :span="2" style="text-align: right;">
                    <n-space vertical justify="space-between" style="height: 100%;">
                      <div />
                      <div>
                        <n-text depth="3">浏览 {{ job.viewCount }} 次</n-text>
                        <n-divider vertical />
                        <n-text depth="3">{{ formatTime(job.createTime) }}</n-text>
                      </div>
                    </n-space>
                  </n-gi>
                </n-grid>
              </n-card>
            </n-gi>
          </n-grid>

          <n-pagination
            v-model:page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :item-count="pagination.total"
            :show-size-picker="true"
            :page-sizes="[10, 20, 50]"
            show-quick-jumper
            @update:page="loadJobs"
            @update:page-size="loadJobs"
            class="pagination"
          />
        </div>
      </n-layout-content>
    </n-layout>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessage } from 'naive-ui'
import dayjs from 'dayjs'
import {
  iosBriefcase
} from '@vicons/ionicons5'
import { jobApi } from '@/api/job'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const jobs = ref([])

const searchForm = reactive({
  keyword: '',
  city: null,
  jobType: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const cityOptions = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '广州', value: '广州' }
]

const typeOptions = [
  { label: '全职', value: '全职' },
  { label: '兼职', value: '兼职' },
  { label: '实习', value: '实习' }
]

const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD')
}

const loadJobs = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword || undefined,
      city: searchForm.city || undefined,
      jobType: searchForm.jobType || undefined
    }
    const res = await jobApi.list(params)
    if (res.code === 200) {
      jobs.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    console.error('加载职位列表失败:', e)
    message.error('加载职位列表失败')
  } finally {
    loading.value = false
  }
}

const goHome = () => {
  router.push('/home')
}

const goLogin = () => {
  router.push('/login')
}

const goCenter = () => {
  const role = userStore.userInfo?.role
  if (role === 1) router.push('/user/dashboard')
  else if (role === 2) router.push('/user/company')
  else if (role === 3) router.push('/user/admin/dashboard')
}

const goDetail = (id) => {
  router.push(`/job/detail/${id}`)
}

onMounted(() => {
  userStore.initStore()
  if (route.query.keyword) {
    searchForm.keyword = route.query.keyword
  }
  loadJobs()
})
</script>

<style scoped>
.job-list-container {
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

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
  cursor: pointer;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.search-section {
  margin-bottom: 24px;
}

.search-box {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.job-list-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

.job-item {
  cursor: pointer;
}

.job-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.job-title {
  font-size: 18px;
  font-weight: bold;
}

.salary {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.company-name {
  display: block;
  margin-top: 8px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
