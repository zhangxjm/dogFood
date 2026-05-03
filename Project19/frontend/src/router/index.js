import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'club',
        name: 'ClubList',
        component: () => import('@/views/club/ClubList.vue'),
        meta: { title: '社团列表', requiresAuth: true }
      },
      {
        path: 'club/:id',
        name: 'ClubDetail',
        component: () => import('@/views/club/ClubDetail.vue'),
        meta: { title: '社团详情', requiresAuth: true }
      },
      {
        path: 'activity',
        name: 'ActivityList',
        component: () => import('@/views/activity/ActivityList.vue'),
        meta: { title: '活动列表', requiresAuth: true }
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/activity/ActivityDetail.vue'),
        meta: { title: '活动详情', requiresAuth: true }
      },
      {
        path: 'activity/create',
        name: 'CreateActivity',
        component: () => import('@/views/activity/CreateActivity.vue'),
        meta: { title: '发布活动', requiresAuth: true }
      },
      {
        path: 'activity/edit/:id',
        name: 'EditActivity',
        component: () => import('@/views/activity/CreateActivity.vue'),
        meta: { title: '编辑活动', requiresAuth: true }
      },
      {
        path: 'club/:clubId/members',
        name: 'ClubMemberManage',
        component: () => import('@/views/club/ClubMemberManage.vue'),
        meta: { title: '成员管理', requiresAuth: true }
      },
      {
        path: 'my-club',
        name: 'MyClub',
        component: () => import('@/views/club/MyClubs.vue'),
        meta: { title: '我的社团', requiresAuth: true }
      },
      {
        path: 'my-activity',
        name: 'MyActivity',
        component: () => import('@/views/activity/MyActivities.vue'),
        meta: { title: '我的活动', requiresAuth: true }
      },
      {
        path: 'admin/user',
        name: 'AdminUser',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/club',
        name: 'AdminClub',
        component: () => import('@/views/admin/ClubManage.vue'),
        meta: { title: '社团管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/activity',
        name: 'AdminActivity',
        component: () => import('@/views/admin/ActivityManage.vue'),
        meta: { title: '活动管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'admin/category',
        name: 'AdminCategory',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '校园社团管理系统'
  
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/home')
    return
  }
  
  if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    next('/home')
    return
  }
  
  next()
})

export default router
