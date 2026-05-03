from datetime import datetime
from app import db

class Transaction(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    member_id = db.Column(db.Integer, db.ForeignKey('member.id'), nullable=False)
    transaction_type = db.Column(db.String(20), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    balance_before = db.Column(db.Float, default=0.0)
    balance_after = db.Column(db.Float, default=0.0)
    description = db.Column(db.String(255), nullable=True)
    payment_method = db.Column(db.String(20), default='balance')
    status = db.Column(db.String(20), default='completed')
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        from app.models.member import Member
        member = Member.query.get(self.member_id)
        return {
            'id': self.id,
            'member_id': self.member_id,
            'member_name': member.name if member else None,
            'transaction_type': self.transaction_type,
            'amount': self.amount,
            'balance_before': self.balance_before,
            'balance_after': self.balance_after,
            'description': self.description,
            'payment_method': self.payment_method,
            'status': self.status,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }
