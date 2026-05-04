from django.db import models
from django.conf import settings


class Category(models.Model):
    name = models.CharField('分类名称', max_length=50, unique=True)
    description = models.TextField('分类描述', blank=True, null=True)
    icon = models.CharField('图标', max_length=100, blank=True, null=True)
    sort_order = models.IntegerField('排序', default=0)
    is_active = models.BooleanField('是否启用', default=True)
    created_at = models.DateTimeField('创建时间', auto_now_add=True)
    updated_at = models.DateTimeField('更新时间', auto_now=True)
    
    class Meta:
        db_table = 'category'
        verbose_name = '物品分类'
        verbose_name_plural = verbose_name
        ordering = ['sort_order', '-created_at']
    
    def __str__(self):
        return self.name


class Item(models.Model):
    LOST = 'lost'
    FOUND = 'found'
    
    TYPE_CHOICES = [
        (LOST, '寻物启事'),
        (FOUND, '招领启事'),
    ]
    
    PENDING = 'pending'
    ACTIVE = 'active'
    RESOLVED = 'resolved'
    REJECTED = 'rejected'
    
    STATUS_CHOICES = [
        (PENDING, '待审核'),
        (ACTIVE, '已发布'),
        (RESOLVED, '已完成'),
        (REJECTED, '已驳回'),
    ]
    
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name='items',
        verbose_name='发布用户'
    )
    category = models.ForeignKey(
        Category,
        on_delete=models.SET_NULL,
        null=True,
        related_name='items',
        verbose_name='物品分类'
    )
    title = models.CharField('标题', max_length=200)
    description = models.TextField('详细描述')
    item_type = models.CharField('类型', max_length=10, choices=TYPE_CHOICES)
    status = models.CharField('状态', max_length=20, choices=STATUS_CHOICES, default=PENDING)
    location = models.CharField('地点', max_length=200)
    item_time = models.DateTimeField('物品时间', help_text='遗失或发现的时间')
    contact_name = models.CharField('联系人', max_length=50)
    contact_phone = models.CharField('联系电话', max_length=20)
    reward = models.DecimalField('悬赏金额', max_digits=10, decimal_places=2, default=0.00, blank=True, null=True)
    image1 = models.ImageField('图片1', upload_to='items/', blank=True, null=True)
    image2 = models.ImageField('图片2', upload_to='items/', blank=True, null=True)
    image3 = models.ImageField('图片3', upload_to='items/', blank=True, null=True)
    view_count = models.IntegerField('浏览次数', default=0)
    collect_count = models.IntegerField('收藏次数', default=0)
    admin_note = models.TextField('管理员备注', blank=True, null=True)
    created_at = models.DateTimeField('创建时间', auto_now_add=True)
    updated_at = models.DateTimeField('更新时间', auto_now=True)
    
    class Meta:
        db_table = 'item'
        verbose_name = '物品信息'
        verbose_name_plural = verbose_name
        ordering = ['-created_at']
    
    def __str__(self):
        return self.title
