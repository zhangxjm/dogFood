from datetime import datetime
from werkzeug.security import generate_password_hash, check_password_hash
from extensions import db

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    name = db.Column(db.String(50), nullable=False)
    phone = db.Column(db.String(20), nullable=False)
    room_number = db.Column(db.String(20), nullable=False)
    building = db.Column(db.String(20), nullable=False)
    unit = db.Column(db.String(20), nullable=False)
    role = db.Column(db.String(20), default='owner')
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    bills = db.relationship('Bill', backref='owner', lazy=True)
    repairs = db.relationship('Repair', backref='owner', lazy=True)
    suggestions = db.relationship('Suggestion', backref='owner', lazy=True)
    visitor_registrations = db.relationship('VisitorRegistration', backref='owner', lazy=True)
    
    def set_password(self, password):
        self.password_hash = generate_password_hash(password)
    
    def check_password(self, password):
        return check_password_hash(self.password_hash, password)
    
    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'name': self.name,
            'phone': self.phone,
            'room_number': self.room_number,
            'building': self.building,
            'unit': self.unit,
            'role': self.role
        }

class Bill(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    bill_type = db.Column(db.String(50), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    description = db.Column(db.Text)
    period = db.Column(db.String(20), nullable=False)
    status = db.Column(db.String(20), default='unpaid')
    paid_at = db.Column(db.DateTime)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'owner_id': self.owner_id,
            'bill_type': self.bill_type,
            'amount': self.amount,
            'description': self.description,
            'period': self.period,
            'status': self.status,
            'paid_at': self.paid_at.isoformat() if self.paid_at else None,
            'created_at': self.created_at.isoformat()
        }

class Repair(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    repair_type = db.Column(db.String(50), nullable=False)
    title = db.Column(db.String(100), nullable=False)
    description = db.Column(db.Text, nullable=False)
    location = db.Column(db.String(100), nullable=False)
    images = db.Column(db.Text)
    status = db.Column(db.String(20), default='pending')
    assigned_to = db.Column(db.String(50))
    progress = db.Column(db.Text)
    rating = db.Column(db.Integer)
    comment = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    completed_at = db.Column(db.DateTime)
    
    def to_dict(self):
        return {
            'id': self.id,
            'owner_id': self.owner_id,
            'repair_type': self.repair_type,
            'title': self.title,
            'description': self.description,
            'location': self.location,
            'images': self.images.split(',') if self.images else [],
            'status': self.status,
            'assigned_to': self.assigned_to,
            'progress': self.progress,
            'rating': self.rating,
            'comment': self.comment,
            'created_at': self.created_at.isoformat(),
            'completed_at': self.completed_at.isoformat() if self.completed_at else None
        }

class Notice(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(100), nullable=False)
    content = db.Column(db.Text, nullable=False)
    notice_type = db.Column(db.String(50), default='general')
    priority = db.Column(db.String(20), default='normal')
    publish_date = db.Column(db.DateTime, default=datetime.utcnow)
    is_active = db.Column(db.Boolean, default=True)
    
    def to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'content': self.content,
            'notice_type': self.notice_type,
            'priority': self.priority,
            'publish_date': self.publish_date.isoformat(),
            'is_active': self.is_active
        }

class Suggestion(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    type = db.Column(db.String(50), nullable=False)
    title = db.Column(db.String(100), nullable=False)
    content = db.Column(db.Text, nullable=False)
    status = db.Column(db.String(20), default='pending')
    reply = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    replied_at = db.Column(db.DateTime)
    
    def to_dict(self):
        return {
            'id': self.id,
            'owner_id': self.owner_id,
            'type': self.type,
            'title': self.title,
            'content': self.content,
            'status': self.status,
            'reply': self.reply,
            'created_at': self.created_at.isoformat(),
            'replied_at': self.replied_at.isoformat() if self.replied_at else None
        }

class VisitorRegistration(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    visitor_name = db.Column(db.String(50), nullable=False)
    visitor_phone = db.Column(db.String(20), nullable=False)
    visitor_id_card = db.Column(db.String(20))
    visit_date = db.Column(db.Date, nullable=False)
    visit_time = db.Column(db.String(20), nullable=False)
    purpose = db.Column(db.String(100))
    status = db.Column(db.String(20), default='pending')
    remark = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'owner_id': self.owner_id,
            'visitor_name': self.visitor_name,
            'visitor_phone': self.visitor_phone,
            'visitor_id_card': self.visitor_id_card,
            'visit_date': self.visit_date.isoformat() if self.visit_date else None,
            'visit_time': self.visit_time,
            'purpose': self.purpose,
            'status': self.status,
            'remark': self.remark,
            'created_at': self.created_at.isoformat()
        }
