from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import MedicationReminder, ReminderLog


class ReminderLogInline(admin.TabularInline):
    model = ReminderLog
    extra = 0
    readonly_fields = ['reminder_time', 'status', 'acknowledged_at', 'note', 'created_at']
    can_delete = False


@admin.register(MedicationReminder)
class MedicationReminderAdmin(admin.ModelAdmin):
    list_display = [
        'resident_name', 'medication_name', 'dosage', 'frequency',
        'start_date', 'end_date', 'status', 'created_at'
    ]
    list_filter = ['status', 'frequency', 'start_date']
    search_fields = ['resident__name', 'medication_name', 'medication_type']
    readonly_fields = ['created_at', 'updated_at']
    raw_id_fields = ['resident', 'prescribing_doctor', 'created_by']
    inlines = [ReminderLogInline]
    date_hierarchy = 'start_date'
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('resident', 'medication_name', 'medication_type', 'prescribing_doctor', 'prescription_source')
        }),
        (_('用药信息'), {
            'fields': ('dosage', 'frequency', 'custom_frequency', 'reminder_times', 
                       'start_date', 'end_date', 'instructions')
        }),
        (_('注意事项'), {
            'fields': ('side_effects', 'precautions')
        }),
        (_('状态信息'), {
            'fields': ('status', 'notes', 'created_at', 'updated_at', 'created_by')
        }),
    )
    
    @admin.display(description=_('居民'))
    def resident_name(self, obj):
        return obj.resident.name
    
    def save_model(self, request, obj, form, change):
        if not change:
            obj.created_by = request.user
        super().save_model(request, obj, form, change)


@admin.register(ReminderLog)
class ReminderLogAdmin(admin.ModelAdmin):
    list_display = [
        'reminder_info', 'reminder_time', 'status', 'acknowledged_at', 'created_at'
    ]
    list_filter = ['status', 'reminder_time']
    search_fields = ['reminder__medication_name', 'reminder__resident__name']
    readonly_fields = ['reminder', 'reminder_time', 'status', 'acknowledged_at', 'note', 'created_at']
    date_hierarchy = 'reminder_time'
    
    @admin.display(description=_('提醒信息'))
    def reminder_info(self, obj):
        return f'{obj.reminder.resident.name} - {obj.reminder.medication_name}'
