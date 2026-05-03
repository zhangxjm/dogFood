from datetime import datetime
from werkzeug.security import generate_password_hash, check_password_hash
from app import db

class Member(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    phone = db.Column(db.String(20), unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    member_card_id = db.Column(db.String(20), unique=True, nullable=True)
    balance = db.Column(db.Float, default=0.0)
    member_type = db.Column(db.String(20), default='普通会员')
    avatar = db.Column(db.String(256), nullable=True)
    join_date = db.Column(db.DateTime, default=datetime.utcnow)
    is_active = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    reservations = db.relationship('Reservation', backref='member', lazy='dynamic', cascade='all, delete-orphan')
    checkins = db.relationship('CheckIn', backref='member', lazy='dynamic', cascade='all, delete-orphan')
    transactions = db.relationship('Transaction', backref='member', lazy='dynamic', cascade='all, delete-orphan')

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'phone': self.phone,
            'member_card_id': self.member_card_id,
            'balance': self.balance,
            'member_type': self.member_type,
            'avatar': self.avatar,
            'join_date': self.join_date.isoformat() if self.join_date else None,
            'is_active': self.is_active,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
