from django.db import IntegrityError
from rest_framework import viewsets, permissions
from rest_framework.decorators import action
from apps.collection.models import Collection
from apps.collection.serializers import CollectionSerializer, CollectionCreateSerializer
from apps.item.models import Item
from apps.utils import ApiResponse


class CollectionViewSet(viewsets.ModelViewSet):
    queryset = Collection.objects.all().order_by('-created_at')
    serializer_class = CollectionSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_serializer_class(self):
        if self.action == 'create':
            return CollectionCreateSerializer
        return CollectionSerializer
    
    def get_queryset(self):
        return Collection.objects.filter(user=self.request.user).order_by('-created_at')
    
    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        serializer = self.get_serializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    def create(self, request, *args, **kwargs):
        item_id = request.data.get('item')
        try:
            item = Item.objects.get(id=item_id)
            collection, created = Collection.objects.get_or_create(
                user=request.user,
                item=item
            )
            if created:
                item.collect_count += 1
                item.save()
                return ApiResponse.success(
                    CollectionSerializer(collection).data, 
                    '收藏成功'
                )
            return ApiResponse.error('已收藏该物品')
        except Item.DoesNotExist:
            return ApiResponse.error('物品不存在', code=404)
        except IntegrityError:
            return ApiResponse.error('已收藏该物品')
    
    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        if instance.user != request.user:
            return ApiResponse.error('无权限取消收藏', code=403)
        
        item = instance.item
        item.collect_count -= 1
        if item.collect_count < 0:
            item.collect_count = 0
        item.save()
        
        instance.delete()
        return ApiResponse.success(message='已取消收藏')
    
    @action(detail=False, methods=['post'])
    def toggle(self, request):
        item_id = request.data.get('item_id')
        if not item_id:
            return ApiResponse.error('请提供物品ID')
        
        try:
            item = Item.objects.get(id=item_id)
            collection = Collection.objects.filter(user=request.user, item=item).first()
            
            if collection:
                collection.delete()
                item.collect_count -= 1
                if item.collect_count < 0:
                    item.collect_count = 0
                item.save()
                return ApiResponse.success({'is_collected': False}, '已取消收藏')
            else:
                Collection.objects.create(user=request.user, item=item)
                item.collect_count += 1
                item.save()
                return ApiResponse.success({'is_collected': True}, '收藏成功')
        except Item.DoesNotExist:
            return ApiResponse.error('物品不存在', code=404)
    
    @action(detail=False, methods=['get'])
    def check(self, request):
        item_id = request.query_params.get('item_id')
        if not item_id:
            return ApiResponse.error('请提供物品ID')
        
        is_collected = Collection.objects.filter(
            user=request.user,
            item_id=item_id
        ).exists()
        
        return ApiResponse.success({'is_collected': is_collected})
