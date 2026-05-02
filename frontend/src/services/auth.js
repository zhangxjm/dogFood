/**
 * 认证相关 API 服务
 */
import { post, get, put } from '@/utils/request';

/**
 * 用户注册
 * @param {Object} data - 注册数据
 * @param {string} data.username - 用户名
 * @param {string} data.email - 邮箱
 * @param {string} data.password - 密码
 * @param {string} [data.realName] - 真实姓名
 */
export const register = (data) => {
  return post('/api/auth/register', data);
};

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 */
export const login = (data) => {
  return post('/api/auth/login', data);
};

/**
 * 用户登出
 */
export const logout = () => {
  return post('/api/auth/logout');
};

/**
 * 获取当前登录用户信息
 */
export const getCurrentUser = () => {
  return get('/api/auth/me');
};

/**
 * 修改密码
 * @param {Object} data - 密码数据
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 */
export const changePassword = (data) => {
  return put('/api/auth/change-password', data);
};

export default {
  register,
  login,
  logout,
  getCurrentUser,
  changePassword
};
