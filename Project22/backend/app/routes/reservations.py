from flask import Blueprint, request, jsonify
from datetime import datetime, date, timedelta
from app import db
from app.models import Reservation, Member, Course, Coach, Transaction

reservations_bp = Blueprint('reservations', __name__)

@reservations_bp.route('/', methods=['GET'])
def get_reservations():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    member_id = request.args.get('member_id', None, type=int)
    status = request.args.get('status', None)
    reservation_type = request.args.get('type', None)
    
    query = Reservation.query
    
    if member_id:
        query = query.filter_by(member_id=member_id)
    if status:
        query = query.filter_by(status=status)
    if reservation_type:
        query = query.filter_by(reservation_type=reservation_type)
    
    pagination = query.order_by(Reservation.reservation_date.desc(), Reservation.reservation_time.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'reservations': [reservation.to_dict() for reservation in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@reservations_bp.route('/<int:reservation_id>', methods=['GET'])
def get_reservation(reservation_id):
    reservation = Reservation.query.get_or_404(reservation_id)
    return jsonify({
        'success': True,
        'data': reservation.to_dict()
    })

@reservations_bp.route('/group', methods=['POST'])
def create_group_reservation():
    data = request.get_json()
    
    if not data or 'member_id' not in data or 'course_id' not in data or 'reservation_date' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    member = Member.query.get(data['member_id'])
    if not member:
        return jsonify({'success': False, 'message': '会员不存在'}), 400
    
    course = Course.query.get(data['course_id'])
    if not course or not course.is_active:
        return jsonify({'success': False, 'message': '课程不存在或已禁用'}), 400
    
    reservation_date = datetime.strptime(data['reservation_date'], '%Y-%m-%d').date()
    
    if reservation_date < date.today():
        return jsonify({'success': False, 'message': '不能预约过去的日期'}), 400
    
    existing = Reservation.query.filter_by(
        member_id=member.id,
        course_id=course.id,
        reservation_date=reservation_date,
        reservation_type='group'
    ).first()
    
    if existing:
        return jsonify({'success': False, 'message': '该日期已预约此课程'}), 400
    
    if course.current_count >= course.capacity:
        return jsonify({'success': False, 'message': '课程名额已满'}), 400
    
    reservation = Reservation(
        member_id=member.id,
        course_id=course.id,
        reservation_date=reservation_date,
        reservation_time=course.start_time,
        reservation_type='group',
        status='confirmed',
        notes=data.get('notes')
    )
    
    course.current_count += 1
    
    db.session.add(reservation)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '预约成功',
        'data': reservation.to_dict()
    }), 201

@reservations_bp.route('/private', methods=['POST'])
def create_private_reservation():
    data = request.get_json()
    
    if not data or 'member_id' not in data or 'private_coach_id' not in data or 'reservation_date' not in data or 'reservation_time' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    member = Member.query.get(data['member_id'])
    if not member:
        return jsonify({'success': False, 'message': '会员不存在'}), 400
    
    coach = Coach.query.get(data['private_coach_id'])
    if not coach or not coach.is_active:
        return jsonify({'success': False, 'message': '教练不存在或已禁用'}), 400
    
    reservation_date = datetime.strptime(data['reservation_date'], '%Y-%m-%d').date()
    reservation_time = datetime.strptime(data['reservation_time'], '%H:%M:%S').time()
    
    if reservation_date < date.today():
        return jsonify({'success': False, 'message': '不能预约过去的日期'}), 400
    
    existing = Reservation.query.filter_by(
        private_coach_id=coach.id,
        reservation_date=reservation_date,
        reservation_time=reservation_time,
        reservation_type='private'
    ).filter(Reservation.status != 'cancelled').first()
    
    if existing:
        return jsonify({'success': False, 'message': '该教练此时间段已有预约'}), 400
    
    price = data.get('price', 100.0)
    
    if member.balance < price:
        return jsonify({'success': False, 'message': '余额不足，请先充值'}), 400
    
    balance_before = member.balance
    member.balance -= price
    
    reservation = Reservation(
        member_id=member.id,
        private_coach_id=coach.id,
        reservation_date=reservation_date,
        reservation_time=reservation_time,
        reservation_type='private',
        status='confirmed',
        notes=data.get('notes')
    )
    
    transaction = Transaction(
        member_id=member.id,
        transaction_type='consumption',
        amount=price,
        balance_before=balance_before,
        balance_after=member.balance,
        description=f'私教课程预约 - {coach.name}',
        payment_method='balance'
    )
    
    db.session.add(reservation)
    db.session.add(transaction)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '预约成功',
        'data': {
            'reservation': reservation.to_dict(),
            'transaction': transaction.to_dict(),
            'balance': member.balance
        }
    }), 201

@reservations_bp.route('/<int:reservation_id>/cancel', methods=['POST'])
def cancel_reservation(reservation_id):
    reservation = Reservation.query.get_or_404(reservation_id)
    
    if reservation.status == 'cancelled':
        return jsonify({'success': False, 'message': '预约已取消'}), 400
    
    if reservation.reservation_date < date.today():
        return jsonify({'success': False, 'message': '不能取消过去的预约'}), 400
    
    reservation.status = 'cancelled'
    
    if reservation.reservation_type == 'group' and reservation.course_id:
        course = Course.query.get(reservation.course_id)
        if course and course.current_count > 0:
            course.current_count -= 1
    
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '预约已取消',
        'data': reservation.to_dict()
    })

@reservations_bp.route('/my/<int:member_id>', methods=['GET'])
def get_my_reservations(member_id):
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    status = request.args.get('status', None)
    
    member = Member.query.get_or_404(member_id)
    
    query = Reservation.query.filter_by(member_id=member.id)
    
    if status:
        query = query.filter_by(status=status)
    
    pagination = query.order_by(Reservation.reservation_date.desc(), Reservation.reservation_time.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'reservations': [reservation.to_dict() for reservation in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })
