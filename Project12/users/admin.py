from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.utils.translation import gettext_lazy as _
from .models import User


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ['username', 'email', 'phone', 'role', 'is_verified', 'is_active', 'created_at']
    list_filter = ['role', 'is_verified', 'is_active', 'is_staff']
    search_fields = ['username', 'email', 'phone', 'first_name', 'last_name']
    readonly_fields = ['created_at', 'updated_at', 'last_login']
    
    fieldsets = (
        (None, {'fields': ('username', 'password')}),
        (_('个人信息'), {'fields': ('first_name', 'last_name', 'email', 'phone', 'avatar')}),
        (_('权限信息'), {'fields': ('role', 'is_verified', 'is_active', 'is_staff', 'is_superuser', 'groups', 'user_permissions')}),
        (_('重要日期'), {'fields': ('last_login', 'date_joined', 'created_at', 'updated_at')}),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('username', 'email', 'phone', 'password1', 'password2', 'role'),
        }),
    )
    
    ordering = ['-created_at']
    
    actions = ['make_admin', 'make_doctor', 'make_resident', 'verify_users']
    
    @admin.action(description=_('设为管理员'))
    def make_admin(self, request, queryset):
        queryset.update(role=User.Role.ADMIN)
    
    @admin.action(description=_('设为医生'))
    def make_doctor(self, request, queryset):
        queryset.update(role=User.Role.DOCTOR)
    
    @admin.action(description=_('设为居民'))
    def make_resident(self, request, queryset):
        queryset.update(role=User.Role.RESIDENT)
    
    @admin.action(description=_('实名认证'))
    def verify_users(self, request, queryset):
        queryset.update(is_verified=True)
