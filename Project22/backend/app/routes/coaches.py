from flask import Blueprint, request, jsonify
from app import db
from app.models import Coach, Course

coaches_bp = Blueprint('coaches', __name__)

@coaches_bp.route('/', methods=['GET'])
def get_coaches():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_active = request.args.get('is_active', None)
    specialty = request.args.get('specialty', None)
    
    query = Coach.query
    
    if is_active is not None:
        query = query.filter_by(is_active=is_active.lower() == 'true')
    
    if specialty:
        query = query.filter(Coach.specialty.like(f'%{specialty}%'))
    
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

@coaches_bp.route('/<int:coach_id>', methods=['GET'])
def get_coach(coach_id):
    coach = Coach.query.get_or_404(coach_id)
    
    courses = Course.query.filter_by(coach_id=coach.id, is_active=True).all()
    
    return jsonify({
        'success': True,
        'data': {
            'coach': coach.to_dict(),
            'courses': [course.to_dict() for course in courses]
        }
    })

@coaches_bp.route('/', methods=['POST'])
def create_coach():
    data = request.get_json()
    
    if not data or 'name' not in data or 'phone' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    if Coach.query.filter_by(phone=data['phone']).first():
        return jsonify({'success': False, 'message': '手机号已存在'}), 400
    
    coach = Coach(
        name=data['name'],
        phone=data['phone'],
        specialty=data.get('specialty'),
        experience=data.get('experience', 0),
        avatar=data.get('avatar'),
        description=data.get('description')
    )
    
    db.session.add(coach)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '教练创建成功',
        'data': coach.to_dict()
    }), 201

@coaches_bp.route('/<int:coach_id>', methods=['PUT'])
def update_coach(coach_id):
    coach = Coach.query.get_or_404(coach_id)
    data = request.get_json()
    
    if 'name' in data:
        coach.name = data['name']
    if 'phone' in data:
        existing = Coach.query.filter_by(phone=data['phone']).first()
        if existing and existing.id != coach.id:
            return jsonify({'success': False, 'message': '手机号已存在'}), 400
        coach.phone = data['phone']
    if 'specialty' in data:
        coach.specialty = data['specialty']
    if 'experience' in data:
        coach.experience = data['experience']
    if 'avatar' in data:
        coach.avatar = data['avatar']
    if 'description' in data:
        coach.description = data['description']
    if 'is_active' in data:
        coach.is_active = data['is_active']
    
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '教练信息更新成功',
        'data': coach.to_dict()
    })

@coaches_bp.route('/<int:coach_id>', methods=['DELETE'])
def delete_coach(coach_id):
    coach = Coach.query.get_or_404(coach_id)
    
    coach.is_active = False
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '教练已禁用'
    })

@coaches_bp.route('/specialties', methods=['GET'])
def get_specialties():
    coaches = Coach.query.filter_by(is_active=True).all()
    specialties = set()
    for coach in coaches:
        if coach.specialty:
            specialties.update([s.strip() for s in coach.specialty.split(',')])
    
    return jsonify({
        'success': True,
        'data': {
            'specialties': list(specialties)
        }
    })
