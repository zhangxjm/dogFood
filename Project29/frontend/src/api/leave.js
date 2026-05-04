import request from '@/utils/request'

export function getLeavePage(params) {
  return request({
    url: '/leave/page',
    method: 'get',
    params
  })
}

export function getLeaveList(params) {
  return request({
    url: '/leave/list',
    method: 'get',
    params
  })
}

export function getLeaveById(id) {
  return request({
    url: `/leave/${id}`,
    method: 'get'
  })
}

export function addLeave(data) {
  return request({
    url: '/leave',
    method: 'post',
    data
  })
}

export function updateLeave(data) {
  return request({
    url: '/leave',
    method: 'put',
    data
  })
}

export function deleteLeave(id) {
  return request({
    url: `/leave/${id}`,
    method: 'delete'
  })
}

export function approveLeave(data) {
  return request({
    url: '/leave/approve',
    method: 'post',
    data
  })
}
