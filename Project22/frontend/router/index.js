import { createRouter, createWebHashHistory } from "vue-router";

const routes = [
  {
    path: "/",
    redirect: "/index",
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/pages/login/login.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/pages/register/register.vue"),
  },
  {
    path: "/index",
    name: "Index",
    component: () => import("@/pages/index/index.vue"),
    meta: { requiresAuth: true },
  },
  {
    path: "/course-detail/:id",
    name: "CourseDetail",
    component: () => import("@/pages/course-detail/course-detail.vue"),
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const memberId = localStorage.getItem("memberId");

  if (to.meta.requiresAuth && !memberId) {
    next("/login");
  } else if ((to.path === "/login" || to.path === "/register") && memberId) {
    next("/index");
  } else {
    next();
  }
});

export default router;
