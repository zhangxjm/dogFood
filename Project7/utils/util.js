const formatTime = (date) => {
  if (!(date instanceof Date)) {
    date = new Date(date);
  }
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();

  return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute, second].map(formatNumber).join(':')}`;
};

const formatDate = (date) => {
  if (!(date instanceof Date)) {
    date = new Date(date);
  }
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();

  return `${[year, month, day].map(formatNumber).join('-')}`;
};

const formatNumber = (n) => {
  n = n.toString();
  return n[1] ? n : `0${n}`;
};

const formatViewCount = (count) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + 'w';
  } else if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k';
  }
  return count.toString();
};

const getTimeDiff = (timestamp) => {
  const now = Date.now();
  const diff = now - timestamp;
  
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const month = 30 * day;

  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前';
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前';
  } else {
    return formatDate(timestamp);
  }
};

const debounce = (fn, delay = 300) => {
  let timer = null;
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
};

const throttle = (fn, delay = 300) => {
  let lastTime = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastTime >= delay) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
};

const checkIsLogin = () => {
  const app = getApp();
  return app.globalData.isLogged;
};

const getStorage = (key) => {
  try {
    const value = wx.getStorageSync(key);
    return value;
  } catch (e) {
    console.error('读取缓存失败:', e);
    return null;
  }
};

const setStorage = (key, value) => {
  try {
    wx.setStorageSync(key, value);
    return true;
  } catch (e) {
    console.error('写入缓存失败:', e);
    return false;
  }
};

const removeStorage = (key) => {
  try {
    wx.removeStorageSync(key);
    return true;
  } catch (e) {
    console.error('删除缓存失败:', e);
    return false;
  }
};

module.exports = {
  formatTime,
  formatDate,
  formatNumber,
  formatViewCount,
  getTimeDiff,
  debounce,
  throttle,
  checkIsLogin,
  getStorage,
  setStorage,
  removeStorage
};
