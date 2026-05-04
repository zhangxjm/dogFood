from django.db import models
from django.contrib.auth.models import AbstractUser


class User(AbstractUser):
    STUDENT = 'student'
    TEACHER = 'teacher'
    ADMIN = 'admin'
    
    ROLE_CHOICES = [
        (STUDENT, '学生'),
        (TEACHER, '教师'),
        (ADMIN, '管理员'),
    ]
    
    MALE = 'male'
    FEMALE = 'female'
    
    GENDER_CHOICES = [
        (MALE, '男'),
        (FEMALE, '女'),
    ]
    
    phone = models.CharField('手机号', max_length=11, blank=True, null=True)
    avatar = models.ImageField('头像', upload_to='avatars/', blank=True, null=True)
    role = models.CharField('角色', max_length=20, choices=ROLE_CHOICES, default=STUDENT)
    gender = models.CharField('性别', max_length=10, choices=GENDER_CHOICES, default=MALE)
    student_id = models.CharField('学号/工号', max_length=20, blank=True, null=True)
    department = models.CharField('院系', max_length=100, blank=True, null=True)
    bio = models.TextField('个人简介', blank=True, null=True)
    created_at = models.DateTimeField('创建时间', auto_now_add=True)
    updated_at = models.DateTimeField('更新时间', auto_now=True)
    
    class Meta:
        db_table = 'user'
        verbose_name = '用户'
        verbose_name_plural = verbose_name
    
    def __str__(self):
        return self.username
