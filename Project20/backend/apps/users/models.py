from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils.translation import gettext_lazy as _


class User(AbstractUser):
    """
    自定义用户模型，扩展Django默认用户
    """
    ROLE_CHOICES = (
        ('reader', '读者'),
        ('admin', '管理员'),
    )
    
    GENDER_CHOICES = (
        ('male', '男'),
        ('female', '女'),
        ('unknown', '未知'),
    )
    
    role = models.CharField(
        max_length=20,
        choices=ROLE_CHOICES,
        default='reader',
        verbose_name='用户角色'
    )
    phone = models.CharField(
        max_length=11,
        blank=True,
        null=True,
        verbose_name='联系电话'
    )
    address = models.CharField(
        max_length=200,
        blank=True,
        null=True,
        verbose_name='联系地址'
    )
    gender = models.CharField(
        max_length=10,
        choices=GENDER_CHOICES,
        default='unknown',
        verbose_name='性别'
    )
    avatar = models.URLField(
        max_length=500,
        blank=True,
        null=True,
        verbose_name='头像URL'
    )
    max_borrow_count = models.IntegerField(
        default=5,
        verbose_name='最大可借数量'
    )
    borrow_days = models.IntegerField(
        default=30,
        verbose_name='借阅天数'
    )
    created_at = models.DateTimeField(
        auto_now_add=True,
        verbose_name='创建时间'
    )
    updated_at = models.DateTimeField(
        auto_now=True,
        verbose_name='更新时间'
    )
    
    class Meta:
        db_table = 'users'
        verbose_name = '用户'
        verbose_name_plural = '用户'
        ordering = ['-created_at']
    
    def __str__(self):
        return self.username
    
    @property
    def is_admin(self):
        return self.role == 'admin'
    
    @property
    def is_reader(self):
        return self.role == 'reader'
