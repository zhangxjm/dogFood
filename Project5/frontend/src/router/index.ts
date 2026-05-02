import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import NProgress from "nprogress";
import { getToken } from "@/utils/auth";
import { useUserStore } from "@/store/modules/user";

const Layout = () => import("@/views/layout/index.vue");
const Login = () => import("@/views/login/index.vue");
const UserManage = () => import("@/views/system/user/index.vue");
const RoleManage = () => import("@/views/system/role/index.vue");
const PermissionManage = () => import("@/views/system/permission/index.vue");

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: { title: "登录", hidden: true },
  },
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },
  {
    path: "/404",
    component: () => import("@/views/error/404.vue"),
    meta: { hidden: true },
  },
  {
    path: "/401",
    component: () => import("@/views/error/401.vue"),
    meta: { hidden: true },
  },
];

export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        meta: { title: "首页", icon: "HomeFilled" },
      },
    ],
  },
  {
    path: "/system",
    component: Layout,
    name: "System",
    meta: { title: "系统管理", icon: "Setting" },
    children: [
      {
        path: "user",
        name: "UserManage",
        component: UserManage,
        meta: { title: "用户管理", icon: "User" },
      },
      {
        path: "role",
        name: "RoleManage",
        component: RoleManage,
        meta: { title: "角色管理", icon: "Avatar" },
      },
      {
        path: "permission",
        name: "PermissionManage",
        component: PermissionManage,
        meta: { title: "权限管理", icon: "Lock" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

const whiteList = ["/login", "/404", "/401"];

router.beforeEach(async (to, _from, next) => {
  NProgress.start();

  const hasToken = getToken();

  if (hasToken) {
    if (to.path === "/login") {
      next({ path: "/" });
    } else {
      const userStore = useUserStore();
      const hasRoles = userStore.roles && userStore.roles.length > 0;

      if (hasRoles) {
        next();
      } else {
        try {
          await userStore.getUserInfo();
          const accessRoutes = await userStore.generateRoutes();

          accessRoutes.forEach((route) => {
            router.addRoute(route);
          });

          next({ ...to, replace: true });
        } catch (error) {
          await userStore.resetToken();
          next(`/login?redirect=${to.path}`);
        }
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next();
    } else {
      next(`/login?redirect=${to.path}`);
    }
  }
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
