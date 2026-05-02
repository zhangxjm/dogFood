import React, { createContext, useContext, useState, useEffect } from "react";
import * as SecureStore from "expo-secure-store";
import apiClient from "../services/api";
import { API_ENDPOINTS } from "../config/api";

// 创建Context
const AuthContext = createContext();

// 自定义Hook
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

// Token 存储键名
const TOKEN_KEY = "auth_token";
const USER_KEY = "user_data";

// Provider 组件
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // 初始化时检查登录状态
  useEffect(() => {
    loadStoredAuth();
  }, []);

  // 从安全存储加载认证信息
  const loadStoredAuth = async () => {
    try {
      const storedToken = await SecureStore.getItemAsync(TOKEN_KEY);
      const storedUser = await SecureStore.getItemAsync(USER_KEY);

      if (storedToken && storedUser) {
        const parsedUser = JSON.parse(storedUser);
        setToken(storedToken);
        setUser(parsedUser);
        setIsAuthenticated(true);

        // 验证token是否有效
        await validateToken(storedToken);
      }
    } catch (error) {
      console.error("加载认证信息失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 验证token有效性
  const validateToken = async (authToken) => {
    try {
      const response = await apiClient.get(API_ENDPOINTS.PROFILE, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (response.data && response.data.success) {
        setUser(response.data.data);
        // 更新存储的用户信息
        await SecureStore.setItemAsync(
          USER_KEY,
          JSON.stringify(response.data.data),
        );
      } else {
        // token无效，清除登录状态
        await logout();
      }
    } catch (error) {
      console.error("Token验证失败:", error);
      // 网络错误或token过期，保持登录状态但可以在后续操作中处理
    }
  };

  // 登录
  const login = async (email, password) => {
    try {
      const response = await apiClient.post(API_ENDPOINTS.LOGIN, {
        email,
        password,
      });

      if (response.data && response.data.success) {
        const { token, user } = response.data.data;

        // 保存到状态
        setToken(token);
        setUser(user);
        setIsAuthenticated(true);

        // 保存到安全存储
        await SecureStore.setItemAsync(TOKEN_KEY, token);
        await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));

        return { success: true, message: response.data.message };
      } else {
        return {
          success: false,
          message: response.data?.message || "登录失败",
        };
      }
    } catch (error) {
      console.error("登录错误:", error);
      const message = error.response?.data?.message || "网络错误，请稍后重试";
      return { success: false, message };
    }
  };

  // 注册
  const register = async (username, email, password) => {
    try {
      const response = await apiClient.post(API_ENDPOINTS.REGISTER, {
        username,
        email,
        password,
      });

      if (response.data && response.data.success) {
        const { token, user } = response.data.data;

        // 保存到状态
        setToken(token);
        setUser(user);
        setIsAuthenticated(true);

        // 保存到安全存储
        await SecureStore.setItemAsync(TOKEN_KEY, token);
        await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));

        return { success: true, message: response.data.message };
      } else {
        return {
          success: false,
          message: response.data?.message || "注册失败",
        };
      }
    } catch (error) {
      console.error("注册错误:", error);
      const message = error.response?.data?.message || "网络错误，请稍后重试";
      return { success: false, message };
    }
  };

  // 退出登录
  const logout = async () => {
    try {
      // 清除安全存储
      await SecureStore.deleteItemAsync(TOKEN_KEY);
      await SecureStore.deleteItemAsync(USER_KEY);

      // 清除状态
      setToken(null);
      setUser(null);
      setIsAuthenticated(false);

      return { success: true };
    } catch (error) {
      console.error("退出登录错误:", error);
      return { success: false, message: "退出登录失败" };
    }
  };

  // 更新用户信息
  const updateUser = async (userData) => {
    try {
      const response = await apiClient.put(API_ENDPOINTS.PROFILE, userData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.data && response.data.success) {
        const updatedUser = response.data.data;

        // 更新状态
        setUser(updatedUser);

        // 更新安全存储
        await SecureStore.setItemAsync(USER_KEY, JSON.stringify(updatedUser));

        return {
          success: true,
          message: response.data.message,
          data: updatedUser,
        };
      } else {
        return {
          success: false,
          message: response.data?.message || "更新失败",
        };
      }
    } catch (error) {
      console.error("更新用户信息错误:", error);
      const message = error.response?.data?.message || "网络错误，请稍后重试";
      return { success: false, message };
    }
  };

  // 修改密码
  const changePassword = async (oldPassword, newPassword) => {
    try {
      const response = await apiClient.put(
        API_ENDPOINTS.CHANGE_PASSWORD,
        {
          oldPassword,
          newPassword,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );

      if (response.data && response.data.success) {
        return { success: true, message: response.data.message };
      } else {
        return {
          success: false,
          message: response.data?.message || "修改密码失败",
        };
      }
    } catch (error) {
      console.error("修改密码错误:", error);
      const message = error.response?.data?.message || "网络错误，请稍后重试";
      return { success: false, message };
    }
  };

  // Context值
  const value = {
    user,
    token,
    loading,
    isAuthenticated,
    login,
    register,
    logout,
    updateUser,
    changePassword,
    getCurrentUser: validateToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
