import axios from 'axios';
import { message } from 'antd';
import { useAuthStore } from '@/store/authStore';

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const { accessToken } = useAuthStore.getState();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          if (!originalRequest._retry) {
            originalRequest._retry = true;
            const { refreshToken, logout } = useAuthStore.getState();
            
            if (refreshToken) {
              try {
                const response = await axios.post('/api/auth/token/refresh/', {
                  refresh: refreshToken,
                });
                const newAccessToken = response.data.access;
                useAuthStore.getState().refreshAccessToken(newAccessToken);
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return api(originalRequest);
              } catch (refreshError) {
                logout();
                message.error('登录已过期，请重新登录');
                window.location.href = '/login';
                return Promise.reject(refreshError);
              }
            } else {
              logout();
              window.location.href = '/login';
            }
          }
          break;
        case 403:
          message.error('权限不足');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器错误，请稍后重试');
          break;
        default:
          if (error.response.data && error.response.data.detail) {
            message.error(error.response.data.detail);
          } else if (error.response.data && typeof error.response.data === 'object') {
            const errors = Object.values(error.response.data).flat();
            message.error(errors[0] as string);
          }
      }
    } else {
      message.error('网络错误，请检查网络连接');
    }
    
    return Promise.reject(error);
  }
);

export default api;
