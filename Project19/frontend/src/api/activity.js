import request from './index'

export const getActivityList = (params) => {
  return request.get('/activity/list', { params })
}

export const getActivityDetail = (id) => {
  return request.get(`/activity/${id}`)
}

export const createActivity = (data) => {
  return request.post('/activity/create', data)
}

export const updateActivity = (data) => {
  return request.post('/activity/update', data)
}

export const deleteActivity = (id) => {
  return request.delete(`/activity/${id}`)
}

export const registerActivity = (activityId) => {
  return request.post(`/activity/register/${activityId}`)
}

export const cancelRegistration = (activityId) => {
  return request.post(`/activity/cancel/${activityId}`)
}

export const getActivityRegistrations = (activityId) => {
  return request.get(`/activity/registrations/${activityId}`)
}

export const getMyActivities = () => {
  return request.get('/activity/myActivities')
}

export const getClubActivities = (clubId) => {
  return request.get(`/activity/clubActivities/${clubId}`)
}

export const cancelActivity = (id) => {
  return request.delete(`/activity/${id}`)
}
