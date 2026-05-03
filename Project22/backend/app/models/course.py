from datetime import datetime, time
from app import db

class Course(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.Text, nullable=True)
    coach_id = db.Column(db.Integer, db.ForeignKey('coach.id'), nullable=False)
    capacity = db.Column(db.Integer, default=20)
    current_count = db.Column(db.Integer, default=0)
    start_time = db.Column(db.Time, nullable=False)
    duration = db.Column(db.Integer, default=60)
    day_of_week = db.Column(db.Integer, nullable=False)
    price = db.Column(db.Float, default=0.0)
    image = db.Column(db.String(256), nullable=True)
    is_active = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    reservations = db.relationship('Reservation', backref='course', lazy='dynamic', cascade='all, delete-orphan')

    def to_dict(self):
        from app.models.coach import Coach
        coach = Coach.query.get(self.coach_id)
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'coach_id': self.coach_id,
            'coach_name': coach.name if coach else None,
            'max_capacity': self.capacity,
            'current_capacity': self.current_count,
            'start_time': str(self.start_time),
            'end_time': self.calculate_end_time(),
            'duration': self.duration,
            'day_of_week': self.day_of_week,
            'day_name': self.get_day_name(),
            'price': self.price,
            'image': self.image,
            'is_active': self.is_active,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
    
    def calculate_end_time(self):
        from datetime import datetime, timedelta
        if self.start_time:
            base_date = datetime(2000, 1, 1, self.start_time.hour, self.start_time.minute)
            end_date = base_date + timedelta(minutes=self.duration)
            return f"{end_date.hour:02d}:{end_date.minute:02d}"
        return None
    
    def get_day_name(self):
        day_names = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        return day_names[self.day_of_week - 1] if 1 <= self.day_of_week <= 7 else '未知'
