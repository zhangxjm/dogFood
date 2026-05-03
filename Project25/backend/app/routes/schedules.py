from datetime import datetime
from flask import Blueprint, request, jsonify
from app.models import db, Schedule, Order, Hall, Seat

schedules_bp = Blueprint('schedules', __name__)


@schedules_bp.route('', methods=['GET'])
def get_schedules():
    movie_id = request.args.get('movie_id', type=int)
    cinema_id = request.args.get('cinema_id', type=int)
    hall_id = request.args.get('hall_id', type=int)
    
    query = Schedule.query
    
    if movie_id:
        query = query.filter_by(movie_id=movie_id)
    
    if cinema_id:
        query = query.join(Schedule.hall).filter(Hall.cinema_id == cinema_id)
    
    if hall_id:
        query = query.filter_by(hall_id=hall_id)
    
    schedules = query.order_by(Schedule.start_time).all()
    
    return jsonify([schedule.to_dict() for schedule in schedules]), 200


@schedules_bp.route('/<int:schedule_id>', methods=['GET'])
def get_schedule(schedule_id):
    schedule = Schedule.query.get(schedule_id)
    
    if not schedule:
        return jsonify({'message': '场次不存在'}), 404
    
    return jsonify(schedule.to_dict()), 200


@schedules_bp.route('/<int:schedule_id>/seats', methods=['GET'])
def get_schedule_seats(schedule_id):
    schedule = Schedule.query.get(schedule_id)
    
    if not schedule:
        return jsonify({'message': '场次不存在'}), 404
    
    hall = schedule.hall
    if not hall:
        return jsonify({'message': '放映厅不存在'}), 404
    
    ordered_seats = set()
    orders = Order.query.filter_by(schedule_id=schedule_id, status='paid').all()
    for order in orders:
        seat_list = order.seats.split(',')
        for seat in seat_list:
            ordered_seats.add(seat.strip())
    
    seats = Seat.query.filter_by(hall_id=hall.id).order_by(Seat.row_num, Seat.col_num).all()
    
    seat_map = []
    for seat in seats:
        seat_key = f'{seat.row_num}-{seat.col_num}'
        seat_map.append({
            'id': seat.id,
            'row_num': seat.row_num,
            'col_num': seat.col_num,
            'is_available': seat.is_available and (seat_key not in ordered_seats),
            'is_ordered': seat_key in ordered_seats
        })
    
    rows = []
    for row in range(1, hall.rows + 1):
        row_seats = [s for s in seat_map if s['row_num'] == row]
        rows.append({
            'row_num': row,
            'seats': row_seats
        })
    
    return jsonify({
        'hall': hall.to_dict(),
        'rows': rows
    }), 200


@schedules_bp.route('/movie/<int:movie_id>', methods=['GET'])
def get_movie_schedules(movie_id):
    schedules = Schedule.query.filter_by(movie_id=movie_id).order_by(Schedule.start_time).all()
    
    cinema_groups = {}
    for schedule in schedules:
        cinema_id = schedule.hall.cinema_id
        if cinema_id not in cinema_groups:
            cinema_groups[cinema_id] = {
                'cinema': schedule.hall.cinema.to_dict(),
                'schedules': []
            }
        cinema_groups[cinema_id]['schedules'].append(schedule.to_dict())
    
    return jsonify(list(cinema_groups.values())), 200
