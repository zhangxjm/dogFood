from flask import Blueprint, request, jsonify
from datetime import datetime
from app import db
from app.models import Member, Transaction, CheckIn, Reservation

members_bp = Blueprint('members', __name__)

@members_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    if not data or 'phone' not in data or 'password' not in data or 'name' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    if Member.query.filter_by(phone=data['phone']).first():
        return jsonify({'success': False, 'message': '手机号已注册'}), 400
    
    member = Member(
        name=data['name'],
        phone=data['phone']
    )
    member.set_password(data['password'])
    
    member.member_card_id = f"FC{datetime.now().strftime('%Y%m%d%H%M%S')}"
    
    db.session.add(member)
    db.session.commit()
    
    return jsonify({
        'success': True, 
        'message': '注册成功',
        'data': member.to_dict()
    }), 201

@members_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    if not data or 'phone' not in data or 'password' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    member = Member.query.filter_by(phone=data['phone']).first()
    if not member or not member.check_password(data['password']):
        return jsonify({'success': False, 'message': '手机号或密码错误'}), 401
    
    if not member.is_active:
        return jsonify({'success': False, 'message': '账号已被禁用'}), 403
    
    return jsonify({
        'success': True, 
        'message': '登录成功',
        'data': member.to_dict()
    })

@members_bp.route('/<int:member_id>', methods=['GET'])
def get_member(member_id):
    member = Member.query.get_or_404(member_id)
    return jsonify({
        'success': True, 
        'data': member.to_dict()
    })

@members_bp.route('/<int:member_id>', methods=['PUT'])
def update_member(member_id):
    member = Member.query.get_or_404(member_id)
    data = request.get_json()
    
    if 'name' in data:
        member.name = data['name']
    if 'avatar' in data:
        member.avatar = data['avatar']
    if 'member_type' in data:
        member.member_type = data['member_type']
    
    db.session.commit()
    
    return jsonify({
        'success': True, 
        'message': '更新成功',
        'data': member.to_dict()
    })

@members_bp.route('/<int:member_id>/change-password', methods=['POST'])
def change_password(member_id):
    member = Member.query.get_or_404(member_id)
    data = request.get_json()
    
    if not data or 'old_password' not in data or 'new_password' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    if not member.check_password(data['old_password']):
        return jsonify({'success': False, 'message': '原密码错误'}), 400
    
    member.set_password(data['new_password'])
    db.session.commit()
    
    return jsonify({
        'success': True, 
        'message': '密码修改成功'
    })

@members_bp.route('/<int:member_id>/recharge', methods=['POST'])
def recharge(member_id):
    member = Member.query.get_or_404(member_id)
    data = request.get_json()
    
    if not data or 'amount' not in data:
        return jsonify({'success': False, 'message': '缺少必要参数'}), 400
    
    amount = float(data['amount'])
    if amount <= 0:
        return jsonify({'success': False, 'message': '充值金额必须大于0'}), 400
    
    balance_before = member.balance
    member.balance += amount
    
    transaction = Transaction(
        member_id=member.id,
        transaction_type='recharge',
        amount=amount,
        balance_before=balance_before,
        balance_after=member.balance,
        description=f'账户充值 {amount} 元',
        payment_method=data.get('payment_method', 'wechat')
    )
    
    db.session.add(transaction)
    db.session.commit()
    
    return jsonify({
        'success': True, 
        'message': '充值成功',
        'data': {
            'balance': member.balance,
            'transaction': transaction.to_dict()
        }
    })

@members_bp.route('/<int:member_id>/balance', methods=['GET'])
def get_balance(member_id):
    member = Member.query.get_or_404(member_id)
    return jsonify({
        'success': True, 
        'data': {
            'balance': member.balance,
            'member_type': member.member_type,
            'member_card_id': member.member_card_id
        }
    })

@members_bp.route('/<int:member_id>/stats', methods=['GET'])
def get_member_stats(member_id):
    member = Member.query.get_or_404(member_id)
    
    total_checkins = CheckIn.query.filter_by(member_id=member.id).count()
    total_reservations = Reservation.query.filter_by(member_id=member.id).count()
    total_transactions = Transaction.query.filter_by(member_id=member.id).count()
    
    return jsonify({
        'success': True,
        'data': {
            'total_checkins': total_checkins,
            'total_reservations': total_reservations,
            'total_transactions': total_transactions,
            'balance': member.balance,
            'member_type': member.member_type
        }
    })
