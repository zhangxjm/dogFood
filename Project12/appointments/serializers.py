from rest_framework import serializers
from .models import Appointment


class AppointmentSerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    appointment_type_display = serializers.CharField(source='get_appointment_type_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.username', read_only=True)
    
    class Meta:
        model = Appointment
        fields = [
            'id', 'resident', 'resident_name', 'appointment_type', 'appointment_type_display',
            'appointment_date', 'appointment_time', 'doctor', 'doctor_name',
            'department', 'description', 'status', 'status_display',
            'notes', 'cancel_reason', 'completed_at',
            'created_at', 'updated_at', 'created_by'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at', 'created_by']


class AppointmentSimpleSerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    appointment_type_display = serializers.CharField(source='get_appointment_type_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.username', read_only=True)
    
    class Meta:
        model = Appointment
        fields = [
            'id', 'resident', 'resident_name', 'appointment_type', 'appointment_type_display',
            'appointment_date', 'appointment_time', 'doctor', 'doctor_name',
            'department', 'status', 'status_display', 'created_at'
        ]
