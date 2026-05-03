from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import HealthRecord
from .serializers import HealthRecordSerializer, HealthRecordSimpleSerializer
from users.permissions import IsAdminOrDoctor, IsDoctorOrResidentOwner


class HealthRecordViewSet(viewsets.ModelViewSet):
    queryset = HealthRecord.objects.all().select_related(
        'resident', 'doctor', 'created_by'
    ).order_by('-record_date', '-created_at')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['record_type', 'record_date', 'resident', 'doctor']
    search_fields = ['resident__name', 'resident__id_card', 'diagnosis', 'hospital']
    ordering_fields = ['record_date', 'created_at', 'bmi', 'blood_glucose']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return HealthRecordSimpleSerializer
        return HealthRecordSerializer
    
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
            return HealthRecord.objects.all().select_related(
                'resident', 'doctor', 'created_by'
            ).order_by('-record_date', '-created_at')
        return HealthRecord.objects.filter(
            resident__user=user
        ).select_related('resident', 'doctor', 'created_by').order_by('-record_date', '-created_at')
    
    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)
