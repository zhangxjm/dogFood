from sqlalchemy import create_engine, text
from sqlalchemy.orm import Session

from app.config import settings
from app.database import Base, engine
from app.models import User, Poll, Candidate
from app.services.auth import get_password_hash
from datetime import datetime, timedelta


def create_database_if_not_exists():
    engine_url = f"mysql+pymysql://{settings.MYSQL_USER}:{settings.MYSQL_PASSWORD}@{settings.MYSQL_HOST}:{settings.MYSQL_PORT}"
    temp_engine = create_engine(engine_url, echo=settings.DEBUG)
    
    with temp_engine.connect() as conn:
        conn.execute(text(f"CREATE DATABASE IF NOT EXISTS {settings.MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"))
        conn.commit()
    
    temp_engine.dispose()
    print(f"数据库 {settings.MYSQL_DATABASE} 已创建或已存在")


def create_tables():
    Base.metadata.create_all(bind=engine)
    print("数据库表已创建")


def init_admin_user(db: Session):
    admin = db.query(User).filter(User.username == settings.ADMIN_USERNAME).first()
    if not admin:
        admin = User(
            username=settings.ADMIN_USERNAME,
            password=get_password_hash(settings.ADMIN_PASSWORD),
            is_admin=True
        )
        db.add(admin)
        db.commit()
        print(f"管理员用户已创建: {settings.ADMIN_USERNAME}")
    else:
        print("管理员用户已存在")


def init_sample_data(db: Session):
    existing_poll = db.query(Poll).filter(Poll.title == "2024年度最佳员工评选").first()
    if not existing_poll:
        poll = Poll(
            title="2024年度最佳员工评选",
            description="请为您心目中的最佳员工投票，每人可投1票",
            start_time=datetime.now(),
            end_time=datetime.now() + timedelta(days=30),
            is_active=True,
            max_votes_per_user=1
        )
        db.add(poll)
        db.flush()
        
        candidates_data = [
            {"name": "张三", "description": "销售部主管，年度业绩第一", "avatar_url": None},
            {"name": "李四", "description": "技术部核心开发，多项创新", "avatar_url": None},
            {"name": "王五", "description": "市场部经理，客户满意度高", "avatar_url": None},
            {"name": "赵六", "description": "人事部专员，员工关系处理得当", "avatar_url": None},
        ]
        
        for cand_data in candidates_data:
            candidate = Candidate(
                poll_id=poll.id,
                name=cand_data["name"],
                description=cand_data["description"],
                avatar_url=cand_data["avatar_url"]
            )
            db.add(candidate)
        
        db.commit()
        print("示例投票活动已创建")
    else:
        print("示例数据已存在")


def init_database():
    create_database_if_not_exists()
    create_tables()
    
    from app.database import SessionLocal
    db = SessionLocal()
    try:
        init_admin_user(db)
        init_sample_data(db)
    finally:
        db.close()
    
    print("数据库初始化完成！")
    print(f"管理员账号: {settings.ADMIN_USERNAME}")
    print(f"管理员密码: {settings.ADMIN_PASSWORD}")


if __name__ == "__main__":
    init_database()
