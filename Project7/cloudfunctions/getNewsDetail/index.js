const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const newsCollection = db.collection('news');
const collectionsCollection = db.collection('collections');
const _ = db.command;

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;
  const { newsId } = event;

  if (!newsId) {
    return {
      success: false,
      data: null,
      message: '缺少资讯ID参数'
    };
  }

  try {
    const newsResult = await newsCollection.doc(newsId).get();
    
    if (!newsResult.data) {
      return {
        success: false,
        data: null,
        message: '资讯不存在'
      };
    }

    await newsCollection.doc(newsId).update({
      data: {
        views: _.inc(1)
      }
    });

    let isCollected = false;
    if (openid) {
      const collectionResult = await collectionsCollection.where({
        _openid: openid,
        newsId: newsId
      }).get();
      
      isCollected = collectionResult.data.length > 0;
    }

    return {
      success: true,
      data: {
        ...newsResult.data,
        isCollected
      },
      message: '获取资讯详情成功'
    };
  } catch (error) {
    console.error('获取资讯详情云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '获取资讯详情失败'
    };
  }
};
