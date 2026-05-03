import axios from 'axios'

const baseUrl = '/api'

const service = axios.create({
  baseURL: baseUrl,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.success) {
      return res
    } else {
      throw new Error(res.message || '请求失败')
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('member')
      localStorage.removeItem('memberId')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

const request = {
  get: (url, config = {}) => {
    return service.get(url, { params: config.params, ...config })
  },
  post: (url, data, config = {}) => {
    return service.post(url, data, config)
  },
  put: (url, data, config = {}) => {
    return service.put(url, data, config)
  },
  delete: (url, config = {}) => {
    return service.delete(url, config)
  }
}

export default request
export { service, request }
