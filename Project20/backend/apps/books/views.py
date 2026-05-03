from rest_framework import viewsets, permissions
from rest_framework.decorators import action
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.filters import SearchFilter, OrderingFilter

from .models import Category, Book
from .serializers import (
    CategorySerializer, CategoryTreeSerializer,
    BookSerializer, BookListSerializer, BookCreateSerializer, BookUpdateSerializer
)
from apps.common.utils import (
    success_response, created_response, error_response,
    forbidden_response
)


class IsAdminOrReadOnly(permissions.BasePermission):
    """
    自定义权限：管理员可以写，普通用户只能读
    """
    
    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            return request.user and request.user.is_authenticated
        return request.user and request.user.is_admin


class CategoryViewSet(viewsets.ModelViewSet):
    """
    图书分类视图集
    """
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    permission_classes = [permissions.IsAuthenticated, IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = ['is_active', 'parent']
    search_fields = ['name', 'code', 'description']
    ordering_fields = ['sort_order', 'name', 'created_at']
    ordering = ['sort_order', 'name']
    
    def get_queryset(self):
        if self.action == 'tree':
            return Category.objects.filter(parent__isnull=True, is_active=True)
        return Category.objects.all()
    
    def get_serializer_class(self):
        if self.action == 'tree':
            return CategoryTreeSerializer
        return CategorySerializer
    
    @action(detail=False, methods=['get'], url_path='tree')
    def get_category_tree(self, request):
        """
        获取分类树形结构
        """
        categories = self.get_queryset()
        serializer = self.get_serializer(categories, many=True)
        return success_response(data=serializer.data)
    
    @action(detail=False, methods=['get'], url_path='active')
    def get_active_categories(self, request):
        """
        获取所有启用的分类
        """
        categories = Category.objects.filter(is_active=True).order_by('sort_order', 'name')
        serializer = self.get_serializer(categories, many=True)
        return success_response(data=serializer.data)


class BookViewSet(viewsets.ModelViewSet):
    """
    图书管理视图集
    """
    queryset = Book.objects.all()
    serializer_class = BookSerializer
    permission_classes = [permissions.IsAuthenticated, IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = ['category', 'status', 'is_active']
    search_fields = ['isbn', 'title', 'author', 'publisher', 'description']
    ordering_fields = ['created_at', 'title', 'price', 'publish_date']
    ordering = ['-created_at']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return BookListSerializer
        elif self.action == 'create':
            return BookCreateSerializer
        elif self.action in ['update', 'partial_update']:
            return BookUpdateSerializer
        return BookSerializer
    
    def get_queryset(self):
        queryset = super().get_queryset()
        category = self.request.query_params.get('category_id')
        if category:
            queryset = queryset.filter(category_id=category)
        return queryset
    
    def create(self, request, *args, **kwargs):
        """
        新增图书
        """
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        book = serializer.save()
        
        output_serializer = BookSerializer(book)
        return created_response(data=output_serializer.data, message='图书添加成功')
    
    @action(detail=False, methods=['get'], url_path='available')
    def get_available_books(self, request):
        """
        获取可借图书列表
        """
        books = Book.objects.filter(
            is_active=True,
            status='available',
            available_quantity__gt=0
        )
        page = self.paginate_queryset(books)
        if page is not None:
            serializer = BookListSerializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = BookListSerializer(books, many=True)
        return success_response(data=serializer.data)
    
    @action(detail=True, methods=['get'], url_path='detail')
    def get_book_detail(self, request, pk=None):
        """
        获取图书详情
        """
        book = self.get_object()
        serializer = BookSerializer(book)
        return success_response(data=serializer.data)
    
    @action(detail=True, methods=['post'], url_path='activate')
    def activate_book(self, request, pk=None):
        """
        激活图书
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以执行此操作')
        
        book = self.get_object()
        book.is_active = True
        book.status = 'available'
        book.save()
        
        return success_response(message='图书已激活')
    
    @action(detail=True, methods=['post'], url_path='deactivate')
    def deactivate_book(self, request, pk=None):
        """
        停用图书
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以执行此操作')
        
        book = self.get_object()
        book.is_active = False
        book.save()
        
        return success_response(message='图书已停用')
    
    @action(detail=False, methods=['get'], url_path='statistics')
    def get_book_statistics(self, request):
        """
        获取图书统计信息
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以查看统计')
        
        total_books = Book.objects.count()
        active_books = Book.objects.filter(is_active=True).count()
        available_books = Book.objects.filter(
            is_active=True, status='available', available_quantity__gt=0
        ).count()
        borrowed_books = Book.objects.filter(status='borrowed').count()
        
        data = {
            'total_books': total_books,
            'active_books': active_books,
            'available_books': available_books,
            'borrowed_books': borrowed_books
        }
        return success_response(data=data)
