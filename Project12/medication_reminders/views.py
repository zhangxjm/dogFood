from rest_framework import viewsets, permissions, filters
from django_filters.rest_framework import DjangoFilterBackend
from .models import MedicationReminder, ReminderLog
from .serializers import (
    MedicationReminderSerializer, MedicationReminderSimpleSerializer, ReminderLogSerializer
)
from users.permissions import IsAdminOrDoctor, IsDoctorOrResidentOwner


class MedicationReminderViewSet(viewsets.ModelViewSet):
    queryset = MedicationReminder.objects.all().select_related(
        'resident', 'prescribing_doctor', 'created_by'
    ).prefetch_related('logs').order_by('-created_at')
    permission_classes = [permissions.IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['status', 'frequency', 'start_date']
    search_fields = ['resident__name', 'medication_name', 'medication_type']
    ordering_fields = ['start_date', 'end_date', 'created_at']
    
    def get_serializer_class(self):
        if self.action == 'list':
            return MedicationReminderSimpleSerializer
        return MedicationReminderSerializer
    
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
            return MedicationReminder.objects.all().select_related(
                'resident', 'prescribing_doctor', 'created_by'
            ).prefetch_related('logs').order_by('-created_at')
        return MedicationReminder.objects.filter(
            resident__user=user
        ).select_related('resident', 'prescribing_doctor', 'created_by').prefetch_related('logs').order_by('-created_at')
    
    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)


class ReminderLogViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = ReminderLog.objects.all().select_related('reminder').order_by('-reminder_time')
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = ReminderLogSerializer
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    filterset_fields = ['status', 'reminder']
    search_fields = ['reminder__medication_name', 'reminder__resident__name']
    ordering_fields = ['reminder_time', 'created_at']
    
    def get_queryset(self):
        user = self.request.user
        if user.is_admin or user.is_doctor:
            return ReminderLog.objects.all().select_related('reminder').order_by('-reminder_time')
        return ReminderLog.objects.filter(
            reminder__resident__user=user
        ).select_related('reminder').order_by('-reminder_time')
