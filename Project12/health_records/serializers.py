from rest_framework import serializers
from .models import HealthRecord


class HealthRecordSerializer(serializers.ModelSerializer):
    record_type_display = serializers.CharField(source='get_record_type_display', read_only=True)
    blood_glucose_type_display = serializers.SerializerMethodField()
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.username', read_only=True)
    blood_pressure_display = serializers.SerializerMethodField()
    
    class Meta:
        model = HealthRecord
        fields = [
            'id', 'resident', 'resident_name', 'record_date', 'record_type', 'record_type_display',
            'height', 'weight', 'bmi', 'blood_pressure_systolic', 'blood_pressure_diastolic',
            'blood_pressure_display', 'heart_rate', 'body_temperature', 'respiratory_rate',
            'blood_glucose', 'blood_glucose_type', 'blood_glucose_type_display',
            'cholesterol', 'triglyceride', 'hdl', 'ldl',
            'vision_left', 'vision_right', 'hearing_left', 'hearing_right',
            'oral_health', 'skin_condition', 'abnormal_findings', 'diagnosis',
            'recommendations', 'next_checkup_date', 'doctor', 'doctor_name',
            'hospital', 'notes', 'attachments', 'created_at', 'updated_at', 'created_by'
        ]
        read_only_fields = ['id', 'bmi', 'created_at', 'updated_at', 'created_by']
    
    def get_blood_glucose_type_display(self, obj):
        type_map = {
            'fasting': '空腹血糖',
            'postprandial': '餐后血糖',
            'random': '随机血糖'
        }
        return type_map.get(obj.blood_glucose_type, obj.blood_glucose_type)
    
    def get_blood_pressure_display(self, obj):
        if obj.blood_pressure_systolic and obj.blood_pressure_diastolic:
            return f'{obj.blood_pressure_systolic}/{obj.blood_pressure_diastolic}'
        return None


class HealthRecordSimpleSerializer(serializers.ModelSerializer):
    record_type_display = serializers.CharField(source='get_record_type_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    
    class Meta:
        model = HealthRecord
        fields = [
            'id', 'resident', 'resident_name', 'record_date', 'record_type',
            'record_type_display', 'bmi', 'blood_pressure_systolic',
            'blood_pressure_diastolic', 'blood_glucose', 'diagnosis',
            'doctor', 'hospital', 'created_at'
        ]
