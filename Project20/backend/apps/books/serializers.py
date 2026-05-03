from rest_framework import serializers
from .models import Category, Book


class CategorySerializer(serializers.ModelSerializer):
    """
    图书分类序列化器
    """
    book_count = serializers.IntegerField(read_only=True)
    
    class Meta:
        model = Category
        fields = [
            'id', 'name', 'code', 'description', 'parent',
            'book_count', 'sort_order', 'is_active', 'created_at'
        ]
        read_only_fields = ['id', 'created_at', 'book_count']


class CategoryTreeSerializer(serializers.ModelSerializer):
    """
    分类树形结构序列化器
    """
    children = CategorySerializer(many=True, read_only=True)
    book_count = serializers.IntegerField(read_only=True)
    
    class Meta:
        model = Category
        fields = [
            'id', 'name', 'code', 'description', 'parent',
            'children', 'book_count', 'sort_order', 'is_active'
        ]


class BookSerializer(serializers.ModelSerializer):
    """
    图书序列化器
    """
    category_name = serializers.CharField(source='category.name', read_only=True)
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    is_available = serializers.BooleanField(read_only=True)
    
    class Meta:
        model = Book
        fields = [
            'id', 'isbn', 'title', 'author', 'publisher', 'publish_date',
            'category', 'category_name', 'price', 'total_quantity',
            'available_quantity', 'location', 'description', 'cover',
            'status', 'status_display', 'is_active', 'is_available',
            'created_at', 'updated_at'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at', 'is_available']


class BookListSerializer(serializers.ModelSerializer):
    """
    图书列表序列化器（简化版）
    """
    category_name = serializers.CharField(source='category.name', read_only=True)
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    
    class Meta:
        model = Book
        fields = [
            'id', 'isbn', 'title', 'author', 'publisher',
            'category_name', 'price', 'available_quantity',
            'status', 'status_display', 'cover'
        ]


class BookCreateSerializer(serializers.ModelSerializer):
    """
    图书创建序列化器
    """
    
    class Meta:
        model = Book
        fields = [
            'isbn', 'title', 'author', 'publisher', 'publish_date',
            'category', 'price', 'total_quantity', 'available_quantity',
            'location', 'description', 'cover', 'status', 'is_active'
        ]
    
    def validate(self, attrs):
        if attrs.get('available_quantity') is None:
            attrs['available_quantity'] = attrs.get('total_quantity', 1)
        elif attrs['available_quantity'] > attrs.get('total_quantity', 1):
            raise serializers.ValidationError('可借数量不能大于总数量')
        return attrs


class BookUpdateSerializer(serializers.ModelSerializer):
    """
    图书更新序列化器
    """
    
    class Meta:
        model = Book
        fields = [
            'title', 'author', 'publisher', 'publish_date',
            'category', 'price', 'total_quantity', 'available_quantity',
            'location', 'description', 'cover', 'status', 'is_active'
        ]
    
    def validate(self, attrs):
        total = attrs.get('total_quantity')
        available = attrs.get('available_quantity')
        
        if total is not None and available is not None:
            if available > total:
                raise serializers.ValidationError('可借数量不能大于总数量')
        return attrs
