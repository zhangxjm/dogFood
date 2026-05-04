from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Article, Comment
from app.utils import APIResponse

comments_bp = Blueprint('comments', __name__)

@comments_bp.route('/article/<int:article_id>', methods=['GET'])
def get_article_comments(article_id):
    article = Article.query.get(article_id)
    
    if not article:
        return APIResponse.not_found('文章不存在')
    
    comments = Comment.query.filter_by(
        article_id=article_id,
        is_approved=True,
        parent_id=None
    ).order_by(Comment.created_at.desc()).all()
    
    return APIResponse.success([comment.to_dict() for comment in comments])

@comments_bp.route('/', methods=['GET'])
@jwt_required()
def get_all_comments():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限访问')
    
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_approved = request.args.get('is_approved', type=bool)
    
    query = Comment.query
    
    if is_approved is not None:
        query = query.filter_by(is_approved=is_approved)
    
    pagination = query.order_by(Comment.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return APIResponse.paginate(
        [comment.to_dict() for comment in pagination.items],
        page,
        per_page,
        pagination.total
    )

@comments_bp.route('/', methods=['POST'])
def create_comment():
    data = request.get_json()
    
    if not data or not data.get('content') or not data.get('nickname'):
        return APIResponse.error('评论内容和昵称不能为空')
    
    if not data.get('article_id'):
        return APIResponse.error('文章ID不能为空')
    
    article = Article.query.get(data.get('article_id'))
    
    if not article:
        return APIResponse.not_found('文章不存在')
    
    comment = Comment(
        content=data.get('content'),
        nickname=data.get('nickname'),
        email=data.get('email'),
        article_id=data.get('article_id'),
        is_approved=True
    )
    
    if data.get('parent_id'):
        parent_comment = Comment.query.get(data.get('parent_id'))
        if parent_comment:
            comment.parent_id = data.get('parent_id')
    
    db.session.add(comment)
    db.session.commit()
    
    return APIResponse.success(comment.to_dict(), '评论成功')

@comments_bp.route('/<int:comment_id>/approve', methods=['PUT'])
@jwt_required()
def approve_comment(comment_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限操作')
    
    comment = Comment.query.get(comment_id)
    
    if not comment:
        return APIResponse.not_found('评论不存在')
    
    comment.is_approved = True
    db.session.commit()
    
    return APIResponse.success(comment.to_dict(), '审核通过')

@comments_bp.route('/<int:comment_id>', methods=['DELETE'])
@jwt_required()
def delete_comment(comment_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限删除评论')
    
    comment = Comment.query.get(comment_id)
    
    if not comment:
        return APIResponse.not_found('评论不存在')
    
    db.session.delete(comment)
    db.session.commit()
    
    return APIResponse.success(None, '删除成功')
