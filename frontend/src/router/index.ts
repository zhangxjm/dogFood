import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' },
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' },
      },
      {
        path: 'system/permission',
        name: 'SystemPermission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '权限管理', icon: 'Lock' },
      },
      {
        path: 'demo/form',
        name: 'DemoForm',
        component: () => import('@/views/demo/form/index.vue'),
        meta: { title: '表单演示', icon: 'Edit' },
      },
      {
        path: 'demo/table',
        name: 'DemoTable',
        component: () => import('@/views/demo/table/index.vue'),
        meta: { title: '表格演示', icon: 'Grid' },
      },
      {
        path: 'demo/upload',
        name: 'DemoUpload',
        component: () => import('@/views/demo/upload/index.vue'),
        meta: { title: '上传演示', icon: 'Upload' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  document.title = to.meta.title ? `${to.meta.title} - 后台管理系统` : '后台管理系统'
  
  if (to.meta.public) {
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/')
    } else {
      next()
    }
    return
  }
  
  if (!userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  if (userStore.menus.length === 0) {
    try {
      await userStore.fetchMenus()
    } catch (error) {
      console.error('获取菜单失败:', error)
    }
  }
  
  next()
})

export default router
