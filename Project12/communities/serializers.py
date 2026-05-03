from rest_framework import serializers
from .models import Community


class CommunitySerializer(serializers.ModelSerializer):
    status_display = serializers.CharField(source='get_status_display', read_only=True)
    resident_count = serializers.ReadOnlyField()
    full_address = serializers.ReadOnlyField()
    manager_name = serializers.CharField(source='manager.username', read_only=True)
    
    class Meta:
        model = Community
        fields = [
            'id', 'code', 'name', 'address', 'province', 'city',
            'district', 'phone', 'manager', 'manager_name',
            'description', 'status', 'status_display',
            'resident_count', 'full_address', 'created_at', 'updated_at'
        ]
        read_only_fields = ['id', 'created_at', 'updated_at']


class CommunitySimpleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Community
        fields = ['id', 'code', 'name', 'full_address']
