import time
from datetime import datetime
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models import db, Order, Schedule, User

orders_bp = Blueprint('orders', __name__)


def generate_order_no():
    timestamp = str(int(time.time()))
    import random
    random_str = ''.join([str(random.randint(0, 9)) for _ in range(4)])
    return f'ORD{timestamp}{random_str}'


@orders_bp.route('', methods=['GET'])
@jwt_required()
def get_orders():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    query = Order.query
    if not user.is_admin:
        query = query.filter_by(user_id=user_id)
    
    orders = query.order_by(Order.created_at.desc()).all()
    
    return jsonify([order.to_dict() for order in orders]), 200


@orders_bp.route('/<int:order_id>', methods=['GET'])
@jwt_required()
def get_order(order_id):
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    order = Order.query.get(order_id)
    
    if not order:
        return jsonify({'message': '订单不存在'}), 404
    
    if not user.is_admin and order.user_id != user_id:
        return jsonify({'message': '无权访问此订单'}), 403
    
    return jsonify(order.to_dict()), 200


@orders_bp.route('/create', methods=['POST'])
@jwt_required()
def create_order():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    data = request.get_json()
    
    schedule_id = data.get('schedule_id')
    seats = data.get('seats')
    
    if not schedule_id or not seats:
        return jsonify({'message': '场次和座位不能为空'}), 400
    
    schedule = Schedule.query.get(schedule_id)
    if not schedule:
        return jsonify({'message': '场次不存在'}), 404
    
    seat_list = [s.strip() for s in seats.split(',') if s.strip()]
    if not seat_list:
        return jsonify({'message': '座位不能为空'}), 400
    
    existing_orders = Order.query.filter_by(schedule_id=schedule_id, status='paid').all()
    for existing_order in existing_orders:
        existing_seats = set([s.strip() for s in existing_order.seats.split(',')])
        for seat in seat_list:
            if seat in existing_seats:
                return jsonify({'message': f'座位 {seat} 已被售出'}), 400
    
    total_price = schedule.price * len(seat_list)
    order_no = generate_order_no()
    
    order = Order(
        order_no=order_no,
        user_id=user_id,
        schedule_id=schedule_id,
        total_price=total_price,
        status='paid',
        seats=','.join(seat_list)
    )
    
    db.session.add(order)
    db.session.commit()
    
    return jsonify({
        'message': '订单创建成功',
        'order': order.to_dict()
    }), 201


@orders_bp.route('/<int:order_id>/cancel', methods=['POST'])
@jwt_required()
def cancel_order(order_id):
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    order = Order.query.get(order_id)
    
    if not order:
        return jsonify({'message': '订单不存在'}), 404
    
    if not user.is_admin and order.user_id != user_id:
        return jsonify({'message': '无权取消此订单'}), 403
    
    if order.status == 'cancelled':
        return jsonify({'message': '订单已取消'}), 400
    
    order.status = 'cancelled'
    db.session.commit()
    
    return jsonify({
        'message': '订单已取消',
        'order': order.to_dict()
    }), 200
