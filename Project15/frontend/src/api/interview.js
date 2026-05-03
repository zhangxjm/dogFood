import request from '@/utils/request'

export const interviewApi = {
  create(data) {
    return request.post('/interview/create', data)
  },

  listByCompany(params) {
    return request.get('/interview/company/list', { params })
  },

  listByUser(params) {
    return request.get('/interview/my/list', { params })
  },

  getById(id) {
    return request.get(`/interview/detail/${id}`)
  },

  updateStatus(id, status) {
    return request.put(`/interview/status/${id}?status=${status}`)
  }
}
