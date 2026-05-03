from datetime import datetime
from app import db

class Reservation(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    member_id = db.Column(db.Integer, db.ForeignKey('member.id'), nullable=False)
    course_id = db.Column(db.Integer, db.ForeignKey('course.id'), nullable=True)
    private_coach_id = db.Column(db.Integer, db.ForeignKey('coach.id'), nullable=True)
    reservation_date = db.Column(db.Date, nullable=False)
    reservation_time = db.Column(db.Time, nullable=False)
    reservation_type = db.Column(db.String(20), default='group')
    status = db.Column(db.String(20), default='pending')
    notes = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        from app.models.course import Course
        from app.models.coach import Coach
        from app.models.member import Member
        
        course = Course.query.get(self.course_id) if self.course_id else None
        coach = Coach.query.get(self.private_coach_id) if self.private_coach_id else None
        member = Member.query.get(self.member_id)
        
        return {
            'id': self.id,
            'member_id': self.member_id,
            'member_name': member.name if member else None,
            'course_id': self.course_id,
            'course_name': course.name if course else None,
            'private_coach_id': self.private_coach_id,
            'private_coach_name': coach.name if coach else None,
            'reservation_date': str(self.reservation_date),
            'reservation_time': str(self.reservation_time),
            'reservation_type': self.reservation_type,
            'status': self.status,
            'notes': self.notes,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
