import request from '@/utils/request'

export function getCategoryList(params) {
  return request({
    url: '/categories/',
    method: 'get',
    params,
  })
}

export function getCategoryTree() {
  return request({
    url: '/categories/tree/',
    method: 'get',
  })
}

export function getActiveCategories() {
  return request({
    url: '/categories/active/',
    method: 'get',
  })
}

export function getCategoryById(id) {
  return request({
    url: `/categories/${id}/`,
    method: 'get',
  })
}

export function createCategory(data) {
  return request({
    url: '/categories/',
    method: 'post',
    data,
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/categories/${id}/`,
    method: 'put',
    data,
  })
}

export function deleteCategory(id) {
  return request({
    url: `/categories/${id}/`,
    method: 'delete',
  })
}

export function getBookList(params) {
  return request({
    url: '/books/',
    method: 'get',
    params,
  })
}

export function getAvailableBooks(params) {
  return request({
    url: '/books/available/',
    method: 'get',
    params,
  })
}

export function getBookById(id) {
  return request({
    url: `/books/${id}/`,
    method: 'get',
  })
}

export function createBook(data) {
  return request({
    url: '/books/',
    method: 'post',
    data,
  })
}

export function updateBook(id, data) {
  return request({
    url: `/books/${id}/`,
    method: 'put',
    data,
  })
}

export function deleteBook(id) {
  return request({
    url: `/books/${id}/`,
    method: 'delete',
  })
}

export function activateBook(id) {
  return request({
    url: `/books/${id}/activate/`,
    method: 'post',
  })
}

export function deactivateBook(id) {
  return request({
    url: `/books/${id}/deactivate/`,
    method: 'post',
  })
}

export function getBookStatistics() {
  return request({
    url: '/books/statistics/',
    method: 'get',
  })
}
