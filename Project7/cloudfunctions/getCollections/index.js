const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const collectionsCollection = db.collection('collections');
const _ = db.command;

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;
  const { page = 1, pageSize = 10 } = event;

  try {
    const countResult = await collectionsCollection.where({
      _openid: openid
    }).count();
    const total = countResult.total;

    const totalPage = Math.ceil(total / pageSize);

    const result = await collectionsCollection
      .where({
        _openid: openid
      })
      .orderBy('createTime', 'desc')
      .skip((page - 1) * pageSize)
      .limit(pageSize)
      .get();

    return {
      success: true,
      data: {
        list: result.data,
        total,
        page,
        pageSize,
        totalPage,
        hasMore: page < totalPage
      },
      message: '获取收藏列表成功'
    };
  } catch (error) {
    console.error('获取收藏列表云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '获取收藏列表失败'
    };
  }
};
