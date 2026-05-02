import os
from app import create_app, db
from app.models import User, DataEntry

app = create_app(os.getenv('FLASK_ENV') or 'default')

@app.shell_context_processor
def make_shell_context():
    return dict(db=db, User=User, DataEntry=DataEntry)

@app.cli.command()
def init_db():
    db.create_all()
    print('Database initialized!')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
