const app = getApp();
const cloud = require('../../utils/cloud.js');
const util = require('../../utils/util.js');
const toast = require('../../utils/toast.js');

Page({
  data: {
    newsId: '',
    newsDetail: null,
    isCollected: false,
    isLoading: true,
    showShareMenu: false
  },

  onLoad: function (options) {
    const newsId = options.id;
    if (!newsId) {
      toast.showError('资讯不存在');
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }

    this.setData({ newsId: newsId });
    this.loadNewsDetail(newsId);
  },

  onShow: function () {
    
  },

  loadNewsDetail: function (newsId) {
    this.setData({ isLoading: true });

    cloud.getNewsDetail(newsId)
      .then(res => {
        const detail = res.data;
        this.setData({
          newsDetail: detail,
          isCollected: detail.isCollected || false,
          isLoading: false
        });
        
        wx.setNavigationBarTitle({
          title: detail.title || '资讯详情'
        });
      })
      .catch(err => {
        console.error('加载资讯详情失败:', err);
        this.setData({ isLoading: false });
        toast.showError('加载失败，请稍后重试');
      });
  },

  onCollectionTap: function () {
    if (!util.checkIsLogin()) {
      toast.showModal('温馨提示', '请先登录后再收藏', true)
        .then(res => {
          if (res.confirm) {
            this.goToLogin();
          }
        });
      return;
    }

    const { newsId, newsDetail, isCollected } = this.data;
    
    const newsInfo = {
      title: newsDetail.title,
      cover: newsDetail.cover,
      category: newsDetail.category
    };

    cloud.toggleCollection(newsId, newsInfo)
      .then(res => {
        const data = res.data;
        this.setData({
          isCollected: data.isCollected
        });
        toast.showSuccess(data.isCollected ? '收藏成功' : '已取消收藏');
      })
      .catch(err => {
        console.error('操作收藏失败:', err);
        toast.showError('操作失败，请稍后重试');
      });
  },

  onShareAppMessage: function () {
    const { newsDetail, newsId } = this.data;
    return {
      title: newsDetail ? newsDetail.title : '分享一篇资讯',
      path: `/pages/detail/detail?id=${newsId}`,
      imageUrl: newsDetail ? newsDetail.cover : ''
    };
  },

  goToLogin: function () {
    wx.switchTab({
      url: '/pages/user/user'
    });
  },

  onPageScroll: function (e) {
    
  }
});
