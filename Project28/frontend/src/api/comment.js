import request from '@/utils/request'

export const commentApi = {
  getList(itemId) {
    return request({
      url: '/comment/comments/',
      method: 'get',
      params: { item: itemId }
    })
  },
  
  create(data) {
    return request({
      url: '/comment/comments/',
      method: 'post',
      data
    })
  },
  
  delete(id) {
    return request({
      url: `/comment/comments/${id}/`,
      method: 'delete'
    })
  },
  
  getViolations() {
    return request({
      url: '/comment/comments/violations/',
      method: 'get'
    })
  },
  
  markViolation(id, note) {
    return request({
      url: `/comment/comments/${id}/mark_violation/`,
      method: 'post',
      data: { note }
    })
  },
  
  unmarkViolation(id) {
    return request({
      url: `/comment/comments/${id}/unmark_violation/`,
      method: 'post'
    })
  }
}
