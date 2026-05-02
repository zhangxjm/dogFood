import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/courses'
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
    path: '/courses',
    name: 'Courses',
    component: () => import('@/views/Courses.vue'),
    meta: { title: '课程列表' }
  },
  {
    path: '/courses/:id',
    name: 'CourseDetail',
    component: () => import('@/views/CourseDetail.vue'),
    meta: { title: '课程详情' }
  },
  {
    path: '/course/learn/:chapterId',
    name: 'CourseLearn',
    component: () => import('@/views/CourseLearn.vue'),
    meta: { title: '视频学习', requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: { title: '个人中心', requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/user/enrollments'
      },
      {
        path: 'enrollments',
        name: 'MyEnrollments',
        component: () => import('@/views/user/MyEnrollments.vue'),
        meta: { title: '我的课程' }
      },
      {
        path: 'favorites',
        name: 'MyFavorites',
        component: () => import('@/views/user/MyFavorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/teacher',
    name: 'TeacherCenter',
    component: () => import('@/views/TeacherCenter.vue'),
    meta: { title: '教师后台', requiresAuth: true, requiresTeacher: true },
    children: [
      {
        path: '',
        redirect: '/teacher/courses'
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: () => import('@/views/teacher/CourseList.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'courses/create',
        name: 'CourseCreate',
        component: () => import('@/views/teacher/CourseForm.vue'),
        meta: { title: '创建课程' }
      },
      {
        path: 'courses/edit/:id',
        name: 'CourseEdit',
        component: () => import('@/views/teacher/CourseForm.vue'),
        meta: { title: '编辑课程' }
      },
      {
        path: 'courses/:id/chapters',
        name: 'ChapterManage',
        component: () => import('@/views/teacher/ChapterManage.vue'),
        meta: { title: '章节管理' }
      },
      {
        path: 'courses/:id/students',
        name: 'StudentManage',
        component: () => import('@/views/teacher/StudentManage.vue'),
        meta: { title: '学员管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 在线课程学习平台` : '在线课程学习平台'
  
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresTeacher && !userStore.isTeacher) {
    next('/')
  } else if (to.meta.guest && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
