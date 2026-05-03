import pytest
from datetime import time
from app import db
from app.models import Member, Coach, Course, Reservation, CheckIn, Transaction

class TestMemberModel:
    def test_create_member(self, app):
        with app.app_context():
            member = Member(
                name='张三',
                phone='13800000001'
            )
            member.set_password('password123')
            db.session.add(member)
            db.session.commit()
            
            assert member.id is not None
            assert member.name == '张三'
            assert member.phone == '13800000001'
            assert member.balance == 0.0
            assert member.is_active == True

    def test_password_hashing(self, app):
        with app.app_context():
            member = Member(
                name='测试用户',
                phone='13800000002'
            )
            member.set_password('mypassword')
            db.session.add(member)
            db.session.commit()
            
            assert member.check_password('mypassword') == True
            assert member.check_password('wrongpassword') == False
            assert member.password_hash != 'mypassword'

    def test_member_to_dict(self, app):
        with app.app_context():
            member = Member(
                name='李四',
                phone='13800000003',
                member_type='VIP会员'
            )
            member.set_password('123456')
            db.session.add(member)
            db.session.commit()
            
            data = member.to_dict()
            assert data['id'] == member.id
            assert data['name'] == '李四'
            assert data['phone'] == '13800000003'
            assert data['member_type'] == 'VIP会员'
            assert 'password_hash' not in data

class TestCoachModel:
    def test_create_coach(self, app):
        with app.app_context():
            coach = Coach(
                name='王教练',
                phone='13900000001',
                specialty='力量训练',
                experience=8
            )
            db.session.add(coach)
            db.session.commit()
            
            assert coach.id is not None
            assert coach.name == '王教练'
            assert coach.specialty == '力量训练'
            assert coach.experience == 8

    def test_coach_to_dict(self, app):
        with app.app_context():
            coach = Coach(
                name='李教练',
                phone='13900000002',
                specialty='瑜伽',
                experience=5
            )
            db.session.add(coach)
            db.session.commit()
            
            data = coach.to_dict()
            assert data['id'] == coach.id
            assert data['name'] == '李教练'
            assert data['specialty'] == '瑜伽'
            assert data['experience'] == 5

class TestCourseModel:
    def test_create_course(self, app, test_coach):
        with app.app_context():
            coach = Coach.query.first()
            course = Course(
                name='动感单车',
                description='高强度有氧训练',
                coach_id=coach.id,
                capacity=20,
                start_time=time(9, 0),
                duration=45,
                day_of_week=1,
                price=30.0
            )
            db.session.add(course)
            db.session.commit()
            
            assert course.id is not None
            assert course.name == '动感单车'
            assert course.capacity == 20
            assert course.duration == 45
            assert course.price == 30.0

    def test_course_calculate_end_time(self, app, test_coach):
        with app.app_context():
            coach = Coach.query.first()
            course = Course(
                name='测试课程',
                coach_id=coach.id,
                start_time=time(9, 30),
                duration=60,
                day_of_week=1
            )
            db.session.add(course)
            db.session.commit()
            
            assert course.calculate_end_time() == '10:30'

    def test_course_get_day_name(self, app, test_coach):
        with app.app_context():
            coach = Coach.query.first()
            course1 = Course(
                name='周一课程',
                coach_id=coach.id,
                start_time=time(9, 0),
                day_of_week=1
            )
            course7 = Course(
                name='周日课程',
                coach_id=coach.id,
                start_time=time(9, 0),
                day_of_week=7
            )
            db.session.add_all([course1, course7])
            db.session.commit()
            
            assert course1.get_day_name() == '周一'
            assert course7.get_day_name() == '周日'

    def test_course_to_dict(self, app, test_coach):
        with app.app_context():
            coach = Coach.query.first()
            course = Course(
                name='瑜伽课',
                description='放松身心',
                coach_id=coach.id,
                capacity=15,
                start_time=time(10, 0),
                duration=60,
                day_of_week=2,
                price=40.0
            )
            db.session.add(course)
            db.session.commit()
            
            data = course.to_dict()
            assert data['name'] == '瑜伽课'
            assert data['max_capacity'] == 15
            assert data['duration'] == 60
            assert data['price'] == 40.0
            assert data['day_name'] == '周二'

class TestTransactionModel:
    def test_create_transaction(self, app, test_member):
        with app.app_context():
            member = Member.query.first()
            transaction = Transaction(
                member_id=member.id,
                transaction_type='recharge',
                amount=100.0,
                balance_before=0.0,
                balance_after=100.0,
                description='账户充值'
            )
            db.session.add(transaction)
            db.session.commit()
            
            assert transaction.id is not None
            assert transaction.transaction_type == 'recharge'
            assert transaction.amount == 100.0

    def test_transaction_to_dict(self, app, test_member):
        with app.app_context():
            member = Member.query.first()
            transaction = Transaction(
                member_id=member.id,
                transaction_type='consume',
                amount=50.0,
                balance_before=100.0,
                balance_after=50.0,
                description='课程消费'
            )
            db.session.add(transaction)
            db.session.commit()
            
            data = transaction.to_dict()
            assert data['id'] == transaction.id
            assert data['transaction_type'] == 'consume'
            assert data['amount'] == 50.0
