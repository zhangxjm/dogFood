import api from './index'

export const login = (data) => {
  return api.post('/auth/login', data)
}

export const register = (data) => {
  return api.post('/auth/register', data)
}

export const getCurrentUser = () => {
  return api.get('/auth/me')
}

export const getUsers = () => {
  return api.get('/auth/users')
}

export const updateUser = (userId, data) => {
  return api.put(`/auth/users/${userId}`, data)
}
