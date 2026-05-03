from django.contrib import admin
from .models import BorrowRecord, Fine


@admin.register(BorrowRecord)
class BorrowRecordAdmin(admin.ModelAdmin):
    """
    借阅记录管理后台
    """
    list_display = [
        'id', 'user', 'book', 'borrow_date', 'due_date',
        'return_date', 'status', 'overdue_days_display', 'fine_amount_display'
    ]
    list_display_links = ['id', 'user']
    list_filter = ['status', 'borrow_date', 'due_date']
    search_fields = ['user__username', 'book__title', 'book__isbn']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    list_per_page = 20
    
    fieldsets = (
        ('基本信息', {
            'fields': ('user', 'book')
        }),
        ('借阅时间', {
            'fields': ('borrow_date', 'due_date', 'return_date')
        }),
        ('续借信息', {
            'fields': ('renew_count', 'max_renew_count')
        }),
        ('状态信息', {
            'fields': ('status', 'remarks')
        }),
        ('时间信息', {
            'fields': ('created_at', 'updated_at')
        }),
    )
    
    def overdue_days_display(self, obj):
        return obj.overdue_days
    overdue_days_display.short_description = '逾期天数'
    
    def fine_amount_display(self, obj):
        return f'¥{obj.fine_amount}'
    fine_amount_display.short_description = '罚款金额'
    
    actions = ['return_books', 'renew_books']
    
    def return_books(self, request, queryset):
        count = 0
        for record in queryset.filter(status__in=['borrowed', 'overdue', 'renewed']):
            success, message = record.return_book()
            if success:
                count += 1
        self.message_user(request, f'{count} 本图书已归还')
    return_books.short_description = '批量归还'
    
    def renew_books(self, request, queryset):
        count = 0
        for record in queryset.filter(status__in=['borrowed', 'renewed']):
            success, message = record.renew()
            if success:
                count += 1
        self.message_user(request, f'{count} 本图书已续借')
    renew_books.short_description = '批量续借'


@admin.register(Fine)
class FineAdmin(admin.ModelAdmin):
    """
    罚款记录管理后台
    """
    list_display = [
        'id', 'user', 'borrow_record', 'amount', 'overdue_days',
        'status', 'paid_date', 'created_at'
    ]
    list_display_links = ['id', 'user']
    list_filter = ['status', 'created_at']
    search_fields = ['user__username', 'reason']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    
    fieldsets = (
        ('基本信息', {
            'fields': ('borrow_record', 'user')
        }),
        ('罚款信息', {
            'fields': ('amount', 'overdue_days', 'reason')
        }),
        ('支付信息', {
            'fields': ('status', 'paid_date')
        }),
        ('时间信息', {
            'fields': ('created_at', 'updated_at')
        }),
    )
    
    actions = ['mark_as_paid', 'waive_fines']
    
    def mark_as_paid(self, request, queryset):
        count = 0
        for fine in queryset.filter(status='unpaid'):
            fine.pay()
            count += 1
        self.message_user(request, f'{count} 条罚款已标记为已支付')
    mark_as_paid.short_description = '标记为已支付'
    
    def waive_fines(self, request, queryset):
        count = 0
        for fine in queryset.filter(status='unpaid'):
            fine.waive('管理员豁免')
            count += 1
        self.message_user(request, f'{count} 条罚款已豁免')
    waive_fines.short_description = '豁免罚款'
