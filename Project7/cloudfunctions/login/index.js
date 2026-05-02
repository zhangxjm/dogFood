const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const usersCollection = db.collection('users');

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;

  try {
    const userResult = await usersCollection.where({
      _openid: openid
    }).get();

    if (userResult.data.length === 0) {
      const createResult = await usersCollection.add({
        data: {
          _openid: openid,
          nickName: '',
          avatarUrl: '',
          gender: 0,
          city: '',
          province: '',
          country: '',
          createTime: db.serverDate(),
          updateTime: db.serverDate()
        }
      });

      return {
        success: true,
        data: {
          openid: openid,
          isNewUser: true,
          userInfo: null
        },
        message: '用户创建成功'
      };
    } else {
      return {
        success: true,
        data: {
          openid: openid,
          isNewUser: false,
          userInfo: userResult.data[0]
        },
        message: '登录成功'
      };
    }
  } catch (error) {
    console.error('登录云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '登录失败'
    };
  }
};
