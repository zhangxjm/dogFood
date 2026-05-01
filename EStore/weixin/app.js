App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/api'
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
      this.checkLogin()
    }
  },

  checkLogin() {
    const token = this.globalData.token
    if (!token) return
    
    wx.request({
      url: this.globalData.baseUrl + '/user/info',
      header: {
        'Authorization': 'Bearer ' + token
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.globalData.userInfo = res.data.data
        } else {
          this.globalData.token = null
          this.globalData.userInfo = null
          wx.removeStorageSync('token')
        }
      },
      fail: () => {
        console.log('网络错误')
      }
    })
  },

  wxLogin() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            wx.request({
              url: this.globalData.baseUrl + '/user/auth/login',
              method: 'POST',
              data: { code: res.code },
              success: (response) => {
                if (response.data.code === 200) {
                  const { token, user } = response.data.data
                  this.globalData.token = token
                  this.globalData.userInfo = user
                  wx.setStorageSync('token', token)
                  wx.setStorageSync('userInfo', user)
                  resolve({ token, user })
                } else {
                  reject(new Error(response.data.message))
                }
              },
              fail: () => {
                reject(new Error('网络请求失败'))
              }
            })
          } else {
            reject(new Error('登录失败'))
          }
        },
        fail: () => {
          reject(new Error('微信登录失败'))
        }
      })
    })
  }
})
