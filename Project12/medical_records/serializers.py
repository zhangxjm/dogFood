from rest_framework import serializers
from .models import MedicalRecord


class MedicalRecordSerializer(serializers.ModelSerializer):
    visit_type_display = serializers.CharField(source='get_visit_type_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.username', read_only=True)
    
    class Meta:
        model = MedicalRecord
        fields = [
            'id', 'resident', 'resident_name', 'visit_date', 'visit_type', 'visit_type_display',
            'doctor', 'doctor_name', 'department', 'hospital',
            'chief_complaint', 'present_illness', 'past_history', 'allergy_history',
            'physical_examination', 'auxiliary_examination',
            'diagnosis', 'treatment_plan', 'prescription',
            'follow_up_requirements', 'next_visit_date',
            'notes', 'attachments', 'created_at', 'updated_at', 'created_by'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at', 'created_by']


class MedicalRecordSimpleSerializer(serializers.ModelSerializer):
    visit_type_display = serializers.CharField(source='get_visit_type_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.username', read_only=True)
    
    class Meta:
        model = MedicalRecord
        fields = [
            'id', 'resident', 'resident_name', 'visit_date', 'visit_type',
            'visit_type_display', 'doctor', 'doctor_name', 'department',
            'hospital', 'diagnosis', 'created_at'
        ]
