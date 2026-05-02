App({
  globalData: {
    userInfo: null,
    openid: null,
    isLogged: false
  },

  onLaunch: function () {
    if (!wx.cloud) {
      console.error('请使用 2.2.3 或以上的基础库以使用云能力');
    } else {
      wx.cloud.init({
        env: 'your-cloud-env-id',
        traceUser: true,
      });
    }
    this.checkLoginStatus();
  },

  checkLoginStatus: function () {
    const userInfo = wx.getStorageSync('userInfo');
    const openid = wx.getStorageSync('openid');
    
    if (userInfo && openid) {
      this.globalData.userInfo = userInfo;
      this.globalData.openid = openid;
      this.globalData.isLogged = true;
    }
  },

  setUserInfo: function (userInfo) {
    this.globalData.userInfo = userInfo;
    wx.setStorageSync('userInfo', userInfo);
  },

  setOpenid: function (openid) {
    this.globalData.openid = openid;
    wx.setStorageSync('openid', openid);
  },

  setLoginStatus: function (status) {
    this.globalData.isLogged = status;
  }
})
