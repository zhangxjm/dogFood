import request from './index'

export const getAnnouncementList = (clubId) => {
  return request.get(`/announcement/club/${clubId}`)
}

export const getAnnouncementPage = (clubId, params) => {
  return request.get(`/announcement/club/${clubId}/page`, { params })
}

export const getAnnouncementDetail = (id) => {
  return request.get(`/announcement/${id}`)
}

export const createAnnouncement = (data) => {
  return request.post('/announcement/create', data)
}

export const updateAnnouncement = (data) => {
  return request.post('/announcement/update', data)
}

export const deleteAnnouncement = (id) => {
  return request.post(`/announcement/delete/${id}`)
}

export const toggleAnnouncementTop = (id) => {
  return request.post(`/announcement/toggleTop/${id}`)
}
