from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from .models import Resident


@admin.register(Resident)
class ResidentAdmin(admin.ModelAdmin):
    list_display = [
        'name', 'id_card_masked', 'gender', 'age', 'phone',
        'community', 'is_active', 'is_insured', 'created_at'
    ]
    list_filter = ['gender', 'blood_type', 'marital_status', 'is_active', 'is_insured', 'community']
    search_fields = ['name', 'id_card', 'phone', 'user__username']
    readonly_fields = ['created_at', 'updated_at', 'age']
    raw_id_fields = ['user', 'community', 'created_by']
    
    fieldsets = (
        (_('基本信息'), {
            'fields': ('user', 'community', 'name', 'id_card', 'gender', 'birth_date', 'age')
        }),
        (_('联系方式'), {
            'fields': ('phone', 'emergency_contact', 'emergency_phone', 'address')
        }),
        (_('健康信息'), {
            'fields': ('blood_type', 'marital_status', 'occupation', 'nationality',
                       'allergy_history', 'chronic_diseases')
        }),
        (_('医保信息'), {
            'fields': ('is_insured', 'insurance_number')
        }),
        (_('状态信息'), {
            'fields': ('is_active', 'created_at', 'updated_at', 'created_by')
        }),
    )
    
    @admin.display(description=_('身份证号'))
    def id_card_masked(self, obj):
        if obj.id_card:
            return f'{obj.id_card[:6]}****{obj.id_card[-4:]}'
        return '-'
    
    @admin.display(description=_('年龄'))
    def age(self, obj):
        return obj.age or '-'
    
    def save_model(self, request, obj, form, change):
        if not change:
            obj.created_by = request.user
        super().save_model(request, obj, form, change)
