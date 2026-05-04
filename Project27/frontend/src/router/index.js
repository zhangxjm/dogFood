import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const MainLayout = () => import('@/views/layout/MainLayout.vue')
const AdminLayout = () => import('@/views/layout/AdminLayout.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'services',
        name: 'Services',
        component: () => import('@/views/Services.vue'),
        meta: { title: '服务列表' }
      },
      {
        path: 'services/:id',
        name: 'ServiceDetail',
        component: () => import('@/views/ServiceDetail.vue'),
        meta: { title: '服务详情' }
      },
      {
        path: 'workers',
        name: 'Workers',
        component: () => import('@/views/Workers.vue'),
        meta: { title: '师傅列表' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'worker-apply',
        name: 'WorkerApply',
        component: () => import('@/views/WorkerApply.vue'),
        meta: { title: '师傅入驻', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'workers',
        name: 'AdminWorkers',
        component: () => import('@/views/admin/Workers.vue'),
        meta: { title: '师傅审核' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue'),
        meta: { title: '服务分类' }
      },
      {
        path: 'services',
        name: 'AdminServices',
        component: () => import('@/views/admin/Services.vue'),
        meta: { title: '服务管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/Announcements.vue'),
        meta: { title: '公告管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 家政服务平台` : '家政服务平台'
  
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  if (token && !userStore.username) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      localStorage.removeItem('token')
      next('/login')
      return
    }
  }
  
  if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
    next('/home')
    return
  }
  
  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/home')
    return
  }
  
  next()
})

export default router
