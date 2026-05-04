import request from '@/utils/request'

export function getDeptList(params) {
  return request({
    url: '/dept/list',
    method: 'get',
    params
  })
}

export function getDeptPage(params) {
  return request({
    url: '/dept/page',
    method: 'get',
    params
  })
}

export function getDeptById(id) {
  return request({
    url: `/dept/${id}`,
    method: 'get'
  })
}

export function addDept(data) {
  return request({
    url: '/dept',
    method: 'post',
    data
  })
}

export function updateDept(data) {
  return request({
    url: '/dept',
    method: 'put',
    data
  })
}

export function deleteDept(id) {
  return request({
    url: `/dept/${id}`,
    method: 'delete'
  })
}

export function deleteDeptBatch(ids) {
  return request({
    url: '/dept/batch',
    method: 'delete',
    data: ids
  })
}
