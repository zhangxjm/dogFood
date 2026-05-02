const cloud = require('wx-server-sdk');

cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
const collectionsCollection = db.collection('collections');

exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const openid = wxContext.OPENID;
  const { newsId, newsInfo } = event;

  if (!newsId) {
    return {
      success: false,
      data: null,
      message: '缺少资讯ID参数'
    };
  }

  try {
    const existingCollection = await collectionsCollection.where({
      _openid: openid,
      newsId: newsId
    }).get();

    if (existingCollection.data.length > 0) {
      const deleteResult = await collectionsCollection.doc(existingCollection.data[0]._id).remove();
      
      return {
        success: true,
        data: {
          isCollected: false
        },
        message: '取消收藏成功'
      };
    } else {
      const addResult = await collectionsCollection.add({
        data: {
          _openid: openid,
          newsId: newsId,
          newsTitle: newsInfo ? newsInfo.title : '',
          newsCover: newsInfo ? newsInfo.cover : '',
          newsCategory: newsInfo ? newsInfo.category : '',
          createTime: db.serverDate()
        }
      });

      return {
        success: true,
        data: {
          isCollected: true
        },
        message: '收藏成功'
      };
    }
  } catch (error) {
    console.error('切换收藏状态云函数执行失败:', error);
    return {
      success: false,
      data: null,
      message: error.message || '操作失败'
    };
  }
};
