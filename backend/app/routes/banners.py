from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Banner
from app.utils import APIResponse

banners_bp = Blueprint('banners', __name__)

@banners_bp.route('/', methods=['GET'])
def get_banners():
    banners = Banner.query.filter_by(is_active=True).order_by(Banner.order).all()
    return APIResponse.success([banner.to_dict() for banner in banners])

@banners_bp.route('/all', methods=['GET'])
@jwt_required()
def get_all_banners():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限访问')
    
    banners = Banner.query.order_by(Banner.order).all()
    return APIResponse.success([banner.to_dict() for banner in banners])

@banners_bp.route('/', methods=['POST'])
@jwt_required()
def create_banner():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限创建轮播图')
    
    data = request.get_json()
    
    if not data or not data.get('title') or not data.get('image_url'):
        return APIResponse.error('标题和图片URL不能为空')
    
    banner = Banner(
        title=data.get('title'),
        image_url=data.get('image_url'),
        link_url=data.get('link_url'),
        is_active=data.get('is_active', True),
        order=data.get('order', 0)
    )
    
    db.session.add(banner)
    db.session.commit()
    
    return APIResponse.success(banner.to_dict(), '创建成功')

@banners_bp.route('/<int:banner_id>', methods=['PUT'])
@jwt_required()
def update_banner(banner_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限修改轮播图')
    
    banner = Banner.query.get(banner_id)
    
    if not banner:
        return APIResponse.not_found('轮播图不存在')
    
    data = request.get_json()
    
    if data.get('title'):
        banner.title = data.get('title')
    if data.get('image_url'):
        banner.image_url = data.get('image_url')
    if data.get('link_url') is not None:
        banner.link_url = data.get('link_url')
    if data.get('is_active') is not None:
        banner.is_active = data.get('is_active')
    if data.get('order') is not None:
        banner.order = data.get('order')
    
    db.session.commit()
    
    return APIResponse.success(banner.to_dict(), '更新成功')

@banners_bp.route('/<int:banner_id>', methods=['DELETE'])
@jwt_required()
def delete_banner(banner_id):
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user or not user.is_admin:
        return APIResponse.unauthorized('无权限删除轮播图')
    
    banner = Banner.query.get(banner_id)
    
    if not banner:
        return APIResponse.not_found('轮播图不存在')
    
    db.session.delete(banner)
    db.session.commit()
    
    return APIResponse.success(None, '删除成功')
