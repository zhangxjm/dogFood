from flask import Blueprint, request, jsonify
from datetime import datetime, date, timedelta
from app import db
from app.models import CheckIn, Member

checkins_bp = Blueprint('checkins', __name__)

@checkins_bp.route('/', methods=['GET'])
def get_checkins():
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

@checkins_bp.route('/<int:checkin_id>', methods=['GET'])
def get_checkin(checkin_id):
    checkin = CheckIn.query.get_or_404(checkin_id)
    return jsonify({
        'success': True,
        'data': checkin.to_dict()
    })

@checkins_bp.route('/', methods=['POST'])
def create_checkin():
    data = request.get_json()
    
    if not data or 'member_id' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    member = Member.query.get(data['member_id'])
    if not member:
        return jsonify({'success': False, 'message': '会员不存在'}), 400
    
    if not member.is_active:
        return jsonify({'success': False, 'message': '会员账号已被禁用'}), 400
    
    today = date.today()
    today_start = datetime(today.year, today.month, today.day)
    today_end = datetime(today.year, today.month, today.day, 23, 59, 59)
    
    existing = CheckIn.query.filter(
        CheckIn.member_id == member.id,
        CheckIn.checkin_time >= today_start,
        CheckIn.checkin_time <= today_end
    ).first()
    
    if existing:
        return jsonify({'success': False, 'message': '今日已签到'}), 400
    
    checkin = CheckIn(
        member_id=member.id,
        checkin_type=data.get('checkin_type', 'normal'),
        location=data.get('location'),
        notes=data.get('notes')
    )
    
    db.session.add(checkin)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '签到成功',
        'data': checkin.to_dict()
    }), 201

@checkins_bp.route('/my/<int:member_id>', methods=['GET'])
def get_my_checkins(member_id):
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    start_date = request.args.get('start_date', None)
    end_date = request.args.get('end_date', None)
    
    member = Member.query.get_or_404(member_id)
    
    query = CheckIn.query.filter_by(member_id=member.id)
    
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

@checkins_bp.route('/my/<int:member_id>/stats', methods=['GET'])
def get_checkin_stats(member_id):
    member = Member.query.get_or_404(member_id)
    
    total_checkins = CheckIn.query.filter_by(member_id=member.id).count()
    
    today = date.today()
    today_start = datetime(today.year, today.month, today.day)
    today_checkin = CheckIn.query.filter(
        CheckIn.member_id == member.id,
        CheckIn.checkin_time >= today_start
    ).first()
    
    week_ago = today - timedelta(days=7)
    week_start = datetime(week_ago.year, week_ago.month, week_ago.day)
    week_checkins = CheckIn.query.filter(
        CheckIn.member_id == member.id,
        CheckIn.checkin_time >= week_start
    ).count()
    
    return jsonify({
        'success': True,
        'data': {
            'total_checkins': total_checkins,
            'today_checked': today_checkin is not None,
            'week_checkins': week_checkins
        }
    })
