import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { requiresAuth: true, title: '数据概览' }
      },
      {
        path: 'data',
        name: 'Data',
        component: () => import('@/views/Data.vue'),
        meta: { requiresAuth: true, title: '数据管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()
  const token = localStorage.getItem('token')
  
  console.log(`[Router] Navigating to: ${to.path}, token exists: ${!!token}, user loaded: ${!!authStore.user}`)
  
  if (token && !authStore.user) {
    console.log('[Router] Token exists but user not loaded, fetching user...')
    const success = await authStore.fetchUser()
    console.log(`[Router] Fetch user result: ${success}`)
  }
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    console.log('[Router] Auth required, redirecting to login')
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (!to.meta.requiresAuth && authStore.isAuthenticated && (to.name === 'Login' || to.name === 'Register')) {
    console.log('[Router] Already authenticated, redirecting to dashboard')
    next({ name: 'Dashboard' })
  } else {
    console.log('[Router] Proceeding to:', to.path)
    next()
  }
})

export default router
