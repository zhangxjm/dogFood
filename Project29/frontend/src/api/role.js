import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/role/list',
    method: 'get',
    params
  })
}

export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params
  })
}

export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function deleteRoleBatch(ids) {
  return request({
    url: '/role/batch',
    method: 'delete',
    data: ids
  })
}
