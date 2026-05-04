import request from './index'

export const getNotificationList = () => {
  return request.get('/notification/list')
}

export const getNotificationPage = (params) => {
  return request.get('/notification/page', { params })
}

export const getUnreadCount = () => {
  return request.get('/notification/unreadCount')
}

export const markAsRead = (id) => {
  return request.post(`/notification/read/${id}`)
}

export const markAllAsRead = () => {
  return request.post('/notification/readAll')
}
