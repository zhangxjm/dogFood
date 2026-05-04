import os

import pymysql
pymysql.install_as_MySQLdb()

from django.core.asgi import get_asgi_application

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'school_lost_found.settings')

application = get_asgi_application()
