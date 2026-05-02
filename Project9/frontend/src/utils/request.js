import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log(`[Request] ${config.method?.toUpperCase()} ${config.url} - with token`)
    } else {
      console.log(`[Request] ${config.method?.toUpperCase()} ${config.url} - no token`)
    }
    
    return config
  },
  (error) => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    console.log(`[Response] ${response.status} - ${response.config.url}`)
    return response
  },
  (error) => {
    console.error('[Response Error]', error.response?.status || 'Network Error', error.config?.url)
    
    if (error.response) {
      const { status } = error.response
      
      if (status === 401) {
        console.warn('[Auth] 401 Unauthorized, clearing auth and redirecting...')
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    }
    
    return Promise.reject(error)
  }
)

export default request
