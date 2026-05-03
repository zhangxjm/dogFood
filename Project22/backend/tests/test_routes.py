import pytest
from datetime import time
from app import db
from app.models import Member, Coach, Course, Transaction

class TestHealthCheck:
    def test_health_check(self, client):
        response = client.get('/api/health')
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'ok'
        assert 'Fitness Club API' in data['message']

class TestMemberRoutes:
    def test_register_success(self, client):
        response = client.post('/api/members/register', json={
            'name': '新用户',
            'phone': '13700000001',
            'password': 'testpassword'
        })
        assert response.status_code == 201
        data = response.get_json()
        assert data['success'] == True
        assert data['message'] == '注册成功'
        assert data['data']['name'] == '新用户'
        assert data['data']['phone'] == '13700000001'

    def test_register_missing_fields(self, client):
        response = client.post('/api/members/register', json={
            'phone': '13700000002'
        })
        assert response.status_code == 400
        data = response.get_json()
        assert data['success'] == False
        assert '缺少必要参数' in data['message']

    def test_register_duplicate_phone(self, client, test_member):
        response = client.post('/api/members/register', json={
            'name': '重复用户',
            'phone': '13800138000',
            'password': 'testpassword'
        })
        assert response.status_code == 400
        data = response.get_json()
        assert data['success'] == False
        assert '手机号已注册' in data['message']

    def test_login_success(self, client, test_member):
        response = client.post('/api/members/login', json={
            'phone': '13800138000',
            'password': 'test123456'
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['message'] == '登录成功'

    def test_login_wrong_password(self, client, test_member):
        response = client.post('/api/members/login', json={
            'phone': '13800138000',
            'password': 'wrongpassword'
        })
        assert response.status_code == 401
        data = response.get_json()
        assert data['success'] == False
        assert '手机号或密码错误' in data['message']

    def test_get_member(self, client, test_member):
        response = client.get(f'/api/members/{test_member.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['data']['name'] == '测试会员'
        assert data['data']['phone'] == '13800138000'

    def test_get_member_not_found(self, client):
        response = client.get('/api/members/99999')
        assert response.status_code == 404

    def test_update_member(self, client, test_member):
        response = client.put(f'/api/members/{test_member.id}', json={
            'name': '更新后的名字',
            'member_type': 'VIP会员'
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['data']['name'] == '更新后的名字'
        assert data['data']['member_type'] == 'VIP会员'

    def test_change_password_success(self, client, test_member):
        response = client.post(f'/api/members/{test_member.id}/change-password', json={
            'old_password': 'test123456',
            'new_password': 'newpassword'
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert '密码修改成功' in data['message']

    def test_change_password_wrong_old(self, client, test_member):
        response = client.post(f'/api/members/{test_member.id}/change-password', json={
            'old_password': 'wrongpassword',
            'new_password': 'newpassword'
        })
        assert response.status_code == 400
        data = response.get_json()
        assert data['success'] == False
        assert '原密码错误' in data['message']

    def test_recharge_success(self, client, test_member):
        response = client.post(f'/api/members/{test_member.id}/recharge', json={
            'amount': 100.0,
            'payment_method': 'wechat'
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['data']['balance'] == 100.0

    def test_recharge_negative_amount(self, client, test_member):
        response = client.post(f'/api/members/{test_member.id}/recharge', json={
            'amount': -50.0
        })
        assert response.status_code == 400
        data = response.get_json()
        assert data['success'] == False

    def test_get_balance(self, client, test_member):
        response = client.get(f'/api/members/{test_member.id}/balance')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert 'balance' in data['data']

    def test_get_member_stats(self, client, test_member):
        response = client.get(f'/api/members/{test_member.id}/stats')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert 'total_checkins' in data['data']
        assert 'total_reservations' in data['data']
        assert 'balance' in data['data']

class TestCourseRoutes:
    def test_get_all_courses(self, client, test_course):
        response = client.get('/api/courses/')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert len(data['data']['courses']) > 0

    def test_get_course_by_id(self, client, test_course):
        response = client.get(f'/api/courses/{test_course.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['data']['name'] == '测试课程'

    def test_get_course_not_found(self, client):
        response = client.get('/api/courses/99999')
        assert response.status_code == 404

class TestCoachRoutes:
    def test_get_all_coaches(self, client, test_coach):
        response = client.get('/api/coaches/')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert len(data['data']['coaches']) > 0

    def test_get_coach_by_id(self, client, test_coach):
        response = client.get(f'/api/coaches/{test_coach.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert data['data']['coach']['name'] == '测试教练'

class TestCheckInRoutes:
    def test_checkin_member(self, client, test_member):
        response = client.post('/api/checkins/', json={
            'member_id': test_member.id,
            'checkin_type': 'normal'
        })
        assert response.status_code == 201
        data = response.get_json()
        assert data['success'] == True

    def test_get_my_checkins(self, client, test_member):
        response = client.get(f'/api/checkins/my/{test_member.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert 'checkins' in data['data']

class TestReservationRoutes:
    def test_get_my_reservations(self, client, test_member):
        response = client.get(f'/api/reservations/my/{test_member.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert 'reservations' in data['data']

class TestTransactionRoutes:
    def test_get_my_transactions(self, client, test_member):
        response = client.get(f'/api/transactions/my/{test_member.id}')
        assert response.status_code == 200
        data = response.get_json()
        assert data['success'] == True
        assert 'transactions' in data['data']
