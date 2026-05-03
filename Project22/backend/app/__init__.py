from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from config import Config

db = SQLAlchemy()

def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)
    
    db.init_app(app)
    CORS(app, resources={r"/api/*": {"origins": app.config['CORS_ORIGINS']}})
    
    from app.routes.members import members_bp
    from app.routes.courses import courses_bp
    from app.routes.coaches import coaches_bp
    from app.routes.reservations import reservations_bp
    from app.routes.checkins import checkins_bp
    from app.routes.transactions import transactions_bp
    from app.routes.admin import admin_bp
    
    app.register_blueprint(members_bp, url_prefix='/api/members')
    app.register_blueprint(courses_bp, url_prefix='/api/courses')
    app.register_blueprint(coaches_bp, url_prefix='/api/coaches')
    app.register_blueprint(reservations_bp, url_prefix='/api/reservations')
    app.register_blueprint(checkins_bp, url_prefix='/api/checkins')
    app.register_blueprint(transactions_bp, url_prefix='/api/transactions')
    app.register_blueprint(admin_bp, url_prefix='/api/admin')
    
    @app.route('/api/health')
    def health_check():
        return {'status': 'ok', 'message': 'Fitness Club API is running'}
    
    return app
