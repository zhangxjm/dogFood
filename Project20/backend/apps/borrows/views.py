from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from django.utils import timezone
from datetime import timedelta
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.filters import SearchFilter, OrderingFilter
from django.db.models import Count, Sum

from .models import BorrowRecord, Fine
from .serializers import (
    BorrowRecordSerializer, BorrowRecordListSerializer,
    BorrowBookSerializer, RenewBookSerializer, ReturnBookSerializer,
    FineSerializer
)
from apps.books.models import Book
from apps.common.utils import (
    success_response, created_response, error_response,
    forbidden_response, not_found_response
)


class IsAdminOrSelf(permissions.BasePermission):
    """
    自定义权限：管理员可以操作所有记录，普通用户只能操作自己的记录
    """
    
    def has_object_permission(self, request, view, obj):
        if request.user.is_admin:
            return True
        return obj.user == request.user


class BorrowRecordViewSet(viewsets.ModelViewSet):
    """
    借阅记录视图集
    """
    queryset = BorrowRecord.objects.all()
    serializer_class = BorrowRecordSerializer
    permission_classes = [permissions.IsAuthenticated, IsAdminOrSelf]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = ['status', 'user']
    search_fields = ['user__username', 'book__title', 'book__isbn']
    ordering_fields = ['borrow_date', 'due_date', 'return_date', 'created_at']
    ordering = ['-created_at']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return BorrowRecordListSerializer
        return BorrowRecordSerializer
    
    def get_queryset(self):
        queryset = super().get_queryset()
        if not self.request.user.is_admin:
            queryset = queryset.filter(user=self.request.user)
        return queryset
    
    @action(detail=False, methods=['post'], url_path='borrow')
    def borrow_book(self, request):
        """
        借书接口
        """
        serializer = BorrowBookSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        book_id = serializer.validated_data['book_id']
        borrow_days = serializer.validated_data.get('borrow_days', 30)
        
        user = request.user
        
        # 检查用户是否有未支付的罚款
        unpaid_fines = Fine.objects.filter(user=user, status='unpaid')
        if unpaid_fines.exists():
            return error_response(message='您有未支付的罚款，请先支付罚款后再借书')
        
        # 检查用户当前借阅数量
        current_borrows = BorrowRecord.objects.filter(
            user=user,
            status__in=['borrowed', 'overdue', 'renewed']
        ).count()
        
        if current_borrows >= user.max_borrow_count:
            return error_response(
                message=f'您已达到最大借阅数量 {user.max_borrow_count} 本，请先归还部分图书'
            )
        
        try:
            book = Book.objects.get(id=book_id)
        except Book.DoesNotExist:
            return not_found_response(message='图书不存在')
        
        # 借书操作
        if not book.borrow_book():
            return error_response(message='图书库存不足')
        
        # 创建借阅记录
        borrow_record = BorrowRecord.objects.create(
            user=user,
            book=book,
            due_date=timezone.now() + timedelta(days=borrow_days),
            max_renew_count=2
        )
        
        output_serializer = BorrowRecordSerializer(borrow_record)
        return created_response(data=output_serializer.data, message='借书成功')
    
    @action(detail=True, methods=['post'], url_path='return')
    def return_book(self, request, pk=None):
        """
        还书接口
        """
        serializer = ReturnBookSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        borrow_record = self.get_object()
        
        # 检查是否已经归还
        if borrow_record.status == 'returned':
            return error_response(message='该图书已归还')
        
        # 计算逾期罚款
        overdue_days = borrow_record.overdue_days
        if overdue_days > 0:
            fine_amount = overdue_days * 0.5
            Fine.objects.create(
                borrow_record=borrow_record,
                user=borrow_record.user,
                amount=fine_amount,
                overdue_days=overdue_days,
                reason=f'图书逾期 {overdue_days} 天，每天罚款0.5元'
            )
        
        # 更新备注
        if 'remarks' in serializer.validated_data:
            borrow_record.remarks = serializer.validated_data['remarks']
        
        # 还书操作
        success, message = borrow_record.return_book()
        if not success:
            return error_response(message=message)
        
        output_serializer = BorrowRecordSerializer(borrow_record)
        return success_response(data=output_serializer.data, message='归还成功')
    
    @action(detail=True, methods=['post'], url_path='renew')
    def renew_book(self, request, pk=None):
        """
        续借接口
        """
        serializer = RenewBookSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        borrow_record = self.get_object()
        
        # 检查权限
        if not request.user.is_admin and borrow_record.user != request.user:
            return forbidden_response(message='您没有权限操作此借阅记录')
        
        # 检查是否有未支付罚款
        unpaid_fines = Fine.objects.filter(user=borrow_record.user, status='unpaid')
        if unpaid_fines.exists():
            return error_response(message='您有未支付的罚款，请先支付罚款后再续借')
        
        renew_days = serializer.validated_data.get('renew_days', 30)
        success, message = borrow_record.renew(renew_days)
        
        if not success:
            return error_response(message=message)
        
        output_serializer = BorrowRecordSerializer(borrow_record)
        return success_response(data=output_serializer.data, message='续借成功')
    
    @action(detail=False, methods=['get'], url_path='my')
    def get_my_borrows(self, request):
        """
        获取当前用户的借阅记录
        """
        queryset = self.get_queryset().filter(user=request.user)
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = self.get_serializer(queryset, many=True)
        return success_response(data=serializer.data)
    
    @action(detail=False, methods=['get'], url_path='overdue')
    def get_overdue_records(self, request):
        """
        获取逾期记录（管理员）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以查看逾期记录')
        
        queryset = self.get_queryset().filter(status='overdue')
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = self.get_serializer(queryset, many=True)
        return success_response(data=serializer.data)
    
    @action(detail=False, methods=['get'], url_path='statistics')
    def get_borrow_statistics(self, request):
        """
        获取借阅统计（管理员）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以查看统计')
        
        today = timezone.now().date()
        total_borrows = BorrowRecord.objects.count()
        today_borrows = BorrowRecord.objects.filter(
            borrow_date__date=today
        ).count()
        today_returns = BorrowRecord.objects.filter(
            return_date__date=today
        ).count()
        current_borrows = BorrowRecord.objects.filter(
            status__in=['borrowed', 'overdue', 'renewed']
        ).count()
        overdue_count = BorrowRecord.objects.filter(
            status='overdue'
        ).count()
        
        # 计算总罚款金额
        total_fines = Fine.objects.aggregate(
            total=Sum('amount')
        )['total'] or 0
        
        # 计算未支付罚款
        unpaid_fines = Fine.objects.filter(
            status='unpaid'
        ).aggregate(
            total=Sum('amount')
        )['total'] or 0
        
        data = {
            'total_borrows': total_borrows,
            'today_borrows': today_borrows,
            'today_returns': today_returns,
            'current_borrows': current_borrows,
            'overdue_count': overdue_count,
            'total_fines': float(total_fines),
            'unpaid_fines': float(unpaid_fines)
        }
        return success_response(data=data)


class FineViewSet(viewsets.ModelViewSet):
    """
    罚款记录视图集
    """
    queryset = Fine.objects.all()
    serializer_class = FineSerializer
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = ['status', 'user']
    search_fields = ['user__username', 'reason']
    ordering_fields = ['created_at', 'amount']
    ordering = ['-created_at']
    
    def get_queryset(self):
        queryset = super().get_queryset()
        if not self.request.user.is_admin:
            queryset = queryset.filter(user=self.request.user)
        return queryset
    
    @action(detail=True, methods=['post'], url_path='pay')
    def pay_fine(self, request, pk=None):
        """
        支付罚款
        """
        fine = self.get_object()
        
        # 检查权限
        if not request.user.is_admin and fine.user != request.user:
            return forbidden_response(message='您没有权限操作此罚款记录')
        
        if fine.status == 'paid':
            return error_response(message='该罚款已支付')
        
        fine.pay()
        serializer = self.get_serializer(fine)
        return success_response(data=serializer.data, message='罚款支付成功')
    
    @action(detail=True, methods=['post'], url_path='waive')
    def waive_fine(self, request, pk=None):
        """
        豁免罚款（管理员）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以豁免罚款')
        
        fine = self.get_object()
        
        if fine.status == 'paid':
            return error_response(message='该罚款已支付，无法豁免')
        
        reason = request.data.get('reason', '管理员豁免')
        fine.waive(reason)
        
        serializer = self.get_serializer(fine)
        return success_response(data=serializer.data, message='罚款已豁免')
    
    @action(detail=False, methods=['get'], url_path='my')
    def get_my_fines(self, request):
        """
        获取当前用户的罚款记录
        """
        queryset = self.get_queryset().filter(user=request.user)
        page = self.paginate_queryset(queryset)
        if page is not None:
            serializer = self.get_serializer(page, many=True)
            return self.get_paginated_response(serializer.data)
        serializer = self.get_serializer(queryset, many=True)
        return success_response(data=serializer.data)
