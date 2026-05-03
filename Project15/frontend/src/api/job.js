import request from '@/utils/request'

export const jobApi = {
  list(params) {
    return request.get('/job/list', { params })
  },

  getById(id) {
    return request.get(`/job/detail/${id}`)
  },

  listByCompany(params) {
    return request.get('/job/company/list', { params })
  },

  publish(data) {
    return request.post('/job/publish', data)
  },

  update(data) {
    return request.put('/job/update', data)
  },

  updateStatus(id, status) {
    return request.put(`/job/status/${id}?status=${status}`)
  }
}
