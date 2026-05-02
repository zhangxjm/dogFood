from marshmallow import Schema, fields, validate, validates, ValidationError
from app.models import User

class UserSchema(Schema):
    id = fields.Int(dump_only=True)
    username = fields.Str(required=True, validate=validate.Length(min=3, max=80))
    email = fields.Email(allow_none=True)
    is_active = fields.Bool(dump_only=True)
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)
    
    class Meta:
        fields = ('id', 'username', 'email', 'is_active', 'created_at', 'updated_at')

class RegisterSchema(Schema):
    username = fields.Str(required=True, validate=validate.Length(min=3, max=80))
    email = fields.Email(allow_none=True)
    password = fields.Str(required=True, validate=validate.Length(min=6, max=128))
    
    @validates('username')
    def validate_username_unique(self, value):
        if User.query.filter_by(username=value).first():
            raise ValidationError('Username already exists')
    
    @validates('email')
    def validate_email_unique(self, value):
        if value and User.query.filter_by(email=value).first():
            raise ValidationError('Email already exists')

class LoginSchema(Schema):
    username = fields.Str(required=True)
    password = fields.Str(required=True)
