const app = getApp();
const cloud = require('../../utils/cloud.js');
const util = require('../../utils/util.js');
const toast = require('../../utils/toast.js');

Page({
  data: {
    userInfo: null,
    isLogged: false,
    collections: [],
    collectionPage: 1,
    collectionPageSize: 10,
    collectionTotal: 0,
    collectionHasMore: true,
    collectionIsLoading: false,
    showLoginModal: false,
    currentTab: 'profile'
  },

  onLoad: function (options) {
    this.initData();
  },

  onShow: function () {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 1
      });
    }
    
    const isLogged = app.globalData.isLogged;
    const userInfo = app.globalData.userInfo;
    
    this.setData({
      isLogged: isLogged,
      userInfo: userInfo
    });

    if (isLogged && this.data.currentTab === 'collections') {
      this.loadCollections(true);
    }
  },

  initData: function () {
    const isLogged = app.globalData.isLogged;
    const userInfo = app.globalData.userInfo;
    
    this.setData({
      isLogged: isLogged,
      userInfo: userInfo,
      collectionPage: 1,
      collections: [],
      collectionHasMore: true
    });
  },

  onTabChange: function (e) {
    const tab = e.currentTarget.dataset.tab;
    
    if (tab === this.data.currentTab) return;

    this.setData({
      currentTab: tab
    });

    if (tab === 'collections' && this.data.isLogged) {
      this.loadCollections(true);
    }
  },

  onGetUserInfo: function (e) {
    const userInfo = e.detail.userInfo;
    
    if (!userInfo) {
      toast.showError('登录失败，请重试');
      return;
    }

    this.doLogin(userInfo);
  },

  doLogin: function (userInfo) {
    toast.showLoading('登录中...');

    cloud.login()
      .then(loginRes => {
        const loginData = loginRes.data;
        
        app.setOpenid(loginData.openid);
        app.setLoginStatus(true);

        return cloud.updateUserInfo(userInfo);
      })
      .then(updateRes => {
        app.setUserInfo(userInfo);
        
        this.setData({
          userInfo: userInfo,
          isLogged: true
        });

        toast.hideLoading();
        toast.showSuccess('登录成功');
      })
      .catch(err => {
        console.error('登录失败:', err);
        toast.hideLoading();
        toast.showError('登录失败，请稍后重试');
      });
  },

  onLogout: function () {
    toast.showModal('温馨提示', '确定要退出登录吗？', true)
      .then(res => {
        if (res.confirm) {
          this.doLogout();
        }
      });
  },

  doLogout: function () {
    app.setUserInfo(null);
    app.setOpenid('');
    app.setLoginStatus(false);
    
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('openid');

    this.setData({
      userInfo: null,
      isLogged: false,
      collections: [],
      currentTab: 'profile'
    });

    toast.showSuccess('已退出登录');
  },

  loadCollections: function (isRefresh = true) {
    if (!this.data.isLogged) {
      return;
    }

    if (this.data.collectionIsLoading) return;

    this.setData({ collectionIsLoading: true });

    if (isRefresh) {
      this.setData({
        collectionPage: 1,
        collections: [],
        collectionHasMore: true
      });
    }

    const params = {
      page: this.data.collectionPage,
      pageSize: this.data.collectionPageSize
    };

    return cloud.getCollections(params)
      .then(res => {
        const data = res.data;
        let collections = [];
        
        if (isRefresh) {
          collections = data.list;
        } else {
          collections = this.data.collections.concat(data.list);
        }

        this.setData({
          collections: collections,
          collectionTotal: data.total,
          collectionHasMore: data.hasMore,
          collectionIsLoading: false
        });
      })
      .catch(err => {
        console.error('加载收藏列表失败:', err);
        this.setData({ collectionIsLoading: false });
      });
  },

  onCollectionItemTap: function (e) {
    const newsId = e.currentTarget.dataset.newsid;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${newsId}`
    });
  },

  onPullDownRefresh: function () {
    if (this.data.currentTab === 'collections' && this.data.isLogged) {
      this.loadCollections(true).finally(() => {
        wx.stopPullDownRefresh();
      });
    } else {
      wx.stopPullDownRefresh();
    }
  },

  onReachBottom: function () {
    if (this.data.currentTab === 'collections' && 
        this.data.collectionHasMore && 
        !this.data.collectionIsLoading) {
      this.setData({
        collectionPage: this.data.collectionPage + 1
      });
      this.loadCollections(false);
    }
  },

  onItemTap: function (e) {
    const item = e.currentTarget.dataset.item;
    
    switch (item) {
      case 'collections':
        if (!this.data.isLogged) {
          toast.showModal('温馨提示', '请先登录后查看收藏', true)
            .then(res => {
              if (res.confirm) {
                this.setData({ showLoginModal: true });
              }
            });
          return;
        }
        this.setData({ currentTab: 'collections' });
        this.loadCollections(true);
        break;
      case 'settings':
        toast.showToast('设置功能开发中');
        break;
      case 'about':
        toast.showToast('关于功能开发中');
        break;
      case 'feedback':
        toast.showToast('反馈功能开发中');
        break;
    }
  },

  closeLoginModal: function () {
    this.setData({ showLoginModal: false });
  }
});
