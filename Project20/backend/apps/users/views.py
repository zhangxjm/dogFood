from rest_framework import viewsets, status, permissions
from rest_framework.decorators import action
from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework_simplejwt.views import TokenObtainPairView
from django.contrib.auth import authenticate
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.filters import SearchFilter, OrderingFilter

from .models import User
from .serializers import (
    UserSerializer, UserCreateSerializer, UserUpdateSerializer,
    PasswordChangeSerializer, UserLoginSerializer
)
from apps.common.utils import (
    success_response, created_response, error_response,
    forbidden_response, not_found_response, APIResponse
)


class IsAdminOrSelf(permissions.BasePermission):
    """
    自定义权限：管理员可以操作所有用户，普通用户只能操作自己
    """
    
    def has_permission(self, request, view):
        if request.user and request.user.is_authenticated:
            if request.user.is_admin:
                return True
            if view.action in ['retrieve', 'update', 'partial_update']:
                return True
            if view.action in ['list', 'create', 'destroy']:
                return request.user.is_admin
        return False
    
    def has_object_permission(self, request, view, obj):
        if request.user.is_admin:
            return True
        return obj == request.user


class UserViewSet(viewsets.ModelViewSet):
    """
    用户管理视图集
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated, IsAdminOrSelf]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = ['role', 'is_active', 'gender']
    search_fields = ['username', 'email', 'phone', 'first_name', 'last_name']
    ordering_fields = ['created_at', 'username', 'email']
    ordering = ['-created_at']
    
    def get_serializer_class(self):
        if self.action == 'create':
            return UserCreateSerializer
        elif self.action in ['update', 'partial_update']:
            return UserUpdateSerializer
        return UserSerializer
    
    def get_permissions(self):
        if self.action == 'create':
            return [permissions.AllowAny()]
        return super().get_permissions()
    
    def create(self, request, *args, **kwargs):
        """
        用户注册
        """
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        
        refresh = RefreshToken.for_user(user)
        data = {
            'user': UserSerializer(user).data,
            'access': str(refresh.access_token),
            'refresh': str(refresh)
        }
        return created_response(data=data, message='注册成功')
    
    @action(detail=False, methods=['get', 'put'], url_path='me')
    def current_user(self, request):
        """
        获取或更新当前登录用户信息
        """
        if request.method == 'GET':
            serializer = self.get_serializer(request.user)
            return success_response(data=serializer.data)
        elif request.method == 'PUT':
            serializer = UserUpdateSerializer(request.user, data=request.data, partial=True)
            serializer.is_valid(raise_exception=True)
            serializer.save()
            return success_response(data=UserSerializer(request.user).data, message='更新成功')
    
    @action(detail=False, methods=['put'], url_path='change-password')
    def change_password(self, request):
        """
        修改密码
        """
        serializer = PasswordChangeSerializer(data=request.data, context={'request': request})
        serializer.is_valid(raise_exception=True)
        
        user = request.user
        user.set_password(serializer.validated_data['new_password'])
        user.save()
        
        return success_response(message='密码修改成功')
    
    @action(detail=True, methods=['post'], url_path='activate')
    def activate_user(self, request, pk=None):
        """
        激活用户（管理员操作）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以执行此操作')
        
        user = self.get_object()
        user.is_active = True
        user.save()
        
        return success_response(message='用户已激活')
    
    @action(detail=True, methods=['post'], url_path='deactivate')
    def deactivate_user(self, request, pk=None):
        """
        停用用户（管理员操作）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以执行此操作')
        
        user = self.get_object()
        if user == request.user:
            return error_response(message='不能停用自己的账户')
        
        user.is_active = False
        user.save()
        
        return success_response(message='用户已停用')
    
    def destroy(self, request, *args, **kwargs):
        """
        删除用户（管理员操作）
        """
        if not request.user.is_admin:
            return forbidden_response(message='只有管理员可以执行此操作')
        
        user = self.get_object()
        if user == request.user:
            return error_response(message='不能删除自己的账户')
        
        user.delete()
        return success_response(message='用户已删除')


class CustomTokenObtainPairView(TokenObtainPairView):
    """
    自定义JWT登录视图，返回统一格式的响应
    """
    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        
        try:
            serializer.is_valid(raise_exception=True)
        except Exception as e:
            return error_response(message='用户名或密码错误', code=400)
        
        user = serializer.user
        refresh = serializer.validated_data.get('refresh')
        access = serializer.validated_data.get('access')
        
        data = {
            'access': str(access),
            'refresh': str(refresh),
            'user': UserSerializer(user).data
        }
        
        return success_response(data=data, message='登录成功')
