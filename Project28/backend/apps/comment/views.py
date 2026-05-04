from rest_framework import viewsets, permissions
from rest_framework.decorators import action
from apps.comment.models import Comment
from apps.comment.serializers import CommentSerializer, CommentCreateSerializer
from apps.utils import ApiResponse


class CommentViewSet(viewsets.ModelViewSet):
    queryset = Comment.objects.filter(is_violation=False, parent=None).order_by('-created_at')
    permission_classes = [permissions.AllowAny]
    
    def get_serializer_class(self):
        if self.action == 'create':
            return CommentCreateSerializer
        return CommentSerializer
    
    def get_permissions(self):
        if self.action in ['create', 'update', 'partial_update', 'destroy']:
            return [permissions.IsAuthenticated()]
        return super().get_permissions()
    
    def get_queryset(self):
        queryset = Comment.objects.filter(is_violation=False, parent=None).order_by('-created_at')
        item_id = self.request.query_params.get('item')
        
        if item_id:
            queryset = queryset.filter(item_id=item_id)
        
        return queryset
    
    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        serializer = self.get_serializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        return ApiResponse.success(serializer.data)
    
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            comment = serializer.save(user=request.user)
            return ApiResponse.success(
                CommentSerializer(comment).data, 
                '评论发表成功'
            )
        return ApiResponse.error('发表失败', data=serializer.errors)
    
    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限删除此评论', code=403)
        
        instance.delete()
        return ApiResponse.success(message='删除成功')
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def mark_violation(self, request, pk=None):
        comment = self.get_object()
        comment.is_violation = True
        comment.violation_note = request.data.get('note', '')
        comment.save()
        return ApiResponse.success(message='已标记为违规')
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def unmark_violation(self, request, pk=None):
        comment = self.get_object()
        comment.is_violation = False
        comment.save()
        return ApiResponse.success(message='已取消违规标记')
    
    @action(detail=False, methods=['get'], permission_classes=[permissions.IsAdminUser])
    def violations(self, request):
        queryset = Comment.objects.filter(is_violation=True).order_by('-created_at')
        serializer = CommentSerializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
