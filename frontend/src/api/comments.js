import api from './index'

export const getArticleComments = (articleId) => {
  return api.get(`/comments/article/${articleId}`)
}

export const getComments = (params) => {
  return api.get('/comments', { params })
}

export const createComment = (data) => {
  return api.post('/comments', data)
}

export const approveComment = (id) => {
  return api.put(`/comments/${id}/approve`)
}

export const deleteComment = (id) => {
  return api.delete(`/comments/${id}`)
}
