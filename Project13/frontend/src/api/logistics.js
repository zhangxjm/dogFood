import request from './request'

export function initLogistics(orderId) {
  return request.post(`/logistics/init/${orderId}`)
}

export function updateLogistics(data) {
  return request.post('/logistics/update', data)
}

export function getLogisticsByOrderId(orderId) {
  return request.get(`/logistics/order/${orderId}`)
}

export function getLogisticsTracks(orderId) {
  return request.get(`/logistics/track/${orderId}`)
}

export function getLogisticsPage(params) {
  return request.get('/logistics/page', { params })
}

export function saveLogistics(data) {
  return request.post('/logistics/save', data)
}

export function deleteLogistics(id) {
  return request.delete(`/logistics/${id}`)
}
