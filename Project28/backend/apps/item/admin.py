from django.contrib import admin
from apps.item.models import Category, Item


@admin.register(Category)
class CategoryAdmin(admin.ModelAdmin):
    list_display = ['id', 'name', 'description', 'icon', 'sort_order', 'is_active', 'created_at']
    list_filter = ['is_active', 'created_at']
    search_fields = ['name', 'description']
    ordering = ['sort_order', '-created_at']
    list_editable = ['sort_order', 'is_active']


@admin.register(Item)
class ItemAdmin(admin.ModelAdmin):
    list_display = ['id', 'title', 'user', 'category', 'item_type', 'status', 
                    'location', 'contact_name', 'contact_phone', 'view_count', 
                    'collect_count', 'created_at']
    list_filter = ['item_type', 'status', 'category', 'created_at']
    search_fields = ['title', 'description', 'location', 'contact_name']
    ordering = ['-created_at']
    readonly_fields = ['view_count', 'collect_count', 'created_at', 'updated_at']
    date_hierarchy = 'created_at'
    list_per_page = 20
    
    actions = ['make_active', 'make_resolved', 'make_rejected']
    
    def make_active(self, request, queryset):
        queryset.update(status='active')
    make_active.short_description = '标记为已发布'
    
    def make_resolved(self, request, queryset):
        queryset.update(status='resolved')
    make_resolved.short_description = '标记为已完成'
    
    def make_rejected(self, request, queryset):
        queryset.update(status='rejected')
    make_rejected.short_description = '标记为已驳回'
