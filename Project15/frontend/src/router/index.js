import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/job/list',
    name: 'JobList',
    component: () => import('@/views/job/JobList.vue')
  },
  {
    path: '/job/detail/:id',
    name: 'JobDetail',
    component: () => import('@/views/job/JobDetail.vue')
  },
  {
    path: '/company/detail/:id',
    name: 'CompanyDetail',
    component: () => import('@/views/company/CompanyDetail.vue')
  },
  {
    path: '/user',
    component: () => import('@/views/layout/UserLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/user/dashboard'
      },
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/user/Dashboard.vue')
      },
      {
        path: 'resume',
        name: 'ResumeEdit',
        component: () => import('@/views/user/ResumeEdit.vue'),
        meta: { role: 1 }
      },
      {
        path: 'applications',
        name: 'MyApplications',
        component: () => import('@/views/user/Applications.vue'),
        meta: { role: 1 }
      },
      {
        path: 'favorites',
        name: 'MyFavorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { role: 1 }
      },
      {
        path: 'interviews',
        name: 'MyInterviews',
        component: () => import('@/views/user/Interviews.vue')
      },
      {
        path: 'messages',
        name: 'MyMessages',
        component: () => import('@/views/user/Messages.vue')
      },
      {
        path: 'company',
        name: 'CompanyEdit',
        component: () => import('@/views/hr/CompanyEdit.vue'),
        meta: { role: 2 }
      },
      {
        path: 'jobs',
        name: 'HrJobList',
        component: () => import('@/views/hr/JobList.vue'),
        meta: { role: 2 }
      },
      {
        path: 'job/publish',
        name: 'JobPublish',
        component: () => import('@/views/hr/JobPublish.vue'),
        meta: { role: 2 }
      },
      {
        path: 'resumes',
        name: 'ReceivedResumes',
        component: () => import('@/views/hr/ResumeList.vue'),
        meta: { role: 2 }
      },
      {
        path: 'admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { role: 3 }
      },
      {
        path: 'admin/users',
        name: 'AdminUserList',
        component: () => import('@/views/admin/UserList.vue'),
        meta: { role: 3 }
      },
      {
        path: 'admin/companies',
        name: 'AdminCompanyList',
        component: () => import('@/views/admin/CompanyList.vue'),
        meta: { role: 3 }
      },
      {
        path: 'admin/jobs',
        name: 'AdminJobList',
        component: () => import('@/views/admin/JobList.vue'),
        meta: { role: 3 }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  if (to.meta.role && userStore.userInfo?.role !== to.meta.role) {
    if (userStore.userInfo?.role === 1) {
      next('/user/dashboard')
    } else if (userStore.userInfo?.role === 2) {
      next('/user/company')
    } else if (userStore.userInfo?.role === 3) {
      next('/user/admin/dashboard')
    } else {
      next('/home')
    }
    return
  }

  next()
})

export default router
