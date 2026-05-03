from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import Community
from .serializers import CommunitySerializer, CommunitySimpleSerializer
from users.permissions import IsAdminUser


class CommunityViewSet(viewsets.ModelViewSet):
    queryset = Community.objects.all().select_related('manager').order_by('-created_at')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['status', 'province', 'city', 'district']
    search_fields = ['code', 'name', 'address', 'phone']
    ordering_fields = ['name', 'code', 'created_at', 'updated_at']
    
    def get_serializer_class(self):
        if self.action == 'list' and self.request.user.is_resident:
            return CommunitySimpleSerializer
        return CommunitySerializer
    
    def get_permissions(self):
        if self.action in ['list', 'retrieve']:
            return [permissions.IsAuthenticated()]
        return [IsAdminUser()]
    
    def get_queryset(self):
        user = self.request.user
        if user.is_admin:
            return Community.objects.all().select_related('manager').order_by('-created_at')
        return Community.objects.filter(status=Community.Status.ACTIVE).select_related('manager').order_by('name')
