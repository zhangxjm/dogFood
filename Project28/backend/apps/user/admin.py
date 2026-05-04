from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from apps.user.models import User


@admin.register(User)
class CustomUserAdmin(UserAdmin):
    list_display = ['id', 'username', 'email', 'phone', 'role', 'gender', 'student_id', 'department', 'is_active', 'created_at']
    list_filter = ['role', 'gender', 'is_active', 'created_at']
    search_fields = ['username', 'email', 'phone', 'student_id', 'first_name', 'last_name']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    
    fieldsets = UserAdmin.fieldsets + (
        ('额外信息', {
            'fields': ('phone', 'avatar', 'role', 'gender', 'student_id', 'department', 'bio', 'created_at', 'updated_at')
        }),
    )
    
    add_fieldsets = UserAdmin.add_fieldsets + (
        ('额外信息', {
            'fields': ('phone', 'avatar', 'role', 'gender', 'student_id', 'department', 'bio')
        }),
    )
