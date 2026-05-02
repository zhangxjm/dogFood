from marshmallow import Schema, fields, validate, validates, ValidationError

class DataEntrySchema(Schema):
    id = fields.Int(dump_only=True)
    title = fields.Str(required=True, validate=validate.Length(min=1, max=200))
    description = fields.Str(allow_none=True)
    category = fields.Str(allow_none=True, validate=validate.Length(max=100))
    status = fields.Str(
        allow_none=True,
        validate=validate.OneOf(['draft', 'active', 'completed', 'archived'])
    )
    priority = fields.Int(allow_none=True, validate=validate.Range(min=1, max=5))
    extra_data = fields.Dict(allow_none=True)
    created_by = fields.Int(dump_only=True)
    is_deleted = fields.Bool(dump_only=True)
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)
    
    class Meta:
        fields = (
            'id', 'title', 'description', 'category', 'status', 
            'priority', 'extra_data', 'created_by', 'is_deleted',
            'created_at', 'updated_at'
        )

class DataEntryCreateSchema(Schema):
    title = fields.Str(required=True, validate=validate.Length(min=1, max=200))
    description = fields.Str(allow_none=True)
    category = fields.Str(allow_none=True, validate=validate.Length(max=100))
    status = fields.Str(
        missing='draft',
        validate=validate.OneOf(['draft', 'active', 'completed', 'archived'])
    )
    priority = fields.Int(missing=1, validate=validate.Range(min=1, max=5))
    extra_data = fields.Dict(allow_none=True)

class DataEntryUpdateSchema(Schema):
    title = fields.Str(validate=validate.Length(min=1, max=200))
    description = fields.Str(allow_none=True)
    category = fields.Str(allow_none=True, validate=validate.Length(max=100))
    status = fields.Str(
        allow_none=True,
        validate=validate.OneOf(['draft', 'active', 'completed', 'archived'])
    )
    priority = fields.Int(allow_none=True, validate=validate.Range(min=1, max=5))
    extra_data = fields.Dict(allow_none=True)
