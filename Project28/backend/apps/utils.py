from rest_framework.response import Response
from rest_framework import status


class ApiResponse:
    
    @staticmethod
    def success(data=None, message='操作成功', code=200):
        return Response({
            'code': code,
            'message': message,
            'data': data
        }, status=status.HTTP_200_OK)
    
    @staticmethod
    def error(message='操作失败', code=400, data=None):
        return Response({
            'code': code,
            'message': message,
            'data': data
        }, status=status.HTTP_200_OK)
    
    @staticmethod
    def paginated_response(paginated_data, serializer, message='获取成功'):
        return Response({
            'code': 200,
            'message': message,
            'data': {
                'list': serializer.data,
                'total': paginated_data.paginator.count,
                'page': paginated_data.number,
                'page_size': paginated_data.paginator.per_page,
                'total_pages': paginated_data.paginator.num_pages
            }
        })
