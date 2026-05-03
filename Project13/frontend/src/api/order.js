import request from './request'

export function createOrder(data) {
  return request.post('/order/create', data)
}

export function getOrderById(id) {
  return request.get(`/order/${id}`)
}

export function getOrderByOrderNo(orderNo) {
  return request.get(`/order/orderNo/${orderNo}`)
}

export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function getOrdersByUserId(userId) {
  return request.get(`/order/user/${userId}`)
}

export function updateOrderStatus(orderId, status) {
  return request.put(`/order/status/${orderId}`, null, { params: { status } })
}

export function reviewOrder(orderId, approved) {
  return request.post(`/order/review/${orderId}`, null, { params: { approved } })
}

export function saveOrder(data) {
  return request.post('/order/save', data)
}

export function deleteOrder(id) {
  return request.delete(`/order/${id}`)
}
