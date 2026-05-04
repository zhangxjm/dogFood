import request from '@/utils/request'

export const itemApi = {
  getCategories() {
    return request({
      url: '/item/categories/',
      method: 'get'
    })
  },
  
  createCategory(data) {
    return request({
      url: '/item/categories/',
      method: 'post',
      data
    })
  },
  
  updateCategory(id, data) {
    return request({
      url: `/item/categories/${id}/`,
      method: 'put',
      data
    })
  },
  
  deleteCategory(id) {
    return request({
      url: `/item/categories/${id}/`,
      method: 'delete'
    })
  },
  
  getList(params) {
    return request({
      url: '/item/items/',
      method: 'get',
      params
    })
  },
  
  getDetail(id) {
    return request({
      url: `/item/items/${id}/`,
      method: 'get'
    })
  },
  
  create(data) {
    return request({
      url: '/item/items/',
      method: 'post',
      data
    })
  },
  
  update(id, data) {
    return request({
      url: `/item/items/${id}/`,
      method: 'put',
      data
    })
  },
  
  delete(id) {
    return request({
      url: `/item/items/${id}/`,
      method: 'delete'
    })
  },
  
  getMyItems(params) {
    return request({
      url: '/item/items/my_items/',
      method: 'get',
      params
    })
  },
  
  getPendingItems(params) {
    return request({
      url: '/item/items/pending/',
      method: 'get',
      params
    })
  },
  
  approve(id, note) {
    return request({
      url: `/item/items/${id}/approve/`,
      method: 'post',
      data: { note }
    })
  },
  
  reject(id, note) {
    return request({
      url: `/item/items/${id}/reject/`,
      method: 'post',
      data: { note }
    })
  },
  
  markResolved(id) {
    return request({
      url: `/item/items/${id}/mark_resolved/`,
      method: 'post'
    })
  }
}
