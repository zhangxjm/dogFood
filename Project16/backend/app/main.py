import os
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import JSONResponse

from app.config import get_settings
from app.database import connect_to_mongo, close_mongo_connection, get_database
from app.routers import auth, categories, works, interactions, orders, upload, users
from app.routers.categories import init_default_categories


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    
    os.makedirs(settings.upload_dir, exist_ok=True)
    
    await connect_to_mongo()
    
    db = get_database()
    await init_default_categories(db)
    
    yield
    
    await close_mongo_connection()


app = FastAPI(
    title="文创作品平台 API",
    description="一个完整的文创/艺术作品展示和交易平台",
    version="1.0.0",
    lifespan=lifespan
)

settings = get_settings()

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.mount("/uploads", StaticFiles(directory=settings.upload_dir), name="uploads")

app.include_router(auth.router)
app.include_router(categories.router)
app.include_router(works.router)
app.include_router(interactions.router)
app.include_router(orders.router)
app.include_router(upload.router)
app.include_router(users.router)


@app.get("/")
async def root():
    return {
        "name": "文创作品平台 API",
        "version": "1.0.0",
        "docs": "/docs",
        "redoc": "/redoc"
    }


@app.get("/health")
async def health_check():
    return {"status": "healthy"}


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={"detail": "Internal server error"}
    )
