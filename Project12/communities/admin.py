from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import Community


@admin.register(Community)
class CommunityAdmin(admin.ModelAdmin):
    list_display = ['code', 'name', 'city', 'district', 'manager', 'resident_count', 'status', 'created_at']
    list_filter = ['status', 'province', 'city']
    search_fields = ['code', 'name', 'address', 'phone']
    readonly_fields = ['created_at', 'updated_at', 'resident_count', 'full_address']
    raw_id_fields = ['manager']
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('code', 'name', 'manager', 'status')
        }),
        (_('地址信息'), {
            'fields': ('province', 'city', 'district', 'address', 'full_address')
        }),
        (_('联系方式'), {
            'fields': ('phone',)
        }),
        (_('其他信息'), {
            'fields': ('description', 'resident_count', 'created_at', 'updated_at')
        }),
    )
    
    @admin.display(description=_('居民数'))
    def resident_count(self, obj):
        return obj.resident_count
