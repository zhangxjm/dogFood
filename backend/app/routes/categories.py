from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Category
from app.utils import APIResponse

categories_bp = Blueprint('categories', __name__)

@categories_bp.route('/', methods=['GET'])
def get_categories():
    categories = Category.query.all()
    return APIResponse.success([category.to_dict() for category in categories])

@categories_bp.route('/<int:category_id>', methods=['GET'])
def get_category(category_id):
    category = Category.query.get(category_id)
    
    if not category:
        return APIResponse.not_found('分类不存在')
    
    return APIResponse.success(category.to_dict())

@categories_bp.route('/', methods=['POST'])
@jwt_required()
def create_category():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限创建分类')
    
    data = request.get_json()
    
    if not data or not data.get('name'):
        return APIResponse.error('分类名称不能为空')
    
    if Category.query.filter_by(name=data.get('name')).first():
        return APIResponse.error('分类名称已存在')
    
    category = Category(
        name=data.get('name'),
        description=data.get('description')
    )
    
    db.session.add(category)
    db.session.commit()
    
    return APIResponse.success(category.to_dict(), '创建成功')

@categories_bp.route('/<int:category_id>', methods=['PUT'])
@jwt_required()
def update_category(category_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限修改分类')
    
    category = Category.query.get(category_id)
    
    if not category:
        return APIResponse.not_found('分类不存在')
    
    data = request.get_json()
    
    if data.get('name'):
        existing_category = Category.query.filter_by(name=data.get('name')).first()
        if existing_category and existing_category.id != category_id:
            return APIResponse.error('分类名称已存在')
        category.name = data.get('name')
    
    if data.get('description') is not None:
        category.description = data.get('description')
    
    db.session.commit()
    
    return APIResponse.success(category.to_dict(), '更新成功')

@categories_bp.route('/<int:category_id>', methods=['DELETE'])
@jwt_required()
def delete_category(category_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限删除分类')
    
    category = Category.query.get(category_id)
    
    if not category:
        return APIResponse.not_found('分类不存在')
    
    if category.articles:
        return APIResponse.error('分类下有文章，无法删除')
    
    db.session.delete(category)
    db.session.commit()
    
    return APIResponse.success(None, '删除成功')
