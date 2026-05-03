import request from '@/utils/request'

export const userApi = {
  login(data) {
    return request.post('/user/login', data)
  },

  register(data) {
    return request.post('/user/register', data)
  },

  getInfo() {
    return request.get('/user/info')
  },

  list(params) {
    return request.get('/user/list', { params })
  },

  updateStatus(id, status) {
    return request.put(`/user/status/${id}?status=${status}`)
  }
}
