from rest_framework import viewsets, permissions
from rest_framework.decorators import action
from apps.claim.models import Claim
from apps.claim.serializers import ClaimSerializer, ClaimCreateSerializer, ClaimUpdateSerializer
from apps.item.models import Item
from apps.utils import ApiResponse


class ClaimViewSet(viewsets.ModelViewSet):
    queryset = Claim.objects.all().order_by('-created_at')
    permission_classes = [permissions.IsAuthenticated]
    
    def get_serializer_class(self):
        if self.action == 'create':
            return ClaimCreateSerializer
        elif self.action in ['update', 'partial_update']:
            return ClaimUpdateSerializer
        return ClaimSerializer
    
    def get_queryset(self):
        user = self.request.user
        if user.is_staff or user.role == 'admin':
            return Claim.objects.all().order_by('-created_at')
        return Claim.objects.filter(user=user).order_by('-created_at')
    
    def list(self, request, *args, **kwargs):
        queryset = self.filter_queryset(self.get_queryset())
        serializer = self.get_serializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        return ApiResponse.success(serializer.data)
    
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            item_id = serializer.validated_data['item']
            try:
                item = Item.objects.get(id=item_id.id)
                if item.user == request.user:
                    return ApiResponse.error('不能认领自己发布的物品')
                
                existing_claim = Claim.objects.filter(
                    item=item,
                    user=request.user,
                    status='pending'
                ).first()
                if existing_claim:
                    return ApiResponse.error('您已提交认领申请，请等待确认')
                
                claim = serializer.save(user=request.user)
                return ApiResponse.success(
                    ClaimSerializer(claim).data, 
                    '认领申请提交成功，请等待确认'
                )
            except Item.DoesNotExist:
                return ApiResponse.error('物品不存在', code=404)
        return ApiResponse.error('提交失败', data=serializer.errors)
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        item = instance.item
        
        if item.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限处理此认领申请', code=403)
        
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        if serializer.is_valid():
            serializer.save()
            return ApiResponse.success(
                ClaimSerializer(instance).data, 
                '处理成功'
            )
        return ApiResponse.error('处理失败', data=serializer.errors)
    
    @action(detail=False, methods=['get'], permission_classes=[permissions.IsAuthenticated])
    def my_claims(self, request):
        queryset = Claim.objects.filter(user=request.user).order_by('-created_at')
        serializer = ClaimSerializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    @action(detail=False, methods=['get'], permission_classes=[permissions.IsAuthenticated])
    def received(self, request):
        user_items = Item.objects.filter(user=request.user).values_list('id', flat=True)
        queryset = Claim.objects.filter(item_id__in=user_items).order_by('-created_at')
        serializer = ClaimSerializer(queryset, many=True)
        return ApiResponse.success(serializer.data)
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAuthenticated])
    def approve(self, request, pk=None):
        claim = self.get_object()
        item = claim.item
        
        if item.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限处理此认领申请', code=403)
        
        claim.status = 'approved'
        claim.save()
        
        item.status = 'resolved'
        item.save()
        
        return ApiResponse.success(message='已确认认领，物品状态已更新为已完成')
    
    @action(detail=True, methods=['post'], permission_classes=[permissions.IsAuthenticated])
    def reject(self, request, pk=None):
        claim = self.get_object()
        item = claim.item
        
        if item.user != request.user and not (request.user.is_staff or request.user.role == 'admin'):
            return ApiResponse.error('无权限处理此认领申请', code=403)
        
        claim.status = 'rejected'
        claim.reject_reason = request.data.get('reason', '')
        claim.save()
        
        return ApiResponse.success(message='已拒绝认领申请')
