from rest_framework import serializers
from apps.claim.models import Claim
from apps.user.serializers import UserSerializer
from apps.item.serializers import ItemListSerializer


class ClaimSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    item = ItemListSerializer(read_only=True)
    
    class Meta:
        model = Claim
        fields = ['id', 'item', 'user', 'description', 'contact_phone', 
                  'status', 'reject_reason', 'created_at', 'updated_at']
        read_only_fields = ['id', 'user', 'status', 'reject_reason', 
                           'created_at', 'updated_at']


class ClaimCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Claim
        fields = ['item', 'description', 'contact_phone']


class ClaimUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Claim
        fields = ['status', 'reject_reason']
