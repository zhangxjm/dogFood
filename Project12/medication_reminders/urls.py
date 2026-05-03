from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import MedicationReminderViewSet, ReminderLogViewSet

router = DefaultRouter()
router.register(r'', MedicationReminderViewSet, basename='medicationreminder')
router.register(r'logs', ReminderLogViewSet, basename='reminderlog')

urlpatterns = [
    path('', include(router.urls)),
]
