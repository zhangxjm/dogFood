from rest_framework import serializers
from .models import MedicationReminder, ReminderLog


class ReminderLogSerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    
    class Meta:
        model = ReminderLog
        fields = [
            'id', 'reminder', 'reminder_time', 'status', 'status_display',
            'acknowledged_at', 'note', 'created_at'
        ]
        read_only_fields = ['id', 'created_at']


class MedicationReminderSerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    frequency_display = serializers.CharField(source='get_frequency_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    doctor_name = serializers.CharField(source='prescribing_doctor.username', read_only=True)
    reminder_times_list = serializers.SerializerMethodField()
    logs = ReminderLogSerializer(many=True, read_only=True)
    
    class Meta:
        model = MedicationReminder
        fields = [
            'id', 'resident', 'resident_name', 'medication_name', 'medication_type',
            'dosage', 'frequency', 'frequency_display', 'custom_frequency',
            'reminder_times', 'reminder_times_list', 'start_date', 'end_date',
            'instructions', 'side_effects', 'precautions', 'prescribing_doctor',
            'doctor_name', 'prescription_source', 'status', 'status_display',
            'notes', 'logs', 'created_at', 'updated_at', 'created_by'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at', 'created_by']
    
    def get_reminder_times_list(self, obj):
        return obj.get_reminder_times_list()


class MedicationReminderSimpleSerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    frequency_display = serializers.CharField(source='get_frequency_display', read_only=True)
    resident_name = serializers.CharField(source='resident.name', read_only=True)
    
    class Meta:
        model = MedicationReminder
        fields = [
            'id', 'resident', 'resident_name', 'medication_name', 'medication_type',
            'dosage', 'frequency', 'frequency_display', 'reminder_times',
            'start_date', 'end_date', 'status', 'status_display', 'created_at'
        ]
