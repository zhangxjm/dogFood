import request from '@/utils/request'

export const messageApi = {
  list(params) {
    return request.get('/message/list', { params })
  },

  unreadCount() {
    return request.get('/message/unread/count')
  },

  markAsRead(id) {
    return request.put(`/message/read/${id}`)
  },

  markAllAsRead() {
    return request.put('/message/read/all')
  },

  send(receiverId, content) {
    return request.post(`/message/send/${receiverId}`, { content })
  },

  listChat(otherUserId, params) {
    return request.get(`/message/chat/${otherUserId}`, { params })
  }
}
