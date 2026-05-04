from flask import jsonify

class APIResponse:
    @staticmethod
    def success(data=None, message='操作成功'):
        return jsonify({
            'code': 200,
            'message': message,
            'data': data
        })
    
    @staticmethod
    def error(message='操作失败', code=400):
        return jsonify({
            'code': code,
            'message': message,
            'data': None
        })
    
    @staticmethod
    def unauthorized(message='未授权访问'):
        return jsonify({
            'code': 401,
            'message': message,
            'data': None
        })
    
    @staticmethod
    def not_found(message='资源不存在'):
        return jsonify({
            'code': 404,
            'message': message,
            'data': None
        })
    
    @staticmethod
    def paginate(data, page, per_page, total):
        return jsonify({
            'code': 200,
            'message': '获取成功',
            'data': {
                'items': data,
                'pagination': {
                    'page': page,
                    'per_page': per_page,
                    'total': total,
                    'total_pages': (total + per_page - 1) // per_page
                }
            }
        })
