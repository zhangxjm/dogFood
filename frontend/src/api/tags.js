import api from './index'

export const getTags = () => {
  return api.get('/tags')
}

export const getTag = (id) => {
  return api.get(`/tags/${id}`)
}

export const createTag = (data) => {
  return api.post('/tags', data)
}

export const updateTag = (id, data) => {
  return api.put(`/tags/${id}`, data)
}

export const deleteTag = (id) => {
  return api.delete(`/tags/${id}`)
}
