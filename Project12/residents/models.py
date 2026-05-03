from django.db import models
from django.utils.translation import gettext_lazy as _
from users.models import User


class Resident(models.Model):
    class Gender(models.TextChoices):
        MALE = 'male', _('男')
        FEMALE = 'female', _('女')
        UNKNOWN = 'unknown', _('未知')

    class BloodType(models.TextChoices):
        A = 'A', 'A型'
        B = 'B', 'B型'
        AB = 'AB', 'AB型'
        O = 'O', 'O型'
        UNKNOWN = 'unknown', _('未知')

    class MaritalStatus(models.TextChoices):
        SINGLE = 'single', _('未婚')
        MARRIED = 'married', _('已婚')
        DIVORCED = 'divorced', _('离异')
        WIDOWED = 'widowed', _('丧偶')
        UNKNOWN = 'unknown', _('未知')

    user = models.OneToOneField(
        User,
        on_delete=models.CASCADE,
        related_name='resident_profile',
        verbose_name=_('关联用户'),
        help_text=_('居民对应的系统用户账号')
    )
    community = models.ForeignKey(
        'communities.Community',
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='residents',
        verbose_name=_('所属社区')
    )
    name = models.CharField(
        _('姓名'),
        max_length=50,
        help_text=_('居民真实姓名')
    )
    id_card = models.CharField(
        _('身份证号'),
        max_length=18,
        unique=True,
        help_text=_('18位身份证号码')
    )
    gender = models.CharField(
        _('性别'),
        max_length=10,
        choices=Gender.choices,
        default=Gender.UNKNOWN
    )
    birth_date = models.DateField(
        _('出生日期'),
        null=True,
        blank=True
    )
    phone = models.CharField(
        _('联系电话'),
        max_length=11,
        null=True,
        blank=True
    )
    emergency_contact = models.CharField(
        _('紧急联系人'),
        max_length=50,
        null=True,
        blank=True
    )
    emergency_phone = models.CharField(
        _('紧急联系电话'),
        max_length=11,
        null=True,
        blank=True
    )
    address = models.TextField(
        _('详细地址'),
        null=True,
        blank=True
    )
    blood_type = models.CharField(
        _('血型'),
        max_length=10,
        choices=BloodType.choices,
        default=BloodType.UNKNOWN
    )
    marital_status = models.CharField(
        _('婚姻状况'),
        max_length=20,
        choices=MaritalStatus.choices,
        default=MaritalStatus.UNKNOWN
    )
    occupation = models.CharField(
        _('职业'),
        max_length=100,
        null=True,
        blank=True
    )
    nationality = models.CharField(
        _('民族'),
        max_length=50,
        default='汉族'
    )
    allergy_history = models.TextField(
        _('过敏史'),
        null=True,
        blank=True,
        help_text=_('已知的药物或食物过敏史')
    )
    chronic_diseases = models.TextField(
        _('慢性病'),
        null=True,
        blank=True,
        help_text=_('如高血压、糖尿病等慢性病史')
    )
    is_insured = models.BooleanField(
        _('是否参保'),
        default=True,
        help_text=_('是否参加医疗保险')
    )
    insurance_number = models.CharField(
        _('医保卡号'),
        max_length=50,
        null=True,
        blank=True
    )
    is_active = models.BooleanField(
        _('是否在籍'),
        default=True,
        help_text=_('是否为社区在籍居民')
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)
    created_by = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        related_name='created_residents',
        verbose_name=_('创建人')
    )

    class Meta:
        verbose_name = _('居民')
        verbose_name_plural = _('居民')
        ordering = ['-created_at']

    def __str__(self):
        return f'{self.name} ({self.id_card[-4:] if self.id_card else "未登记"})'

    @property
    def age(self):
        if self.birth_date:
            from datetime import date
            today = date.today()
            return today.year - self.birth_date.year - (
                (today.month, today.day) < (self.birth_date.month, self.birth_date.day)
            )
        return None
