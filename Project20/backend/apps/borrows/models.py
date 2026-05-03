from django.db import models
from django.utils import timezone
from datetime import timedelta
from apps.users.models import User
from apps.books.models import Book


class BorrowRecord(models.Model):
    """
    借阅记录模型
    """
    STATUS_CHOICES = (
        ('borrowed', '借阅中'),
        ('returned', '已归还'),
        ('overdue', '已逾期'),
        ('renewed', '已续借'),
    )
    
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='borrow_records',
        verbose_name='借阅人'
    )
    book = models.ForeignKey(
        Book,
        on_delete=models.CASCADE,
        related_name='borrow_records',
        verbose_name='借阅图书'
    )
    borrow_date = models.DateTimeField(
        auto_now_add=True,
        verbose_name='借阅日期'
    )
    due_date = models.DateTimeField(
        verbose_name='应还日期'
    )
    return_date = models.DateTimeField(
        null=True,
        blank=True,
        verbose_name='实际归还日期'
    )
    renew_count = models.IntegerField(
        default=0,
        verbose_name='续借次数'
    )
    max_renew_count = models.IntegerField(
        default=2,
        verbose_name='最大续借次数'
    )
    status = models.CharField(
        max_length=20,
        choices=STATUS_CHOICES,
        default='borrowed',
        verbose_name='借阅状态'
    )
    remarks = models.TextField(
        blank=True,
        null=True,
        verbose_name='备注'
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
        db_table = 'borrow_records'
        verbose_name = '借阅记录'
        verbose_name_plural = '借阅记录'
        ordering = ['-created_at']
    
    def __str__(self):
        return f'{self.user.username} - {self.book.title}'
    
    @property
    def is_overdue(self):
        """
        判断是否逾期
        """
        if self.status == 'returned':
            return False
        return timezone.now() > self.due_date
    
    @property
    def overdue_days(self):
        """
        计算逾期天数
        """
        if not self.is_overdue:
            return 0
        overdue_time = timezone.now() - self.due_date
        return max(0, overdue_time.days)
    
    @property
    def fine_amount(self):
        """
        计算罚款金额（每天0.5元）
        """
        return self.overdue_days * 0.5
    
    def save(self, *args, **kwargs):
        """
        保存时自动更新状态
        """
        if self.status not in ['returned'] and self.is_overdue:
            self.status = 'overdue'
        super().save(*args, **kwargs)
    
    def renew(self, renew_days=30):
        """
        续借操作
        """
        if self.renew_count >= self.max_renew_count:
            return False, '已达到最大续借次数'
        
        if self.status == 'returned':
            return False, '该图书已归还，无法续借'
        
        self.due_date += timedelta(days=renew_days)
        self.renew_count += 1
        self.status = 'renewed'
        self.save()
        return True, '续借成功'
    
    def return_book(self):
        """
        还书操作
        """
        if self.status == 'returned':
            return False, '该图书已归还'
        
        self.return_date = timezone.now()
        self.status = 'returned'
        self.save()
        
        # 更新图书库存
        self.book.return_book()
        
        return True, '归还成功'


class Fine(models.Model):
    """
    罚款记录模型
    """
    STATUS_CHOICES = (
        ('unpaid', '未支付'),
        ('paid', '已支付'),
        ('waived', '已豁免'),
    )
    
    borrow_record = models.ForeignKey(
        BorrowRecord,
        on_delete=models.CASCADE,
        related_name='fines',
        verbose_name='借阅记录'
    )
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='fines',
        verbose_name='用户'
    )
    amount = models.DecimalField(
        max_digits=10,
        decimal_places=2,
        verbose_name='罚款金额'
    )
    overdue_days = models.IntegerField(
        default=0,
        verbose_name='逾期天数'
    )
    reason = models.TextField(
        verbose_name='罚款原因'
    )
    status = models.CharField(
        max_length=20,
        choices=STATUS_CHOICES,
        default='unpaid',
        verbose_name='支付状态'
    )
    paid_date = models.DateTimeField(
        null=True,
        blank=True,
        verbose_name='支付日期'
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
        db_table = 'fines'
        verbose_name = '罚款记录'
        verbose_name_plural = '罚款记录'
        ordering = ['-created_at']
    
    def __str__(self):
        return f'{self.user.username} - ¥{self.amount}'
    
    def pay(self):
        """
        支付罚款
        """
        self.status = 'paid'
        self.paid_date = timezone.now()
        self.save()
        return True
    
    def waive(self, reason=''):
        """
        豁免罚款
        """
        self.status = 'waived'
        if reason:
            self.reason += f' (豁免原因: {reason})'
        self.save()
        return True
