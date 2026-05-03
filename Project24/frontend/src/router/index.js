import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Home.vue"),
    meta: { title: "首页" },
  },
  {
    path: "/poll/:id",
    name: "PollDetail",
    component: () => import("@/views/PollDetail.vue"),
    meta: { title: "投票详情" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "管理员登录" },
  },
  {
    path: "/admin",
    component: () => import("@/views/Admin.vue"),
    meta: { title: "管理后台", requiresAuth: true },
    children: [
      {
        path: "",
        name: "Admin",
        redirect: "/admin/polls",
      },
      {
        path: "polls",
        name: "AdminPolls",
        component: () => import("@/views/admin/Polls.vue"),
        meta: { title: "投票活动管理" },
      },
      {
        path: "poll/:id",
        name: "AdminPollEdit",
        component: () => import("@/views/admin/PollEdit.vue"),
        meta: { title: "编辑投票活动" },
      },
      {
        path: "stats/:id",
        name: "AdminStats",
        component: () => import("@/views/admin/Stats.vue"),
        meta: { title: "投票统计" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 在线投票系统`
    : "在线投票系统";

  if (to.meta.requiresAuth) {
    const token = localStorage.getItem("token");
    if (!token) {
      ElMessage.warning("请先登录");
      next("/login");
      return;
    }
  }
  next();
});

export default router;
