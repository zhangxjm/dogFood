import request from './index'

export const getCategoryList = () => {
  return request.get('/category/list')
}

export const getEnabledCategories = () => {
  return request.get('/category/enabled')
}

export const getCategoryById = (id) => {
  return request.get(`/category/${id}`)
}

export const createCategory = (data) => {
  return request.post('/category/create', data)
}

export const updateCategory = (data) => {
  return request.post('/category/update', data)
}

export const deleteCategory = (id) => {
  return request.delete(`/category/${id}`)
}
