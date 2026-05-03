from django.contrib import admin
from .models import Category, Book


@admin.register(Category)
class CategoryAdmin(admin.ModelAdmin):
    """
    图书分类管理后台
    """
    list_display = [
        'id', 'name', 'code', 'parent', 'book_count', 
        'sort_order', 'is_active', 'created_at'
    ]
    list_display_links = ['id', 'name']
    list_filter = ['is_active', 'parent']
    search_fields = ['name', 'code', 'description']
    ordering = ['sort_order', 'name']
    readonly_fields = ['created_at', 'updated_at']
    
    fieldsets = (
        (None, {'fields': ('name', 'code', 'parent')}),
        ('描述信息', {'fields': ('description',)}),
        ('排序状态', {'fields': ('sort_order', 'is_active')}),
        ('时间信息', {'fields': ('created_at', 'updated_at')}),
    )
    
    def book_count(self, obj):
        return obj.book_count
    book_count.short_description = '图书数量'


@admin.register(Book)
class BookAdmin(admin.ModelAdmin):
    """
    图书管理后台
    """
    list_display = [
        'id', 'isbn', 'title', 'author', 'publisher', 
        'category', 'total_quantity', 'available_quantity',
        'status', 'is_active', 'created_at'
    ]
    list_display_links = ['id', 'isbn', 'title']
    list_filter = ['status', 'is_active', 'category']
    search_fields = ['isbn', 'title', 'author', 'publisher', 'description']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    list_per_page = 20
    
    fieldsets = (
        ('基本信息', {
            'fields': ('isbn', 'title', 'author', 'publisher', 'publish_date', 'category')
        }),
        ('数量信息', {
            'fields': ('price', 'total_quantity', 'available_quantity', 'location')
        }),
        ('描述信息', {
            'fields': ('description', 'cover')
        }),
        ('状态信息', {
            'fields': ('status', 'is_active')
        }),
        ('时间信息', {
            'fields': ('created_at', 'updated_at')
        }),
    )
    
    actions = ['make_available', 'make_maintenance', 'deactivate_books']
    
    def make_available(self, request, queryset):
        queryset.update(status='available', is_active=True)
        self.message_user(request, f'{queryset.count()} 本图书已设置为可借状态')
    make_available.short_description = '设置为可借'
    
    def make_maintenance(self, request, queryset):
        queryset.update(status='maintenance')
        self.message_user(request, f'{queryset.count()} 本图书已设置为维护中')
    make_maintenance.short_description = '设置为维护中'
    
    def deactivate_books(self, request, queryset):
        queryset.update(is_active=False)
        self.message_user(request, f'{queryset.count()} 本图书已停用')
    deactivate_books.short_description = '停用图书'
