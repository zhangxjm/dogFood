from app import create_app
from app.models import db, User, Category, Tag, Article, Comment, Banner
from datetime import datetime

app = create_app('development')

with app.app_context():
    db.drop_all()
    db.create_all()
    
    admin = User(
        username='admin',
        email='admin@example.com',
        is_admin=True
    )
    admin.set_password('admin123')
    db.session.add(admin)
    
    category1 = Category(name='技术', description='技术相关文章')
    category2 = Category(name='生活', description='生活感悟')
    category3 = Category(name='教程', description='教程分享')
    db.session.add_all([category1, category2, category3])
    
    tag1 = Tag(name='Python')
    tag2 = Tag(name='Flask')
    tag3 = Tag(name='Vue')
    tag4 = Tag(name='前端')
    tag5 = Tag(name='后端')
    db.session.add_all([tag1, tag2, tag3, tag4, tag5])
    
    db.session.commit()
    
    article1 = Article(
        title='Flask 框架入门教程',
        content='## Flask 介绍\n\nFlask 是一个轻量级的 Python Web 框架，由 Armin Ronacher 开发。它被称为 "微框架"，因为它不包含数据库抽象层、表单验证等功能，但可以通过扩展来添加这些功能。\n\n### 安装 Flask\n\n```bash\npip install flask\n```\n\n### 简单示例\n\n```python\nfrom flask import Flask\n\napp = Flask(__name__)\n\n@app.route(\'/\')\ndef hello():\n    return \'Hello, World!\'\n\nif __name__ == \'__main__\':\n    app.run(debug=True)\n```\n\n### 路由\n\nFlask 使用 @app.route() 装饰器来定义路由。你可以定义动态路由，接收 URL 参数。\n\n```python\n@app.route(\'/user/<username>\')\ndef show_user_profile(username):\n    return f\'User {username}\'\n\n@app.route(\'/post/<int:post_id>\')\ndef show_post(post_id):\n    return f\'Post {post_id}\'\n```',
        summary='Flask 是一个轻量级的 Python Web 框架，本文介绍 Flask 的基本使用方法。',
        is_published=True,
        category=category1,
        views=125
    )
    article1.tags.extend([tag1, tag2, tag5])
    
    article2 = Article(
        title='Vue 3 组合式 API 详解',
        content='## Vue 3 简介\n\nVue 3 带来了许多新特性，其中最重要的是组合式 API（Composition API）。\n\n### 为什么使用组合式 API\n\n1. **更好的逻辑复用**：可以将相关逻辑抽取到组合函数中\n2. **更灵活的代码组织**：相关代码可以放在一起\n3. **更好的 TypeScript 支持**：组合式 API 天然支持类型推导\n\n### 基本示例\n\n```javascript\nimport { ref, reactive, computed, onMounted } from \'vue\'\n\nexport default {\n  name: \'UserProfile\',\n  setup() {\n    const count = ref(0)\n    const user = reactive({ name: \'张三\', age: 25 })\n    \n    const doubleCount = computed(() => count.value * 2)\n    \n    const increment = () => {\n      count.value++\n    }\n    \n    onMounted(() => {\n      console.log(\'组件已挂载\')\n    })\n    \n    return {\n      count,\n      user,\n      doubleCount,\n      increment\n    }\n  }\n}\n```',
        summary='Vue 3 的组合式 API 提供了更好的代码组织和复用方式，本文详细介绍其使用方法。',
        is_published=True,
        category=category1,
        views=89
    )
    article2.tags.extend([tag3, tag4])
    
    article3 = Article(
        title='我的编程学习之路',
        content='## 前言\n\n作为一名程序员，我经常被问到：你是怎么开始学习编程的？今天我想分享一下我的学习经历。\n\n### 起步\n\n我的编程之旅始于大学时期。当时我主修计算机科学，第一门编程语言是 C++。说实话，刚开始学的时候我感到很困难，指针、内存管理这些概念让我头疼不已。\n\n### 转折点\n\n真正让我爱上编程的是 Python。Python 的简洁语法让我能够专注于解决问题，而不是纠结于语言本身。我开始用 Python 写一些小工具，比如爬虫、自动化脚本等，这让我获得了很大的成就感。\n\n### 持续学习\n\n编程是一个需要不断学习的领域。新技术、新框架层出不穷，保持学习的心态非常重要。我现在每天都会花一些时间阅读技术博客、观看教程视频。\n\n### 建议\n\n对于初学者，我的建议是：\n\n1. 不要急于求成，打好基础\n2. 多写代码，实践出真知\n3. 遇到问题先自己思考，再求助\n4. 保持好奇心和热情\n\n希望我的分享对你有所帮助！',
        summary='分享我从编程新手到熟练开发者的学习经历和心得。',
        is_published=True,
        category=category2,
        views=256
    )
    
    article4 = Article(
        title='使用 Flask-SQLAlchemy 操作数据库',
        content='## Flask-SQLAlchemy 简介\n\nFlask-SQLAlchemy 是一个为 Flask 应用添加 SQLAlchemy 支持的扩展。它简化了在 Flask 中使用 SQLAlchemy 的方式。\n\n### 安装\n\n```bash\npip install flask-sqlalchemy\n```\n\n### 配置\n\n```python\nfrom flask import Flask\nfrom flask_sqlalchemy import SQLAlchemy\n\napp = Flask(__name__)\napp.config[\'SQLALCHEMY_DATABASE_URI\'] = \'sqlite:///mydb.db\'\napp.config[\'SQLALCHEMY_TRACK_MODIFICATIONS\'] = False\n\ndb = SQLAlchemy(app)\n```\n\n### 定义模型\n\n```python\nclass User(db.Model):\n    id = db.Column(db.Integer, primary_key=True)\n    username = db.Column(db.String(80), unique=True, nullable=False)\n    email = db.Column(db.String(120), unique=True, nullable=False)\n\n    def __repr__(self):\n        return f\'<User {self.username}>\'\n```\n\n### 创建表\n\n```python\nwith app.app_context():\n    db.create_all()\n```\n\n### 基本操作\n\n#### 添加数据\n\n```python\nuser = User(username=\'john\', email=\'john@example.com\')\ndb.session.add(user)\ndb.session.commit()\n```\n\n#### 查询数据\n\n```python\n# 查询所有\nusers = User.query.all()\n\n# 按条件查询\nuser = User.query.filter_by(username=\'john\').first()\n\n# 分页查询\npagination = User.query.paginate(page=1, per_page=10)\n```\n\n#### 更新数据\n\n```python\nuser = User.query.get(1)\nuser.email = \'newemail@example.com\'\ndb.session.commit()\n```\n\n#### 删除数据\n\n```python\nuser = User.query.get(1)\ndb.session.delete(user)\ndb.session.commit()\n```',
        summary='详细介绍 Flask-SQLAlchemy 的配置、模型定义和基本数据库操作。',
        is_published=True,
        category=category3,
        views=178
    )
    article4.tags.extend([tag1, tag2, tag5])
    
    db.session.add_all([article1, article2, article3, article4])
    
    comment1 = Comment(
        content='写得很好，对初学者很有帮助！',
        nickname='读者A',
        email='reader@example.com',
        article=article1,
        is_approved=True
    )
    comment2 = Comment(
        content='组合式 API 确实更灵活，感谢分享！',
        nickname='前端小白',
        article=article2,
        is_approved=True
    )
    db.session.add_all([comment1, comment2])
    
    banner1 = Banner(
        title='欢迎来到我的博客',
        image_url='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20blog%20website%20banner%20with%20technology%20theme&image_size=landscape_16_9',
        order=1,
        is_active=True
    )
    banner2 = Banner(
        title='Python 全栈开发',
        image_url='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=python%20programming%20code%20on%20screen%20dark%20theme&image_size=landscape_16_9',
        order=2,
        is_active=True
    )
    db.session.add_all([banner1, banner2])
    
    db.session.commit()
    
    print('数据库初始化完成！')
    print('管理员账号: admin')
    print('管理员密码: admin123')
