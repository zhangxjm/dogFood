from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import BorrowRecordViewSet, FineViewSet

router = DefaultRouter()
router.register('borrows', BorrowRecordViewSet, basename='borrow')
router.register('fines', FineViewSet, basename='fine')

urlpatterns = [
    path('', include(router.urls)),
]
