import request from '@/utils/request'

export const companyApi = {
  list(params) {
    return request.get('/company/list', { params })
  },

  getMyCompany() {
    return request.get('/company/my')
  },

  getById(id) {
    return request.get(`/company/detail/${id}`)
  },

  save(data) {
    return request.post('/company/save', data)
  },

  audit(id, status) {
    return request.put(`/company/audit/${id}?status=${status}`)
  }
}
