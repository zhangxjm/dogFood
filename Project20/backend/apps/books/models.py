from django.db import models
from django.utils import timezone


class Category(models.Model):
    """
    图书分类模型
    """
    name = models.CharField(
        max_length=50,
        unique=True,
        verbose_name='分类名称'
    )
    code = models.CharField(
        max_length=20,
        unique=True,
        verbose_name='分类代码'
    )
    description = models.TextField(
        blank=True,
        null=True,
        verbose_name='分类描述'
    )
    parent = models.ForeignKey(
        'self',
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='children',
        verbose_name='父分类'
    )
    sort_order = models.IntegerField(
        default=0,
        verbose_name='排序'
    )
    is_active = models.BooleanField(
        default=True,
        verbose_name='是否启用'
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
        db_table = 'categories'
        verbose_name = '图书分类'
        verbose_name_plural = '图书分类'
        ordering = ['sort_order', 'name']
    
    def __str__(self):
        return self.name
    
    @property
    def book_count(self):
        return self.books.filter(is_active=True).count()


class Book(models.Model):
    """
    图书模型
    """
    STATUS_CHOICES = (
        ('available', '可借'),
        ('borrowed', '已借'),
        ('maintenance', '维护中'),
        ('lost', '丢失'),
    )
    
    isbn = models.CharField(
        max_length=20,
        unique=True,
        verbose_name='ISBN'
    )
    title = models.CharField(
        max_length=200,
        verbose_name='书名'
    )
    author = models.CharField(
        max_length=100,
        verbose_name='作者'
    )
    publisher = models.CharField(
        max_length=100,
        verbose_name='出版社'
    )
    publish_date = models.DateField(
        null=True,
        blank=True,
        verbose_name='出版日期'
    )
    category = models.ForeignKey(
        Category,
        on_delete=models.SET_NULL,
        null=True,
        related_name='books',
        verbose_name='图书分类'
    )
    price = models.DecimalField(
        max_digits=10,
        decimal_places=2,
        default=0.00,
        verbose_name='价格'
    )
    total_quantity = models.IntegerField(
        default=1,
        verbose_name='总数量'
    )
    available_quantity = models.IntegerField(
        default=1,
        verbose_name='可借数量'
    )
    location = models.CharField(
        max_length=100,
        blank=True,
        null=True,
        verbose_name='存放位置'
    )
    description = models.TextField(
        blank=True,
        null=True,
        verbose_name='图书简介'
    )
    cover = models.URLField(
        max_length=500,
        blank=True,
        null=True,
        verbose_name='封面URL'
    )
    status = models.CharField(
        max_length=20,
        choices=STATUS_CHOICES,
        default='available',
        verbose_name='状态'
    )
    is_active = models.BooleanField(
        default=True,
        verbose_name='是否启用'
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
        db_table = 'books'
        verbose_name = '图书'
        verbose_name_plural = '图书'
        ordering = ['-created_at']
    
    def __str__(self):
        return f'{self.title} ({self.isbn})'
    
    @property
    def is_available(self):
        return self.status == 'available' and self.available_quantity > 0
    
    def borrow_book(self):
        """
        借书操作
        """
        if self.available_quantity > 0:
            self.available_quantity -= 1
            if self.available_quantity == 0:
                self.status = 'borrowed'
            self.save()
            return True
        return False
    
    def return_book(self):
        """
        还书操作
        """
        self.available_quantity += 1
        if self.status == 'borrowed' and self.available_quantity > 0:
            self.status = 'available'
        self.save()
        return True
