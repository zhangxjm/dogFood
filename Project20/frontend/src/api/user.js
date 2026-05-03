import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/token/',
    method: 'post',
    data,
  })
}

export function register(data) {
  return request({
    url: '/users/',
    method: 'post',
    data,
  })
}

export function createUser(data) {
  return request({
    url: '/users/',
    method: 'post',
    data,
  })
}

export function getUserInfo() {
  return request({
    url: '/users/me/',
    method: 'get',
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/users/me/',
    method: 'put',
    data,
  })
}

export function changePassword(data) {
  return request({
    url: '/users/change-password/',
    method: 'put',
    data,
  })
}

export function getUserList(params) {
  return request({
    url: '/users/',
    method: 'get',
    params,
  })
}

export function getUserById(id) {
  return request({
    url: `/users/${id}/`,
    method: 'get',
  })
}

export function getUser(id) {
  return request({
    url: `/users/${id}/`,
    method: 'get',
  })
}

export function updateUser(id, data) {
  return request({
    url: `/users/${id}/`,
    method: 'put',
    data,
  })
}

export function deleteUser(id) {
  return request({
    url: `/users/${id}/`,
    method: 'delete',
  })
}

export function activateUser(id) {
  return request({
    url: `/users/${id}/activate/`,
    method: 'post',
  })
}

export function deactivateUser(id) {
  return request({
    url: `/users/${id}/deactivate/`,
    method: 'post',
  })
}
