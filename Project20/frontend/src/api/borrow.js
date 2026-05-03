import request from '@/utils/request'

export function getBorrowList(params) {
  return request({
    url: '/borrows/',
    method: 'get',
    params,
  })
}

export function getMyBorrows(params) {
  return request({
    url: '/borrows/my/',
    method: 'get',
    params,
  })
}

export function getOverdueRecords(params) {
  return request({
    url: '/borrows/overdue/',
    method: 'get',
    params,
  })
}

export function getBorrowById(id) {
  return request({
    url: `/borrows/${id}/`,
    method: 'get',
  })
}

export function borrowBook(data) {
  return request({
    url: '/borrows/borrow/',
    method: 'post',
    data,
  })
}

export function returnBook(id, data) {
  return request({
    url: `/borrows/${id}/return/`,
    method: 'post',
    data,
  })
}

export function renewBook(id, data) {
  return request({
    url: `/borrows/${id}/renew/`,
    method: 'post',
    data,
  })
}

export function getBorrowStatistics() {
  return request({
    url: '/borrows/statistics/',
    method: 'get',
  })
}

export function getFineList(params) {
  return request({
    url: '/fines/',
    method: 'get',
    params,
  })
}

export function getMyFines(params) {
  return request({
    url: '/fines/my/',
    method: 'get',
    params,
  })
}

export function getFineById(id) {
  return request({
    url: `/fines/${id}/`,
    method: 'get',
  })
}

export function payFine(id) {
  return request({
    url: `/fines/${id}/pay/`,
    method: 'post',
  })
}

export function waiveFine(id, data) {
  return request({
    url: `/fines/${id}/waive/`,
    method: 'post',
    data,
  })
}
