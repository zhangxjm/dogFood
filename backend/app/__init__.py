from flask import Flask
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from config import config
from app.models import db
from app.routes import register_routes

jwt = JWTManager()

def create_app(config_name='default'):
    app = Flask(__name__)
    app.config.from_object(config[config_name])
    config[config_name].init_app(app)
    
    app.url_map.strict_slashes = False
    
    CORS(app, supports_credentials=True)
    db.init_app(app)
    jwt.init_app(app)
    
    register_routes(app)
    
    @app.route('/')
    def index():
        return {'message': 'Blog CMS API is running'}
    
    @app.route('/api/health')
    def health():
        return {'status': 'healthy'}
    
    return app
