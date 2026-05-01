import request from '@/utils/request'

export function login(data) {
  return request.post('/admin/auth/login', data)
}

export function getInfo() {
  return request.get('/admin/info')
}
