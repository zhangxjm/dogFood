from django.contrib import admin
from apps.claim.models import Claim


@admin.register(Claim)
class ClaimAdmin(admin.ModelAdmin):
    list_display = ['id', 'item', 'user', 'description', 'contact_phone', 
                    'status', 'created_at']
    list_filter = ['status', 'created_at']
    search_fields = ['item__title', 'user__username', 'description']
    ordering = ['-created_at']
    readonly_fields = ['created_at', 'updated_at']
    actions = ['approve', 'reject']
    
    def approve(self, request, queryset):
        queryset.update(status='approved')
    approve.short_description = '确认认领'
    
    def reject(self, request, queryset):
        queryset.update(status='rejected')
    reject.short_description = '拒绝认领'
