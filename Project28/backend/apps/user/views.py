from django.contrib.auth import authenticate
from django.contrib.auth import get_user_model
from rest_framework import generics, permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework_simplejwt.tokens import RefreshToken
from apps.user.serializers import (
    UserSerializer, UserRegisterSerializer, UserLoginSerializer,
    UserUpdateSerializer, UserListSerializer
)
from apps.utils import ApiResponse

User = get_user_model()


class UserRegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserRegisterSerializer
    permission_classes = [permissions.AllowAny]
    
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            refresh = RefreshToken.for_user(user)
            return ApiResponse.success({
                'user': UserSerializer(user).data,
                'refresh': str(refresh),
                'access': str(refresh.access_token)
            }, '注册成功')
        return ApiResponse.error('注册失败', data=serializer.errors)


class UserLoginView(generics.GenericAPIView):
    serializer_class = UserLoginSerializer
    permission_classes = [permissions.AllowAny]
    
    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        
        username = serializer.validated_data['username']
        password = serializer.validated_data['password']
        
        user = authenticate(username=username, password=password)
        
        if user is not None:
            refresh = RefreshToken.for_user(user)
            return ApiResponse.success({
                'user': UserSerializer(user).data,
                'refresh': str(refresh),
                'access': str(refresh.access_token)
            }, '登录成功')
        
        return ApiResponse.error('用户名或密码错误', code=401)


class UserInfoView(generics.RetrieveUpdateAPIView):
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_object(self):
        return self.request.user
    
    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        return ApiResponse.success(serializer.data)
    
    def update(self, request, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = UserUpdateSerializer(instance, data=request.data, partial=partial)
        
        if serializer.is_valid():
            serializer.save()
            return ApiResponse.success(UserSerializer(instance).data, '更新成功')
        return ApiResponse.error('更新失败', data=serializer.errors)


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all().order_by('-created_at')
    serializer_class = UserListSerializer
    permission_classes = [permissions.IsAdminUser]
    
    @action(detail=True, methods=['post'])
    def toggle_active(self, request, pk=None):
        user = self.get_object()
        user.is_active = not user.is_active
        user.save()
        return ApiResponse.success(
            {'is_active': user.is_active}, 
            f'账号已{"启用" if user.is_active else "禁用"}'
        )
    
    @action(detail=True, methods=['post'])
    def set_role(self, request, pk=None):
        user = self.get_object()
        role = request.data.get('role')
        if role in ['student', 'teacher', 'admin']:
            user.role = role
            user.save()
            return ApiResponse.success({'role': user.role}, '角色已更新')
        return ApiResponse.error('无效的角色')
