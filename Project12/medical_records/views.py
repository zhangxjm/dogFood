from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import MedicalRecord
from .serializers import MedicalRecordSerializer, MedicalRecordSimpleSerializer
from users.permissions import IsAdminOrDoctor, IsDoctorOrResidentOwner


class MedicalRecordViewSet(viewsets.ModelViewSet):
    queryset = MedicalRecord.objects.all().select_related(
        'resident', 'doctor', 'created_by'
    ).order_by('-visit_date', '-created_at')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['visit_type', 'visit_date', 'doctor']
    search_fields = ['resident__name', 'resident__id_card', 'diagnosis', 'chief_complaint']
    ordering_fields = ['visit_date', 'created_at']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return MedicalRecordSimpleSerializer
        return MedicalRecordSerializer
    
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
            return MedicalRecord.objects.all().select_related(
                'resident', 'doctor', 'created_by'
            ).order_by('-visit_date', '-created_at')
        return MedicalRecord.objects.filter(
            resident__user=user
        ).select_related('resident', 'doctor', 'created_by').order_by('-visit_date', '-created_at')
    
    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)
