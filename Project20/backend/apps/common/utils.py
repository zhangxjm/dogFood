from rest_framework.response import Response
from rest_framework import status


class APIResponse(Response):
    """
    统一API响应格式
    """
    
    def __init__(self, data=None, code=200, message='success', status=None, **kwargs):
        standard_data = {
            'code': code,
            'message': message,
            'data': data
        }
        if status is None:
            status = self._get_status_code(code)
        super().__init__(standard_data, status=status, **kwargs)
    
    @staticmethod
    def _get_status_code(code):
        """
        根据业务代码获取HTTP状态码
        """
        status_map = {
            200: status.HTTP_200_OK,
            201: status.HTTP_201_CREATED,
            400: status.HTTP_400_BAD_REQUEST,
            401: status.HTTP_401_UNAUTHORIZED,
            403: status.HTTP_403_FORBIDDEN,
            404: status.HTTP_404_NOT_FOUND,
            500: status.HTTP_500_INTERNAL_SERVER_ERROR,
        }
        return status_map.get(code, status.HTTP_200_OK)


def success_response(data=None, message='操作成功'):
    """
    成功响应
    """
    return APIResponse(data=data, code=200, message=message)


def created_response(data=None, message='创建成功'):
    """
    创建成功响应
    """
    return APIResponse(data=data, code=201, message=message)


def error_response(message='操作失败', code=400, data=None):
    """
    错误响应
    """
    return APIResponse(data=data, code=code, message=message)


def not_found_response(message='资源不存在'):
    """
    404响应
    """
    return APIResponse(code=404, message=message)


def unauthorized_response(message='未授权访问'):
    """
    401响应
    """
    return APIResponse(code=401, message=message)


def forbidden_response(message='无权限访问'):
    """
    403响应
    """
    return APIResponse(code=403, message=message)
