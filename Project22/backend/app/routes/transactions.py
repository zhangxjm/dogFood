from flask import Blueprint, request, jsonify
from datetime import datetime, timedelta
from app import db
from app.models import Transaction, Member

transactions_bp = Blueprint('transactions', __name__)

@transactions_bp.route('/', methods=['GET'])
def get_transactions():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    member_id = request.args.get('member_id', None, type=int)
    transaction_type = request.args.get('type', None)
    start_date = request.args.get('start_date', None)
    end_date = request.args.get('end_date', None)
    
    query = Transaction.query
    
    if member_id:
        query = query.filter_by(member_id=member_id)
    if transaction_type:
        query = query.filter_by(transaction_type=transaction_type)
    if start_date:
        start = datetime.strptime(start_date, '%Y-%m-%d')
        query = query.filter(Transaction.created_at >= start)
    if end_date:
        end = datetime.strptime(end_date, '%Y-%m-%d') + timedelta(days=1)
        query = query.filter(Transaction.created_at < end)
    
    pagination = query.order_by(Transaction.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'transactions': [transaction.to_dict() for transaction in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@transactions_bp.route('/<int:transaction_id>', methods=['GET'])
def get_transaction(transaction_id):
    transaction = Transaction.query.get_or_404(transaction_id)
    return jsonify({
        'success': True,
        'data': transaction.to_dict()
    })

@transactions_bp.route('/my/<int:member_id>', methods=['GET'])
def get_my_transactions(member_id):
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    transaction_type = request.args.get('type', None)
    
    member = Member.query.get_or_404(member_id)
    
    query = Transaction.query.filter_by(member_id=member.id)
    
    if transaction_type:
        query = query.filter_by(transaction_type=transaction_type)
    
    pagination = query.order_by(Transaction.created_at.desc()).paginate(
        page=page, per_page=per_page, error_out=False
    )
    
    return jsonify({
        'success': True,
        'data': {
            'transactions': [transaction.to_dict() for transaction in pagination.items],
            'total': pagination.total,
            'page': page,
            'per_page': per_page,
            'pages': pagination.pages
        }
    })

@transactions_bp.route('/my/<int:member_id>/stats', methods=['GET'])
def get_transaction_stats(member_id):
    member = Member.query.get_or_404(member_id)
    
    total_recharge = db.session.query(
        db.func.sum(Transaction.amount)
    ).filter(
        Transaction.member_id == member.id,
        Transaction.transaction_type == 'recharge',
        Transaction.status == 'completed'
    ).scalar() or 0
    
    total_consumption = db.session.query(
        db.func.sum(Transaction.amount)
    ).filter(
        Transaction.member_id == member.id,
        Transaction.transaction_type == 'consumption',
        Transaction.status == 'completed'
    ).scalar() or 0
    
    total_transactions = Transaction.query.filter_by(member_id=member.id).count()
    
    return jsonify({
        'success': True,
        'data': {
            'total_recharge': total_recharge,
            'total_consumption': total_consumption,
            'total_transactions': total_transactions,
            'current_balance': member.balance
        }
    })
