export interface LoginParams {
  username: string;
  password: string;
  remember?: boolean;
}

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  email: string;
  phone: string;
}

export interface LoginResult {
  token: string;
  userInfo: UserInfo;
  roles: string[];
  permissions: string[];
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResult<T = any> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface User {
  id: number;
  username: string;
  nickname: string;
  email: string;
  phone: string;
  avatar: string;
  status: number;
  roles?: Role[];
  createTime: string;
  updateTime: string;
}

export interface Role {
  id: number;
  name: string;
  code: string;
  description: string;
  status: number;
  sort: number;
  permissions?: Permission[];
  createTime: string;
  updateTime: string;
}

export interface Permission {
  id: number;
  name: string;
  code: string;
  type: number;
  parentId: number;
  path: string;
  component: string;
  icon: string;
  sort: number;
  status: number;
  children?: Permission[];
}

export interface MenuItem {
  id: number;
  path: string;
  name: string;
  component: string;
  meta: {
    title: string;
    icon: string;
  };
  children?: MenuItem[];
}
