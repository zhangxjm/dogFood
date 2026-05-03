import { defineStore } from "pinia";
import userApi from "@/api/user";

export const useUserStore = defineStore("user", {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem("token") || "",
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) =>
      state.userInfo?.role === "super_admin" ||
      state.userInfo?.role === "admin",
  },

  actions: {
    async login(loginForm) {
      const res = await userApi.login(loginForm);
      this.token = res.data.token;
      localStorage.setItem("token", res.data.token);
      await this.getUserInfo();
      return res;
    },

    async register(registerForm) {
      return await userApi.register(registerForm);
    },

    async getUserInfo() {
      if (!this.token) return null;
      const res = await userApi.getInfo();
      this.userInfo = res.data;
      return res;
    },

    async logout() {
      try {
        await userApi.logout();
      } catch (e) {
        console.error("Logout api error:", e);
      } finally {
        this.token = "";
        this.userInfo = null;
        localStorage.removeItem("token");
      }
    },
  },
});
