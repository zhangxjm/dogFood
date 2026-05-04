from datetime import datetime
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Article, Category, Tag
from app.utils import APIResponse

articles_bp = Blueprint('articles', __name__)

@articles_bp.route('/', methods=['GET'])
def get_articles():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    category_id = request.args.get('category_id', type=int)
    tag_id = request.args.get('tag_id', type=int)
    keyword = request.args.get('keyword', '')
    
    query = Article.query.filter_by(is_published=True)
    
    if category_id:
        query = query.filter_by(category_id=category_id)
    
    if tag_id:
        tag = Tag.query.get(tag_id)
        if tag:
            query = query.join(Article.tags).filter(Tag.id == tag_id)
    
    if keyword:
        query = query.filter(
            (Article.title.contains(keyword)) | 
            (Article.content.contains(keyword))
        )
    
    query = query.order_by(Article.created_at.desc())
    pagination = query.paginate(page=page, per_page=per_page, error_out=False)
    
    return APIResponse.paginate(
        [article.to_dict() for article in pagination.items],
        page,
        per_page,
        pagination.total
    )

@articles_bp.route('/archive', methods=['GET'])
def get_archive():
    articles = Article.query.filter_by(is_published=True).order_by(Article.created_at.desc()).all()
    
    archive = {}
    for article in articles:
        year_month = article.created_at.strftime('%Y年%m月')
        if year_month not in archive:
            archive[year_month] = []
        archive[year_month].append({
            'id': article.id,
            'title': article.title,
            'created_at': article.created_at.isoformat()
        })
    
    result = [{'month': key, 'articles': value} for key, value in archive.items()]
    
    return APIResponse.success(result)

@articles_bp.route('/popular', methods=['GET'])
def get_popular_articles():
    limit = request.args.get('limit', 5, type=int)
    articles = Article.query.filter_by(is_published=True).order_by(
        Article.views.desc()
    ).limit(limit).all()
    
    return APIResponse.success([article.to_dict() for article in articles])

@articles_bp.route('/<int:article_id>', methods=['GET'])
def get_article(article_id):
    article = Article.query.get(article_id)
    
    if not article:
        return APIResponse.not_found('文章不存在')
    
    article.views += 1
    db.session.commit()
    
    return APIResponse.success(article.to_dict())

@articles_bp.route('/', methods=['POST'])
@jwt_required()
def create_article():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限创建文章')
    
    data = request.get_json()
    
    if not data or not data.get('title') or not data.get('content'):
        return APIResponse.error('标题和内容不能为空')
    
    article = Article(
        title=data.get('title'),
        content=data.get('content'),
        summary=data.get('summary'),
        cover_image=data.get('cover_image'),
        is_published=data.get('is_published', True)
    )
    
    if data.get('category_id'):
        category = Category.query.get(data.get('category_id'))
        if category:
            article.category = category
    
    if data.get('tag_ids'):
        for tag_id in data.get('tag_ids'):
            tag = Tag.query.get(tag_id)
            if tag:
                article.tags.append(tag)
    
    db.session.add(article)
    db.session.commit()
    
    return APIResponse.success(article.to_dict(), '创建成功')

@articles_bp.route('/<int:article_id>', methods=['PUT'])
@jwt_required()
def update_article(article_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限修改文章')
    
    article = Article.query.get(article_id)
    
    if not article:
        return APIResponse.not_found('文章不存在')
    
    data = request.get_json()
    
    if data.get('title'):
        article.title = data.get('title')
    if data.get('content'):
        article.content = data.get('content')
    if data.get('summary') is not None:
        article.summary = data.get('summary')
    if data.get('cover_image') is not None:
        article.cover_image = data.get('cover_image')
    if data.get('is_published') is not None:
        article.is_published = data.get('is_published')
    
    if data.get('category_id'):
        category = Category.query.get(data.get('category_id'))
        if category:
            article.category = category
    elif data.get('category_id') == 0:
        article.category = None
    
    if data.get('tag_ids') is not None:
        article.tags = []
        for tag_id in data.get('tag_ids'):
            tag = Tag.query.get(tag_id)
            if tag:
                article.tags.append(tag)
    
    article.updated_at = datetime.utcnow()
    db.session.commit()
    
    return APIResponse.success(article.to_dict(), '更新成功')

@articles_bp.route('/<int:article_id>', methods=['DELETE'])
@jwt_required()
def delete_article(article_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限删除文章')
    
    article = Article.query.get(article_id)
    
    if not article:
        return APIResponse.not_found('文章不存在')
    
    db.session.delete(article)
    db.session.commit()
    
    return APIResponse.success(None, '删除成功')
