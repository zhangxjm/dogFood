from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('api/user/', include('apps.user.urls')),
    path('api/item/', include('apps.item.urls')),
    path('api/comment/', include('apps.comment.urls')),
    path('api/collection/', include('apps.collection.urls')),
    path('api/claim/', include('apps.claim.urls')),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
