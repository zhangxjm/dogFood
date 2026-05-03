from datetime import time

class TestConfig:
    SECRET_KEY = 'test-secret-key'
    SQLALCHEMY_DATABASE_URI = 'sqlite:///:memory:'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    CORS_ORIGINS = '*'
    JWT_SECRET_KEY = 'test-jwt-secret-key'
    TESTING = True
