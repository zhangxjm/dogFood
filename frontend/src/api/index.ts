import { request } from '@/utils/request'
import type { LoginParams, LoginResult, UserInfo, MenuItem, User, Role, Permission, PageResult } from '@/types'

export const authApi = {
  login(params: LoginParams) {
    return request.post<LoginResult>('/auth/login', params)
  },
  
  logout() {
    return request.post('/auth/logout')
  },
  
  getProfile() {
    return request.get<UserInfo>('/auth/profile')
  },
  
  getMenus() {
    return request.get<MenuItem[]>('/users/menus')
  },
}

export const userApi = {
  getList(params: {
    page?: number
    pageSize?: number
    username?: string
    status?: number
  }) {
    return request.get<PageResult<User>>('/users', { params })
  },
  
  getById(id: number) {
    return request.get<User>(`/users/${id}`)
  },
  
  create(data: Partial<User> & { password?: string; roleIds?: number[] }) {
    return request.post<User>('/users', data)
  },
  
  update(id: number, data: Partial<User> & { roleIds?: number[] }) {
    return request.put<User>(`/users/${id}`, data)
  },
  
  delete(id: number) {
    return request.delete(`/users/${id}`)
  },
  
  resetPassword(id: number, password: string) {
    return request.post(`/users/${id}/reset-password`, { password })
  },
}

export const roleApi = {
  getList(params: {
    page?: number
    pageSize?: number
    name?: string
    status?: number
  }) {
    return request.get<PageResult<Role>>('/roles', { params })
  },
  
  getSimpleList() {
    return request.get<Role[]>('/roles/simple')
  },
  
  getById(id: number) {
    return request.get<Role>(`/roles/${id}`)
  },
  
  create(data: Partial<Role> & { permissionIds?: number[] }) {
    return request.post<Role>('/roles', data)
  },
  
  update(id: number, data: Partial<Role> & { permissionIds?: number[] }) {
    return request.put<Role>(`/roles/${id}`, data)
  },
  
  delete(id: number) {
    return request.delete(`/roles/${id}`)
  },
  
  assignPermissions(id: number, permissionIds: number[]) {
    return request.post<Role>(`/roles/${id}/permissions`, { permissionIds })
  },
}

export const permissionApi = {
  getList(params?: { type?: number }) {
    return request.get<Permission[]>('/permissions', { params })
  },
  
  getTree() {
    return request.get<Permission[]>('/permissions/tree')
  },
  
  getById(id: number) {
    return request.get<Permission>(`/permissions/${id}`)
  },
  
  create(data: Partial<Permission>) {
    return request.post<Permission>('/permissions', data)
  },
  
  update(id: number, data: Partial<Permission>) {
    return request.put<Permission>(`/permissions/${id}`, data)
  },
  
  delete(id: number) {
    return request.delete(`/permissions/${id}`)
  },
}
