import request from '@/utils/request'

export const dataApi = {
  getList(params = {}) {
    return request.get('/data', { params })
  },
  
  getById(id) {
    return request.get(`/data/${id}`)
  },
  
  create(data) {
    return request.post('/data', data)
  },
  
  update(id, data) {
    return request.put(`/data/${id}`, data)
  },
  
  delete(id) {
    return request.delete(`/data/${id}`)
  },
  
  exportCsv(params = {}) {
    return request.get('/data/export/csv', { 
      params,
      responseType: 'blob'
    })
  },
  
  getCategories() {
    return request.get('/data/categories')
  },
  
  getStats() {
    return request.get('/data/stats')
  }
}
