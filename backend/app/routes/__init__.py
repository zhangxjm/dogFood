from .auth import auth_bp
from .articles import articles_bp
from .categories import categories_bp
from .tags import tags_bp
from .comments import comments_bp
from .banners import banners_bp

def register_routes(app):
    app.register_blueprint(auth_bp, url_prefix='/api/auth')
    app.register_blueprint(articles_bp, url_prefix='/api/articles')
    app.register_blueprint(categories_bp, url_prefix='/api/categories')
    app.register_blueprint(tags_bp, url_prefix='/api/tags')
    app.register_blueprint(comments_bp, url_prefix='/api/comments')
    app.register_blueprint(banners_bp, url_prefix='/api/banners')
