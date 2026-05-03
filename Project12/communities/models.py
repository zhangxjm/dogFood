from django.db import models
from django.utils.translation import gettext_lazy as _
from users.models import User


class Community(models.Model):
    class Status(models.TextChoices):
        ACTIVE = 'active', _('正常运营')
        SUSPENDED = 'suspended', _('暂停服务')
        CLOSED = 'closed', _('已关闭')

    code = models.CharField(
        _('社区编号'),
        max_length=50,
        unique=True,
        help_text=_('社区唯一识别编号')
    )
    name = models.CharField(
        _('社区名称'),
        max_length=100,
        help_text=_('社区名称')
    )
    address = models.TextField(
        _('详细地址'),
        null=True,
        blank=True
    )
    province = models.CharField(
        _('省份'),
        max_length=50,
        null=True,
        blank=True
    )
    city = models.CharField(
        _('城市'),
        max_length=50,
        null=True,
        blank=True
    )
    district = models.CharField(
        _('区县'),
        max_length=50,
        null=True,
        blank=True
    )
    phone = models.CharField(
        _('联系电话'),
        max_length=20,
        null=True,
        blank=True
    )
    manager = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='managed_communities',
        limit_choices_to={'role': User.Role.ADMIN},
        verbose_name=_('社区负责人')
    )
    description = models.TextField(
        _('描述'),
        null=True,
        blank=True,
        help_text=_('社区简介或备注信息')
    )
    status = models.CharField(
        _('状态'),
        max_length=20,
        choices=Status.choices,
        default=Status.ACTIVE
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)

    class Meta:
        verbose_name = _('社区')
        verbose_name_plural = _('社区')
        ordering = ['-created_at']

    def __str__(self):
        return f'{self.name} ({self.code})'

    @property
    def resident_count(self):
        return self.residents.filter(is_active=True).count()

    @property
    def full_address(self):
        parts = [self.province, self.city, self.district, self.address]
        return ''.join([p for p in parts if p])
