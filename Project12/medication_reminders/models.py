from django.db import models
from django.utils.translation import gettext_lazy as _
from residents.models import Resident
from users.models import User


class MedicationReminder(models.Model):
    class Frequency(models.TextChoices):
        ONCE = 'once', _('每日一次')
        TWICE = 'twice', _('每日两次')
        THREE_TIMES = 'three_times', _('每日三次')
        FOUR_TIMES = 'four_times', _('每日四次')
        AS_NEEDED = 'as_needed', _('按需')
        CUSTOM = 'custom', _('自定义')

    class Status(models.TextChoices):
        ACTIVE = 'active', _('生效中')
        PAUSED = 'paused', _('已暂停')
        COMPLETED = 'completed', _('已完成')
        CANCELLED = 'cancelled', _('已取消')

    resident = models.ForeignKey(
        Resident,
        on_delete=models.CASCADE,
        related_name='medication_reminders',
        verbose_name=_('居民')
    )
    medication_name = models.CharField(
        _('药品名称'),
        max_length=200
    )
    medication_type = models.CharField(
        _('药品类型'),
        max_length=100,
        null=True,
        blank=True,
        help_text=_('如：降压药、降糖药、抗生素等')
    )
    dosage = models.CharField(
        _('剂量'),
        max_length=200,
        help_text=_('如：1片、10mg、5ml等')
    )
    frequency = models.CharField(
        _('用药频次'),
        max_length=20,
        choices=Frequency.choices,
        default=Frequency.ONCE
    )
    custom_frequency = models.CharField(
        _('自定义频次'),
        max_length=200,
        null=True,
        blank=True,
        help_text=_('当频次为自定义时填写')
    )
    reminder_times = models.CharField(
        _('提醒时间'),
        max_length=200,
        help_text=_('用逗号分隔，如：08:00,12:00,20:00')
    )
    start_date = models.DateField(
        _('开始日期')
    )
    end_date = models.DateField(
        _('结束日期'),
        null=True,
        blank=True,
        help_text=_('为空表示长期用药')
    )
    instructions = models.TextField(
        _('用药说明'),
        null=True,
        blank=True,
        help_text=_('如：饭前服用、饭后服用等')
    )
    side_effects = models.TextField(
        _('可能的副作用'),
        null=True,
        blank=True
    )
    precautions = models.TextField(
        _('注意事项'),
        null=True,
        blank=True
    )
    prescribing_doctor = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='prescribed_medications',
        limit_choices_to={'role': User.Role.DOCTOR},
        verbose_name=_('开单医生')
    )
    prescription_source = models.CharField(
        _('处方来源'),
        max_length=100,
        null=True,
        blank=True,
        help_text=_('如：医院门诊、社区医院等')
    )
    status = models.CharField(
        _('状态'),
        max_length=20,
        choices=Status.choices,
        default=Status.ACTIVE
    )
    notes = models.TextField(
        _('备注'),
        null=True,
        blank=True
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)
    created_by = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        related_name='created_medication_reminders',
        verbose_name=_('创建人')
    )

    class Meta:
        verbose_name = _('用药提醒')
        verbose_name_plural = _('用药提醒')
        ordering = ['-created_at']

    def __str__(self):
        return f'{self.resident.name} - {self.medication_name}'

    def get_reminder_times_list(self):
        if self.reminder_times:
            return [t.strip() for t in self.reminder_times.split(',')]
        return []


class ReminderLog(models.Model):
    class ReminderStatus(models.TextChoices):
        SENT = 'sent', _('已发送')
        ACKNOWLEDGED = 'acknowledged', _('已确认')
        MISSED = 'missed', _('已错过')

    reminder = models.ForeignKey(
        MedicationReminder,
        on_delete=models.CASCADE,
        related_name='logs',
        verbose_name=_('用药提醒')
    )
    reminder_time = models.DateTimeField(
        _('提醒时间')
    )
    status = models.CharField(
        _('状态'),
        max_length=20,
        choices=ReminderStatus.choices,
        default=ReminderStatus.SENT
    )
    acknowledged_at = models.DateTimeField(
        _('确认时间'),
        null=True,
        blank=True
    )
    note = models.TextField(
        _('备注'),
        null=True,
        blank=True
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)

    class Meta:
        verbose_name = _('提醒日志')
        verbose_name_plural = _('提醒日志')
        ordering = ['-reminder_time']

    def __str__(self):
        return f'{self.reminder.medication_name} - {self.reminder_time}'
