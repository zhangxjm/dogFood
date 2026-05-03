from datetime import datetime
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, User, Movie, MovieCategory, Cinema, Hall, Schedule, Order

admin_bp = Blueprint('admin', __name__)


def is_admin():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    return user and user.is_admin


@admin_bp.route('/movies', methods=['GET'])
@jwt_required()
def get_admin_movies():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    movies = Movie.query.all()
    
    return jsonify([movie.to_dict() for movie in movies]), 200


@admin_bp.route('/movies', methods=['POST'])
@jwt_required()
def create_movie():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    data = request.get_json()
    
    if not data or not data.get('title') or not data.get('duration'):
        return jsonify({'message': '电影名称和时长不能为空'}), 400
    
    movie = Movie(
        title=data.get('title'),
        poster=data.get('poster'),
        description=data.get('description'),
        duration=data.get('duration'),
        release_date=datetime.strptime(data.get('release_date'), '%Y-%m-%d').date() if data.get('release_date') else None,
        rating=data.get('rating', 0.0),
        director=data.get('director'),
        actors=data.get('actors'),
        category_id=data.get('category_id'),
        status=data.get('status', 'showing'),
        is_hot=data.get('is_hot', False)
    )
    
    db.session.add(movie)
    db.session.commit()
    
    return jsonify({
        'message': '电影创建成功',
        'movie': movie.to_dict()
    }), 201


@admin_bp.route('/movies/<int:movie_id>', methods=['PUT'])
@jwt_required()
def update_movie(movie_id):
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    movie = Movie.query.get(movie_id)
    
    if not movie:
        return jsonify({'message': '电影不存在'}), 404
    
    data = request.get_json()
    
    if data.get('title'):
        movie.title = data.get('title')
    if data.get('poster'):
        movie.poster = data.get('poster')
    if data.get('description'):
        movie.description = data.get('description')
    if data.get('duration'):
        movie.duration = data.get('duration')
    if data.get('release_date'):
        movie.release_date = datetime.strptime(data.get('release_date'), '%Y-%m-%d').date()
    if data.get('rating') is not None:
        movie.rating = data.get('rating')
    if data.get('director'):
        movie.director = data.get('director')
    if data.get('actors'):
        movie.actors = data.get('actors')
    if data.get('category_id'):
        movie.category_id = data.get('category_id')
    if data.get('status'):
        movie.status = data.get('status')
    if data.get('is_hot') is not None:
        movie.is_hot = data.get('is_hot')
    
    db.session.commit()
    
    return jsonify({
        'message': '电影更新成功',
        'movie': movie.to_dict()
    }), 200


@admin_bp.route('/movies/<int:movie_id>', methods=['DELETE'])
@jwt_required()
def delete_movie(movie_id):
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    movie = Movie.query.get(movie_id)
    
    if not movie:
        return jsonify({'message': '电影不存在'}), 404
    
    if movie.schedules:
        return jsonify({'message': '该电影有关联场次，无法删除'}), 400
    
    db.session.delete(movie)
    db.session.commit()
    
    return jsonify({'message': '电影删除成功'}), 200


@admin_bp.route('/categories', methods=['GET'])
@jwt_required()
def get_categories():
    categories = MovieCategory.query.all()
    
    return jsonify([category.to_dict() for category in categories]), 200


@admin_bp.route('/categories', methods=['POST'])
@jwt_required()
def create_category():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    data = request.get_json()
    
    if not data or not data.get('name'):
        return jsonify({'message': '分类名称不能为空'}), 400
    
    if MovieCategory.query.filter_by(name=data.get('name')).first():
        return jsonify({'message': '分类名称已存在'}), 400
    
    category = MovieCategory(
        name=data.get('name'),
        description=data.get('description')
    )
    
    db.session.add(category)
    db.session.commit()
    
    return jsonify({
        'message': '分类创建成功',
        'category': category.to_dict()
    }), 201


@admin_bp.route('/schedules', methods=['GET'])
@jwt_required()
def get_admin_schedules():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    movie_id = request.args.get('movie_id', type=int)
    cinema_id = request.args.get('cinema_id', type=int)
    
    query = Schedule.query
    
    if movie_id:
        query = query.filter_by(movie_id=movie_id)
    
    if cinema_id:
        query = query.join(Schedule.hall).filter(Hall.cinema_id == cinema_id)
    
    schedules = query.order_by(Schedule.start_time).all()
    
    return jsonify([schedule.to_dict() for schedule in schedules]), 200


@admin_bp.route('/schedules', methods=['POST'])
@jwt_required()
def create_schedule():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    data = request.get_json()
    
    required_fields = ['movie_id', 'hall_id', 'start_time', 'end_time', 'price']
    for field in required_fields:
        if not data.get(field):
            return jsonify({'message': f'{field} 不能为空'}), 400
    
    try:
        start_time = datetime.fromisoformat(data.get('start_time').replace('Z', '+00:00'))
        end_time = datetime.fromisoformat(data.get('end_time').replace('Z', '+00:00'))
    except ValueError:
        return jsonify({'message': '时间格式错误'}), 400
    
    schedule = Schedule(
        movie_id=data.get('movie_id'),
        hall_id=data.get('hall_id'),
        start_time=start_time,
        end_time=end_time,
        price=data.get('price')
    )
    
    db.session.add(schedule)
    db.session.commit()
    
    return jsonify({
        'message': '场次创建成功',
        'schedule': schedule.to_dict()
    }), 201


@admin_bp.route('/schedules/<int:schedule_id>', methods=['PUT'])
@jwt_required()
def update_schedule(schedule_id):
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    schedule = Schedule.query.get(schedule_id)
    
    if not schedule:
        return jsonify({'message': '场次不存在'}), 404
    
    data = request.get_json()
    
    if data.get('movie_id'):
        schedule.movie_id = data.get('movie_id')
    if data.get('hall_id'):
        schedule.hall_id = data.get('hall_id')
    if data.get('start_time'):
        try:
            schedule.start_time = datetime.fromisoformat(data.get('start_time').replace('Z', '+00:00'))
        except ValueError:
            return jsonify({'message': '时间格式错误'}), 400
    if data.get('end_time'):
        try:
            schedule.end_time = datetime.fromisoformat(data.get('end_time').replace('Z', '+00:00'))
        except ValueError:
            return jsonify({'message': '时间格式错误'}), 400
    if data.get('price'):
        schedule.price = data.get('price')
    
    db.session.commit()
    
    return jsonify({
        'message': '场次更新成功',
        'schedule': schedule.to_dict()
    }), 200


@admin_bp.route('/schedules/<int:schedule_id>', methods=['DELETE'])
@jwt_required()
def delete_schedule(schedule_id):
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    schedule = Schedule.query.get(schedule_id)
    
    if not schedule:
        return jsonify({'message': '场次不存在'}), 404
    
    if schedule.orders:
        return jsonify({'message': '该场次有关联订单，无法删除'}), 400
    
    db.session.delete(schedule)
    db.session.commit()
    
    return jsonify({'message': '场次删除成功'}), 200


@admin_bp.route('/orders', methods=['GET'])
@jwt_required()
def get_admin_orders():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    status = request.args.get('status')
    order_no = request.args.get('order_no')
    
    query = Order.query
    
    if status:
        query = query.filter_by(status=status)
    
    if order_no:
        query = query.filter(Order.order_no.contains(order_no))
    
    orders = query.order_by(Order.created_at.desc()).all()
    
    return jsonify([order.to_dict() for order in orders]), 200


@admin_bp.route('/cinemas', methods=['GET'])
@jwt_required()
def get_admin_cinemas():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    cinemas = Cinema.query.all()
    
    return jsonify([cinema.to_dict() for cinema in cinemas]), 200


@admin_bp.route('/cinemas', methods=['POST'])
@jwt_required()
def create_cinema():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    data = request.get_json()
    
    if not data or not data.get('name') or not data.get('address'):
        return jsonify({'message': '影院名称和地址不能为空'}), 400
    
    cinema = Cinema(
        name=data.get('name'),
        address=data.get('address'),
        phone=data.get('phone'),
        description=data.get('description')
    )
    
    db.session.add(cinema)
    db.session.commit()
    
    return jsonify({
        'message': '影院创建成功',
        'cinema': cinema.to_dict()
    }), 201


@admin_bp.route('/halls', methods=['GET'])
@jwt_required()
def get_admin_halls():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    cinema_id = request.args.get('cinema_id', type=int)
    
    query = Hall.query
    
    if cinema_id:
        query = query.filter_by(cinema_id=cinema_id)
    
    halls = query.all()
    
    return jsonify([hall.to_dict() for hall in halls]), 200


@admin_bp.route('/halls', methods=['POST'])
@jwt_required()
def create_hall():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    data = request.get_json()
    
    required_fields = ['name', 'cinema_id', 'rows', 'cols']
    for field in required_fields:
        if not data.get(field):
            return jsonify({'message': f'{field} 不能为空'}), 400
    
    hall = Hall(
        name=data.get('name'),
        cinema_id=data.get('cinema_id'),
        rows=data.get('rows'),
        cols=data.get('cols')
    )
    
    db.session.add(hall)
    db.session.commit()
    
    for row in range(1, hall.rows + 1):
        for col in range(1, hall.cols + 1):
            seat = Seat(hall_id=hall.id, row_num=row, col_num=col)
            db.session.add(seat)
    db.session.commit()
    
    return jsonify({
        'message': '放映厅创建成功',
        'hall': hall.to_dict()
    }), 201


@admin_bp.route('/users', methods=['GET'])
@jwt_required()
def get_admin_users():
    if not is_admin():
        return jsonify({'message': '无权访问'}), 403
    
    users = User.query.all()
    
    return jsonify([user.to_dict() for user in users]), 200
