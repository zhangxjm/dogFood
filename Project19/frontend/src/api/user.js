import request from './index'

export const login = (data) => {
  return request.post('/user/login', data)
}

export const register = (data) => {
  return request.post('/user/register', data)
}

export const getUserInfo = () => {
  return request.get('/user/info')
}

export const getUserList = (params) => {
  return request.get('/user/list', { params })
}

export const getUserById = (id) => {
  return request.get(`/user/${id}`)
}

export const updateUser = (data) => {
  return request.post('/user/update', data)
}

export const updateUserStatus = (id, status) => {
  return request.post(`/user/updateStatus/${id}`, null, { params: { status } })
}

export const updateUserRole = (id, role) => {
  return request.post(`/user/updateRole/${id}`, null, { params: { role } })
}

export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}
