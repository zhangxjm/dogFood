from rest_framework import serializers
from apps.collection.models import Collection
from apps.item.serializers import ItemListSerializer


class CollectionSerializer(serializers.ModelSerializer):
    item = ItemListSerializer(read_only=True)
    item_id = serializers.IntegerField(write_only=True)
    
    class Meta:
        model = Collection
        fields = ['id', 'item', 'item_id', 'created_at']
        read_only_fields = ['id', 'created_at']


class CollectionCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Collection
        fields = ['item']
