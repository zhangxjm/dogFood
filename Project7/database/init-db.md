# 云数据库初始化说明

## 数据库集合

请在微信云开发控制台创建以下数据库集合：

### 1. users (用户表)

**字段说明：**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| _id | string | 记录ID（自动生成） |
| _openid | string | 用户openid |
| nickName | string | 用户昵称 |
| avatarUrl | string | 用户头像URL |
| gender | number | 性别（0未知，1男，2女） |
| city | string | 城市 |
| province | string | 省份 |
| country | string | 国家 |
| createTime | date | 创建时间 |
| updateTime | date | 更新时间 |

**权限设置：**
- 仅创建者可写，所有人可读
- 或者：自定义权限规则（推荐）

```json
{
  "read": true,
  "write": "doc._openid == auth.openid"
}
```

### 2. news (资讯表)

**字段说明：**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| _id | string | 记录ID（自动生成） |
| title | string | 资讯标题 |
| summary | string | 资讯简介 |
| content | string/html | 资讯内容（支持rich-text） |
| cover | string | 封面图片URL |
| author | string | 作者 |
| category | string | 分类（tech/life/entertainment/sports） |
| views | number | 阅读量 |
| likes | number | 点赞数 |
| status | number | 状态（0隐藏，1显示） |
| _openid | string | 创建者openid |
| createTime | date | 创建时间 |
| updateTime | date | 更新时间 |

**权限设置：**
- 所有人可读，仅管理员可写
- 或者：自定义权限规则

```json
{
  "read": true,
  "write": "auth.openid == '你的管理员openid'"
}
```

### 3. collections (收藏表)

**字段说明：**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| _id | string | 记录ID（自动生成） |
| _openid | string | 用户openid |
| newsId | string | 资讯ID |
| newsTitle | string | 资讯标题（冗余字段，方便展示） |
| newsCover | string | 资讯封面（冗余字段） |
| newsCategory | string | 资讯分类（冗余字段） |
| createTime | date | 创建时间 |

**权限设置：**
- 仅创建者可读写
- 或者：自定义权限规则

```json
{
  "read": "doc._openid == auth.openid",
  "write": "doc._openid == auth.openid"
}
```

## 导入测试数据

### 资讯测试数据示例

```json
[
  {
    "title": "微信小程序开发入门教程",
    "summary": "本文将详细介绍微信小程序的开发基础知识，帮助开发者快速上手小程序开发。",
    "content": "<h2>小程序简介</h2><p>微信小程序是一种不需要下载安装即可使用的应用，它实现了应用「触手可及」的梦想。</p><h2>开发环境搭建</h2><p>1. 下载微信开发者工具</p><p>2. 注册小程序账号</p><p>3. 创建项目</p>",
    "cover": "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=wechat%20miniprogram%20development%20tutorial&image_size=landscape_4_3",
    "author": "技术团队",
    "category": "tech",
    "views": 1234,
    "likes": 86,
    "status": 1,
    "createTime": {
      "$date": "2026-01-01T00:00:00.000Z"
    },
    "updateTime": {
      "$date": "2026-01-01T00:00:00.000Z"
    }
  },
  {
    "title": "健康生活小常识",
    "summary": "分享一些实用的健康生活小常识，帮助你养成良好的生活习惯。",
    "content": "<h2>饮食健康</h2><p>保持均衡饮食，多吃蔬菜水果，少吃油腻辛辣食物。</p><h2>作息规律</h2><p>每天保证8小时睡眠，早睡早起身体好。</p>",
    "cover": "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=healthy%20lifestyle%20tips&image_size=landscape_4_3",
    "author": "生活编辑部",
    "category": "life",
    "views": 856,
    "likes": 120,
    "status": 1,
    "createTime": {
      "$date": "2026-01-02T00:00:00.000Z"
    },
    "updateTime": {
      "$date": "2026-01-02T00:00:00.000Z"
    }
  },
  {
    "title": "2026年最值得期待的电影",
    "summary": "盘点2026年即将上映的热门电影，科幻大片、爱情喜剧应有尽有。",
    "content": "<h2>科幻大片</h2><p>多部科幻巨制即将上映，带你探索未知的宇宙世界。</p><h2>爱情喜剧</h2><p>温馨浪漫的爱情故事，让你笑中带泪。</p>",
    "cover": "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=movie%20poster%20cinema%20entertainment&image_size=landscape_4_3",
    "author": "娱乐频道",
    "category": "entertainment",
    "views": 2345,
    "likes": 312,
    "status": 1,
    "createTime": {
      "$date": "2026-01-03T00:00:00.000Z"
    },
    "updateTime": {
      "$date": "2026-01-03T00:00:00.000Z"
    }
  }
]
```

## 云数据库操作API

### 查询数据
```javascript
const db = wx.cloud.database();
db.collection('news')
  .where({
    category: 'tech'
  })
  .orderBy('createTime', 'desc')
  .get()
  .then(res => {
    console.log(res.data);
  });
```

### 添加数据
```javascript
const db = wx.cloud.database();
db.collection('news').add({
  data: {
    title: '新的资讯标题',
    content: '资讯内容...'
  }
});
```

### 更新数据
```javascript
const db = wx.cloud.database();
const _ = db.command;
db.collection('news').doc('news_id').update({
  data: {
    views: _.inc(1)
  }
});
```

### 删除数据
```javascript
const db = wx.cloud.database();
db.collection('news').doc('news_id').remove();
```
