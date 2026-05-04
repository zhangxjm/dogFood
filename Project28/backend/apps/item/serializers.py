from rest_framework import serializers
from apps.item.models import Category, Item
from apps.user.serializers import UserSerializer


class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = ['id', 'name', 'description', 'icon', 'sort_order', 'is_active']
        read_only_fields = ['id', 'created_at', 'updated_at']


class ItemListSerializer(serializers.ModelSerializer):
    category_name = serializers.CharField(source='category.name', read_only=True)
    username = serializers.CharField(source='user.username', read_only=True)
    user_avatar = serializers.ImageField(source='user.avatar', read_only=True)
    
    class Meta:
        model = Item
        fields = ['id', 'title', 'item_type', 'status', 'location', 
                  'item_time', 'view_count', 'collect_count', 'image1',
                  'category_name', 'username', 'user_avatar', 'created_at']


class ItemDetailSerializer(serializers.ModelSerializer):
    category = CategorySerializer(read_only=True)
    category_id = serializers.IntegerField(write_only=True, required=False, allow_null=True)
    user = UserSerializer(read_only=True)
    
    class Meta:
        model = Item
        fields = ['id', 'user', 'category', 'category_id', 'title', 'description', 
                  'item_type', 'status', 'location', 'item_time', 
                  'contact_name', 'contact_phone', 'reward', 
                  'image1', 'image2', 'image3', 'view_count', 
                  'collect_count', 'admin_note', 'created_at', 'updated_at']
        read_only_fields = ['id', 'user', 'view_count', 'collect_count', 
                           'status', 'admin_note', 'created_at', 'updated_at']


class ItemCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item
        fields = ['category', 'title', 'description', 'item_type', 
                  'location', 'item_time', 'contact_name', 'contact_phone', 
                  'reward', 'image1', 'image2', 'image3']


class ItemUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item
        fields = ['category', 'title', 'description', 'item_type', 
                  'location', 'item_time', 'contact_name', 'contact_phone', 
                  'reward', 'image1', 'image2', 'image3']


class ItemAdminUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Item
        fields = ['status', 'admin_note']
