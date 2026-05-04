from django.urls import path, include
from rest_framework.routers import DefaultRouter
from apps.user.views import (
    UserRegisterView, UserLoginView, UserInfoView, UserViewSet
)

router = DefaultRouter()
router.register(r'list', UserViewSet, basename='user-list')

urlpatterns = [
    path('register/', UserRegisterView.as_view(), name='user-register'),
    path('login/', UserLoginView.as_view(), name='user-login'),
    path('info/', UserInfoView.as_view(), name='user-info'),
    path('', include(router.urls)),
]
