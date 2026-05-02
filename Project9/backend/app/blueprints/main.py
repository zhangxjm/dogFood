from flask import Blueprint, jsonify

main_bp = Blueprint('main', __name__)

@main_bp.route('/')
def index():
    return jsonify({
        'message': 'Data Management API',
        'version': '1.0.0',
        'endpoints': {
            'auth': '/api/auth',
            'data': '/api/data'
        }
    })

@main_bp.route('/health')
def health():
    return jsonify({'status': 'ok'})
