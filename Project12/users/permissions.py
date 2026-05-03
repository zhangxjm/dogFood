from rest_framework.permissions import BasePermission, IsAuthenticated, SAFE_METHODS
from .models import User


class IsAdminUser(BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated and request.user.is_admin)


class IsDoctorUser(BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated and request.user.is_doctor)


class IsResidentUser(BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and request.user.is_authenticated and request.user.is_resident)


class IsAdminOrDoctor(BasePermission):
    def has_permission(self, request, view):
        return bool(
            request.user and request.user.is_authenticated and 
            (request.user.is_admin or request.user.is_doctor)
        )


class IsOwnerOrAdmin(BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.user and request.user.is_authenticated:
            if request.user.is_admin:
                return True
            if hasattr(obj, 'user') and obj.user == request.user:
                return True
            if hasattr(obj, 'resident') and obj.resident.user == request.user:
                return True
        return False


class IsDoctorOrResidentOwner(BasePermission):
    def has_object_permission(self, request, view, obj):
        if request.user and request.user.is_authenticated:
            if request.user.is_doctor:
                return True
            if hasattr(obj, 'user') and obj.user == request.user:
                return True
            if hasattr(obj, 'resident') and obj.resident.user == request.user:
                return True
        return False
