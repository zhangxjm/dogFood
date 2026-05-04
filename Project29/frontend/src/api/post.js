import request from '@/utils/request'

export function getPostList(params) {
  return request({
    url: '/post/list',
    method: 'get',
    params
  })
}

export function getPostPage(params) {
  return request({
    url: '/post/page',
    method: 'get',
    params
  })
}

export function getPostById(id) {
  return request({
    url: `/post/${id}`,
    method: 'get'
  })
}

export function addPost(data) {
  return request({
    url: '/post',
    method: 'post',
    data
  })
}

export function updatePost(data) {
  return request({
    url: '/post',
    method: 'put',
    data
  })
}

export function deletePost(id) {
  return request({
    url: `/post/${id}`,
    method: 'delete'
  })
}

export function deletePostBatch(ids) {
  return request({
    url: '/post/batch',
    method: 'delete',
    data: ids
  })
}
