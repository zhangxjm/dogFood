import os
import sys
import django

import pymysql
pymysql.install_as_MySQLdb()

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'school_lost_found.settings')
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
django.setup()

from django.contrib.auth import get_user_model
from apps.item.models import Category

User = get_user_model()

def create_admin():
    admin_username = 'admin'
    admin_email = 'admin@example.com'
    admin_password = 'admin123456'
    
    if not User.objects.filter(username=admin_username).exists():
        user = User.objects.create_superuser(
            username=admin_username,
            email=admin_email,
            password=admin_password,
            role='admin',
            phone='13800138000'
        )
        print(f'管理员用户创建成功!')
        print(f'  用户名: {admin_username}')
        print(f'  密码: {admin_password}')
        print(f'  邮箱: {admin_email}')
    else:
        print(f'管理员用户 "{admin_username}" 已存在')

def create_categories():
    categories = [
        {'name': '电子产品', 'description': '手机、电脑、耳机等电子设备', 'icon': 'laptop', 'sort_order': 1},
        {'name': '证件卡片', 'description': '身份证、学生证、银行卡等', 'icon': 'idcard', 'sort_order': 2},
        {'name': '钱包现金', 'description': '钱包、现金、银行卡等', 'icon': 'wallet', 'sort_order': 3},
        {'name': '钥匙锁具', 'description': '钥匙、锁具等', 'icon': 'key', 'sort_order': 4},
        {'name': '书籍文具', 'description': '书籍、笔记本、笔等', 'icon': 'book', 'sort_order': 5},
        {'name': '服饰箱包', 'description': '衣服、包包、配饰等', 'icon': 'shoppingBag', 'sort_order': 6},
        {'name': '运动器材', 'description': '篮球、球拍、运动装备等', 'icon': 'basketball', 'sort_order': 7},
        {'name': '其他物品', 'description': '其他未分类物品', 'icon': 'more', 'sort_order': 100},
    ]
    
    created_count = 0
    for cat_data in categories:
        category, created = Category.objects.get_or_create(
            name=cat_data['name'],
            defaults=cat_data
        )
        if created:
            created_count += 1
            print(f'分类 "{cat_data["name"]}" 创建成功')
        else:
            print(f'分类 "{cat_data["name"]}" 已存在')
    
    print(f'分类初始化完成，新增 {created_count} 个分类')

if __name__ == '__main__':
    print('=' * 50)
    print('开始初始化数据...')
    print('=' * 50)
    
    print('\n[1] 创建管理员用户...')
    create_admin()
    
    print('\n[2] 初始化物品分类...')
    create_categories()
    
    print('\n' + '=' * 50)
    print('数据初始化完成!')
    print('=' * 50)
