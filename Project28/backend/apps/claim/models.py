from django.db import models
from django.conf import settings
from apps.item.models import Item


class Claim(models.Model):
    PENDING = 'pending'
    APPROVED = 'approved'
    REJECTED = 'rejected'
    
    STATUS_CHOICES = [
        (PENDING, '待确认'),
        (APPROVED, '已确认'),
        (REJECTED, '已拒绝'),
    ]
    
    item = models.ForeignKey(
        Item,
        on_delete=models.CASCADE,
        related_name='claims',
        verbose_name='物品'
    )
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='claims',
        verbose_name='认领人'
    )
    description = models.TextField('认领说明', help_text='描述物品特征等证明信息')
    contact_phone = models.CharField('联系电话', max_length=20)
    status = models.CharField('状态', max_length=20, choices=STATUS_CHOICES, default=PENDING)
    reject_reason = models.TextField('拒绝原因', blank=True, null=True)
    created_at = models.DateTimeField('创建时间', auto_now_add=True)
    updated_at = models.DateTimeField('更新时间', auto_now=True)
    
    class Meta:
        db_table = 'claim'
        verbose_name = '认领申请'
        verbose_name_plural = verbose_name
        ordering = ['-created_at']
    
    def __str__(self):
        return f'{self.user.username} - {self.item.title}'
