from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status, Query
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from typing import List, Optional

from app.auth import get_current_user, get_designer_user
from app.database import get_database
from app.models import (
    WorkCreate,
    WorkUpdate,
    WorkResponse,
    UserResponse
)

router = APIRouter(prefix="/api/works", tags=["作品"])


@router.get("/", response_model=List[WorkResponse])
async def get_works(
    category: Optional[str] = None,
    search: Optional[str] = None,
    designer_id: Optional[str] = None,
    limit: int = Query(default=20, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    sort_by: str = Query(default="created_at"),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    query = {"is_active": True}
    
    if category:
        query["category"] = category
    
    if designer_id:
        query["designer_id"] = ObjectId(designer_id)
    
    if search:
        query["$or"] = [
            {"title": {"$regex": search, "$options": "i"}},
            {"description": {"$regex": search, "$options": "i"}},
            {"tags": {"$regex": search, "$options": "i"}}
        ]
    
    sort_order = -1 if sort_by == "created_at" else -1
    if sort_by == "likes":
        sort_order = -1
        cursor = db.works.find(query).sort("likes_count", sort_order).skip(offset).limit(limit)
    elif sort_by == "views":
        cursor = db.works.find(query).sort("views_count", sort_order).skip(offset).limit(limit)
    else:
        cursor = db.works.find(query).sort("created_at", sort_order).skip(offset).limit(limit)
    
    works = []
    async for work in cursor:
        works.append(WorkResponse(**work))
    
    return works


@router.get("/featured", response_model=List[WorkResponse])
async def get_featured_works(
    limit: int = Query(default=12, ge=1, le=50),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    pipeline = [
        {"$match": {"is_active": True}},
        {"$sample": {"size": limit}}
    ]
    
    cursor = db.works.aggregate(pipeline)
    works = []
    async for work in cursor:
        works.append(WorkResponse(**work))
    
    return works


@router.get("/{work_id}", response_model=WorkResponse)
async def get_work(
    work_id: str,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    work = await db.works.find_one({"_id": ObjectId(work_id), "is_active": True})
    if not work:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Work not found"
        )
    
    await db.works.update_one(
        {"_id": ObjectId(work_id)},
        {"$inc": {"views_count": 1}}
    )
    
    work["views_count"] = work.get("views_count", 0) + 1
    
    return WorkResponse(**work)


@router.post("/", response_model=WorkResponse)
async def create_work(
    work_data: WorkCreate,
    current_user: UserResponse = Depends(get_designer_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    category = await db.categories.find_one({"name": work_data.category})
    if not category:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid category"
        )
    
    work_dict = work_data.model_dump()
    work_dict["designer_id"] = ObjectId(current_user.id)
    work_dict["designer_name"] = current_user.username
    work_dict["designer_avatar"] = current_user.avatar
    work_dict["is_active"] = True
    work_dict["likes_count"] = 0
    work_dict["favorites_count"] = 0
    work_dict["comments_count"] = 0
    work_dict["views_count"] = 0
    work_dict["created_at"] = datetime.utcnow()
    
    result = await db.works.insert_one(work_dict)
    work_dict["_id"] = result.inserted_id
    
    return WorkResponse(**work_dict)


@router.put("/{work_id}", response_model=WorkResponse)
async def update_work(
    work_id: str,
    update_data: WorkUpdate,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    work = await db.works.find_one({"_id": ObjectId(work_id)})
    if not work:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Work not found"
        )
    
    if str(work["designer_id"]) != str(current_user.id):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to update this work"
        )
    
    update_dict = {k: v for k, v in update_data.model_dump().items() if v is not None}
    update_dict["updated_at"] = datetime.utcnow()
    
    if update_dict:
        await db.works.update_one(
            {"_id": ObjectId(work_id)},
            {"$set": update_dict}
        )
    
    updated_work = await db.works.find_one({"_id": ObjectId(work_id)})
    return WorkResponse(**updated_work)


@router.delete("/{work_id}")
async def delete_work(
    work_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    work = await db.works.find_one({"_id": ObjectId(work_id)})
    if not work:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Work not found"
        )
    
    if str(work["designer_id"]) != str(current_user.id):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Not authorized to delete this work"
        )
    
    await db.works.update_one(
        {"_id": ObjectId(work_id)},
        {"$set": {"is_active": False, "updated_at": datetime.utcnow()}}
    )
    
    return {"message": "Work deleted successfully"}
