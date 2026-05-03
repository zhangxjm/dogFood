import request from '@/utils/request'

export const applicationApi = {
  apply(jobId) {
    return request.post(`/application/apply/${jobId}`)
  },

  listMy(params) {
    return request.get('/application/my/list', { params })
  },

  listReceived(params) {
    return request.get('/application/received/list', { params })
  },

  getById(id) {
    return request.get(`/application/detail/${id}`)
  },

  updateStatus(id, status) {
    return request.put(`/application/status/${id}?status=${status}`)
  }
}
