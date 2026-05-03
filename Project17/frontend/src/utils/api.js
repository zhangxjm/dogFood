import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default api

export const deviceApi = {
  list: () => api.get('/devices/list'),
  page: (params) => api.get('/devices', { params }),
  get: (id) => api.get(`/devices/${id}`),
  save: (data) => api.post('/devices', data),
  update: (data) => api.put('/devices', data),
  delete: (id) => api.delete(`/devices/${id}`),
  updateStatus: (id, status) => api.put(`/devices/${id}/status?status=${status}`)
}

export const deviceDataApi = {
  page: (params) => api.get('/device-data', { params }),
  latest: () => api.get('/device-data/latest'),
  recent: (deviceId, limit) => api.get(`/device-data/device/${deviceId}/recent?limit=${limit}`),
  latestByDevice: (deviceId) => api.get(`/device-data/device/${deviceId}/latest`)
}

export const alarmApi = {
  page: (params) => api.get('/alarms', { params }),
  get: (id) => api.get(`/alarms/${id}`),
  handle: (id, handleUser, handleRemark) => 
    api.put(`/alarms/${id}/handle?handleUser=${handleUser}&handleRemark=${handleRemark || ''}`),
  unhandledCount: () => api.get('/alarms/unhandled/count')
}

export const workOrderApi = {
  page: (params) => api.get('/work-orders', { params }),
  get: (id) => api.get(`/work-orders/${id}`),
  save: (data) => api.post('/work-orders', data),
  update: (data) => api.put('/work-orders', data),
  delete: (id) => api.delete(`/work-orders/${id}`),
  updateStatus: (id, status, resultRemark) => 
    api.put(`/work-orders/${id}/status?status=${status}&resultRemark=${resultRemark || ''}`)
}

export const operatorApi = {
  list: () => api.get('/operators/list'),
  page: (params) => api.get('/operators', { params }),
  get: (id) => api.get(`/operators/${id}`),
  login: (data) => api.post('/operators/login', data),
  save: (data) => api.post('/operators', data),
  update: (data) => api.put('/operators', data),
  delete: (id) => api.delete(`/operators/${id}`),
  updateStatus: (id, status) => api.put(`/operators/${id}/status?status=${status}`)
}

export const dashboardApi = {
  stats: () => api.get('/dashboard/stats')
}
