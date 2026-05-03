from flask import Flask, jsonify
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from app.config import Config
from app.models import db
from app.routes.auth import auth_bp
from app.routes.movies import movies_bp
from app.routes.cinemas import cinemas_bp
from app.routes.schedules import schedules_bp
from app.routes.orders import orders_bp
from app.routes.admin import admin_bp


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    
    CORS(app, origins=Config.CORS_ORIGINS, supports_credentials=True)
    
    JWTManager(app)
    
    db.init_app(app)
    
    app.register_blueprint(auth_bp, url_prefix='/api/auth')
    app.register_blueprint(movies_bp, url_prefix='/api/movies')
    app.register_blueprint(cinemas_bp, url_prefix='/api/cinemas')
    app.register_blueprint(schedules_bp, url_prefix='/api/schedules')
    app.register_blueprint(orders_bp, url_prefix='/api/orders')
    app.register_blueprint(admin_bp, url_prefix='/api/admin')
    
    @app.route('/')
    def index():
        return jsonify({
            'message': 'Cinema Ticket System API',
            'status': 'running'
        })
    
    @app.route('/api/health')
    def health_check():
        return jsonify({'status': 'healthy'})
    
    return app


def init_database(app):
    with app.app_context():
        db.create_all()
        from app.init_data import init_data
        init_data()
