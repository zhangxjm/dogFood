<template>
  <n-layout has-sider>
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      :native-scrollbar="false"
    >
      <div class="logo" @click="goHome">
        <n-icon size="28" v-if="collapsed">
          <ios-briefcase />
        </n-icon>
        <span v-else class="logo-text">求职招聘系统</span>
      </div>
      <n-menu
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="currentMenu"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered class="header">
        <n-space justify="space-between">
          <n-button quaternary circle @click="collapsed = !collapsed">
            <template #icon>
              <n-icon>
                <component :is="collapsed ? iosMenu : iosArrowBack" />
              </n-icon>
            </template>
          </n-button>
          <n-space>
            <n-badge :value="unreadCount" :show-zero="false" @click="goMessages">
              <n-button quaternary circle>
                <template #icon>
                  <n-icon>
                    <ios-notifications />
                  </n-icon>
                </template>
              </n-button>
            </n-badge>
            <n-dropdown :options="userOptions" trigger="click">
              <n-avatar size="small">
                {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
              </n-avatar>
            </n-dropdown>
          </n-space>
        </n-space>
      </n-layout-header>
      <n-layout-content content-style="padding: 24px; min-height: calc(100vh - 64px);">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessage } from 'naive-ui'
import { h } from 'vue'
import {
  iosBriefcase,
  iosMenu,
  iosArrowBack,
  iosNotifications,
  iosPerson,
  iosDocumentText,
  iosPaperPlane,
  iosHeart,
  iosCalendar,
  iosMail,
  iosBusiness,
  iosCreate,
  iosPeople,
  iosStatsChart,
  iosLogOut
} from '@vicons/ionicons5'
import { messageApi } from '@/api/message'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

const collapsed = ref(false)
const unreadCount = ref(0)

const jobseekerMenus = [
  {
    label: '首页',
    key: '/user/dashboard',
    icon: () => h(iosBriefcase)
  },
  {
    label: '我的简历',
    key: '/user/resume',
    icon: () => h(iosDocumentText)
  },
  {
    label: '投递记录',
    key: '/user/applications',
    icon: () => h(iosPaperPlane)
  },
  {
    label: '我的收藏',
    key: '/user/favorites',
    icon: () => h(iosHeart)
  },
  {
    label: '面试安排',
    key: '/user/interviews',
    icon: () => h(iosCalendar)
  },
  {
    label: '消息中心',
    key: '/user/messages',
    icon: () => h(iosMail)
  }
]

const hrMenus = [
  {
    label: '公司信息',
    key: '/user/company',
    icon: () => h(iosBusiness)
  },
  {
    label: '职位管理',
    key: '/user/jobs',
    icon: () => h(iosBriefcase)
  },
  {
    label: '发布职位',
    key: '/user/job/publish',
    icon: () => h(iosCreate)
  },
  {
    label: '收到的简历',
    key: '/user/resumes',
    icon: () => h(iosPeople)
  },
  {
    label: '面试安排',
    key: '/user/interviews',
    icon: () => h(iosCalendar)
  },
  {
    label: '消息中心',
    key: '/user/messages',
    icon: () => h(iosMail)
  }
]

const adminMenus = [
  {
    label: '数据统计',
    key: '/user/admin/dashboard',
    icon: () => h(iosStatsChart)
  },
  {
    label: '用户管理',
    key: '/user/admin/users',
    icon: () => h(iosPeople)
  },
  {
    label: '公司审核',
    key: '/user/admin/companies',
    icon: () => h(iosBusiness)
  },
  {
    label: '职位审核',
    key: '/user/admin/jobs',
    icon: () => h(iosBriefcase)
  }
]

const menuOptions = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 1) return jobseekerMenus
  if (role === 2) return hrMenus
  if (role === 3) return adminMenus
  return []
})

const currentMenu = computed(() => route.path)

const userOptions = [
  {
    label: userStore.userInfo?.username || '用户',
    key: 'user-info',
    icon: () => h(iosPerson),
    disabled: true
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h(iosLogOut)
  }
]

const handleMenuSelect = (key) => {
  router.push(key)
}

const goHome = () => {
  router.push('/home')
}

const goMessages = () => {
  router.push('/user/messages')
}

const handleUserSelect = (key) => {
  if (key === 'logout') {
    userStore.logout()
    message.success('已退出登录')
    router.push('/login')
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await messageApi.unreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    console.error('获取未读消息数失败:', e)
  }
}

onMounted(() => {
  userStore.initStore()
  if (userStore.token) {
    fetchUnreadCount()
  }
})
</script>

<style scoped>
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom: 1px solid #e8e8e8;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #fff;
}
</style>
