import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' },
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/views/books/BookList.vue'),
        meta: { title: '图书列表', icon: 'Reading' },
      },
      {
        path: 'my-borrows',
        name: 'MyBorrows',
        component: () => import('@/views/borrows/MyBorrows.vue'),
        meta: { title: '我的借阅', icon: 'Document' },
      },
      {
        path: 'my-fines',
        name: 'MyFines',
        component: () => import('@/views/borrows/MyFines.vue'),
        meta: { title: '我的罚款', icon: 'Money' },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'User' },
      },
      {
        path: 'admin/books',
        name: 'AdminBooks',
        component: () => import('@/views/admin/BookManage.vue'),
        meta: { title: '图书管理', icon: 'Notebook', requiresAdmin: true },
      },
      {
        path: 'admin/categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理', icon: 'Menu', requiresAdmin: true },
      },
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', requiresAdmin: true },
      },
      {
        path: 'admin/borrows',
        name: 'AdminBorrows',
        component: () => import('@/views/admin/BorrowManage.vue'),
        meta: { title: '借阅管理', icon: 'Tickets', requiresAdmin: true },
      },
      {
        path: 'admin/fines',
        name: 'AdminFines',
        component: () => import('@/views/admin/FineManage.vue'),
        meta: { title: '罚款管理', icon: 'Wallet', requiresAdmin: true },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 图书馆管理系统` : '图书馆管理系统'
  
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ path: '/dashboard' })
    return
  }
  
  if (to.path === '/login' && userStore.isLoggedIn) {
    next({ path: '/dashboard' })
    return
  }
  
  next()
})

export default router
