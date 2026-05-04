import request from './index'

export const getClubList = (params) => {
  return request.get('/club/list', { params })
}

export const getClubDetail = (id) => {
  return request.get(`/club/${id}`)
}

export const createClub = (data) => {
  return request.post('/club/create', data)
}

export const updateClub = (data) => {
  return request.post('/club/update', data)
}

export const auditClub = (id, data) => {
  return request.post(`/club/audit/${id}`, data)
}

export const applyJoinClub = (clubId, data) => {
  return request.post(`/club/applyJoin/${clubId}`, data)
}

export const auditJoinClub = (memberId, data) => {
  return request.post(`/club/auditJoin/${memberId}`, data)
}

export const quitClub = (clubId) => {
  return request.post(`/club/quit/${clubId}`)
}

export const getClubMembers = (clubId) => {
  return request.get(`/club/members/${clubId}`)
}

export const getMyClubs = () => {
  return request.get('/club/myClubs')
}

export const getMyApplyClubs = () => {
  return request.get('/club/myApplyClubs')
}

export const getPendingApprovals = (clubId) => {
  return request.get(`/club/pendingApprovals/${clubId}`)
}
