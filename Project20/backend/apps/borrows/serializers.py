from rest_framework import serializers
from django.utils import timezone
from datetime import timedelta

from .models import BorrowRecord, Fine
from apps.books.models import Book
from apps.books.serializers import BookListSerializer
from apps.users.serializers import UserSerializer


class BorrowRecordSerializer(serializers.ModelSerializer):
    """
    借阅记录序列化器
    """
    user_info = UserSerializer(source='user', read_only=True)
    book_info = BookListSerializer(source='book', read_only=True)
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    is_overdue = serializers.BooleanField(read_only=True)
    overdue_days = serializers.IntegerField(read_only=True)
    fine_amount = serializers.DecimalField(max_digits=10, decimal_places=2, read_only=True)
    
    class Meta:
        model = BorrowRecord
        fields = [
            'id', 'user', 'user_info', 'book', 'book_info',
            'borrow_date', 'due_date', 'return_date',
            'renew_count', 'max_renew_count',
            'status', 'status_display', 'is_overdue',
            'overdue_days', 'fine_amount', 'remarks',
            'created_at', 'updated_at'
        ]
        read_only_fields = [
            'id', 'borrow_date', 'return_date',
            'renew_count', 'max_renew_count',
            'status', 'is_overdue', 'overdue_days',
            'fine_amount', 'created_at', 'updated_at'
        ]


class BorrowRecordListSerializer(serializers.ModelSerializer):
    """
    借阅记录列表序列化器
    """
    username = serializers.CharField(source='user.username', read_only=True)
    book_title = serializers.CharField(source='book.title', read_only=True)
    book_isbn = serializers.CharField(source='book.isbn', read_only=True)
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    is_overdue = serializers.BooleanField(read_only=True)
    overdue_days = serializers.IntegerField(read_only=True)
    fine_amount = serializers.DecimalField(max_digits=10, decimal_places=2, read_only=True)
    
    class Meta:
        model = BorrowRecord
        fields = [
            'id', 'username', 'book_title', 'book_isbn',
            'borrow_date', 'due_date', 'return_date',
            'renew_count', 'status', 'status_display',
            'is_overdue', 'overdue_days', 'fine_amount'
        ]


class BorrowBookSerializer(serializers.Serializer):
    """
    借书序列化器
    """
    book_id = serializers.IntegerField(required=True)
    borrow_days = serializers.IntegerField(required=False, default=30)
    
    def validate_book_id(self, value):
        try:
            book = Book.objects.get(id=value)
        except Book.DoesNotExist:
            raise serializers.ValidationError('图书不存在')
        
        if not book.is_active:
            raise serializers.ValidationError('该图书已停用')
        
        if book.status != 'available':
            raise serializers.ValidationError('该图书不可借')
        
        if book.available_quantity <= 0:
            raise serializers.ValidationError('该图书库存不足')
        
        return value
    
    def validate_borrow_days(self, value):
        if value < 1:
            raise serializers.ValidationError('借阅天数不能小于1天')
        if value > 90:
            raise serializers.ValidationError('借阅天数不能超过90天')
        return value


class RenewBookSerializer(serializers.Serializer):
    """
    续借序列化器
    """
    renew_days = serializers.IntegerField(required=False, default=30)
    
    def validate_renew_days(self, value):
        if value < 1:
            raise serializers.ValidationError('续借天数不能小于1天')
        if value > 30:
            raise serializers.ValidationError('续借天数不能超过30天')
        return value


class ReturnBookSerializer(serializers.Serializer):
    """
    还书序列化器
    """
    remarks = serializers.CharField(required=False, allow_blank=True, max_length=500)


class FineSerializer(serializers.ModelSerializer):
    """
    罚款记录序列化器
    """
    user_info = UserSerializer(source='user', read_only=True)
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    
    class Meta:
        model = Fine
        fields = [
            'id', 'borrow_record', 'user', 'user_info',
            'amount', 'overdue_days', 'reason',
            'status', 'status_display', 'paid_date',
            'created_at', 'updated_at'
        ]
        read_only_fields = [
            'id', 'user', 'user_info', 'amount',
            'overdue_days', 'reason', 'paid_date',
            'created_at', 'updated_at'
        ]
