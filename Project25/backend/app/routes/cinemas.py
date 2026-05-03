from flask import Blueprint, request, jsonify
from app.models import db, Cinema, Hall

cinemas_bp = Blueprint('cinemas', __name__)


@cinemas_bp.route('', methods=['GET'])
def get_cinemas():
    cinemas = Cinema.query.all()
    
    return jsonify([cinema.to_dict() for cinema in cinemas]), 200


@cinemas_bp.route('/<int:cinema_id>', methods=['GET'])
def get_cinema(cinema_id):
    cinema = Cinema.query.get(cinema_id)
    
    if not cinema:
        return jsonify({'message': '影院不存在'}), 404
    
    return jsonify(cinema.to_dict()), 200


@cinemas_bp.route('/<int:cinema_id>/halls', methods=['GET'])
def get_cinema_halls(cinema_id):
    cinema = Cinema.query.get(cinema_id)
    
    if not cinema:
        return jsonify({'message': '影院不存在'}), 404
    
    halls = Hall.query.filter_by(cinema_id=cinema_id).all()
    
    return jsonify([hall.to_dict() for hall in halls]), 200


@cinemas_bp.route('/halls/<int:hall_id>', methods=['GET'])
def get_hall(hall_id):
    hall = Hall.query.get(hall_id)
    
    if not hall:
        return jsonify({'message': '放映厅不存在'}), 404
    
    return jsonify(hall.to_dict()), 200
