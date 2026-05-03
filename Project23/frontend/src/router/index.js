import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

const routes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: () => import("@/views/Home.vue"),
    meta: { title: "首页" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
    meta: { title: "注册" },
  },
  {
    path: "/product/:id",
    name: "ProductDetail",
    component: () => import("@/views/ProductDetail.vue"),
    meta: { title: "商品详情" },
  },
  {
    path: "/publish",
    name: "Publish",
    component: () => import("@/views/Publish.vue"),
    meta: { title: "发布商品", requiresAuth: true },
  },
  {
    path: "/my-products",
    name: "MyProducts",
    component: () => import("@/views/MyProducts.vue"),
    meta: { title: "我的发布", requiresAuth: true },
  },
  {
    path: "/my-favorites",
    name: "MyFavorites",
    component: () => import("@/views/MyFavorites.vue"),
    meta: { title: "我的收藏", requiresAuth: true },
  },
  {
    path: "/my-transactions",
    name: "MyTransactions",
    component: () => import("@/views/MyTransactions.vue"),
    meta: { title: "交易记录", requiresAuth: true },
  },
  {
    path: "/messages",
    name: "Messages",
    component: () => import("@/views/Messages.vue"),
    meta: { title: "我的消息", requiresAuth: true },
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("@/views/Profile.vue"),
    meta: { title: "个人中心", requiresAuth: true },
  },
  {
    path: "/admin",
    name: "Admin",
    component: () => import("@/views/Admin.vue"),
    meta: { title: "管理后台", requiresAuth: true, requiresAdmin: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 校园二手交易平台`
    : "校园二手交易平台";

  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("userRole");

  if (to.meta.requiresAuth && !token) {
    ElMessage.warning("请先登录");
    next("/login");
    return;
  }

  if (to.meta.requiresAdmin && userRole !== "admin") {
    ElMessage.error("无权限访问");
    next("/home");
    return;
  }

  next();
});

export default router;
