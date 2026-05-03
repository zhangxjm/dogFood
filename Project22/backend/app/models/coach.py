from datetime import datetime
from app import db

class Coach(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    phone = db.Column(db.String(20), unique=True, nullable=False)
    specialty = db.Column(db.String(100), nullable=True)
    experience = db.Column(db.Integer, default=0)
    avatar = db.Column(db.String(256), nullable=True)
    description = db.Column(db.Text, nullable=True)
    is_active = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    courses = db.relationship('Course', backref='coach', lazy='dynamic', cascade='all, delete-orphan')
    private_reservations = db.relationship('Reservation', backref='private_coach', lazy='dynamic', foreign_keys='Reservation.private_coach_id')

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'phone': self.phone,
            'specialty': self.specialty,
            'experience': self.experience,
            'avatar': self.avatar,
            'description': self.description,
            'is_active': self.is_active,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
