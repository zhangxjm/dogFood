// API配置
// 注意：真机运行时需要将localhost替换为你的电脑IP地址
// 例如：http://192.168.1.100:5000/api

export const API_BASE_URL = __DEV__
  ? "http://localhost:5000/api" // 模拟器/开发环境
  : "http://your-server-ip:5000/api"; // 生产环境

// API端点
export const API_ENDPOINTS = {
  // 用户相关
  REGISTER: `${API_BASE_URL}/users/register`,
  LOGIN: `${API_BASE_URL}/users/login`,
  PROFILE: `${API_BASE_URL}/users/profile`,
  CHANGE_PASSWORD: `${API_BASE_URL}/users/password`,

  // 数据相关
  ITEMS: `${API_BASE_URL}/items`,
  ITEM_CATEGORIES: `${API_BASE_URL}/items/categories`,
  MY_ITEMS: `${API_BASE_URL}/items/my`,

  // 健康检查
  HEALTH: `${API_BASE_URL}/health`,
};

// 网络请求超时时间（毫秒）
export const REQUEST_TIMEOUT = 30000;

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE: 1,
  DEFAULT_LIMIT: 10,
};
