from flask import Blueprint, request, g
from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required, get_current_user
from app import db, logger
from app.models import User
from app.schemas import RegisterSchema, LoginSchema, UserSchema
from app.utils import success_response, error_response, validate_request

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/register', methods=['POST'])
@validate_request(RegisterSchema)
def register():
    data = g.validated_data
    logger.info(f'Register attempt for username: {data["username"]}')
    
    user = User(
        username=data['username'],
        email=data.get('email')
    )
    user.set_password(data['password'])
    
    db.session.add(user)
    db.session.commit()
    
    logger.info(f'User registered successfully: {user.username}')
    return success_response(
        data=UserSchema().dump(user),
        message='User registered successfully',
        code=201
    )

@auth_bp.route('/login', methods=['POST'])
@validate_request(LoginSchema)
def login():
    data = g.validated_data
    logger.info(f'Login attempt for username: {data["username"]}')
    
    user = User.query.filter_by(username=data['username']).first()
    
    if not user or not user.check_password(data['password']):
        logger.warning(f'Login failed for username: {data["username"]}')
        return error_response(
            message='Invalid username or password',
            code=401
        )
    
    if not user.is_active:
        logger.warning(f'Inactive user attempted login: {data["username"]}')
        return error_response(
            message='User account is deactivated',
            code=403
        )
    
    access_token = create_access_token(identity=user.id)
    logger.info(f'Login successful for username: {user.username}')
    
    return success_response(
        data={
            'access_token': access_token,
            'user': UserSchema().dump(user)
        },
        message='Login successful'
    )

@auth_bp.route('/me', methods=['GET'])
@jwt_required()
def get_current_user_info():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return error_response(
            message='User not found',
            code=404
        )
    
    return success_response(
        data=UserSchema().dump(user)
    )

@auth_bp.route('/me', methods=['PUT'])
@jwt_required()
@validate_request(UserSchema)
def update_current_user():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return error_response(
            message='User not found',
            code=404
        )
    
    data = g.validated_data
    
    if 'username' in data and data['username'] != user.username:
        existing = User.query.filter_by(username=data['username']).first()
        if existing:
            return error_response(
                message='Username already exists',
                code=400
            )
        user.username = data['username']
    
    if 'email' in data and data.get('email') != user.email:
        if data.get('email'):
            existing = User.query.filter_by(email=data['email']).first()
            if existing:
                return error_response(
                    message='Email already exists',
                    code=400
                )
        user.email = data.get('email')
    
    db.session.commit()
    logger.info(f'User updated: {user.username}')
    
    return success_response(
        data=UserSchema().dump(user),
        message='User updated successfully'
    )

@auth_bp.route('/change-password', methods=['POST'])
@jwt_required()
def change_password():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return error_response(
            message='User not found',
            code=404
        )
    
    data = request.get_json() or {}
    old_password = data.get('old_password')
    new_password = data.get('new_password')
    
    if not old_password or not new_password:
        return error_response(
            message='Old password and new password are required',
            code=400
        )
    
    if not user.check_password(old_password):
        return error_response(
            message='Old password is incorrect',
            code=400
        )
    
    if len(new_password) < 6:
        return error_response(
            message='New password must be at least 6 characters',
            code=400
        )
    
    user.set_password(new_password)
    db.session.commit()
    logger.info(f'Password changed for user: {user.username}')
    
    return success_response(
        message='Password changed successfully'
    )
