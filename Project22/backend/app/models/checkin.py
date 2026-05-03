from datetime import datetime
from app import db

class CheckIn(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    member_id = db.Column(db.Integer, db.ForeignKey('member.id'), nullable=False)
    checkin_time = db.Column(db.DateTime, default=datetime.utcnow)
    checkin_type = db.Column(db.String(20), default='normal')
    location = db.Column(db.String(100), nullable=True)
    notes = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        from app.models.member import Member
        member = Member.query.get(self.member_id)
        return {
            'id': self.id,
            'member_id': self.member_id,
            'member_name': member.name if member else None,
            'checkin_time': self.checkin_time.isoformat() if self.checkin_time else None,
            'checkin_type': self.checkin_type,
            'location': self.location,
            'notes': self.notes,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
