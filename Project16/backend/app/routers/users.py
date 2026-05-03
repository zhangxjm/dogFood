from fastapi import APIRouter, Depends, HTTPException, status
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from typing import List

from app.database import get_database
from app.models import UserResponse

router = APIRouter(prefix="/api/users", tags=["用户"])


@router.get("/{user_id}", response_model=UserResponse)
async def get_user(
    user_id: str,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(user_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid user ID"
        )
    
    user = await db.users.find_one({"_id": ObjectId(user_id)})
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    
    return UserResponse(**user)


@router.get("/designers", response_model=List[UserResponse])
async def get_designers(
    limit: int = 20,
    offset: int = 0,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    cursor = db.users.find({"is_designer": True}).sort("created_at", -1).skip(offset).limit(limit)
    
    designers = []
    async for designer in cursor:
        designers.append(UserResponse(**designer))
    
    return designers
