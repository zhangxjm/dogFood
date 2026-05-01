const app = getApp()

Page({
  data: {
    userInfo: null
  },

  onShow() {
    const token = app.globalData.token || wx.getStorageSync('token')
    if (token) {
      this.loadUserInfo()
    } else {
      this.setData({ userInfo: null })
    }
  },

  loadUserInfo() {
    const token = app.globalData.token || wx.getStorageSync('token')
    
    wx.request({
      url: app.globalData.baseUrl + '/user/info',
      header: {
        'Authorization': 'Bearer ' + token
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({ userInfo: res.data.data })
          app.globalData.userInfo = res.data.data
        } else {
          this.setData({ userInfo: null })
        }
      },
      fail: () => {
        this.setData({ userInfo: null })
      }
    })
  },

  handleLogin() {
    wx.showLoading({ title: '登录中' })
    
    app.wxLogin().then(() => {
      wx.hideLoading()
      this.loadUserInfo()
    }).catch((err) => {
      wx.hideLoading()
      wx.showToast({ title: err.message, icon: 'none' })
    })
  },

  goToOrders(e) {
    if (!this.data.userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    
    const status = e.currentTarget.dataset.status
    const url = status !== undefined 
      ? `/pages/orders/orders?status=${status}`
      : '/pages/orders/orders'
    
    wx.navigateTo({ url })
  },

  goToAddresses() {
    if (!this.data.userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    
    wx.navigateTo({
      url: '/pages/addresses/addresses'
    })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.token = null
          app.globalData.userInfo = null
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          this.setData({ userInfo: null })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  }
})
