from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import HealthRecord


@admin.register(HealthRecord)
class HealthRecordAdmin(admin.ModelAdmin):
    list_display = [
        'resident_name', 'record_date', 'record_type',
        'bmi', 'blood_pressure_display', 'doctor',
        'hospital', 'created_at'
    ]
    list_filter = ['record_type', 'record_date']
    search_fields = ['resident__name', 'resident__id_card', 'diagnosis', 'hospital']
    readonly_fields = ['bmi', 'created_at', 'updated_at']
    raw_id_fields = ['resident', 'doctor', 'created_by']
    date_hierarchy = 'record_date'
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('resident', 'record_date', 'record_type', 'doctor', 'hospital')
        }),
        (_('基本体征'), {
            'fields': ('height', 'weight', 'bmi', 'body_temperature', 'heart_rate', 'respiratory_rate')
        }),
        (_('血压'), {
            'fields': ('blood_pressure_systolic', 'blood_pressure_diastolic')
        }),
        (_('血液指标'), {
            'fields': ('blood_glucose', 'blood_glucose_type', 'cholesterol', 'triglyceride', 'hdl', 'ldl')
        }),
        (_('其他检查'), {
            'fields': ('vision_left', 'vision_right', 'hearing_left', 'hearing_right', 'oral_health', 'skin_condition')
        }),
        (_('诊断信息'), {
            'fields': ('abnormal_findings', 'diagnosis', 'recommendations', 'next_checkup_date')
        }),
        (_('其他'), {
            'fields': ('notes', 'attachments', 'created_at', 'updated_at', 'created_by')
        }),
    )
    
    @admin.display(description=_('居民'))
    def resident_name(self, obj):
        return obj.resident.name
    
    @admin.display(description=_('血压'))
    def blood_pressure_display(self, obj):
        if obj.blood_pressure_systolic and obj.blood_pressure_diastolic:
            return f'{obj.blood_pressure_systolic}/{obj.blood_pressure_diastolic}'
        return '-'
    
    def save_model(self, request, obj, form, change):
        if not change:
            obj.created_by = request.user
        super().save_model(request, obj, form, change)
