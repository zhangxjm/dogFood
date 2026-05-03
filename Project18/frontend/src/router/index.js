import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

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
    meta: { title: "登录", guest: true },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
    meta: { title: "注册", guest: true },
  },
  {
    path: "/product/:id",
    name: "ProductDetail",
    component: () => import("@/views/ProductDetail.vue"),
    meta: { title: "商品详情" },
  },
  {
    path: "/seckill",
    name: "SeckillList",
    component: () => import("@/views/SeckillList.vue"),
    meta: { title: "限时秒杀" },
  },
  {
    path: "/seckill/:id",
    name: "SeckillDetail",
    component: () => import("@/views/SeckillDetail.vue"),
    meta: { title: "秒杀详情" },
  },
  {
    path: "/order",
    name: "OrderList",
    component: () => import("@/views/OrderList.vue"),
    meta: { title: "我的订单", requiresAuth: true },
  },
  {
    path: "/order/:id",
    name: "OrderDetail",
    component: () => import("@/views/OrderDetail.vue"),
    meta: { title: "订单详情", requiresAuth: true },
  },
  {
    path: "/cart",
    name: "Cart",
    component: () => import("@/views/Cart.vue"),
    meta: { title: "购物车", requiresAuth: true },
  },
  {
    path: "/user",
    name: "UserCenter",
    component: () => import("@/views/UserCenter.vue"),
    meta: { title: "个人中心", requiresAuth: true },
  },
  {
    path: "/admin",
    name: "AdminLayout",
    component: () => import("@/views/admin/Layout.vue"),
    meta: { title: "管理后台", requiresAuth: true, requiresAdmin: true },
    redirect: "/admin/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/admin/Dashboard.vue"),
        meta: { title: "数据统计" },
      },
      {
        path: "product",
        name: "AdminProduct",
        component: () => import("@/views/admin/Product.vue"),
        meta: { title: "商品管理" },
      },
      {
        path: "seckill",
        name: "AdminSeckill",
        component: () => import("@/views/admin/Seckill.vue"),
        meta: { title: "秒杀管理" },
      },
      {
        path: "order",
        name: "AdminOrder",
        component: () => import("@/views/admin/Order.vue"),
        meta: { title: "订单管理" },
      },
      {
        path: "user",
        name: "AdminUser",
        component: () => import("@/views/admin/User.vue"),
        meta: { title: "用户管理" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 秒杀电商` : "秒杀电商";

  const userStore = useUserStore();
  const token = localStorage.getItem("token");

  if (token && !userStore.userInfo) {
    try {
      await userStore.getUserInfo();
    } catch (e) {
      localStorage.removeItem("token");
    }
  }

  if (to.meta.requiresAuth && !token) {
    ElMessage.warning("请先登录");
    next({ name: "Login", query: { redirect: to.fullPath } });
    return;
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    ElMessage.error("无权限访问");
    next({ name: "Home" });
    return;
  }

  if (to.meta.guest && token) {
    next({ name: "Home" });
    return;
  }

  next();
});

export default router;
