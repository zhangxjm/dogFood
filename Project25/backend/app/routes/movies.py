from flask import Blueprint, request, jsonify
from app.models import db, Movie, MovieCategory

movies_bp = Blueprint('movies', __name__)


@movies_bp.route('', methods=['GET'])
def get_movies():
    category_id = request.args.get('category_id', type=int)
    status = request.args.get('status')
    is_hot = request.args.get('is_hot', type=bool)
    search = request.args.get('search')
    
    query = Movie.query
    
    if category_id:
        query = query.filter_by(category_id=category_id)
    
    if status:
        query = query.filter_by(status=status)
    
    if is_hot is not None:
        query = query.filter_by(is_hot=is_hot)
    
    if search:
        query = query.filter(Movie.title.contains(search))
    
    movies = query.all()
    
    return jsonify([movie.to_dict() for movie in movies]), 200


@movies_bp.route('/<int:movie_id>', methods=['GET'])
def get_movie(movie_id):
    movie = Movie.query.get(movie_id)
    
    if not movie:
        return jsonify({'message': '电影不存在'}), 404
    
    return jsonify(movie.to_dict()), 200


@movies_bp.route('/categories', methods=['GET'])
def get_categories():
    categories = MovieCategory.query.all()
    
    return jsonify([category.to_dict() for category in categories]), 200


@movies_bp.route('/hot', methods=['GET'])
def get_hot_movies():
    movies = Movie.query.filter_by(is_hot=True, status='showing').all()
    
    return jsonify([movie.to_dict() for movie in movies]), 200


@movies_bp.route('/showing', methods=['GET'])
def get_showing_movies():
    movies = Movie.query.filter_by(status='showing').all()
    
    return jsonify([movie.to_dict() for movie in movies]), 200


@movies_bp.route('/coming', methods=['GET'])
def get_coming_movies():
    movies = Movie.query.filter_by(status='coming').all()
    
    return jsonify([movie.to_dict() for movie in movies]), 200
