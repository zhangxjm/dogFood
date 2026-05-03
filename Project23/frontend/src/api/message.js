import request from '@/utils/request'

export function sendMessage(data) {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

export function getReceivedMessages() {
  return request({
    url: '/message/received',
    method: 'get'
  })
}

export function getSentMessages() {
  return request({
    url: '/message/sent',
    method: 'get'
  })
}

export function getChatHistory(otherUserId, productId) {
  return request({
    url: `/message/chat/${otherUserId}`,
    method: 'get',
    params: { productId }
  })
}

export function markAsRead(messageId) {
  return request({
    url: `/message/read/${messageId}`,
    method: 'post'
  })
}

export function getUnreadCount() {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}
