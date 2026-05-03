<template>
  <div class="home-container">
    <n-layout>
      <n-layout-header class="home-header">
        <div class="header-content">
          <n-space align="center">
            <n-icon size="32" color="#1890ff">
              <iosBriefcase />
            </n-icon>
            <span class="logo-text">求职招聘系统</span>
          </n-space>
          <n-space>
            <n-button v-if="!userStore.token" @click="goLogin">登录</n-button>
            <n-button v-if="!userStore.token" type="primary" @click="goRegister">注册</n-button>
            <n-dropdown v-if="userStore.token" :options="userOptions" trigger="click">
              <n-button quaternary>
                <template #icon>
                  <n-icon>
                    <iosPerson />
                  </n-icon>
                </template>
                {{ userStore.userInfo?.username }}
              </n-button>
            </n-dropdown>
          </n-space>
        </div>
      </n-layout-header>

      <n-layout-content class="home-content">
        <div class="search-section">
          <n-space class="search-box">
            <n-input
              v-model:value="searchKeyword"
              placeholder="搜索职位、公司..."
              style="width: 400px"
              @keyup.enter="searchJobs"
            />
            <n-button type="primary" @click="searchJobs" :loading="loading">
              搜索
            </n-button>
          </n-space>
        </div>

        <div class="hot-jobs">
          <h3 class="section-title">热门职位</h3>
          <n-grid :x-gap="16" :y-gap="16" :cols="3">
            <n-gi v-for="job in jobs" :key="job.id" :span="1">
              <n-card hoverable class="job-card" @click="goJobDetail(job.id)">
                <n-space vertical justify="space-between" style="height: 100%;">
                  <div>
                    <n-text depth="1" class="job-title">{{ job.jobTitle }}</n-text>
                    <n-text depth="3" tag="div" class="salary">
                      {{ job.salaryMin }}-{{ job.salaryMax }}K
                    </n-text>
                  </div>
                  <div class="job-info">
                    <n-space>
                      <n-tag size="small" v-if="job.city">{{ job.city }}</n-tag>
                      <n-tag size="small" v-if="job.experience">{{ job.experience }}</n-tag>
                    </n-space>
                    <n-text depth="3" class="company-name">
                      {{ job.companyName }}
                    </n-text>
                  </div>
                </n-space>
              </n-card>
            </n-gi>
          </n-grid>
        </div>

        <div class="stats-section">
          <n-grid :x-gap="16" :y-gap="16" :cols="4">
            <n-gi :span="1">
              <n-card class="stat-card">
                <n-statistic label="总职位数" :value="totalJobs" />
              </n-card>
            </n-gi>
            <n-gi :span="1">
              <n-card class="stat-card">
                <n-statistic label="合作企业" :value="totalCompanies" />
              </n-card>
            </n-gi>
            <n-gi :span="1">
              <n-card class="stat-card">
                <n-statistic label="注册用户" :value="totalUsers" />
              </n-card>
            </n-gi>
            <n-gi :span="1">
              <n-card class="stat-card">
                <n-statistic label="成功招聘" :value="999" />
              </n-card>
            </n-gi>
          </n-grid>
        </div>
      </n-layout-content>

      <n-layout-footer class="home-footer">
        <n-text depth="3">Copyright &copy; 2024 求职招聘系统 - 企业级全栈项目</n-text>
      </n-layout-footer>
    </n-layout>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessage } from 'naive-ui'
import { h } from 'vue'
import {
  iosBriefcase,
  iosPerson,
  iosLogOut
} from '@vicons/ionicons5'
import { jobApi } from '@/api/job'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const searchKeyword = ref('')
const jobs = ref([])
const loading = ref(false)
const totalJobs = ref(0)
const totalCompanies = ref(0)
const totalUsers = ref(0)

const userOptions = [
  {
    label: '进入中心',
    key: 'center',
    icon: () => h(iosPerson)
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h(iosLogOut)
  }
]

const loadJobs = async () => {
  loading.value = true
  try {
    const res = await jobApi.list({ pageNum: 1, pageSize: 6 })
    if (res.code === 200) {
      jobs.value = res.data.records || []
      totalJobs.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载职位失败:', e)
  } finally {
    loading.value = false
  }
}

const searchJobs = () => {
  router.push({
    path: '/job/list',
    query: { keyword: searchKeyword.value }
  })
}

const goLogin = () => {
  router.push('/login')
}

const goRegister = () => {
  router.push('/register')
}

const goJobDetail = (id) => {
  router.push(`/job/detail/${id}`)
}

const handleUserSelect = (key) => {
  if (key === 'center') {
    const role = userStore.userInfo?.role
    if (role === 1) router.push('/user/dashboard')
    else if (role === 2) router.push('/user/company')
    else if (role === 3) router.push('/user/admin/dashboard')
  } else if (key === 'logout') {
    userStore.logout()
    message.success('已退出登录')
  }
}

onMounted(() => {
  userStore.initStore()
  loadJobs()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.home-header {
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
}

.home-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: calc(100vh - 128px);
}

.search-section {
  padding: 80px 24px;
  text-align: center;
}

.search-box {
  display: flex;
  justify-content: center;
}

.hot-jobs {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.section-title {
  color: #fff;
  margin-bottom: 16px;
  font-size: 24px;
}

.job-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.job-card:hover {
  transform: translateY(-4px);
}

.job-title {
  font-size: 18px;
  font-weight: bold;
}

.salary {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
  margin-top: 8px;
}

.job-info {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.company-name {
  display: block;
  margin-top: 8px;
}

.stats-section {
  max-width: 1200px;
  margin: 40px auto;
  padding: 24px;
}

.stat-card {
  text-align: center;
}

.home-footer {
  text-align: center;
  padding: 24px;
  background: #fff;
}
</style>
