from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status, Query
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from typing import List, Optional

from app.auth import get_current_user
from app.database import get_database
from app.models import (
    CommentCreate,
    CommentResponse,
    LikeResponse,
    FavoriteResponse,
    UserResponse
)

router = APIRouter(prefix="/api", tags=["交互"])


@router.get("/works/{work_id}/comments", response_model=List[CommentResponse])
async def get_comments(
    work_id: str,
    limit: int = Query(default=20, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    cursor = db.comments.find({"work_id": ObjectId(work_id)}).sort(
        "created_at", -1
    ).skip(offset).limit(limit)
    
    comments = []
    async for comment in cursor:
        comments.append(CommentResponse(**comment))
    
    return comments


@router.post("/comments", response_model=CommentResponse)
async def create_comment(
    comment_data: CommentCreate,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(comment_data.work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    work = await db.works.find_one({"_id": ObjectId(comment_data.work_id), "is_active": True})
    if not work:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Work not found"
        )
    
    comment_dict = {
        "work_id": ObjectId(comment_data.work_id),
        "user_id": ObjectId(current_user.id),
        "user_name": current_user.username,
        "user_avatar": current_user.avatar,
        "content": comment_data.content,
        "created_at": datetime.utcnow()
    }
    
    result = await db.comments.insert_one(comment_dict)
    comment_dict["_id"] = result.inserted_id
    
    await db.works.update_one(
        {"_id": ObjectId(comment_data.work_id)},
        {"$inc": {"comments_count": 1}}
    )
    
    return CommentResponse(**comment_dict)


@router.post("/works/{work_id}/like")
async def toggle_like(
    work_id: str,
    current_user: UserResponse = Depends(get_current_user),
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
    
    existing_like = await db.likes.find_one({
        "work_id": ObjectId(work_id),
        "user_id": ObjectId(current_user.id)
    })
    
    if existing_like:
        await db.likes.delete_one({"_id": existing_like["_id"]})
        await db.works.update_one(
            {"_id": ObjectId(work_id)},
            {"$inc": {"likes_count": -1}}
        )
        return {"liked": False, "message": "Like removed"}
    else:
        like_dict = {
            "work_id": ObjectId(work_id),
            "user_id": ObjectId(current_user.id),
            "created_at": datetime.utcnow()
        }
        await db.likes.insert_one(like_dict)
        await db.works.update_one(
            {"_id": ObjectId(work_id)},
            {"$inc": {"likes_count": 1}}
        )
        return {"liked": True, "message": "Like added"}


@router.get("/works/{work_id}/liked")
async def check_like(
    work_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    existing_like = await db.likes.find_one({
        "work_id": ObjectId(work_id),
        "user_id": ObjectId(current_user.id)
    })
    
    return {"liked": existing_like is not None}


@router.post("/works/{work_id}/favorite")
async def toggle_favorite(
    work_id: str,
    current_user: UserResponse = Depends(get_current_user),
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
    
    existing_favorite = await db.favorites.find_one({
        "work_id": ObjectId(work_id),
        "user_id": ObjectId(current_user.id)
    })
    
    if existing_favorite:
        await db.favorites.delete_one({"_id": existing_favorite["_id"]})
        await db.works.update_one(
            {"_id": ObjectId(work_id)},
            {"$inc": {"favorites_count": -1}}
        )
        return {"favorited": False, "message": "Favorite removed"}
    else:
        favorite_dict = {
            "work_id": ObjectId(work_id),
            "user_id": ObjectId(current_user.id),
            "created_at": datetime.utcnow()
        }
        await db.favorites.insert_one(favorite_dict)
        await db.works.update_one(
            {"_id": ObjectId(work_id)},
            {"$inc": {"favorites_count": 1}}
        )
        return {"favorited": True, "message": "Favorite added"}


@router.get("/works/{work_id}/favorited")
async def check_favorite(
    work_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(work_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid work ID"
        )
    
    existing_favorite = await db.favorites.find_one({
        "work_id": ObjectId(work_id),
        "user_id": ObjectId(current_user.id)
    })
    
    return {"favorited": existing_favorite is not None}


@router.get("/users/me/favorites", response_model=List[FavoriteResponse])
async def get_my_favorites(
    limit: int = Query(default=20, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    cursor = db.favorites.find({"user_id": ObjectId(current_user.id)}).sort(
        "created_at", -1
    ).skip(offset).limit(limit)
    
    favorites = []
    async for favorite in cursor:
        favorites.append(FavoriteResponse(**favorite))
    
    return favorites
