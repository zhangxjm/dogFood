from flask import Blueprint, request
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity
from app.models import db, User
from app.utils import APIResponse

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    
    if not data or not data.get('username') or not data.get('password'):
        return APIResponse.error('用户名和密码不能为空')
    
    if User.query.filter_by(username=data.get('username')).first():
        return APIResponse.error('用户名已存在')
    
    user = User(
        username=data.get('username'),
        email=data.get('email')
    )
    user.set_password(data.get('password'))
    user.is_admin = data.get('is_admin', False)
    
    db.session.add(user)
    db.session.commit()
    
    return APIResponse.success(user.to_dict(), '注册成功')

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    
    if not data or not data.get('username') or not data.get('password'):
        return APIResponse.error('用户名和密码不能为空')
    
    user = User.query.filter_by(username=data.get('username')).first()
    
    if not user or not user.check_password(data.get('password')):
        return APIResponse.error('用户名或密码错误', 401)
    
    access_token = create_access_token(identity=str(user.id))
    
    return APIResponse.success({
        'access_token': access_token,
        'user': user.to_dict()
    }, '登录成功')

@auth_bp.route('/me', methods=['GET'])
@jwt_required()
def get_current_user():
    user_id = int(get_jwt_identity())
    user = User.query.get(user_id)
    
    if not user:
        return APIResponse.unauthorized('用户不存在')
    
    return APIResponse.success(user.to_dict())

@auth_bp.route('/users', methods=['GET'])
@jwt_required()
def get_users():
    user_id = int(get_jwt_identity())
    current_user = User.query.get(user_id)
    
    if not current_user or not current_user.is_admin:
        return APIResponse.unauthorized('无权限访问')
    
    users = User.query.all()
    
    return APIResponse.success([user.to_dict() for user in users])

@auth_bp.route('/users/<int:user_id>', methods=['PUT'])
@jwt_required()
def update_user(user_id):
    current_user_id = int(get_jwt_identity())
    current_user = User.query.get(current_user_id)
    
    if not current_user:
        return APIResponse.unauthorized('用户不存在')
    
    if current_user.id != user_id and not current_user.is_admin:
        return APIResponse.unauthorized('无权限修改')
    
    user = User.query.get(user_id)
    if not user:
        return APIResponse.not_found('用户不存在')
    
    data = request.get_json()
    
    if data.get('username'):
        existing_user = User.query.filter_by(username=data.get('username')).first()
        if existing_user and existing_user.id != user_id:
            return APIResponse.error('用户名已存在')
        user.username = data.get('username')
    
    if data.get('email'):
        user.email = data.get('email')
    
    if data.get('password'):
        user.set_password(data.get('password'))
    
    db.session.commit()
    
    return APIResponse.success(user.to_dict(), '更新成功')
