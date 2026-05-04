import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/item/:id',
    name: 'ItemDetail',
    component: () => import('@/views/ItemDetail.vue'),
    meta: { title: '物品详情' }
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/Publish.vue'),
    meta: { title: '发布物品', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true },
    children: [
      {
        path: '',
        redirect: 'my-items'
      },
      {
        path: 'my-items',
        name: 'MyItems',
        component: () => import('@/views/profile/MyItems.vue'),
        meta: { title: '我的发布' }
      },
      {
        path: 'my-collections',
        name: 'MyCollections',
        component: () => import('@/views/profile/MyCollections.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'my-claims',
        name: 'MyClaims',
        component: () => import('@/views/profile/MyClaims.vue'),
        meta: { title: '认领记录' }
      },
      {
        path: 'received-claims',
        name: 'ReceivedClaims',
        component: () => import('@/views/profile/ReceivedClaims.vue'),
        meta: { title: '收到的认领' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/profile/Settings.vue'),
        meta: { title: '个人设置' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/Admin.vue'),
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: 'pending-items'
      },
      {
        path: 'pending-items',
        name: 'PendingItems',
        component: () => import('@/views/admin/PendingItems.vue'),
        meta: { title: '待审核物品' }
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'comments',
        name: 'CommentManage',
        component: () => import('@/views/admin/CommentManage.vue'),
        meta: { title: '评论管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  document.title = to.meta.title || '校园失物招领平台'
  
  if (to.meta.guest && userStore.isLoggedIn) {
    next({ path: '/' })
    return
  }
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ path: '/' })
    return
  }
  
  if (userStore.isLoggedIn && !userStore.userInfo) {
    await userStore.getUserInfo()
  }
  
  next()
})

export default router
