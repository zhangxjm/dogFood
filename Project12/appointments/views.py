from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import Appointment
from .serializers import AppointmentSerializer, AppointmentSimpleSerializer
from users.permissions import IsAdminOrDoctor, IsDoctorOrResidentOwner


class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.all().select_related(
        'resident', 'doctor', 'created_by'
    ).order_by('-appointment_date', '-appointment_time')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['status', 'appointment_type', 'appointment_date', 'doctor']
    search_fields = ['resident__name', 'resident__id_card', 'description']
    ordering_fields = ['appointment_date', 'appointment_time', 'created_at']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return AppointmentSimpleSerializer
        return AppointmentSerializer
    
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
            return Appointment.objects.all().select_related(
                'resident', 'doctor', 'created_by'
            ).order_by('-appointment_date', '-appointment_time')
        return Appointment.objects.filter(
            resident__user=user
        ).select_related('resident', 'doctor', 'created_by').order_by('-appointment_date', '-appointment_time')
    
    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)
