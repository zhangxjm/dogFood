from flask import Flask, request, jsonify
from flask_jwt_extended import JWTManager, create_access_token, create_refresh_token, jwt_required, get_jwt_identity
from flask_cors import CORS
from datetime import datetime, timedelta
from functools import wraps
import os
from extensions import db
from models import User, Bill, Repair, Notice, Suggestion, VisitorRegistration

app = Flask(__name__)
app.config.from_object('config.Config')

db.init_app(app)
jwt = JWTManager(app)
CORS(app)

def admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        current_user_id = get_jwt_identity()
        user = User.query.get(current_user_id)
        if not user or user.role != 'admin':
            return jsonify({'message': '需要管理员权限'}), 403
        return fn(*args, **kwargs)
    return wrapper

@app.route('/api/auth/register', methods=['POST'])
def register():
    data = request.get_json()
    
    if User.query.filter_by(username=data.get('username')).first():
        return jsonify({'message': '用户名已存在'}), 400
    
    user = User(
        username=data.get('username'),
        name=data.get('name'),
        phone=data.get('phone'),
        room_number=data.get('room_number'),
        building=data.get('building'),
        unit=data.get('unit')
    )
    user.set_password(data.get('password'))
    
    db.session.add(user)
    db.session.commit()
    
    return jsonify({
        'message': '注册成功',
        'user': user.to_dict()
    }), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(username=data.get('username')).first()
    
    if not user or not user.check_password(data.get('password')):
        return jsonify({'message': '用户名或密码错误'}), 401
    
    access_token = create_access_token(identity=user.id)
    refresh_token = create_refresh_token(identity=user.id)
    
    return jsonify({
        'access_token': access_token,
        'refresh_token': refresh_token,
        'user': user.to_dict()
    })

@app.route('/api/auth/refresh', methods=['POST'])
@jwt_required(refresh=True)
def refresh():
    current_user_id = get_jwt_identity()
    access_token = create_access_token(identity=current_user_id)
    return jsonify({'access_token': access_token})

@app.route('/api/user/profile', methods=['GET'])
@jwt_required()
def get_profile():
    current_user_id = get_jwt_identity()
    user = User.query.get(current_user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    return jsonify(user.to_dict())

@app.route('/api/user/profile', methods=['PUT'])
@jwt_required()
def update_profile():
    current_user_id = get_jwt_identity()
    user = User.query.get(current_user_id)
    
    if not user:
        return jsonify({'message': '用户不存在'}), 404
    
    data = request.get_json()
    
    if data.get('name'):
        user.name = data.get('name')
    if data.get('phone'):
        user.phone = data.get('phone')
    if data.get('password'):
        user.set_password(data.get('password'))
    
    db.session.commit()
    return jsonify({
        'message': '更新成功',
        'user': user.to_dict()
    })

@app.route('/api/bills', methods=['GET'])
@jwt_required()
def get_bills():
    current_user_id = get_jwt_identity()
    user = User.query.get(current_user_id)
    
    status = request.args.get('status')
    
    query = Bill.query.filter_by(owner_id=current_user_id)
    
    if status:
        query = query.filter_by(status=status)
    
    bills = query.order_by(Bill.created_at.desc()).all()
    
    return jsonify([bill.to_dict() for bill in bills])

@app.route('/api/bills/<int:bill_id>', methods=['GET'])
@jwt_required()
def get_bill(bill_id):
    current_user_id = get_jwt_identity()
    bill = Bill.query.get(bill_id)
    
    if not bill or bill.owner_id != current_user_id:
        return jsonify({'message': '账单不存在'}), 404
    
    return jsonify(bill.to_dict())

@app.route('/api/bills/<int:bill_id>/pay', methods=['POST'])
@jwt_required()
def pay_bill(bill_id):
    current_user_id = get_jwt_identity()
    bill = Bill.query.get(bill_id)
    
    if not bill or bill.owner_id != current_user_id:
        return jsonify({'message': '账单不存在'}), 404
    
    if bill.status == 'paid':
        return jsonify({'message': '账单已支付'}), 400
    
    bill.status = 'paid'
    bill.paid_at = datetime.utcnow()
    
    db.session.commit()
    
    return jsonify({
        'message': '支付成功',
        'bill': bill.to_dict()
    })

@app.route('/api/repairs', methods=['GET'])
@jwt_required()
def get_repairs():
    current_user_id = get_jwt_identity()
    status = request.args.get('status')
    
    query = Repair.query.filter_by(owner_id=current_user_id)
    
    if status:
        query = query.filter_by(status=status)
    
    repairs = query.order_by(Repair.created_at.desc()).all()
    
    return jsonify([repair.to_dict() for repair in repairs])

@app.route('/api/repairs', methods=['POST'])
@jwt_required()
def create_repair():
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    repair = Repair(
        owner_id=current_user_id,
        repair_type=data.get('repair_type'),
        title=data.get('title'),
        description=data.get('description'),
        location=data.get('location'),
        images=','.join(data.get('images', [])) if data.get('images') else None
    )
    
    db.session.add(repair)
    db.session.commit()
    
    return jsonify({
        'message': '报修申请已提交',
        'repair': repair.to_dict()
    }), 201

@app.route('/api/repairs/<int:repair_id>', methods=['GET'])
@jwt_required()
def get_repair(repair_id):
    current_user_id = get_jwt_identity()
    repair = Repair.query.get(repair_id)
    
    if not repair or repair.owner_id != current_user_id:
        return jsonify({'message': '报修记录不存在'}), 404
    
    return jsonify(repair.to_dict())

@app.route('/api/repairs/<int:repair_id>/rate', methods=['POST'])
@jwt_required()
def rate_repair(repair_id):
    current_user_id = get_jwt_identity()
    repair = Repair.query.get(repair_id)
    
    if not repair or repair.owner_id != current_user_id:
        return jsonify({'message': '报修记录不存在'}), 404
    
    if repair.status != 'completed':
        return jsonify({'message': '只能对已完成的维修进行评价'}), 400
    
    data = request.get_json()
    repair.rating = data.get('rating')
    repair.comment = data.get('comment')
    
    db.session.commit()
    
    return jsonify({
        'message': '评价成功',
        'repair': repair.to_dict()
    })

@app.route('/api/notices', methods=['GET'])
@jwt_required()
def get_notices():
    notice_type = request.args.get('type')
    
    query = Notice.query.filter_by(is_active=True)
    
    if notice_type:
        query = query.filter_by(notice_type=notice_type)
    
    notices = query.order_by(Notice.publish_date.desc()).all()
    
    return jsonify([notice.to_dict() for notice in notices])

@app.route('/api/notices/<int:notice_id>', methods=['GET'])
@jwt_required()
def get_notice(notice_id):
    notice = Notice.query.get(notice_id)
    
    if not notice:
        return jsonify({'message': '公告不存在'}), 404
    
    return jsonify(notice.to_dict())

@app.route('/api/suggestions', methods=['GET'])
@jwt_required()
def get_suggestions():
    current_user_id = get_jwt_identity()
    status = request.args.get('status')
    
    query = Suggestion.query.filter_by(owner_id=current_user_id)
    
    if status:
        query = query.filter_by(status=status)
    
    suggestions = query.order_by(Suggestion.created_at.desc()).all()
    
    return jsonify([suggestion.to_dict() for suggestion in suggestions])

@app.route('/api/suggestions', methods=['POST'])
@jwt_required()
def create_suggestion():
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    suggestion = Suggestion(
        owner_id=current_user_id,
        type=data.get('type'),
        title=data.get('title'),
        content=data.get('content')
    )
    
    db.session.add(suggestion)
    db.session.commit()
    
    return jsonify({
        'message': '提交成功',
        'suggestion': suggestion.to_dict()
    }), 201

@app.route('/api/suggestions/<int:suggestion_id>', methods=['GET'])
@jwt_required()
def get_suggestion(suggestion_id):
    current_user_id = get_jwt_identity()
    suggestion = Suggestion.query.get(suggestion_id)
    
    if not suggestion or suggestion.owner_id != current_user_id:
        return jsonify({'message': '记录不存在'}), 404
    
    return jsonify(suggestion.to_dict())

@app.route('/api/visitors', methods=['GET'])
@jwt_required()
def get_visitors():
    current_user_id = get_jwt_identity()
    status = request.args.get('status')
    
    query = VisitorRegistration.query.filter_by(owner_id=current_user_id)
    
    if status:
        query = query.filter_by(status=status)
    
    visitors = query.order_by(VisitorRegistration.created_at.desc()).all()
    
    return jsonify([visitor.to_dict() for visitor in visitors])

@app.route('/api/visitors', methods=['POST'])
@jwt_required()
def create_visitor():
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    try:
        visit_date = datetime.strptime(data.get('visit_date'), '%Y-%m-%d').date()
    except:
        visit_date = datetime.now().date()
    
    visitor = VisitorRegistration(
        owner_id=current_user_id,
        visitor_name=data.get('visitor_name'),
        visitor_phone=data.get('visitor_phone'),
        visitor_id_card=data.get('visitor_id_card'),
        visit_date=visit_date,
        visit_time=data.get('visit_time'),
        purpose=data.get('purpose')
    )
    
    db.session.add(visitor)
    db.session.commit()
    
    return jsonify({
        'message': '登记成功',
        'visitor': visitor.to_dict()
    }), 201

@app.route('/api/visitors/<int:visitor_id>', methods=['GET'])
@jwt_required()
def get_visitor(visitor_id):
    current_user_id = get_jwt_identity()
    visitor = VisitorRegistration.query.get(visitor_id)
    
    if not visitor or visitor.owner_id != current_user_id:
        return jsonify({'message': '记录不存在'}), 404
    
    return jsonify(visitor.to_dict())

@app.route('/api/admin/users', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_users():
    users = User.query.all()
    return jsonify([user.to_dict() for user in users])

@app.route('/api/admin/bills', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_bills():
    status = request.args.get('status')
    
    query = Bill.query
    
    if status:
        query = query.filter_by(status=status)
    
    bills = query.order_by(Bill.created_at.desc()).all()
    
    result = []
    for bill in bills:
        owner = User.query.get(bill.owner_id)
        bill_dict = bill.to_dict()
        bill_dict['owner_name'] = owner.name if owner else None
        bill_dict['room_number'] = owner.room_number if owner else None
        result.append(bill_dict)
    
    return jsonify(result)

@app.route('/api/admin/bills', methods=['POST'])
@jwt_required()
@admin_required
def admin_create_bill():
    data = request.get_json()
    
    bill = Bill(
        owner_id=data.get('owner_id'),
        bill_type=data.get('bill_type'),
        amount=data.get('amount'),
        description=data.get('description'),
        period=data.get('period')
    )
    
    db.session.add(bill)
    db.session.commit()
    
    return jsonify({
        'message': '账单创建成功',
        'bill': bill.to_dict()
    }), 201

@app.route('/api/admin/repairs', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_repairs():
    status = request.args.get('status')
    
    query = Repair.query
    
    if status:
        query = query.filter_by(status=status)
    
    repairs = query.order_by(Repair.created_at.desc()).all()
    
    result = []
    for repair in repairs:
        owner = User.query.get(repair.owner_id)
        repair_dict = repair.to_dict()
        repair_dict['owner_name'] = owner.name if owner else None
        repair_dict['room_number'] = owner.room_number if owner else None
        result.append(repair_dict)
    
    return jsonify(result)

@app.route('/api/admin/repairs/<int:repair_id>/status', methods=['PUT'])
@jwt_required()
@admin_required
def admin_update_repair_status(repair_id):
    repair = Repair.query.get(repair_id)
    
    if not repair:
        return jsonify({'message': '报修记录不存在'}), 404
    
    data = request.get_json()
    repair.status = data.get('status')
    
    if data.get('progress'):
        repair.progress = data.get('progress')
    
    if data.get('assigned_to'):
        repair.assigned_to = data.get('assigned_to')
    
    if data.get('status') == 'completed':
        repair.completed_at = datetime.utcnow()
    
    db.session.commit()
    
    return jsonify({
        'message': '状态更新成功',
        'repair': repair.to_dict()
    })

@app.route('/api/admin/notices', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_notices():
    notices = Notice.query.order_by(Notice.publish_date.desc()).all()
    return jsonify([notice.to_dict() for notice in notices])

@app.route('/api/admin/notices', methods=['POST'])
@jwt_required()
@admin_required
def admin_create_notice():
    data = request.get_json()
    
    notice = Notice(
        title=data.get('title'),
        content=data.get('content'),
        notice_type=data.get('notice_type', 'general'),
        priority=data.get('priority', 'normal')
    )
    
    db.session.add(notice)
    db.session.commit()
    
    return jsonify({
        'message': '公告发布成功',
        'notice': notice.to_dict()
    }), 201

@app.route('/api/admin/notices/<int:notice_id>', methods=['PUT'])
@jwt_required()
@admin_required
def admin_update_notice(notice_id):
    notice = Notice.query.get(notice_id)
    
    if not notice:
        return jsonify({'message': '公告不存在'}), 404
    
    data = request.get_json()
    
    if data.get('title'):
        notice.title = data.get('title')
    if data.get('content'):
        notice.content = data.get('content')
    if data.get('notice_type'):
        notice.notice_type = data.get('notice_type')
    if data.get('priority'):
        notice.priority = data.get('priority')
    if 'is_active' in data:
        notice.is_active = data.get('is_active')
    
    db.session.commit()
    
    return jsonify({
        'message': '公告更新成功',
        'notice': notice.to_dict()
    })

@app.route('/api/admin/suggestions', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_suggestions():
    status = request.args.get('status')
    
    query = Suggestion.query
    
    if status:
        query = query.filter_by(status=status)
    
    suggestions = query.order_by(Suggestion.created_at.desc()).all()
    
    result = []
    for suggestion in suggestions:
        owner = User.query.get(suggestion.owner_id)
        suggestion_dict = suggestion.to_dict()
        suggestion_dict['owner_name'] = owner.name if owner else None
        suggestion_dict['room_number'] = owner.room_number if owner else None
        result.append(suggestion_dict)
    
    return jsonify(result)

@app.route('/api/admin/suggestions/<int:suggestion_id>/reply', methods=['POST'])
@jwt_required()
@admin_required
def admin_reply_suggestion(suggestion_id):
    suggestion = Suggestion.query.get(suggestion_id)
    
    if not suggestion:
        return jsonify({'message': '记录不存在'}), 404
    
    data = request.get_json()
    suggestion.reply = data.get('reply')
    suggestion.status = 'replied'
    suggestion.replied_at = datetime.utcnow()
    
    db.session.commit()
    
    return jsonify({
        'message': '回复成功',
        'suggestion': suggestion.to_dict()
    })

@app.route('/api/admin/visitors', methods=['GET'])
@jwt_required()
@admin_required
def admin_get_visitors():
    status = request.args.get('status')
    
    query = VisitorRegistration.query
    
    if status:
        query = query.filter_by(status=status)
    
    visitors = query.order_by(VisitorRegistration.created_at.desc()).all()
    
    result = []
    for visitor in visitors:
        owner = User.query.get(visitor.owner_id)
        visitor_dict = visitor.to_dict()
        visitor_dict['owner_name'] = owner.name if owner else None
        visitor_dict['room_number'] = owner.room_number if owner else None
        result.append(visitor_dict)
    
    return jsonify(result)

@app.route('/api/dashboard/stats', methods=['GET'])
@jwt_required()
@admin_required
def get_dashboard_stats():
    today = datetime.now().date()
    
    total_users = User.query.count()
    total_bills = Bill.query.count()
    paid_bills = Bill.query.filter_by(status='paid').count()
    unpaid_bills = Bill.query.filter_by(status='unpaid').count()
    total_repairs = Repair.query.count()
    pending_repairs = Repair.query.filter_by(status='pending').count()
    active_notices = Notice.query.filter_by(is_active=True).count()
    pending_suggestions = Suggestion.query.filter_by(status='pending').count()
    
    return jsonify({
        'total_users': total_users,
        'total_bills': total_bills,
        'paid_bills': paid_bills,
        'unpaid_bills': unpaid_bills,
        'total_repairs': total_repairs,
        'pending_repairs': pending_repairs,
        'active_notices': active_notices,
        'pending_suggestions': pending_suggestions
    })

@app.errorhandler(404)
def not_found(error):
    return jsonify({'message': '接口不存在'}), 404

@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return jsonify({'message': '服务器内部错误'}), 500

def init_db():
    with app.app_context():
        db.create_all()
        
        if not User.query.filter_by(username='admin').first():
            admin = User(
                username='admin',
                name='系统管理员',
                phone='13800138000',
                room_number='000',
                building='0',
                unit='0',
                role='admin'
            )
            admin.set_password('admin123')
            db.session.add(admin)
        
        if not User.query.filter_by(username='owner1').first():
            owner1 = User(
                username='owner1',
                name='张三',
                phone='13800138001',
                room_number='101',
                building='1',
                unit='1',
                role='owner'
            )
            owner1.set_password('123456')
            db.session.add(owner1)
            
            bill1 = Bill(
                owner_id=2,
                bill_type='物业费',
                amount=150.00,
                description='2024年1月物业管理费',
                period='2024-01'
            )
            db.session.add(bill1)
            
            bill2 = Bill(
                owner_id=2,
                bill_type='水电费',
                amount=85.50,
                description='2024年1月水电费',
                period='2024-01'
            )
            db.session.add(bill2)
        
        if not Notice.query.first():
            notice1 = Notice(
                title='春节放假通知',
                content='各位业主：春节期间（2月9日-2月17日）物业服务中心正常值班，如需帮助请拨打24小时服务热线。祝大家春节快乐！',
                notice_type='important',
                priority='high'
            )
            db.session.add(notice1)
            
            notice2 = Notice(
                title='小区电梯例行维护通知',
                content='为保障电梯安全运行，物业服务中心将于本月15日对小区所有电梯进行例行维护，维护期间电梯将暂停使用，请各位业主提前做好安排。',
                notice_type='maintenance',
                priority='normal'
            )
            db.session.add(notice2)
        
        db.session.commit()
        print('数据库初始化完成！')
        print('管理员账号: admin / admin123')
        print('业主测试账号: owner1 / 123456')

if __name__ == '__main__':
    init_db()
    
    upload_dir = app.config.get('UPLOAD_FOLDER')
    if not os.path.exists(upload_dir):
        os.makedirs(upload_dir)
    
    app.run(host='0.0.0.0', port=5000, debug=True)
