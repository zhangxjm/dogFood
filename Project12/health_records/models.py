from django.db import models
from django.utils.translation import gettext_lazy as _
from residents.models import Resident
from users.models import User


class HealthRecord(models.Model):
    class RecordType(models.TextChoices):
        ANNUAL_CHECKUP = 'annual_checkup', _('年度体检')
        ROUTINE_CHECK = 'routine_check', _('常规检查')
        SPECIAL_CHECK = 'special_check', _('专项检查')
        FOLLOW_UP = 'follow_up', _('随访记录')

    resident = models.ForeignKey(
        Resident,
        on_delete=models.CASCADE,
        related_name='health_records',
        verbose_name=_('居民')
    )
    record_date = models.DateField(
        _('记录日期'),
        help_text=_('体检或检查日期')
    )
    record_type = models.CharField(
        _('记录类型'),
        max_length=30,
        choices=RecordType.choices,
        default=RecordType.ROUTINE_CHECK
    )
    height = models.DecimalField(
        _('身高(cm)'),
        max_digits=5,
        decimal_places=1,
        null=True,
        blank=True
    )
    weight = models.DecimalField(
        _('体重(kg)'),
        max_digits=5,
        decimal_places=1,
        null=True,
        blank=True
    )
    bmi = models.DecimalField(
        _('BMI'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True,
        help_text=_('体重指数')
    )
    blood_pressure_systolic = models.IntegerField(
        _('收缩压(mmHg)'),
        null=True,
        blank=True,
        help_text=_('高压')
    )
    blood_pressure_diastolic = models.IntegerField(
        _('舒张压(mmHg)'),
        null=True,
        blank=True,
        help_text=_('低压')
    )
    heart_rate = models.IntegerField(
        _('心率(次/分)'),
        null=True,
        blank=True
    )
    body_temperature = models.DecimalField(
        _('体温(℃)'),
        max_digits=3,
        decimal_places=1,
        null=True,
        blank=True
    )
    respiratory_rate = models.IntegerField(
        _('呼吸频率(次/分)'),
        null=True,
        blank=True
    )
    blood_glucose = models.DecimalField(
        _('血糖(mmol/L)'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True
    )
    blood_glucose_type = models.CharField(
        _('血糖类型'),
        max_length=20,
        choices=[
            ('fasting', '空腹血糖'),
            ('postprandial', '餐后血糖'),
            ('random', '随机血糖')
        ],
        null=True,
        blank=True
    )
    cholesterol = models.DecimalField(
        _('总胆固醇(mmol/L)'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True
    )
    triglyceride = models.DecimalField(
        _('甘油三酯(mmol/L)'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True
    )
    hdl = models.DecimalField(
        _('高密度脂蛋白(mmol/L)'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True
    )
    ldl = models.DecimalField(
        _('低密度脂蛋白(mmol/L)'),
        max_digits=4,
        decimal_places=1,
        null=True,
        blank=True
    )
    vision_left = models.CharField(
        _('左眼视力'),
        max_length=20,
        null=True,
        blank=True
    )
    vision_right = models.CharField(
        _('右眼视力'),
        max_length=20,
        null=True,
        blank=True
    )
    hearing_left = models.CharField(
        _('左耳听力'),
        max_length=20,
        null=True,
        blank=True
    )
    hearing_right = models.CharField(
        _('右耳听力'),
        max_length=20,
        null=True,
        blank=True
    )
    oral_health = models.TextField(
        _('口腔健康'),
        null=True,
        blank=True
    )
    skin_condition = models.TextField(
        _('皮肤状况'),
        null=True,
        blank=True
    )
    abnormal_findings = models.TextField(
        _('异常发现'),
        null=True,
        blank=True,
        help_text=_('检查中发现的异常情况')
    )
    diagnosis = models.TextField(
        _('诊断意见'),
        null=True,
        blank=True
    )
    recommendations = models.TextField(
        _('健康建议'),
        null=True,
        blank=True
    )
    next_checkup_date = models.DateField(
        _('下次检查日期'),
        null=True,
        blank=True
    )
    doctor = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='health_records',
        limit_choices_to={'role': User.Role.DOCTOR},
        verbose_name=_('医生')
    )
    hospital = models.CharField(
        _('检查医院'),
        max_length=200,
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
        upload_to='health_records/',
        null=True,
        blank=True,
        help_text=_('体检报告等相关文件')
    )
    created_at = models.DateTimeField(_('创建时间'), auto_now_add=True)
    updated_at = models.DateTimeField(_('更新时间'), auto_now=True)
    created_by = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        related_name='created_health_records',
        verbose_name=_('创建人')
    )

    class Meta:
        verbose_name = _('健康档案')
        verbose_name_plural = _('健康档案')
        ordering = ['-record_date', '-created_at']

    def __str__(self):
        return f'{self.resident.name} - {self.record_date} ({self.get_record_type_display()})'

    def save(self, *args, **kwargs):
        if self.height and self.weight:
            try:
                height_m = float(self.height) / 100
                weight_kg = float(self.weight)
                if height_m > 0:
                    self.bmi = round(weight_kg / (height_m ** 2), 1)
            except (ValueError, TypeError):
                pass
        super().save(*args, **kwargs)
