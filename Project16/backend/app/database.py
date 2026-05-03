from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from app.config import get_settings


class Database:
    client: AsyncIOMotorClient = None
    database: AsyncIOMotorDatabase = None


db = Database()


async def connect_to_mongo():
    settings = get_settings()
    db.client = AsyncIOMotorClient(settings.mongodb_url)
    db.database = db.client[settings.database_name]
    print(f"Connected to MongoDB: {settings.database_name}")
    
    await db.database.users.create_index("email", unique=True)
    await db.database.users.create_index("username", unique=True)
    await db.database.works.create_index("title")
    await db.database.works.create_index("category")
    await db.database.works.create_index("designer_id")
    await db.database.categories.create_index("name", unique=True)


async def close_mongo_connection():
    if db.client:
        db.client.close()
        print("MongoDB connection closed")


def get_database() -> AsyncIOMotorDatabase:
    return db.database
