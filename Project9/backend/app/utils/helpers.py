from functools import wraps
from flask import request, jsonify, g
from marshmallow import ValidationError
from app import logger

def success_response(data=None, message='Success', code=200):
    response = {
        'success': True,
        'message': message
    }
    if data is not None:
        response['data'] = data
    return jsonify(response), code

def error_response(message='Error', code=400, errors=None):
    response = {
        'success': False,
        'message': message
    }
    if errors is not None:
        response['errors'] = errors
    return jsonify(response), code

def validate_request(schema):
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            if request.is_json:
                data = request.get_json()
            else:
                data = request.form.to_dict()
                if request.files:
                    data['files'] = request.files
            
            try:
                validated_data = schema().load(data)
                g.validated_data = validated_data
            except ValidationError as err:
                logger.warning(f'Validation error: {err.messages}')
                return error_response(
                    message='Validation failed',
                    code=400,
                    errors=err.messages
                )
            
            return f(*args, **kwargs)
        return decorated_function
    return decorator
