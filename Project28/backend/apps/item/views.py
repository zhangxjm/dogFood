from django.db.models import Q
from django.utils import timezone
from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.pagination import PageNumberPagination
from apps.item.models import Category, Item
from apps.item.serializers import (
    CategorySerializer, ItemListSerializer, ItemDetailSerializer,
    ItemCreateSerializer, ItemUpdateSerializer, ItemAdminUpdateSerializer
)
from apps.utils import ApiResponse


class CategoryViewSet(viewsets.ModelViewSet):
    queryset = Category.objects.filter(is_active=True).order_by('sort_order', '-created_at')
    serializer_class = CategorySerializer
    permission_classes = [permissions.AllowAny]
    
    def get_permissions(self):
        if self.action in ['create', 'update', 'partial_update', 'destroy']:
            return [permissions.IsAdminUser()]
        return super().get_permissions()
    
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
            serializer.save()
            return ApiResponse.success(serializer.data, '分类创建成功')
        return ApiResponse.error('创建失败', data=serializer.errors)
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        if serializer.is_valid():
            serializer.save()
            return ApiResponse.success(serializer.data, '分类更新成功')
        return ApiResponse.error('更新失败', data=serializer.errors)
    
    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        instance.is_active = False
        instance.save()
        return ApiResponse.success(message='分类已禁用')


class ItemPagination(PageNumberPagination):
    page_size = 10
    page_size_query_param = 'page_size'
    max_page_size = 100


class ItemViewSet(viewsets.ModelViewSet):
    queryset = Item.objects.all().order_by('-created_at')
    pagination_class = ItemPagination
    permission_classes = [permissions.AllowAny]
    
    def get_queryset(self):
        queryset = super().get_queryset()
        item_type = self.request.query_params.get('type')
        category_id = self.request.query_params.get('category')
        status = self.request.query_params.get('status')
        keyword = self.request.query_params.get('keyword')
        start_date = self.request.query_params.get('start_date')
        end_date = self.request.query_params.get('end_date')
        
        if item_type:
            queryset = queryset.filter(item_type=item_type)
        
        if category_id:
            queryset = queryset.filter(category_id=category_id)
        
        if status:
            queryset = queryset.filter(status=status)
        
        if keyword:
            queryset = queryset.filter(
                Q(title__icontains=keyword) | 
                Q(description__icontains=keyword) |
                Q(location__icontains=keyword)
            )
        
        if start_date:
            queryset = queryset.filter(item_time__date__gte=start_date)
        
        if end_date:
            queryset = queryset.filter(item_time__date__lte=end_date)
        
        return queryset
    
    def get_serializer_class(self):
        if self.action == 'list':
            return ItemListSerializer
        elif self.action == 'retrieve':
            return ItemDetailSerializer
        elif self.action == 'create':
            return ItemCreateSerializer
        elif self.action in ['update', 'partial_update']:
            if self.request.user.is_staff or self.request.user.role == 'admin':
                return ItemAdminUpdateSerializer
            return ItemUpdateSerializer
        return ItemListSerializer
    
    def get_permissions(self):
        if self.action in ['create', 'update', 'partial_update', 'destroy']:
            return [permissions.IsAuthenticated()]
        return super().get_permissions()
    
    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        queryset = queryset.filter(status='active')
        page = self.paginate_queryset(queryset)
        
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return ApiResponse.paginated_response(page, serializer)
        
        serializer = self.get_serializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        instance.view_count += 1
        instance.save()
        serializer = self.get_serializer(instance)
        return ApiResponse.success(serializer.data)
    
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            item = serializer.save(user=request.user, status='pending')
            return ApiResponse.success(
                ItemDetailSerializer(item).data, 
                '发布成功，等待管理员审核'
            )
        return ApiResponse.error('发布失败', data=serializer.errors)
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        
        if not (request.user.is_staff or request.user.role == 'admin'):
            if instance.user != request.user:
                return ApiResponse.error('无权限修改此物品', code=403)
        
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        if serializer.is_valid():
            serializer.save()
            return ApiResponse.success(
                ItemDetailSerializer(instance).data, 
                '更新成功'
            )
        return ApiResponse.error('更新失败', data=serializer.errors)
    
    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限删除此物品', code=403)
        
        instance.delete()
        return ApiResponse.success(message='删除成功')
    
    @action(detail=False, methods=['get'], permission_classes=[permissions.IsAuthenticated])
    def my_items(self, request):
        status_filter = request.query_params.get('status')
        queryset = Item.objects.filter(user=request.user).order_by('-created_at')
        
        if status_filter:
            queryset = queryset.filter(status=status_filter)
        
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = ItemListSerializer(page, many=True)
            return ApiResponse.paginated_response(page, serializer)
        
        serializer = ItemListSerializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    @action(detail=False, methods=['get'], permission_classes=[permissions.IsAdminUser])
    def pending(self, request):
        queryset = Item.objects.filter(status='pending').order_by('-created_at')
        page = self.paginate_queryset(queryset)
        
        if page is not None:
            serializer = ItemListSerializer(page, many=True)
            return ApiResponse.paginated_response(page, serializer)
        
        serializer = ItemListSerializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def approve(self, request, pk=None):
        item = self.get_object()
        item.status = 'active'
        item.admin_note = request.data.get('note', '审核通过')
        item.save()
        return ApiResponse.success(message='审核通过，已发布')
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def reject(self, request, pk=None):
        item = self.get_object()
        item.status = 'rejected'
        item.admin_note = request.data.get('note', '审核未通过')
        item.save()
        return ApiResponse.success(message='审核驳回')
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAuthenticated])
    def mark_resolved(self, request, pk=None):
        item = self.get_object()
        if item.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限操作此物品', code=403)
        
        item.status = 'resolved'
        item.save()
        return ApiResponse.success(message='已标记为完成')
