const app = getApp();

const callFunction = (name, data = {}) => {
  return new Promise((resolve, reject) => {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });

    wx.cloud.callFunction({
      name: name,
      data: data,
      success: (res) => {
        wx.hideLoading();
        if (res.result && res.result.success) {
          resolve(res.result);
        } else {
          wx.showToast({
            title: res.result ? res.result.message : '请求失败',
            icon: 'none',
            duration: 2000
          });
          reject(res.result || { message: '请求失败' });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        console.error('云函数调用失败:', err);
        wx.showToast({
          title: '网络错误，请稍后重试',
          icon: 'none',
          duration: 2000
        });
        reject(err);
      }
    });
  });
};

const callFunctionWithoutLoading = (name, data = {}) => {
  return new Promise((resolve, reject) => {
    wx.cloud.callFunction({
      name: name,
      data: data,
      success: (res) => {
        if (res.result && res.result.success) {
          resolve(res.result);
        } else {
          reject(res.result || { message: '请求失败' });
        }
      },
      fail: (err) => {
        console.error('云函数调用失败:', err);
        reject(err);
      }
    });
  });
};

const login = () => {
  return callFunction('login', {});
};

const updateUserInfo = (userInfo) => {
  return callFunction('updateUserInfo', {
    userInfo: userInfo
  });
};

const getNewsList = (params = {}) => {
  return callFunction('getNewsList', params);
};

const getNewsDetail = (newsId) => {
  return callFunction('getNewsDetail', {
    newsId: newsId
  });
};

const toggleCollection = (newsId, newsInfo = null) => {
  return callFunction('toggleCollection', {
    newsId: newsId,
    newsInfo: newsInfo
  });
};

const getCollections = (params = {}) => {
  return callFunction('getCollections', params);
};

module.exports = {
  callFunction,
  callFunctionWithoutLoading,
  login,
  updateUserInfo,
  getNewsList,
  getNewsDetail,
  toggleCollection,
  getCollections
};
