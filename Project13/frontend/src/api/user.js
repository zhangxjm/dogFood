import request from './request'

export function login(data) {
  return request.post('/user/login', data)
}

export function register(data) {
  return request.post('/user/register', data)
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function getUserPage(params) {
  return request.get('/user/page', { params })
}

export function saveUser(data) {
  return request.post('/user/save', data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}
