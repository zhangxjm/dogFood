import request from '@/utils/request'

export function getOvertimePage(params) {
  return request({
    url: '/overtime/page',
    method: 'get',
    params
  })
}

export function getOvertimeList(params) {
  return request({
    url: '/overtime/list',
    method: 'get',
    params
  })
}

export function getOvertimeById(id) {
  return request({
    url: `/overtime/${id}`,
    method: 'get'
  })
}

export function addOvertime(data) {
  return request({
    url: '/overtime',
    method: 'post',
    data
  })
}

export function updateOvertime(data) {
  return request({
    url: '/overtime',
    method: 'put',
    data
  })
}

export function deleteOvertime(id) {
  return request({
    url: `/overtime/${id}`,
    method: 'delete'
  })
}

export function approveOvertime(data) {
  return request({
    url: '/overtime/approve',
    method: 'post',
    data
  })
}
