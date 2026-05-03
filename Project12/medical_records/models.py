from django.db import models
from django.utils.translation import gettext_lazy as _
from residents.models import Resident
from users.models import User


class MedicalRecord(models.Model):
    class VisitType(models.TextChoices):
        OUTPATIENT = 'outpatient', _('门诊')
        EMERGENCY = 'emergency', _('急诊')
        INPATIENT = 'inpatient', _('住院')
        FOLLOW_UP = 'follow_up', _('随访')
        TELEMEDICINE = 'telemedicine', _('远程问诊')

    resident = models.ForeignKey(
        Resident,
        on_delete=models.CASCADE,
        related_name='medical_records',
        verbose_name=_('就诊居民')
    )
    visit_date = models.DateField(
        _('就诊日期')
    )
    visit_type = models.CharField(
        _('就诊类型'),
        max_length=20,
        choices=VisitType.choices,
        default=VisitType.OUTPATIENT
    )
    doctor = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='medical_records',
        limit_choices_to={'role': User.Role.DOCTOR},
        verbose_name=_('接诊医生')
    )
    department = models.CharField(
        _('科室'),
        max_length=100,
        null=True,
        blank=True
    )
    hospital = models.CharField(
        _('就诊医院'),
        max_length=200,
        null=True,
        blank=True
    )
    chief_complaint = models.TextField(
        _('主诉'),
        help_text=_('患者主要症状和持续时间')
    )
    present_illness = models.TextField(
        _('现病史'),
        null=True,
        blank=True,
        help_text=_('本次发病的详细情况')
    )
    past_history = models.TextField(
        _('既往史'),
        null=True,
        blank=True,
        help_text=_('过去的疾病史')
    )
    allergy_history = models.TextField(
        _('过敏史'),
        null=True,
        blank=True
    )
    physical_examination = models.TextField(
        _('体格检查'),
        null=True,
        blank=True
    )
    auxiliary_examination = models.TextField(
        _('辅助检查'),
        null=True,
        blank=True,
        help_text=_('实验室检查、影像检查等结果')
    )
    diagnosis = models.TextField(
        _('诊断'),
        help_text=_('医生诊断结果')
    )
    treatment_plan = models.TextField(
        _('治疗方案'),
        null=True,
        blank=True
    )
    prescription = models.TextField(
        _('处方'),
        null=True,
        blank=True,
        help_text=_('开具的药物处方')
    )
    follow_up_requirements = models.TextField(
        _('随访要求'),
        null=True,
        blank=True
    )
    next_visit_date = models.DateField(
        _('下次就诊日期'),
        null=True,
        blank=True
    )
    notes = models.TextField(
        _('备注'),
        null=True,
        blank=True
    )
    attachments = models.FileField(
        _('附件'),
        upload_to='medical_records/',
        null=True,
        blank=True,
        help_text=_('病历、检查报告等附件')
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)
    created_by = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        related_name='created_medical_records',
        verbose_name=_('创建人')
    )

    class Meta:
        verbose_name = _('就诊记录')
        verbose_name_plural = _('就诊记录')
        ordering = ['-visit_date', '-created_at']

    def __str__(self):
        return f'{self.resident.name} - {self.get_visit_type_display()} ({self.visit_date})'
