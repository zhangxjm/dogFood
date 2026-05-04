from django.urls import path, include
from rest_framework.routers import DefaultRouter
from apps.collection.views import CollectionViewSet

router = DefaultRouter()
router.register(r'collections', CollectionViewSet, basename='collection')

urlpatterns = [
    path('', include(router.urls)),
]
