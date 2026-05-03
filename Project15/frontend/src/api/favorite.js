import request from '@/utils/request'

export const favoriteApi = {
  toggle(jobId) {
    return request.post(`/favorite/toggle/${jobId}`)
  },

  check(jobId) {
    return request.get(`/favorite/check/${jobId}`)
  },

  list(params) {
    return request.get('/favorite/list', { params })
  }
}
