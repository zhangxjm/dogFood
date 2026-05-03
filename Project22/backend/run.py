from app import create_app, db
from app.models import Member, Coach, Course, Reservation, CheckIn, Transaction

app = create_app()

@app.shell_context_processor
def make_shell_context():
    return {'db': db, 'Member': Member, 'Coach': Coach, 
            'Course': Course, 'Reservation': Reservation,
            'CheckIn': CheckIn, 'Transaction': Transaction}

@app.cli.command('init-db')
def init_db():
    with app.app_context():
        db.create_all()
        print('Database initialized successfully!')

@app.cli.command('seed-db')
def seed_db():
    from app.models import Coach, Course
    from datetime import time
    
    with app.app_context():
        if Coach.query.count() == 0:
            coaches = [
                Coach(name='张教练', phone='13800138001', specialty='减脂塑形', experience=5),
                Coach(name='李教练', phone='13800138002', specialty='力量训练', experience=8),
                Coach(name='王教练', phone='13800138003', specialty='瑜伽普拉提', experience=6),
            ]
            for coach in coaches:
                db.session.add(coach)
            db.session.commit()
            print('Coaches seeded successfully!')
        
        if Course.query.count() == 0:
            courses = [
                Course(name='动感单车', description='高强度有氧训练，快速燃脂', 
                      coach_id=1, capacity=20, start_time=time(9, 0), duration=45, day_of_week=1),
                Course(name='瑜伽放松', description='舒缓身心，提升柔韧性', 
                      coach_id=3, capacity=15, start_time=time(10, 0), duration=60, day_of_week=1),
                Course(name='力量训练', description='增肌塑形，提升基础代谢', 
                      coach_id=2, capacity=10, start_time=time(14, 0), duration=60, day_of_week=2),
                Course(name='HIIT训练', description='高强度间歇训练，高效燃脂', 
                      coach_id=1, capacity=15, start_time=time(19, 0), duration=30, day_of_week=3),
            ]
            for course in courses:
                db.session.add(course)
            db.session.commit()
            print('Courses seeded successfully!')
        
        print('Database seeded with initial data!')

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True, host='0.0.0.0', port=5001)
