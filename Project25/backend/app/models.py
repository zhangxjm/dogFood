from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy()


class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=True)
    is_admin = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    orders = db.relationship('Order', backref='user', lazy=True)
    
    def set_password(self, password):
        self.password_hash = generate_password_hash(password)
    
    def check_password(self, password):
        return check_password_hash(self.password_hash, password)
    
    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'is_admin': self.is_admin,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }


class MovieCategory(db.Model):
    __tablename__ = 'movie_categories'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), unique=True, nullable=False)
    description = db.Column(db.Text, nullable=True)
    
    movies = db.relationship('Movie', backref='category', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description
        }


class Movie(db.Model):
    __tablename__ = 'movies'
    
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(100), nullable=False)
    poster = db.Column(db.String(500), nullable=True)
    description = db.Column(db.Text, nullable=True)
    duration = db.Column(db.Integer, nullable=False)
    release_date = db.Column(db.Date, nullable=True)
    rating = db.Column(db.Float, default=0.0)
    director = db.Column(db.String(100), nullable=True)
    actors = db.Column(db.Text, nullable=True)
    category_id = db.Column(db.Integer, db.ForeignKey('movie_categories.id'), nullable=True)
    status = db.Column(db.String(20), default='showing')
    is_hot = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    schedules = db.relationship('Schedule', backref='movie', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'poster': self.poster,
            'description': self.description,
            'duration': self.duration,
            'release_date': self.release_date.isoformat() if self.release_date else None,
            'rating': self.rating,
            'director': self.director,
            'actors': self.actors,
            'category_id': self.category_id,
            'category_name': self.category.name if self.category else None,
            'status': self.status,
            'is_hot': self.is_hot,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }


class Cinema(db.Model):
    __tablename__ = 'cinemas'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    address = db.Column(db.String(200), nullable=False)
    phone = db.Column(db.String(20), nullable=True)
    description = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    halls = db.relationship('Hall', backref='cinema', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'address': self.address,
            'phone': self.phone,
            'description': self.description,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }


class Hall(db.Model):
    __tablename__ = 'halls'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    cinema_id = db.Column(db.Integer, db.ForeignKey('cinemas.id'), nullable=False)
    rows = db.Column(db.Integer, nullable=False)
    cols = db.Column(db.Integer, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    schedules = db.relationship('Schedule', backref='hall', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'cinema_id': self.cinema_id,
            'cinema_name': self.cinema.name if self.cinema else None,
            'rows': self.rows,
            'cols': self.cols,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }


class Schedule(db.Model):
    __tablename__ = 'schedules'
    
    id = db.Column(db.Integer, primary_key=True)
    movie_id = db.Column(db.Integer, db.ForeignKey('movies.id'), nullable=False)
    hall_id = db.Column(db.Integer, db.ForeignKey('halls.id'), nullable=False)
    start_time = db.Column(db.DateTime, nullable=False)
    end_time = db.Column(db.DateTime, nullable=False)
    price = db.Column(db.Float, nullable=False, default=50.0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    orders = db.relationship('Order', backref='schedule', lazy=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'movie_id': self.movie_id,
            'movie_title': self.movie.title if self.movie else None,
            'hall_id': self.hall_id,
            'hall_name': self.hall.name if self.hall else None,
            'cinema_id': self.hall.cinema_id if self.hall else None,
            'cinema_name': self.hall.cinema.name if self.hall and self.hall.cinema else None,
            'start_time': self.start_time.isoformat() if self.start_time else None,
            'end_time': self.end_time.isoformat() if self.end_time else None,
            'price': self.price,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }


class Seat(db.Model):
    __tablename__ = 'seats'
    
    id = db.Column(db.Integer, primary_key=True)
    hall_id = db.Column(db.Integer, db.ForeignKey('halls.id'), nullable=False)
    row_num = db.Column(db.Integer, nullable=False)
    col_num = db.Column(db.Integer, nullable=False)
    is_available = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'hall_id': self.hall_id,
            'row_num': self.row_num,
            'col_num': self.col_num,
            'is_available': self.is_available
        }


class Order(db.Model):
    __tablename__ = 'orders'
    
    id = db.Column(db.Integer, primary_key=True)
    order_no = db.Column(db.String(50), unique=True, nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    schedule_id = db.Column(db.Integer, db.ForeignKey('schedules.id'), nullable=False)
    total_price = db.Column(db.Float, nullable=False)
    status = db.Column(db.String(20), default='paid')
    seats = db.Column(db.Text, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'order_no': self.order_no,
            'user_id': self.user_id,
            'schedule_id': self.schedule_id,
            'movie_title': self.schedule.movie.title if self.schedule and self.schedule.movie else None,
            'cinema_name': self.schedule.hall.cinema.name if self.schedule and self.schedule.hall and self.schedule.hall.cinema else None,
            'hall_name': self.schedule.hall.name if self.schedule and self.schedule.hall else None,
            'start_time': self.schedule.start_time.isoformat() if self.schedule else None,
            'total_price': self.total_price,
            'status': self.status,
            'seats': self.seats,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
