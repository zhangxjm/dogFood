from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model
from apps.books.models import Category, Book

User = get_user_model()


class Command(BaseCommand):
    help = '初始化数据库数据'

    def handle(self, *args, **options):
        self.stdout.write('开始初始化数据...')
        
        self.create_admin()
        self.create_test_user()
        self.create_categories()
        self.create_books()
        
        self.stdout.write(self.style.SUCCESS('数据初始化完成！'))
        self.stdout.write('========================================')
        self.stdout.write('管理员账号: admin')
        self.stdout.write('管理员密码: admin123')
        self.stdout.write('========================================')
        self.stdout.write('测试用户账号: user1')
        self.stdout.write('测试用户密码: user123456')
        self.stdout.write('========================================')

    def create_admin(self):
        if not User.objects.filter(username='admin').exists():
            User.objects.create_superuser(
                username='admin',
                email='admin@example.com',
                password='admin123',
                first_name='管理员',
                role='admin',
                is_active=True,
            )
            self.stdout.write('创建管理员账号成功')
        else:
            self.stdout.write('管理员账号已存在')

    def create_test_user(self):
        if not User.objects.filter(username='user1').exists():
            User.objects.create_user(
                username='user1',
                email='user1@example.com',
                password='user123456',
                first_name='张三',
                role='reader',
                phone='13800138001',
                is_active=True,
            )
            self.stdout.write('创建测试用户账号成功')
        else:
            self.stdout.write('测试用户账号已存在')

    def create_categories(self):
        categories = [
            {'code': 'literature', 'name': '文学', 'description': '文学类书籍'},
            {'code': 'tech', 'name': '科技', 'description': '科技类书籍'},
            {'code': 'education', 'name': '教育', 'description': '教育类书籍'},
            {'code': 'history', 'name': '历史', 'description': '历史类书籍'},
            {'code': 'art', 'name': '艺术', 'description': '艺术类书籍'},
        ]
        
        for cat in categories:
            obj, created = Category.objects.get_or_create(
                code=cat['code'],
                defaults={
                    'name': cat['name'],
                    'description': cat['description'],
                    'is_active': True,
                }
            )
            if created:
                self.stdout.write(f'创建分类: {cat["name"]}')
            else:
                self.stdout.write(f'分类已存在: {cat["name"]}')

    def create_books(self):
        lit_category = Category.objects.filter(code='literature').first()
        tech_category = Category.objects.filter(code='tech').first()
        
        books = [
            {
                'isbn': '9787020002207',
                'title': '红楼梦',
                'author': '曹雪芹',
                'publisher': '人民文学出版社',
                'category': lit_category,
                'price': 59.80,
                'total_quantity': 10,
                'available_quantity': 10,
                'description': '中国古典四大名著之首',
            },
            {
                'isbn': '9787020002214',
                'title': '西游记',
                'author': '吴承恩',
                'publisher': '人民文学出版社',
                'category': lit_category,
                'price': 49.80,
                'total_quantity': 8,
                'available_quantity': 8,
                'description': '中国古典四大名著之一',
            },
            {
                'isbn': '9787111213826',
                'title': 'Python编程从入门到实践',
                'author': 'Eric Matthes',
                'publisher': '机械工业出版社',
                'category': tech_category,
                'price': 89.00,
                'total_quantity': 15,
                'available_quantity': 15,
                'description': 'Python入门经典书籍',
            },
            {
                'isbn': '9787121155350',
                'title': '数据结构与算法分析',
                'author': 'Mark Allen Weiss',
                'publisher': '电子工业出版社',
                'category': tech_category,
                'price': 79.00,
                'total_quantity': 6,
                'available_quantity': 6,
                'description': '经典的数据结构教材',
            },
            {
                'isbn': '9787302167075',
                'title': '算法导论',
                'author': 'Thomas H. Cormen',
                'publisher': '机械工业出版社',
                'category': tech_category,
                'price': 128.00,
                'total_quantity': 5,
                'available_quantity': 5,
                'description': '算法领域的经典之作',
            },
        ]
        
        for book in books:
            if not Book.objects.filter(isbn=book['isbn']).exists():
                Book.objects.create(**book)
                self.stdout.write(f'创建图书: {book["title"]}')
            else:
                self.stdout.write(f'图书已存在: {book["title"]}')
