const app = getApp()

Page({
  data: {
    cartList: [],
    isAllSelected: false,
    totalPrice: '0.00',
    selectedCount: 0
  },

  onShow() {
    this.checkLogin()
  },

  checkLogin() {
    const token = app.globalData.token || wx.getStorageSync('token')
    if (!token) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        success: (res) => {
          if (res.confirm) {
            wx.switchTab({
              url: '/pages/profile/profile'
            })
          }
        }
      })
      return
    }
    this.loadCart()
  },

  loadCart() {
    const token = app.globalData.token || wx.getStorageSync('token')
    
    wx.request({
      url: app.globalData.baseUrl + '/user/cart',
      header: {
        'Authorization': 'Bearer ' + token
      },
      success: (res) => {
        if (res.data.code === 200) {
          const cartList = res.data.data.map(item => ({
            ...item,
            productInfo: {
              name: '商品',
              price: 99.00,
              mainImage: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20placeholder&image_size=square'
            }
          }))
          this.setData({ cartList })
          this.calculateTotal()
        }
      }
    })
  },

  toggleSelect(e) {
    const id = e.currentTarget.dataset.id
    const value = e.detail.value
    
    const cartList = this.data.cartList.map(item => {
      if (item.id === id) {
        item.selected = value.length > 0 ? 1 : 0
      }
      return item
    })
    
    this.setData({ cartList })
    this.calculateTotal()
  },

  toggleSelectAll(e) {
    const isChecked = e.detail.value.length > 0
    const cartList = this.data.cartList.map(item => ({
      ...item,
      selected: isChecked ? 1 : 0
    }))
    
    this.setData({ cartList, isAllSelected: isChecked })
    this.calculateTotal()
  },

  calculateTotal() {
    let total = 0
    let count = 0
    
    this.data.cartList.forEach(item => {
      if (item.selected === 1) {
        total += item.quantity * (item.productInfo?.price || 0)
        count++
      }
    })
    
    const isAllSelected = this.data.cartList.length > 0 && 
      this.data.cartList.every(item => item.selected === 1)
    
    this.setData({
      totalPrice: total.toFixed(2),
      selectedCount: count,
      isAllSelected: isAllSelected
    })
  },

  decreaseQuantity(e) {
    const id = e.currentTarget.dataset.id
    const token = app.globalData.token || wx.getStorageSync('token')
    
    const item = this.data.cartList.find(c => c.id === id)
    if (item && item.quantity > 1) {
      wx.request({
        url: app.globalData.baseUrl + '/user/cart/' + id,
        method: 'PUT',
        header: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        data: { quantity: item.quantity - 1 },
        success: (res) => {
          if (res.data.code === 200) {
            this.loadCart()
          }
        }
      })
    }
  },

  increaseQuantity(e) {
    const id = e.currentTarget.dataset.id
    const token = app.globalData.token || wx.getStorageSync('token')
    
    const item = this.data.cartList.find(c => c.id === id)
    if (item) {
      wx.request({
        url: app.globalData.baseUrl + '/user/cart/' + id,
        method: 'PUT',
        header: {
          'Authorization': 'Bearer ' + token,
          'Content-Type': 'application/json'
        },
        data: { quantity: item.quantity + 1 },
        success: (res) => {
          if (res.data.code === 200) {
            this.loadCart()
          }
        }
      })
    }
  },

  deleteItem(e) {
    const id = e.currentTarget.dataset.id
    const token = app.globalData.token || wx.getStorageSync('token')
    
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: app.globalData.baseUrl + '/user/cart/' + id,
            method: 'DELETE',
            header: {
              'Authorization': 'Bearer ' + token
            },
            success: (res) => {
              if (res.data.code === 200) {
                this.loadCart()
              }
            }
          })
        }
      }
    })
  },

  goToProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/product/product?id=' + id
    })
  },

  goShopping() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  checkout() {
    const token = app.globalData.token || wx.getStorageSync('token')
    const selectedItems = this.data.cartList.filter(item => item.selected === 1)
    
    if (selectedItems.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' })
      return
    }
    
    wx.navigateTo({
      url: '/pages/addresses/addresses?select=1'
    })
  }
})
