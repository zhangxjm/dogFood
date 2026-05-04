import request from '@/utils/request'

export const claimApi = {
  getList() {
    return request({
      url: '/claim/claims/',
      method: 'get'
    })
  },
  
  getMyClaims() {
    return request({
      url: '/claim/claims/my_claims/',
      method: 'get'
    })
  },
  
  getReceived() {
    return request({
      url: '/claim/claims/received/',
      method: 'get'
    })
  },
  
  create(data) {
    return request({
      url: '/claim/claims/',
      method: 'post',
      data
    })
  },
  
  approve(id) {
    return request({
      url: `/claim/claims/${id}/approve/`,
      method: 'post'
    })
  },
  
  reject(id, reason) {
    return request({
      url: `/claim/claims/${id}/reject/`,
      method: 'post',
      data: { reason }
    })
  }
}
