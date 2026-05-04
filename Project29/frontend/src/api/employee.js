import request from '@/utils/request'

export function getEmployeeList(params) {
  return request({
    url: '/employee/list',
    method: 'get',
    params
  })
}

export function getEmployeePage(params) {
  return request({
    url: '/employee/page',
    method: 'get',
    params
  })
}

export function getEmployeeById(id) {
  return request({
    url: `/employee/${id}`,
    method: 'get'
  })
}

export function addEmployee(data) {
  return request({
    url: '/employee',
    method: 'post',
    data
  })
}

export function updateEmployee(data) {
  return request({
    url: '/employee',
    method: 'put',
    data
  })
}

export function deleteEmployee(id) {
  return request({
    url: `/employee/${id}`,
    method: 'delete'
  })
}

export function deleteEmployeeBatch(ids) {
  return request({
    url: '/employee/batch',
    method: 'delete',
    data: ids
  })
}

export function exportEmployee(params) {
  window.location.href = `/api/employee/export?${new URLSearchParams(params).toString()}`
}
