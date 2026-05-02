const BASE_URL = process.env.NODE_ENV === 'development' 
  ? '' 
  : 'http://localhost:5000'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
  showLoading?: boolean
  showError?: boolean
}

function request(options: RequestOptions) {
  return new Promise((resolve, reject) => {
    const { url, method = 'GET', data, showLoading = true, showError = true } = options
    
    if (showLoading) {
      uni.showLoading({
        title: '加载中...',
        mask: true
      })
    }

    const token = uni.getStorageSync('token')
    const header: any = {
      'Content-Type': 'application/json'
    }
    
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      success: (res: any) => {
        if (showLoading) {
          uni.hideLoading()
        }

        if (res.statusCode === 200 || res.statusCode === 201) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({
            url: '/pages/login/login'
          })
          reject(new Error('登录已过期'))
        } else {
          if (showError) {
            uni.showToast({
              title: res.data?.message || '请求失败',
              icon: 'none'
            })
          }
          reject(new Error(res.data?.message || '请求失败'))
        }
      },
      fail: (err: any) => {
        if (showLoading) {
          uni.hideLoading()
        }
        
        if (showError) {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
        }
        reject(err)
      }
    })
  })
}

request.get = (url: string, data?: any, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

request.post = (url: string, data?: any, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

request.put = (url: string, data?: any, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

request.delete = (url: string, data?: any, options?: Omit<RequestOptions, 'url' | 'method' | 'data'>) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export { request, BASE_URL }
