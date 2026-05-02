const showLoading = (title = '加载中...', mask = true) => {
  wx.showLoading({
    title: title,
    mask: mask
  });
};

const hideLoading = () => {
  wx.hideLoading();
};

const showToast = (title, icon = 'none', duration = 2000) => {
  wx.showToast({
    title: title,
    icon: icon,
    duration: duration
  });
};

const showSuccess = (title, duration = 2000) => {
  wx.showToast({
    title: title,
    icon: 'success',
    duration: duration
  });
};

const showError = (title, duration = 2000) => {
  wx.showToast({
    title: title,
    icon: 'none',
    duration: duration
  });
};

const showModal = (title, content, showCancel = true) => {
  return new Promise((resolve, reject) => {
    wx.showModal({
      title: title,
      content: content,
      showCancel: showCancel,
      confirmColor: '#1AAD19',
      success: (res) => {
        resolve(res);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};

const showActionSheet = (itemList) => {
  return new Promise((resolve, reject) => {
    wx.showActionSheet({
      itemList: itemList,
      success: (res) => {
        resolve(res.tapIndex);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};

module.exports = {
  showLoading,
  hideLoading,
  showToast,
  showSuccess,
  showError,
  showModal,
  showActionSheet
};
