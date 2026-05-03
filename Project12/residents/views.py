from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import Resident
from .serializers import (
    ResidentSerializer, ResidentCreateSerializer, ResidentUpdateSerializer
)
from users.permissions import IsAdminOrDoctor, IsDoctorOrResidentOwner


class ResidentViewSet(viewsets.ModelViewSet):
    queryset = Resident.objects.all().select_related('user', 'community').order_by('-created_at')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['gender', 'blood_type', 'marital_status', 'is_active', 'is_insured', 'community']
    search_fields = ['name', 'id_card', 'phone', 'user__username']
    ordering_fields = ['name', 'birth_date', 'created_at', 'updated_at']
    
    def get_serializer_class(self):
        if self.action == 'create':
            return ResidentCreateSerializer
        elif self.action in ['update', 'partial_update']:
            return ResidentUpdateSerializer
        return ResidentSerializer
    
    def get_permissions(self):
        if self.action == 'create':
            return [IsAdminOrDoctor()]
        elif self.action == 'list':
            return [IsAdminOrDoctor()]
        elif self.action in ['retrieve', 'update', 'partial_update']:
            return [IsDoctorOrResidentOwner()]
        elif self.action == 'destroy':
            return [IsAdminOrDoctor()]
        return [permissions.IsAuthenticated()]
    
    def get_queryset(self):
        user = self.request.user
        if user.is_admin or user.is_doctor:
            return Resident.objects.all().select_related('user', 'community').order_by('-created_at')
        return Resident.objects.filter(user=user).select_related('user', 'community')
    
    def perform_create(self, serializer):
        user = self.request.user
        if user.is_resident:
            serializer.save(user=user, created_by=user)
        else:
            serializer.save(created_by=user)
    
    def perform_update(self, serializer):
        serializer.save()
