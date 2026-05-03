from flask import Blueprint, request, jsonify
from datetime import datetime, timedelta
from app import db
from app.models import Member, Coach, Course, Reservation, CheckIn, Transaction

admin_bp = Blueprint('admin', __name__)

@admin_bp.route('/stats', methods=['GET'])
def get_admin_stats():
    total_members = Member.query.count()
    active_members = Member.query.filter_by(is_active=True).count()
    total_coaches = Coach.query.count()
    active_coaches = Coach.query.filter_by(is_active=True).count()
    total_courses = Course.query.count()
    active_courses = Course.query.filter_by(is_active=True).count()
    
    total_reservations = Reservation.query.count()
    today = datetime.today().date()
    today_reservations = Reservation.query.filter_by(reservation_date=today).count()
    
    total_checkins = CheckIn.query.count()
    today_start = datetime(today.year, today.month, today.day)
    today_checkins = CheckIn.query.filter(CheckIn.checkin_time >= today_start).count()
    
    total_recharge = db.session.query(
        db.func.sum(Transaction.amount)
    ).filter(
        Transaction.transaction_type == 'recharge',
        Transaction.status == 'completed'
    ).scalar() or 0
    
    total_consumption = db.session.query(
        db.func.sum(Transaction.amount)
    ).filter(
        Transaction.transaction_type == 'consumption',
        Transaction.status == 'completed'
    ).scalar() or 0
    
    return jsonify({
        'success': True,
        'data': {
            'members': {
                'total': total_members,
                'active': active_members
            },
            'coaches': {
                'total': total_coaches,
                'active': active_coaches
            },
            'courses': {
                'total': total_courses,
                'active': active_courses
            },
            'reservations': {
                'total': total_reservations,
                'today': today_reservations
            },
            'checkins': {
                'total': total_checkins,
                'today': today_checkins
            },
            'finance': {
                'total_recharge': total_recharge,
                'total_consumption': total_consumption
            }
        }
    })

@admin_bp.route('/members', methods=['GET'])
def get_all_members():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_active = request.args.get('is_active', None)
    keyword = request.args.get('keyword', None)
    
    query = Member.query
    
    if is_active is not None:
        query = query.filter_by(is_active=is_active.lower() == 'true')
    
    if keyword:
        query = query.filter(
            db.or_(
                Member.name.like(f'%{keyword}%'),
                Member.phone.like(f'%{keyword}%'),
                Member.member_card_id.like(f'%{keyword}%')
            )
        )
    
    pagination = query.order_by(Member.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'members': [member.to_dict() for member in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@admin_bp.route('/members/<int:member_id>', methods=['PUT'])
def update_member_status(member_id):
    member = Member.query.get_or_404(member_id)
    data = request.get_json()
    
    if 'is_active' in data:
        member.is_active = data['is_active']
    
    if 'member_type' in data:
        member.member_type = data['member_type']
    
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '会员信息更新成功',
        'data': member.to_dict()
    })

@admin_bp.route('/members/<int:member_id>/adjust-balance', methods=['POST'])
def adjust_member_balance(member_id):
    member = Member.query.get_or_404(member_id)
    data = request.get_json()
    
    if not data or 'amount' not in data or 'type' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    amount = float(data['amount'])
    adjust_type = data['type']
    
    balance_before = member.balance
    
    if adjust_type == 'add':
        member.balance += amount
        description = f'管理员增加余额 {amount} 元'
    elif adjust_type == 'subtract':
        if member.balance < amount:
            return jsonify({'success': False, 'message': '余额不足'}), 400
        member.balance -= amount
        description = f'管理员扣减余额 {amount} 元'
    else:
        return jsonify({'success': False, 'message': '无效的调整类型'}), 400
    
    transaction = Transaction(
        member_id=member.id,
        transaction_type='adjust',
        amount=amount if adjust_type == 'add' else -amount,
        balance_before=balance_before,
        balance_after=member.balance,
        description=description,
        payment_method='admin'
    )
    
    db.session.add(transaction)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '余额调整成功',
        'data': {
            'balance': member.balance,
            'transaction': transaction.to_dict()
        }
    })

@admin_bp.route('/courses', methods=['GET'])
def get_all_courses():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_active = request.args.get('is_active', None)
    coach_id = request.args.get('coach_id', None, type=int)
    
    query = Course.query
    
    if is_active is not None:
        query = query.filter_by(is_active=is_active.lower() == 'true')
    
    if coach_id:
        query = query.filter_by(coach_id=coach_id)
    
    pagination = query.order_by(Course.day_of_week, Course.start_time).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'courses': [course.to_dict() for course in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@admin_bp.route('/coaches', methods=['GET'])
def get_all_coaches():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_active = request.args.get('is_active', None)
    
    query = Coach.query
    
    if is_active is not None:
        query = query.filter_by(is_active=is_active.lower() == 'true')
    
    pagination = query.order_by(Coach.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'coaches': [coach.to_dict() for coach in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@admin_bp.route('/reservations', methods=['GET'])
def get_all_reservations():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    status = request.args.get('status', None)
    reservation_type = request.args.get('type', None)
    member_id = request.args.get('member_id', None, type=int)
    
    query = Reservation.query
    
    if status:
        query = query.filter_by(status=status)
    if reservation_type:
        query = query.filter_by(reservation_type=reservation_type)
    if member_id:
        query = query.filter_by(member_id=member_id)
    
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

@admin_bp.route('/checkins', methods=['GET'])
def get_all_checkins():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    member_id = request.args.get('member_id', None, type=int)
    start_date = request.args.get('start_date', None)
    end_date = request.args.get('end_date', None)
    
    query = CheckIn.query
    
    if member_id:
        query = query.filter_by(member_id=member_id)
    if start_date:
        start = datetime.strptime(start_date, '%Y-%m-%d')
        query = query.filter(CheckIn.checkin_time >= start)
    if end_date:
        end = datetime.strptime(end_date, '%Y-%m-%d') + timedelta(days=1)
        query = query.filter(CheckIn.checkin_time < end)
    
    pagination = query.order_by(CheckIn.checkin_time.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'checkins': [checkin.to_dict() for checkin in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@admin_bp.route('/transactions', methods=['GET'])
def get_all_transactions():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    transaction_type = request.args.get('type', None)
    member_id = request.args.get('member_id', None, type=int)
    start_date = request.args.get('start_date', None)
    end_date = request.args.get('end_date', None)
    
    query = Transaction.query
    
    if transaction_type:
        query = query.filter_by(transaction_type=transaction_type)
    if member_id:
        query = query.filter_by(member_id=member_id)
    if start_date:
        start = datetime.strptime(start_date, '%Y-%m-%d')
        query = query.filter(Transaction.created_at >= start)
    if end_date:
        end = datetime.strptime(end_date, '%Y-%m-%d') + timedelta(days=1)
        query = query.filter(Transaction.created_at < end)
    
    pagination = query.order_by(Transaction.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'transactions': [transaction.to_dict() for transaction in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })
