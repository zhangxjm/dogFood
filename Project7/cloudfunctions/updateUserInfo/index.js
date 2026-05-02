const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const usersCollection = db.collection('users');

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;
  const { userInfo } = event;

  if (!userInfo) {
    return {
      success: false,
      data: null,
      message: '缺少用户信息参数'
    };
  }

  try {
    const updateData = {
      nickName: userInfo.nickName || '',
      avatarUrl: userInfo.avatarUrl || '',
      gender: userInfo.gender || 0,
      city: userInfo.city || '',
      province: userInfo.province || '',
      country: userInfo.country || '',
      updateTime: db.serverDate()
    };

    const updateResult = await usersCollection.where({
      _openid: openid
    }).update({
      data: updateData
    });

    if (updateResult.stats.updated > 0) {
      return {
        success: true,
        data: {
          ...updateData
        },
        message: '用户信息更新成功'
      };
    } else {
      return {
        success: false,
        data: null,
        message: '未找到用户记录'
      };
    }
  } catch (error) {
    console.error('更新用户信息云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '更新用户信息失败'
    };
  }
};
