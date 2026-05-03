import pytest
from datetime import time
from app import create_app, db
from app.models import Member, Coach, Course
from tests.test_config import TestConfig

@pytest.fixture
def app():
    app = create_app(TestConfig)
    with app.app_context():
        db.create_all()
        yield app
        db.session.remove()
        db.drop_all()

@pytest.fixture
def client(app):
    return app.test_client()

@pytest.fixture
def test_member(app):
    with app.app_context():
        member = Member(
            name='测试会员',
            phone='13800138000'
        )
        member.set_password('test123456')
        db.session.add(member)
        db.session.commit()
        yield member

@pytest.fixture
def test_coach(app):
    with app.app_context():
        coach = Coach(
            name='测试教练',
            phone='13900139000',
            specialty='减脂塑形',
            experience=5
        )
        db.session.add(coach)
        db.session.commit()
        yield coach

@pytest.fixture
def test_course(app, test_coach):
    with app.app_context():
        coach = Coach.query.first()
        course = Course(
            name='测试课程',
            description='这是一个测试课程',
            coach_id=coach.id,
            capacity=20,
            start_time=time(9, 0),
            duration=60,
            day_of_week=1,
            price=50.0
        )
        db.session.add(course)
        db.session.commit()
        yield course
