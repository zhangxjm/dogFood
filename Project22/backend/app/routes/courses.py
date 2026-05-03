from flask import Blueprint, request, jsonify
from app import db
from app.models import Course, Coach

courses_bp = Blueprint('courses', __name__)

@courses_bp.route('/', methods=['GET'])
def get_courses():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    is_active = request.args.get('is_active', None)
    
    query = Course.query
    
    if is_active is not None:
        query = query.filter_by(is_active=is_active.lower() == 'true')
    
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

@courses_bp.route('/<int:course_id>', methods=['GET'])
def get_course(course_id):
    course = Course.query.get_or_404(course_id)
    return jsonify({
        'success': True,
        'data': course.to_dict()
    })

@courses_bp.route('/', methods=['POST'])
def create_course():
    data = request.get_json()
    
    if not data or 'name' not in data or 'coach_id' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    coach = Coach.query.get(data['coach_id'])
    if not coach:
        return jsonify({'success': False, 'message': '教练不存在'}), 400
    
    course = Course(
        name=data['name'],
        description=data.get('description'),
        coach_id=data['coach_id'],
        capacity=data.get('capacity', 20),
        start_time=data.get('start_time', '09:00:00'),
        duration=data.get('duration', 60),
        day_of_week=data.get('day_of_week', 1),
        price=data.get('price', 0.0),
        image=data.get('image')
    )
    
    db.session.add(course)
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '课程创建成功',
        'data': course.to_dict()
    }), 201

@courses_bp.route('/<int:course_id>', methods=['PUT'])
def update_course(course_id):
    course = Course.query.get_or_404(course_id)
    data = request.get_json()
    
    if 'name' in data:
        course.name = data['name']
    if 'description' in data:
        course.description = data['description']
    if 'coach_id' in data:
        coach = Coach.query.get(data['coach_id'])
        if not coach:
            return jsonify({'success': False, 'message': '教练不存在'}), 400
        course.coach_id = data['coach_id']
    if 'capacity' in data:
        course.capacity = data['capacity']
    if 'start_time' in data:
        course.start_time = data['start_time']
    if 'duration' in data:
        course.duration = data['duration']
    if 'day_of_week' in data:
        course.day_of_week = data['day_of_week']
    if 'price' in data:
        course.price = data['price']
    if 'image' in data:
        course.image = data['image']
    if 'is_active' in data:
        course.is_active = data['is_active']
    
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '课程更新成功',
        'data': course.to_dict()
    })

@courses_bp.route('/<int:course_id>', methods=['DELETE'])
def delete_course(course_id):
    course = Course.query.get_or_404(course_id)
    
    course.is_active = False
    db.session.commit()
    
    return jsonify({
        'success': True,
        'message': '课程已禁用'
    })

@courses_bp.route('/by-day/<int:day_of_week>', methods=['GET'])
def get_courses_by_day(day_of_week):
    courses = Course.query.filter_by(day_of_week=day_of_week, is_active=True).order_by(Course.start_time).all()
    
    return jsonify({
        'success': True,
        'data': {
            'courses': [course.to_dict() for course in courses],
            'day_of_week': day_of_week
        }
    })
