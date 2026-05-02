const app = getApp()

Page({
  data: {
    categories: [],
    currentCategoryId: null,
    products: []
  },

  onLoad(options) {
    this.loadCategories()
  },

  onShow() {
    if (this.data.currentCategoryId) {
      this.loadProducts()
    }
  },

  loadCategories() {
    wx.request({
      url: app.globalData.baseUrl + '/categories',
      success: (res) => {
        if (res.data.code === 200 && res.data.data.length > 0) {
          const categories = res.data.data
          this.setData({
            categories: categories,
            currentCategoryId: categories[0].id
          })
          this.loadProducts()
        }
      }
    })
  },

  selectCategory(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      currentCategoryId: id,
      products: []
    })
    this.loadProducts()
  },

  loadProducts() {
    if (!this.data.currentCategoryId) return
    
    wx.showLoading({ title: '加载中' })
    
    wx.request({
      url: app.globalData.baseUrl + '/products',
      data: {
        categoryId: this.data.currentCategoryId,
        pageNum: 1,
        pageSize: 50
      },
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            products: res.data.data.records
          })
        }
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  goToProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/product/product?id=' + id
    })
  }
})
