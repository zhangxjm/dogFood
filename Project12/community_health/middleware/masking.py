import json
import re
from django.conf import settings
from django.utils.deprecation import MiddlewareMixin


class DataMaskingMiddleware(MiddlewareMixin):
    def __init__(self, get_response):
        self.get_response = get_response
        self.masking_config = getattr(settings, 'DATA_MASKING_FIELDS', {})
        self.mask_for_roles = getattr(settings, 'DATA_MASKING_FOR_ROLES', ['resident'])
        self.exempt_paths = getattr(settings, 'DATA_MASKING_EXEMPT_PATHS', [
            '/api/auth/',
            '/admin/',
            '/swagger/',
            '/redoc/',
            '/static/',
            '/media/',
        ])
    
    def should_mask(self, request):
        for exempt_path in self.exempt_paths:
            if request.path.startswith(exempt_path):
                return False
        
        if not hasattr(request, 'user'):
            return True
        
        if not request.user.is_authenticated:
            return True
        
        user_role = getattr(request.user, 'role', None)
        return user_role in self.mask_for_roles
    
    def process_response(self, request, response):
        if not self.should_mask(request):
            return response
        
        if response.get('Content-Type', '').startswith('application/json'):
            try:
                if hasattr(response, 'data'):
                    data = response.data
                    masked_data = self.mask_data(data)
                    response.data = masked_data
                else:
                    content = response.content.decode('utf-8')
                    data = json.loads(content)
                    masked_data = self.mask_data(data)
                    response.content = json.dumps(masked_data, ensure_ascii=False).encode('utf-8')
            except (json.JSONDecodeError, UnicodeDecodeError):
                pass
        
        return response
    
    def mask_data(self, data, masking_rules=None):
        if masking_rules is None:
            masking_rules = self.masking_config
        
        if isinstance(data, dict):
            result = {}
            for key, value in data.items():
                if key in masking_rules:
                    rule = masking_rules[key]
                    result[key] = mask_value(value, rule.get('type', 'name'), rule.get('show_chars', 4))
                elif isinstance(value, (dict, list)):
                    result[key] = self.mask_data(value, masking_rules)
                else:
                    result[key] = value
            return result
        
        elif isinstance(data, list):
            return [self.mask_data(item, masking_rules) for item in data]
        
        return data


def mask_phone(phone, show_chars=4):
    if not phone or len(str(phone)) < 7:
        return phone
    phone = str(phone)
    show_start = show_chars // 2
    show_end = show_chars - show_start
    return phone[:show_start] + '*' * (len(phone) - show_chars) + phone[-show_end:]


def mask_id_card(id_card, show_chars=6):
    if not id_card or len(str(id_card)) < 10:
        return id_card
    id_card = str(id_card)
    show_start = show_chars // 2
    show_end = show_chars - show_start
    return id_card[:show_start] + '*' * (len(id_card) - show_chars) + id_card[-show_end:]


def mask_email(email, show_chars=2):
    if not email or '@' not in str(email):
        return email
    email = str(email)
    local, domain = email.split('@', 1)
    if len(local) <= show_chars:
        return email
    return local[:show_chars] + '*' * (len(local) - show_chars) + '@' + domain


def mask_name(name, show_chars=1):
    if not name:
        return name
    name = str(name)
    if len(name) <= show_chars:
        return name
    return name[:show_chars] + '*' * (len(name) - show_chars)


def mask_insurance_number(insurance_number, show_chars=4):
    if not insurance_number or len(str(insurance_number)) < 6:
        return insurance_number
    insurance_number = str(insurance_number)
    show_start = show_chars // 2
    show_end = show_chars - show_start
    return insurance_number[:show_start] + '*' * (len(insurance_number) - show_chars) + insurance_number[-show_end:]


def mask_value(value, field_type, show_chars=4):
    if value is None:
        return None
    
    maskers = {
        'phone': mask_phone,
        'id_card': mask_id_card,
        'email': mask_email,
        'name': mask_name,
        'insurance_number': mask_insurance_number,
    }
    
    masker = maskers.get(field_type)
    if masker:
        return masker(value, show_chars)
    return value


def mask_data(data, masking_rules=None):
    if masking_rules is None:
        masking_rules = getattr(settings, 'DATA_MASKING_FIELDS', {})
    
    if isinstance(data, dict):
        result = {}
        for key, value in data.items():
            if key in masking_rules:
                rule = masking_rules[key]
                result[key] = mask_value(value, rule.get('type', 'name'), rule.get('show_chars', 4))
            elif isinstance(value, (dict, list)):
                result[key] = mask_data(value, masking_rules)
            else:
                result[key] = value
        return result
    
    elif isinstance(data, list):
        return [mask_data(item, masking_rules) for item in data]
    
    return data
