import os

DB_TYPE = os.getenv('DB_TYPE', 'sqlite')

if DB_TYPE == 'mysql':
    try:
        import pymysql
        pymysql.install_as_MySQLdb()
    except ImportError:
        pass
