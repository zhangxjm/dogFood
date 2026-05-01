const app = getApp()

Page({
  data: {
    banners: [
      { id: 1, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20ecommerce%20banner%20red%20theme%20shopping%20sale&image_size=landscape_16_9' },
      { id: 2, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=ecommerce%20banner%20electronics%20products%20blue%20theme&image_size=landscape_16_9' },
      { id: 3, image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fashion%20shopping%20banner%20pink%20theme%20clothing&image_size=landscape_16_9' }
    ],
    categories: [],
    hotProducts: [],
    products: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadCategories()
    this.loadHotProducts()
    this.loadProducts()
  },

  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      products: [],
      hasMore: true
    })
    this.loadCategories()
    this.loadHotProducts()
    this.loadProducts().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts()
    }
  },

  loadCategories() {
    wx.request({
      url: app.globalData.baseUrl + '/categories',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            categories: res.data.data
          })
        }
      }
    })
  },

  loadHotProducts() {
    wx.request({
      url: app.globalData.baseUrl + '/products/hot',
      success: (res) => {
        if (res.data.code === 200) {
          this.setData({
            hotProducts: res.data.data
          })
        }
      }
    })
  },

  loadProducts() {
    if (this.data.loading || !this.data.hasMore) return
    
    this.setData({ loading: true })
    
    return new Promise((resolve) => {
      wx.request({
        url: app.globalData.baseUrl + '/products',
        data: {
          pageNum: this.data.pageNum,
          pageSize: this.data.pageSize
        },
        success: (res) => {
          if (res.data.code === 200) {
            const newProducts = res.data.data.records
            const hasMore = res.data.data.pageNum < res.data.data.pages
            
            this.setData({
              products: this.data.products.concat(newProducts),
              pageNum: this.data.pageNum + 1,
              hasMore: hasMore
            })
          }
        },
        complete: () => {
          this.setData({ loading: false })
          resolve()
        }
      })
    })
  },

  goToCategory(e) {
    const id = e.currentTarget.dataset.id
    wx.switchTab({
      url: '/pages/category/category' + (id ? '?id=' + id : '')
    })
  },

  goToProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/product/product?id=' + id
    })
  }
})
