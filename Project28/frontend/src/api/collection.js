import request from '@/utils/request'

export const collectionApi = {
  getList() {
    return request({
      url: '/collection/collections/',
      method: 'get'
    })
  },
  
  create(itemId) {
    return request({
      url: '/collection/collections/',
      method: 'post',
      data: { item: itemId }
    })
  },
  
  delete(id) {
    return request({
      url: `/collection/collections/${id}/`,
      method: 'delete'
    })
  },
  
  toggle(itemId) {
    return request({
      url: '/collection/collections/toggle/',
      method: 'post',
      data: { item_id: itemId }
    })
  },
  
  check(itemId) {
    return request({
      url: '/collection/collections/check/',
      method: 'get',
      params: { item_id: itemId }
    })
  }
}
