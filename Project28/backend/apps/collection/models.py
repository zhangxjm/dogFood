from django.db import models
from django.conf import settings
from apps.item.models import Item


class Collection(models.Model):
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='collections',
        verbose_name='收藏用户'
    )
    item = models.ForeignKey(
        Item,
        on_delete=models.CASCADE,
        related_name='collections',
        verbose_name='收藏物品'
    )
    created_at = models.DateTimeField('创建时间', auto_now_add=True)
    
    class Meta:
        db_table = 'collection'
        verbose_name = '收藏'
        verbose_name_plural = verbose_name
        unique_together = ['user', 'item']
    
    def __str__(self):
        return f'{self.user.username} - {self.item.title}'
