/**
 * Axios 请求封装
 * 包含请求拦截器、响应拦截器、错误处理
 */
import axios from 'axios';
import { message } from 'antd';

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

/**
 * 清除认证信息
 */
const clearAuth = () => {
  // 清除 localStorage 中的用户信息
  localStorage.removeItem('user-storage');
  localStorage.removeItem('token');
  
  // 发送自定义事件通知 Store 更新
  window.dispatchEvent(new CustomEvent('auth:logout'));
};

/**
 * 请求拦截器
 * 在发送请求前添加 Token
 */
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token
    let token = localStorage.getItem('token');
    
    // 如果没有，尝试从 user-storage 中获取
    if (!token) {
      try {
        const userStorage = localStorage.getItem('user-storage');
        if (userStorage) {
          const parsed = JSON.parse(userStorage);
          token = parsed.state?.token;
        }
      } catch (e) {
        // 忽略解析错误
      }
    }
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

/**
 * 响应拦截器
 * 统一处理响应数据和错误
 */
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    
    // 业务成功
    if (res.success) {
      return res.data;
    }
    
    // 业务失败
    message.error(res.message || '请求失败');
    return Promise.reject(new Error(res.message || '请求失败'));
  },
  (error) => {
    const { response } = error;
    
    if (response) {
      const { status, data } = response;
      
      switch (status) {
        case 400:
          message.error(data?.message || '请求参数错误');
          break;
          
        case 401:
          // 未授权，清除 token 并跳转到登录页
          message.error(data?.message || '登录已过期，请重新登录');
          clearAuth();
          
          // 如果当前不是登录页，跳转到登录页
          if (window.location.pathname !== '/login') {
            window.location.href = '/login';
          }
          break;
          
        case 403:
          message.error(data?.message || '无权限访问');
          break;
          
        case 404:
          message.error(data?.message || '资源不存在');
          break;
          
        case 409:
          message.error(data?.message || '资源冲突');
          break;
          
        case 500:
          message.error(data?.message || '服务器内部错误');
          break;
          
        default:
          message.error(data?.message || `请求失败 (${status})`);
      }
    } else if (error.code === 'ECONNABORTED') {
      message.error('请求超时，请稍后重试');
    } else if (error.message === 'Network Error') {
      message.error('网络错误，请检查网络连接');
    } else {
      message.error(error.message || '未知错误');
    }
    
    return Promise.reject(error);
  }
);

/**
 * GET 请求
 * @param {string} url - 请求地址
 * @param {Object} params - 查询参数
 * @param {Object} config - 额外配置
 */
export const get = (url, params = {}, config = {}) => {
  return request({
    method: 'get',
    url,
    params,
    ...config
  });
};

/**
 * POST 请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求体数据
 * @param {Object} config - 额外配置
 */
export const post = (url, data = {}, config = {}) => {
  return request({
    method: 'post',
    url,
    data,
    ...config
  });
};

/**
 * PUT 请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求体数据
 * @param {Object} config - 额外配置
 */
export const put = (url, data = {}, config = {}) => {
  return request({
    method: 'put',
    url,
    data,
    ...config
  });
};

/**
 * DELETE 请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求体数据
 * @param {Object} config - 额外配置
 */
export const del = (url, data = {}, config = {}) => {
  return request({
    method: 'delete',
    url,
    data,
    ...config
  });
};

export default request;
