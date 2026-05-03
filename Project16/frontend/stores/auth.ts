import { defineStore } from "pinia";

export interface User {
  id: string;
  email: string;
  username: string;
  is_designer: boolean;
  avatar: string | null;
  bio: string | null;
  created_at: string;
  updated_at: string | null;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    user: null,
    token: null,
    isAuthenticated: false,
    loading: false,
  }),

  persist: {
    pick: ["token", "user"],
  },

  getters: {
    isDesigner: (state): boolean => state.user?.is_designer ?? false,
    getToken: (state): string | null => state.token,
    getUser: (state): User | null => state.user,
  },

  actions: {
    setToken(token: string) {
      this.token = token;
    },

    setUser(user: User) {
      this.user = user;
      this.isAuthenticated = true;
    },

    async login(email: string, password: string) {
      this.loading = true;
      try {
        const config = useRuntimeConfig();
        const { data, error } = await useFetch<{
          access_token: string;
          user: User;
        }>(`${config.public.apiBase}/api/auth/login-json`, {
          method: "POST",
          body: { email, password },
        });

        if (error.value) {
          throw new Error(error.value.message || "登录失败");
        }

        if (data.value) {
          this.setToken(data.value.access_token);
          this.setUser(data.value.user);
        }

        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error instanceof Error ? error.message : "登录失败",
        };
      } finally {
        this.loading = false;
      }
    },

    async register(userData: {
      email: string;
      password: string;
      username: string;
      is_designer?: boolean;
    }) {
      this.loading = true;
      try {
        const config = useRuntimeConfig();
        const { data, error } = await useFetch<{
          access_token: string;
          user: User;
        }>(`${config.public.apiBase}/api/auth/register`, {
          method: "POST",
          body: userData,
        });

        if (error.value) {
          throw new Error(error.value.message || "注册失败");
        }

        if (data.value) {
          this.setToken(data.value.access_token);
          this.setUser(data.value.user);
        }

        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error instanceof Error ? error.message : "注册失败",
        };
      } finally {
        this.loading = false;
      }
    },

    async fetchCurrentUser() {
      if (!this.token) return;

      try {
        const config = useRuntimeConfig();
        const { data, error } = await useFetch<User>(
          `${config.public.apiBase}/api/auth/me`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          },
        );

        if (error.value) {
          this.logout();
          return;
        }

        if (data.value) {
          this.setUser(data.value);
        }
      } catch {
        this.logout();
      }
    },

    async updateProfile(updates: Partial<User>) {
      if (!this.token) return;

      try {
        const config = useRuntimeConfig();
        const { data, error } = await useFetch<User>(
          `${config.public.apiBase}/api/auth/me`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
            body: updates,
          },
        );

        if (error.value) {
          throw new Error(error.value.message || "更新失败");
        }

        if (data.value) {
          this.setUser(data.value);
        }

        return { success: true };
      } catch (error) {
        return {
          success: false,
          error: error instanceof Error ? error.message : "更新失败",
        };
      }
    },

    logout() {
      this.user = null;
      this.token = null;
      this.isAuthenticated = false;
      const router = useRouter();
      router.push("/");
    },

    initializeAuth() {
      if (this.token) {
        this.isAuthenticated = true;
        this.fetchCurrentUser();
      }
    },
  },
});
