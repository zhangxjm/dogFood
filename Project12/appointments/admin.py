from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import Appointment


@admin.register(Appointment)
class AppointmentAdmin(admin.ModelAdmin):
    list_display = [
        'resident_name', 'appointment_type', 'appointment_date',
        'appointment_time', 'doctor_name', 'status', 'created_at'
    ]
    list_filter = ['status', 'appointment_type', 'appointment_date']
    search_fields = ['resident__name', 'resident__id_card', 'description']
    readonly_fields = ['created_at', 'updated_at']
    raw_id_fields = ['resident', 'doctor', 'created_by']
    date_hierarchy = 'appointment_date'
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('resident', 'appointment_type', 'doctor', 'department')
        }),
        (_('预约时间'), {
            'fields': ('appointment_date', 'appointment_time')
        }),
        (_('状态信息'), {
            'fields': ('status', 'cancel_reason', 'completed_at')
        }),
        (_('描述信息'), {
            'fields': ('description', 'notes')
        }),
        (_('其他信息'), {
            'fields': ('created_at', 'updated_at', 'created_by')
        }),
    )
    
    @admin.display(description=_('居民'))
    def resident_name(self, obj):
        return obj.resident.name
    
    @admin.display(description=_('医生'))
    def doctor_name(self, obj):
        return obj.doctor.username if obj.doctor else '-'
    
    def save_model(self, request, obj, form, change):
        if not change:
            obj.created_by = request.user
        super().save_model(request, obj, form, change)
