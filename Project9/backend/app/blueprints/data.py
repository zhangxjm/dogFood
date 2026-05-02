from io import StringIO
import csv
from flask import Blueprint, request, g, make_response
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime
from app import db, logger
from app.models import DataEntry
from app.schemas import DataEntrySchema, DataEntryCreateSchema, DataEntryUpdateSchema
from app.utils import success_response, error_response, validate_request

data_bp = Blueprint('data', __name__)

@data_bp.route('', methods=['GET'])
@jwt_required()
def get_data_list():
    user_id = get_jwt_identity()
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 20, type=int)
    category = request.args.get('category', None)
    status = request.args.get('status', None)
    search = request.args.get('search', None)
    sort_by = request.args.get('sort_by', 'created_at')
    sort_order = request.args.get('sort_order', 'desc')
    
    query = DataEntry.query.filter_by(created_by=user_id, is_deleted=False)
    
    if category:
        query = query.filter(DataEntry.category == category)
    if status:
        query = query.filter(DataEntry.status == status)
    if search:
        query = query.filter(
            db.or_(
                DataEntry.title.contains(search),
                DataEntry.description.contains(search)
            )
        )
    
    if sort_order == 'desc':
        query = query.order_by(db.desc(getattr(DataEntry, sort_by, DataEntry.created_at)))
    else:
        query = query.order_by(db.asc(getattr(DataEntry, sort_by, DataEntry.created_at)))
    
    pagination = query.paginate(page=page, per_page=per_page, error_out=False)
    items = pagination.items
    
    schema = DataEntrySchema(many=True)
    
    return success_response(data={
        'items': schema.dump(items),
        'total': pagination.total,
        'pages': pagination.pages,
        'current_page': page,
        'per_page': per_page
    })

@data_bp.route('/<int:entry_id>', methods=['GET'])
@jwt_required()
def get_data_entry(entry_id):
    user_id = get_jwt_identity()
    entry = DataEntry.query.filter_by(
        id=entry_id, 
        created_by=user_id, 
        is_deleted=False
    ).first()
    
    if not entry:
        return error_response(
            message='Data entry not found',
            code=404
        )
    
    schema = DataEntrySchema()
    return success_response(data=schema.dump(entry))

@data_bp.route('', methods=['POST'])
@jwt_required()
@validate_request(DataEntryCreateSchema)
def create_data_entry():
    user_id = get_jwt_identity()
    data = g.validated_data
    
    entry = DataEntry(
        title=data['title'],
        description=data.get('description'),
        category=data.get('category'),
        status=data.get('status', 'draft'),
        priority=data.get('priority', 1),
        extra_data=data.get('extra_data'),
        created_by=user_id
    )
    
    db.session.add(entry)
    db.session.commit()
    
    logger.info(f'Data entry created by user {user_id}: {entry.id}')
    
    schema = DataEntrySchema()
    return success_response(
        data=schema.dump(entry),
        message='Data entry created successfully',
        code=201
    )

@data_bp.route('/<int:entry_id>', methods=['PUT'])
@jwt_required()
@validate_request(DataEntryUpdateSchema)
def update_data_entry(entry_id):
    user_id = get_jwt_identity()
    data = g.validated_data
    
    entry = DataEntry.query.filter_by(
        id=entry_id, 
        created_by=user_id, 
        is_deleted=False
    ).first()
    
    if not entry:
        return error_response(
            message='Data entry not found',
            code=404
        )
    
    for key, value in data.items():
        if hasattr(entry, key) and value is not None:
            setattr(entry, key, value)
    
    db.session.commit()
    
    logger.info(f'Data entry updated by user {user_id}: {entry.id}')
    
    schema = DataEntrySchema()
    return success_response(
        data=schema.dump(entry),
        message='Data entry updated successfully'
    )

@data_bp.route('/<int:entry_id>', methods=['DELETE'])
@jwt_required()
def delete_data_entry(entry_id):
    user_id = get_jwt_identity()
    
    entry = DataEntry.query.filter_by(
        id=entry_id, 
        created_by=user_id, 
        is_deleted=False
    ).first()
    
    if not entry:
        return error_response(
            message='Data entry not found',
            code=404
        )
    
    entry.is_deleted = True
    db.session.commit()
    
    logger.info(f'Data entry soft-deleted by user {user_id}: {entry.id}')
    
    return success_response(
        message='Data entry deleted successfully'
    )

@data_bp.route('/export/csv', methods=['GET'])
@jwt_required()
def export_csv():
    user_id = get_jwt_identity()
    category = request.args.get('category', None)
    status = request.args.get('status', None)
    search = request.args.get('search', None)
    
    query = DataEntry.query.filter_by(created_by=user_id, is_deleted=False)
    
    if category:
        query = query.filter(DataEntry.category == category)
    if status:
        query = query.filter(DataEntry.status == status)
    if search:
        query = query.filter(
            db.or_(
                DataEntry.title.contains(search),
                DataEntry.description.contains(search)
            )
        )
    
    entries = query.all()
    
    output = StringIO()
    writer = csv.writer(output)
    
    writer.writerow([
        'ID', 'Title', 'Description', 'Category', 'Status', 
        'Priority', 'Created At', 'Updated At'
    ])
    
    for entry in entries:
        writer.writerow([
            entry.id,
            entry.title,
            entry.description or '',
            entry.category or '',
            entry.status or '',
            entry.priority or 1,
            entry.created_at.isoformat() if entry.created_at else '',
            entry.updated_at.isoformat() if entry.updated_at else ''
        ])
    
    output.seek(0)
    
    response = make_response(output.getvalue())
    response.headers['Content-Type'] = 'text/csv'
    response.headers['Content-Disposition'] = f'attachment; filename=data_export_{datetime.now().strftime("%Y%m%d_%H%M%S")}.csv'
    
    logger.info(f'Data exported as CSV by user {user_id}')
    
    return response

@data_bp.route('/categories', methods=['GET'])
@jwt_required()
def get_categories():
    user_id = get_jwt_identity()
    
    categories = db.session.query(DataEntry.category).filter(
        DataEntry.created_by == user_id,
        DataEntry.is_deleted == False,
        DataEntry.category.isnot(None)
    ).distinct().all()
    
    category_list = [cat[0] for cat in categories]
    
    return success_response(data={
        'categories': category_list
    })

@data_bp.route('/stats', methods=['GET'])
@jwt_required()
def get_stats():
    user_id = get_jwt_identity()
    
    total = DataEntry.query.filter_by(created_by=user_id, is_deleted=False).count()
    by_status = db.session.query(
        DataEntry.status, db.func.count(DataEntry.id)
    ).filter(
        DataEntry.created_by == user_id,
        DataEntry.is_deleted == False
    ).group_by(DataEntry.status).all()
    
    status_stats = {s: c for s, c in by_status}
    
    return success_response(data={
        'total': total,
        'by_status': status_stats
    })
