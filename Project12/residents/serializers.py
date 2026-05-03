from rest_framework import serializers
from .models import Resident


class ResidentSerializer(serializers.ModelSerializer):
    gender_display = serializers.CharField(source='get_gender_display', read_only=True)
    blood_type_display = serializers.CharField(source='get_blood_type_display', read_only=True)
    marital_status_display = serializers.CharField(source='get_marital_status_display', read_only=True)
    age = serializers.ReadOnlyField()
    username = serializers.CharField(source='user.username', read_only=True)
    community_name = serializers.CharField(source='community.name', read_only=True)
    
    class Meta:
        model = Resident
        fields = [
            'id', 'user', 'username', 'community', 'community_name',
            'name', 'id_card', 'gender', 'gender_display',
            'birth_date', 'age', 'phone', 'emergency_contact',
            'emergency_phone', 'address', 'blood_type', 'blood_type_display',
            'marital_status', 'marital_status_display', 'occupation',
            'nationality', 'allergy_history', 'chronic_diseases',
            'is_insured', 'insurance_number', 'is_active',
            'created_at', 'updated_at', 'created_by'
        ]
        read_only_fields = ['id', 'user', 'created_at', 'updated_at', 'created_by']


class ResidentCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Resident
        fields = [
            'community', 'name', 'id_card', 'gender', 'birth_date',
            'phone', 'emergency_contact', 'emergency_phone', 'address',
            'blood_type', 'marital_status', 'occupation', 'nationality',
            'allergy_history', 'chronic_diseases', 'is_insured',
            'insurance_number', 'is_active'
        ]

    def validate_id_card(self, value):
        if len(value) != 18:
            raise serializers.ValidationError('身份证号必须为18位')
        return value


class ResidentUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Resident
        fields = [
            'community', 'name', 'gender', 'birth_date',
            'phone', 'emergency_contact', 'emergency_phone', 'address',
            'blood_type', 'marital_status', 'occupation', 'nationality',
            'allergy_history', 'chronic_diseases', 'is_insured',
            'insurance_number', 'is_active'
        ]
        read_only_fields = ['id_card']
