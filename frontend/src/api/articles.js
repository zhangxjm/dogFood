import api from './index'

export const getArticles = (params) => {
  return api.get('/articles', { params })
}

export const getArticle = (id) => {
  return api.get(`/articles/${id}`)
}

export const createArticle = (data) => {
  return api.post('/articles', data)
}

export const updateArticle = (id, data) => {
  return api.put(`/articles/${id}`, data)
}

export const deleteArticle = (id) => {
  return api.delete(`/articles/${id}`)
}

export const getPopularArticles = (limit) => {
  return api.get('/articles/popular', { params: { limit } })
}

export const getArchive = () => {
  return api.get('/articles/archive')
}
