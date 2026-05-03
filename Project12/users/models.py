from django.contrib.auth.models import AbstractUser, BaseUserManager
from django.db import models
from django.utils.translation import gettext_lazy as _


class UserManager(BaseUserManager):
    def create_user(self, username, email=None, password=None, **extra_fields):
        if not username:
            raise ValueError(_('用户名不能为空'))
        email = self.normalize_email(email)
        user = self.model(username=username, email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_superuser(self, username, email=None, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('role', User.Role.ADMIN)

        if extra_fields.get('is_staff') is not True:
            raise ValueError(_('超级用户必须设置 is_staff=True'))
        if extra_fields.get('is_superuser') is not True:
            raise ValueError(_('超级用户必须设置 is_superuser=True'))

        return self.create_user(username, email, password, **extra_fields)


class User(AbstractUser):
    class Role(models.TextChoices):
        ADMIN = 'admin', _('管理员')
        DOCTOR = 'doctor', _('医生')
        RESIDENT = 'resident', _('居民')

    phone = models.CharField(
        _('手机号'),
        max_length=11,
        unique=True,
        null=True,
        blank=True,
        help_text=_('用于登录和接收通知')
    )
    role = models.CharField(
        _('角色'),
        max_length=20,
        choices=Role.choices,
        default=Role.RESIDENT,
        help_text=_('用户角色决定权限')
    )
    avatar = models.ImageField(
        _('头像'),
        upload_to='avatars/',
        null=True,
        blank=True,
        help_text=_('用户头像')
    )
    is_verified = models.BooleanField(
        _('是否实名认证'),
        default=False,
        help_text=_('居民用户需要实名认证后才能使用完整功能')
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)

    objects = UserManager()

    class Meta:
        verbose_name = _('用户')
        verbose_name_plural = _('用户')
        ordering = ['-created_at']

    def __str__(self):
        return f'{self.get_role_display()} - {self.username}'

    @property
    def is_admin(self):
        return self.role == self.Role.ADMIN

    @property
    def is_doctor(self):
        return self.role == self.Role.DOCTOR

    @property
    def is_resident(self):
        return self.role == self.Role.RESIDENT
