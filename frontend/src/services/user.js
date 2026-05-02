/**
 * 用户管理相关 API 服务
 */
import { get, post, put, del } from '@/utils/request';

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} [params.page=1] - 页码
 * @param {number} [params.pageSize=10] - 每页数量
 * @param {string} [params.search] - 搜索关键词
 * @param {string} [params.role] - 角色筛选
 * @param {string} [params.status] - 状态筛选
 */
export const getUsers = (params = {}) => {
  return get('/api/users', params);
};

/**
 * 根据 ID 获取用户信息
 * @param {string} id - 用户 ID
 */
export const getUserById = (id) => {
  return get(`/api/users/${id}`);
};

/**
 * 创建用户
 * @param {Object} data - 用户数据
 * @param {string} data.username - 用户名
 * @param {string} data.email - 邮箱
 * @param {string} data.password - 密码
 * @param {string} [data.realName] - 真实姓名
 * @param {string} [data.role] - 角色
 * @param {boolean} [data.status] - 状态
 */
export const createUser = (data) => {
  return post('/api/users', data);
};

/**
 * 更新用户
 * @param {string} id - 用户 ID
 * @param {Object} data - 更新数据
 */
export const updateUser = (id, data) => {
  return put(`/api/users/${id}`, data);
};

/**
 * 删除单个用户
 * @param {string} id - 用户 ID
 */
export const deleteUser = (id) => {
  return del(`/api/users/${id}`);
};

/**
 * 批量删除用户
 * @param {string[]} ids - 用户 ID 列表
 */
export const deleteUsers = (ids) => {
  return del('/api/users', { ids });
};

/**
 * 更新用户状态
 * @param {string} id - 用户 ID
 * @param {boolean} status - 新状态
 */
export const updateUserStatus = (id, status) => {
  return put(`/api/users/${id}/status`, { status });
};

export default {
  getUsers,
  getUserById,
  createUser,
  updateUser,
  deleteUser,
  deleteUsers,
  updateUserStatus
};
