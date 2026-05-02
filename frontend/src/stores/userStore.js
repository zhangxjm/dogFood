/**
 * 用户状态管理
 * 管理用户登录状态、用户信息、Token 等
 */
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import * as api from '@/services/auth';

/**
 * 用户状态类型定义
 * @typedef {Object} UserState
 * @property {Object|null} user - 用户信息
 * @property {string|null} token - JWT Token
 * @property {boolean} isLoading - 加载状态
 * @property {Function} login - 登录
 * @property {Function} register - 注册
 * @property {Function} logout - 登出
 * @property {Function} fetchCurrentUser - 获取当前用户信息
 * @property {Function} updateUser - 更新用户信息
 */

/**
 * 用户 Store
 * 使用 persist 中间件将状态持久化到 localStorage
 */
export const useUserStore = create(
  persist(
    (set, get) => ({
      // 状态
      user: null,
      token: null,
      isLoading: false,

      /**
       * 登录
       * @param {string} username - 用户名
       * @param {string} password - 密码
       */
      login: async (username, password) => {
        set({ isLoading: true });
        
        try {
          const result = await api.login({ username, password });
          
          set({
            user: result.user,
            token: result.token,
            isLoading: false
          });
          
          return result;
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      /**
       * 注册
       * @param {Object} data - 注册数据
       * @param {string} data.username - 用户名
       * @param {string} data.email - 邮箱
       * @param {string} data.password - 密码
       * @param {string} [data.realName] - 真实姓名
       */
      register: async (data) => {
        set({ isLoading: true });
        
        try {
          const result = await api.register(data);
          
          set({
            user: result.user,
            token: result.token,
            isLoading: false
          });
          
          return result;
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      /**
       * 登出
       */
      logout: () => {
        set({
          user: null,
          token: null,
          isLoading: false
        });
        
        // 清除 localStorage (由 persist 处理)
      },

      /**
       * 获取当前登录用户信息
       */
      fetchCurrentUser: async () => {
        const { token } = get();
        
        if (!token) {
          return null;
        }
        
        set({ isLoading: true });
        
        try {
          const user = await api.getCurrentUser();
          
          set({
            user,
            isLoading: false
          });
          
          return user;
        } catch (error) {
          set({ isLoading: false });
          
          // Token 无效，清除状态
          if (error.response?.status === 401) {
            set({ user: null, token: null });
          }
          
          throw error;
        }
      },

      /**
       * 更新用户信息
       * @param {Object} userData - 新的用户信息
       */
      updateUser: (userData) => {
        set({ user: userData });
      },

      /**
       * 检查是否已登录
       * @returns {boolean}
       */
      isLoggedIn: () => {
        return !!get().token;
      },

      /**
       * 检查是否为管理员
       * @returns {boolean}
       */
      isAdmin: () => {
        const { user } = get();
        return user?.role === 'admin';
      }
    }),
    {
      name: 'user-storage',
      partialize: (state) => ({
        token: state.token,
        user: state.user
      })
    }
  )
);

export default useUserStore;
