from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Tag
from app.utils import APIResponse

tags_bp = Blueprint('tags', __name__)

@tags_bp.route('/', methods=['GET'])
def get_tags():
    tags = Tag.query.all()
    return APIResponse.success([tag.to_dict() for tag in tags])

@tags_bp.route('/<int:tag_id>', methods=['GET'])
def get_tag(tag_id):
    tag = Tag.query.get(tag_id)
    
    if not tag:
        return APIResponse.not_found('标签不存在')
    
    return APIResponse.success(tag.to_dict())

@tags_bp.route('/', methods=['POST'])
@jwt_required()
def create_tag():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限创建标签')
    
    data = request.get_json()
    
    if not data or not data.get('name'):
        return APIResponse.error('标签名称不能为空')
    
    if Tag.query.filter_by(name=data.get('name')).first():
        return APIResponse.error('标签名称已存在')
    
    tag = Tag(name=data.get('name'))
    
    db.session.add(tag)
    db.session.commit()
    
    return APIResponse.success(tag.to_dict(), '创建成功')

@tags_bp.route('/<int:tag_id>', methods=['PUT'])
@jwt_required()
def update_tag(tag_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限修改标签')
    
    tag = Tag.query.get(tag_id)
    
    if not tag:
        return APIResponse.not_found('标签不存在')
    
    data = request.get_json()
    
    if data.get('name'):
        existing_tag = Tag.query.filter_by(name=data.get('name')).first()
        if existing_tag and existing_tag.id != tag_id:
            return APIResponse.error('标签名称已存在')
        tag.name = data.get('name')
    
    db.session.commit()
    
    return APIResponse.success(tag.to_dict(), '更新成功')

@tags_bp.route('/<int:tag_id>', methods=['DELETE'])
@jwt_required()
def delete_tag(tag_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限删除标签')
    
    tag = Tag.query.get(tag_id)
    
    if not tag:
        return APIResponse.not_found('标签不存在')
    
    if tag.articles.count() > 0:
        return APIResponse.error('标签下有文章，无法删除')
    
    db.session.delete(tag)
    db.session.commit()
    
    return APIResponse.success(None, '删除成功')
