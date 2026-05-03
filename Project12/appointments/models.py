from django.db import models
from django.utils.translation import gettext_lazy as _
from residents.models import Resident
from users.models import User


class Appointment(models.Model):
    class Status(models.TextChoices):
        PENDING = 'pending', _('待确认')
        CONFIRMED = 'confirmed', _('已确认')
        IN_PROGRESS = 'in_progress', _('进行中')
        COMPLETED = 'completed', _('已完成')
        CANCELLED = 'cancelled', _('已取消')
        NO_SHOW = 'no_show', _('未就诊')

    class AppointmentType(models.TextChoices):
        ANNUAL_CHECKUP = 'annual_checkup', _('年度体检')
        ROUTINE_CHECK = 'routine_check', _('常规检查')
        FOLLOW_UP = 'follow_up', _('复诊')
        SPECIAL_EXAM = 'special_exam', _('专项检查')
        VACCINATION = 'vaccination', _('疫苗接种')

    resident = models.ForeignKey(
        Resident,
        on_delete=models.CASCADE,
        related_name='appointments',
        verbose_name=_('预约居民')
    )
    appointment_type = models.CharField(
        _('预约类型'),
        max_length=30,
        choices=AppointmentType.choices,
        default=AppointmentType.ROUTINE_CHECK
    )
    appointment_date = models.DateField(
        _('预约日期')
    )
    appointment_time = models.TimeField(
        _('预约时间')
    )
    doctor = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='doctor_appointments',
        limit_choices_to={'role': User.Role.DOCTOR},
        verbose_name=_('预约医生')
    )
    department = models.CharField(
        _('科室'),
        max_length=100,
        null=True,
        blank=True
    )
    description = models.TextField(
        _('预约说明'),
        null=True,
        blank=True,
        help_text=_('预约原因或特殊说明')
    )
    status = models.CharField(
        _('状态'),
        max_length=20,
        choices=Status.choices,
        default=Status.PENDING
    )
    notes = models.TextField(
        _('备注'),
        null=True,
        blank=True,
        help_text=_('内部备注信息')
    )
    cancel_reason = models.TextField(
        _('取消原因'),
        null=True,
        blank=True
    )
    completed_at = models.DateTimeField(
        _('完成时间'),
        null=True,
        blank=True
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)
    created_by = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        related_name='created_appointments',
        verbose_name=_('创建人')
    )

    class Meta:
        verbose_name = _('预约')
        verbose_name_plural = _('预约')
        ordering = ['-appointment_date', '-appointment_time']
        indexes = [
            models.Index(fields=['appointment_date', 'appointment_time']),
            models.Index(fields=['status']),
        ]

    def __str__(self):
        return f'{self.resident.name} - {self.get_appointment_type_display()} ({self.appointment_date} {self.appointment_time})'
