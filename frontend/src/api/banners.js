import api from './index'

export const getBanners = () => {
  return api.get('/banners')
}

export const getAllBanners = () => {
  return api.get('/banners/all')
}

export const createBanner = (data) => {
  return api.post('/banners', data)
}

export const updateBanner = (id, data) => {
  return api.put(`/banners/${id}`, data)
}

export const deleteBanner = (id) => {
  return api.delete(`/banners/${id}`)
}
