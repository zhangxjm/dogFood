import request from '@/utils/request'

export function getOrders(params) {
  return request.get('/admin/orders', { params })
}

export function getOrder(id) {
  return request.get(`/admin/orders/${id}`)
}

export function deliveryOrder(id) {
  return request.post(`/admin/orders/${id}/delivery`)
}
