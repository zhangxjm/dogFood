import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据大屏' }
      },
      {
        path: 'devices',
        name: 'Devices',
        component: () => import('../views/Devices.vue'),
        meta: { title: '设备管理' }
      },
      {
        path: 'device-data',
        name: 'DeviceData',
        component: () => import('../views/DeviceData.vue'),
        meta: { title: '历史数据' }
      },
      {
        path: 'alarms',
        name: 'Alarms',
        component: () => import('../views/Alarms.vue'),
        meta: { title: '报警管理' }
      },
      {
        path: 'work-orders',
        name: 'WorkOrders',
        component: () => import('../views/WorkOrders.vue'),
        meta: { title: '工单管理' }
      },
      {
        path: 'operators',
        name: 'Operators',
        component: () => import('../views/Operators.vue'),
        meta: { title: '运维人员' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
