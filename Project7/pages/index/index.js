const app = getApp();
const cloud = require('../../utils/cloud.js');
const util = require('../../utils/util.js');
const toast = require('../../utils/toast.js');

Page({
  data: {
    newsList: [],
    page: 1,
    pageSize: 10,
    total: 0,
    hasMore: true,
    isLoading: false,
    isRefreshing: false,
    category: '',
    categories: [
      { name: '全部', value: '' },
      { name: '科技', value: 'tech' },
      { name: '生活', value: 'life' },
      { name: '娱乐', value: 'entertainment' },
      { name: '体育', value: 'sports' }
    ],
    currentCategoryIndex: 0
  },

  onLoad: function (options) {
    this.initData();
  },

  onShow: function () {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 0
      });
    }
  },

  onPullDownRefresh: function () {
    this.setData({
      isRefreshing: true,
      page: 1
    });
    this.loadNewsList(true).finally(() => {
      wx.stopPullDownRefresh();
      this.setData({ isRefreshing: false });
    });
  },

  onReachBottom: function () {
    if (this.data.hasMore && !this.data.isLoading) {
      this.setData({
        page: this.data.page + 1
      });
      this.loadNewsList(false);
    }
  },

  initData: function () {
    this.setData({
      page: 1,
      newsList: [],
      hasMore: true
    });
    this.loadNewsList(true);
  },

  loadNewsList: function (isRefresh = true) {
    if (this.data.isLoading) return;

    this.setData({ isLoading: true });

    const params = {
      page: this.data.page,
      pageSize: this.data.pageSize,
      category: this.data.category
    };

    return cloud.getNewsList(params)
      .then(res => {
        const data = res.data;
        let newsList = [];
        
        if (isRefresh) {
          newsList = data.list;
        } else {
          newsList = this.data.newsList.concat(data.list);
        }

        this.setData({
          newsList: newsList,
          total: data.total,
          hasMore: data.hasMore,
          isLoading: false
        });
      })
      .catch(err => {
        console.error('加载资讯列表失败:', err);
        this.setData({ isLoading: false });
      });
  },

  onCategoryTap: function (e) {
    const index = e.currentTarget.dataset.index;
    const category = this.data.categories[index];
    
    if (index === this.data.currentCategoryIndex) return;

    this.setData({
      currentCategoryIndex: index,
      category: category.value,
      page: 1,
      newsList: [],
      hasMore: true
    });

    this.loadNewsList(true);
  },

  onNewsTap: function (e) {
    const newsId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${newsId}`
    });
  },

  onSearch: function (e) {
    const keyword = e.detail.value;
    this.setData({
      keyword: keyword,
      page: 1,
      newsList: [],
      hasMore: true
    });
    this.loadNewsList(true);
  }
});
