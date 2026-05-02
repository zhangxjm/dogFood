import { defineStore } from "pinia";
import { loginApi, logoutApi, getUserInfoApi, getMenusApi } from "@/api/auth";
import { getToken, setToken, removeToken } from "@/utils/auth";
import { asyncRoutes, constantRoutes } from "@/router";
import type { RouteRecordRaw } from "vue-router";
import type { LoginParams, UserInfo } from "@/types";

function hasPermission(roles: string[], route: RouteRecordRaw) {
  if (route.meta && (route.meta as any).roles) {
    return roles.some((role) => (route.meta as any).roles.includes(role));
  } else {
    return true;
  }
}

export function filterAsyncRoutes(routes: RouteRecordRaw[], roles: string[]) {
  const res: RouteRecordRaw[] = [];

  routes.forEach((route) => {
    const tmp = { ...route };
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles);
      }
      res.push(tmp);
    }
  });

  return res;
}

export const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken() || "",
    userInfo: {} as UserInfo,
    roles: [] as string[],
    permissions: [] as string[],
    menus: [] as any[],
    routes: [] as RouteRecordRaw[],
  }),

  getters: {
    isLogin: (state) => !!state.token,
    username: (state) => state.userInfo.username || "",
    nickname: (state) => state.userInfo.nickname || "",
    avatar: (state) => state.userInfo.avatar || "",
  },

  actions: {
    async login(loginForm: LoginParams) {
      try {
        const res = await loginApi(loginForm);
        const { token } = res.data;
        this.token = token;
        setToken(token, loginForm.remember);
        return res;
      } catch (error) {
        throw error;
      }
    },

    async getUserInfo() {
      try {
        const res = await getUserInfoApi();
        const { userInfo, roles, permissions } = res.data;
        this.userInfo = userInfo;
        this.roles = roles;
        this.permissions = permissions;
        return res;
      } catch (error) {
        throw error;
      }
    },

    async getMenus() {
      try {
        const res = await getMenusApi();
        this.menus = res.data;
        return res;
      } catch (error) {
        throw error;
      }
    },

    async generateRoutes() {
      let accessedRoutes;
      if (this.roles.includes("SUPER_ADMIN")) {
        accessedRoutes = asyncRoutes || [];
      } else {
        accessedRoutes = filterAsyncRoutes(asyncRoutes, this.roles);
      }
      this.routes = accessedRoutes;
      return accessedRoutes;
    },

    async logout() {
      try {
        await logoutApi();
      } finally {
        this.resetToken();
      }
    },

    resetToken() {
      this.token = "";
      this.roles = [];
      this.permissions = [];
      this.userInfo = {} as UserInfo;
      this.menus = [];
      this.routes = [];
      removeToken();
    },
  },
});
