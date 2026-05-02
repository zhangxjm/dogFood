export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface User {
  id: number
  username: string
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  status: number
  roles?: Role[]
  createdAt: string
  updatedAt: string
}

export interface Role {
  id: number
  name: string
  code: string
  description?: string
  status: number
  permissions?: Permission[]
}

export interface Permission {
  id: number
  name: string
  code: string
  type: number
  parentId: number
  path?: string
  component?: string
  icon?: string
  sort: number
  status: number
  children?: Permission[]
}

export interface MenuItem {
  id: number
  name: string
  path: string
  component?: string
  icon?: string
  sort: number
  children?: MenuItem[]
}

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  accessToken: string
  userInfo: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  avatar?: string
  email?: string
  phone?: string
  roles: string[]
  permissions: string[]
}
