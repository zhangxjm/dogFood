from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.auth import (
    create_access_token,
    get_password_hash,
    verify_password,
    get_current_user
)
from app.database import get_database
from app.models import (
    UserCreate,
    UserResponse,
    UserLogin,
    UserUpdate,
    Token
)

router = APIRouter(prefix="/api/auth", tags=["认证"])


@router.post("/register", response_model=Token)
async def register(
    user_data: UserCreate,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    existing_user = await db.users.find_one({"email": user_data.email})
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email already registered"
        )
    
    existing_username = await db.users.find_one({"username": user_data.username})
    if existing_username:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Username already taken"
        )
    
    user_dict = user_data.model_dump()
    user_dict["password"] = get_password_hash(user_dict["password"])
    user_dict["created_at"] = datetime.utcnow()
    
    result = await db.users.insert_one(user_dict)
    user_dict["_id"] = result.inserted_id
    
    user_response = UserResponse(**user_dict)
    access_token = create_access_token(data={"sub": str(result.inserted_id)})
    
    return Token(access_token=access_token, user=user_response)


@router.post("/login", response_model=Token)
async def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    user = await db.users.find_one({"email": form_data.username})
    if not user or not verify_password(form_data.password, user["password"]):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    user_response = UserResponse(**user)
    access_token = create_access_token(data={"sub": str(user["_id"])})
    
    return Token(access_token=access_token, user=user_response)


@router.post("/login-json", response_model=Token)
async def login_json(
    login_data: UserLogin,
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    user = await db.users.find_one({"email": login_data.email})
    if not user or not verify_password(login_data.password, user["password"]):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password"
        )
    
    user_response = UserResponse(**user)
    access_token = create_access_token(data={"sub": str(user["_id"])})
    
    return Token(access_token=access_token, user=user_response)


@router.get("/me", response_model=UserResponse)
async def read_users_me(
    current_user: UserResponse = Depends(get_current_user)
):
    return current_user


@router.put("/me", response_model=UserResponse)
async def update_user_me(
    update_data: UserUpdate,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    update_dict = {k: v for k, v in update_data.model_dump().items() if v is not None}
    update_dict["updated_at"] = datetime.utcnow()
    
    if update_dict:
        await db.users.update_one(
            {"_id": ObjectId(current_user.id)},
            {"$set": update_dict}
        )
    
    updated_user = await db.users.find_one({"_id": ObjectId(current_user.id)})
    return UserResponse(**updated_user)
