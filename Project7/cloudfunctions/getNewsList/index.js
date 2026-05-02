const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const newsCollection = db.collection('news');
const _ = db.command;

const MAX_LIMIT = 100;

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;
  const { page = 1, pageSize = 10, category = '', keyword = '' } = event;

  try {
    let query = {};

    if (category && category !== '') {
      query.category = category;
    }

    if (keyword && keyword !== '') {
      query.title = db.RegExp({
        regexp: keyword,
        options: 'i'
      });
    }

    const countResult = await newsCollection.where(query).count();
    const total = countResult.total;

    const totalPage = Math.ceil(total / pageSize);

    const result = await newsCollection
      .where(query)
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
      message: '获取资讯列表成功'
    };
  } catch (error) {
    console.error('获取资讯列表云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '获取资讯列表失败'
    };
  }
};
