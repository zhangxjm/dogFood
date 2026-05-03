from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import MedicalRecord


@admin.register(MedicalRecord)
class MedicalRecordAdmin(admin.ModelAdmin):
    list_display = [
        'resident_name', 'visit_date', 'visit_type', 'doctor_name',
        'department', 'hospital', 'created_at'
    ]
    list_filter = ['visit_type', 'visit_date']
    search_fields = ['resident__name', 'resident__id_card', 'diagnosis', 'chief_complaint']
    readonly_fields = ['created_at', 'updated_at']
    raw_id_fields = ['resident', 'doctor', 'created_by']
    date_hierarchy = 'visit_date'
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('resident', 'visit_date', 'visit_type', 'doctor', 'department', 'hospital')
        }),
        (_('病史'), {
            'fields': ('chief_complaint', 'present_illness', 'past_history', 'allergy_history')
        }),
        (_('检查'), {
            'fields': ('physical_examination', 'auxiliary_examination')
        }),
        (_('诊断与治疗'), {
            'fields': ('diagnosis', 'treatment_plan', 'prescription', 'follow_up_requirements', 'next_visit_date')
        }),
        (_('其他'), {
            'fields': ('notes', 'attachments', 'created_at', 'updated_at', 'created_by')
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
