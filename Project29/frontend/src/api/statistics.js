import request from '@/utils/request'

export function getStatisticsPage(params) {
  return request({
    url: '/statistics/page',
    method: 'get',
    params
  })
}

export function getStatisticsList(params) {
  return request({
    url: '/statistics/list',
    method: 'get',
    params
  })
}

export function getStatisticsById(id) {
  return request({
    url: `/statistics/${id}`,
    method: 'get'
  })
}

export function addStatistics(data) {
  return request({
    url: '/statistics',
    method: 'post',
    data
  })
}

export function updateStatistics(data) {
  return request({
    url: '/statistics',
    method: 'put',
    data
  })
}

export function deleteStatistics(id) {
  return request({
    url: `/statistics/${id}`,
    method: 'delete'
  })
}

export function exportStatistics(params) {
  window.location.href = `/api/statistics/export?${new URLSearchParams(params).toString()}`
}
