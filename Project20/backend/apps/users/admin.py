from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    """
    用户管理后台
    """
    list_display = [
        'id', 'username', 'email', 'role', 'phone', 
        'max_borrow_count', 'borrow_days', 'is_active', 'created_at'
    ]
    list_display_links = ['id', 'username']
    list_filter = ['role', 'is_active', 'is_staff', 'gender']
    search_fields = ['username', 'email', 'phone', 'first_name', 'last_name']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at', 'last_login']
    
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        ('个人信息', {
            'fields': (
                'first_name', 'last_name', 'email', 'phone', 
                'address', 'gender', 'avatar'
            )
        }),
        ('权限信息', {
            'fields': (
                'role', 'is_active', 'is_staff', 'is_superuser',
                'groups', 'user_permissions'
            )
        }),
        ('借阅设置', {
            'fields': ('max_borrow_count', 'borrow_days')
        }),
        ('时间信息', {
            'fields': ('last_login', 'created_at', 'updated_at')
        }),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('username', 'email', 'password1', 'password2', 'role'),
        }),
    )
    
    def get_queryset(self, request):
        qs = super().get_queryset(request)
        if not request.user.is_superuser:
            return qs.filter(role='reader')
        return qs
