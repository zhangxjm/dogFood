import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'post',
        name: 'Post',
        component: () => import('@/views/system/post/index.vue'),
        meta: { title: '岗位管理', icon: 'Connection' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/system/employee/index.vue'),
        meta: { title: '员工档案', icon: 'User' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/attendance/record/index.vue'),
        meta: { title: '考勤打卡', icon: 'Clock' }
      },
      {
        path: 'leave',
        name: 'Leave',
        component: () => import('@/views/attendance/leave/index.vue'),
        meta: { title: '请假管理', icon: 'Calendar' }
      },
      {
        path: 'overtime',
        name: 'Overtime',
        component: () => import('@/views/attendance/overtime/index.vue'),
        meta: { title: '加班管理', icon: 'Timer' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/attendance/statistics/index.vue'),
        meta: { title: '考勤统计', icon: 'DataAnalysis' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 员工人事考勤管理系统` : '员工人事考勤管理系统'
  
  const userStore = useUserStore()
  const token = userStore.token || localStorage.getItem('token')
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
