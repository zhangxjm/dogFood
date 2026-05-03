from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from typing import List

from app.database import get_database
from app.models import (
    CategoryCreate,
    CategoryResponse
)

router = APIRouter(prefix="/api/categories", tags=["分类"])


@router.get("/", response_model=List[CategoryResponse])
async def get_categories(
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    cursor = db.categories.find().sort("created_at", -1)
    categories = []
    
    async for category in cursor:
        work_count = await db.works.count_documents({"category": category["name"], "is_active": True})
        category["work_count"] = work_count
        categories.append(CategoryResponse(**category))
    
    return categories


@router.get("/{category_name}", response_model=CategoryResponse)
async def get_category(
    category_name: str,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    category = await db.categories.find_one({"name": category_name})
    if not category:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Category not found"
        )
    
    work_count = await db.works.count_documents({"category": category_name, "is_active": True})
    category["work_count"] = work_count
    
    return CategoryResponse(**category)


@router.post("/", response_model=CategoryResponse)
async def create_category(
    category_data: CategoryCreate,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    existing = await db.categories.find_one({"name": category_data.name})
    if existing:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Category already exists"
        )
    
    category_dict = category_data.model_dump()
    category_dict["created_at"] = datetime.utcnow()
    category_dict["work_count"] = 0
    
    result = await db.categories.insert_one(category_dict)
    category_dict["_id"] = result.inserted_id
    
    return CategoryResponse(**category_dict)


async def init_default_categories(db: AsyncIOMotorDatabase):
    default_categories = [
        {"name": "插画", "description": "创意插画艺术作品", "icon": "🎨"},
        {"name": "平面设计", "description": "品牌、海报、UI设计", "icon": "🖼️"},
        {"name": "3D建模", "description": "三维模型与渲染作品", "icon": "🧊"},
        {"name": "摄影", "description": "精选摄影作品集", "icon": "📷"},
        {"name": "动效", "description": "动画与动效设计", "icon": "🎬"},
        {"name": "字体", "description": "字体与排版设计", "icon": "🔤"},
        {"name": "包装", "description": "产品包装设计", "icon": "📦"},
        {"name": "图标", "description": "图标与符号设计", "icon": "✨"},
    ]
    
    for cat in default_categories:
        existing = await db.categories.find_one({"name": cat["name"]})
        if not existing:
            cat["created_at"] = datetime.utcnow()
            cat["work_count"] = 0
            await db.categories.insert_one(cat)
