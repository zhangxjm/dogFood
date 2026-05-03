from datetime import datetime
from typing import List, Optional
from bson import ObjectId
from pydantic import BaseModel, EmailStr, Field, field_validator


class PyObjectId(ObjectId):
    @classmethod
    def __get_validators__(cls):
        yield cls.validate

    @classmethod
    def validate(cls, v):
        if not ObjectId.is_valid(v):
            raise ValueError("Invalid ObjectId")
        return ObjectId(v)

    @classmethod
    def __get_pydantic_json_schema__(cls, field_schema):
        field_schema.update(type="string")


class UserBase(BaseModel):
    email: EmailStr
    username: str
    is_designer: bool = False
    avatar: Optional[str] = None
    bio: Optional[str] = None


class UserCreate(UserBase):
    password: str
    
    @field_validator('password')
    @classmethod
    def password_required(cls, v: str) -> str:
        if not v or len(v) < 6:
            raise ValueError('Password must be at least 6 characters')
        return v


class UserLogin(BaseModel):
    email: EmailStr
    password: str


class UserUpdate(BaseModel):
    username: Optional[str] = None
    avatar: Optional[str] = None
    bio: Optional[str] = None
    is_designer: Optional[bool] = None


class UserResponse(UserBase):
    id: PyObjectId = Field(alias="_id")
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str, datetime: lambda v: v.isoformat()}


class CategoryBase(BaseModel):
    name: str
    description: Optional[str] = None
    icon: Optional[str] = None


class CategoryCreate(CategoryBase):
    pass


class CategoryResponse(CategoryBase):
    id: PyObjectId = Field(alias="_id")
    work_count: int = 0
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class WorkBase(BaseModel):
    title: str
    description: str
    category: str
    price: float = 0.0
    tags: List[str] = []
    images: List[str] = []
    is_premium: bool = False
    download_url: Optional[str] = None


class WorkCreate(WorkBase):
    pass


class WorkUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    category: Optional[str] = None
    price: Optional[float] = None
    tags: Optional[List[str]] = None
    images: Optional[List[str]] = None
    is_premium: Optional[bool] = None
    download_url: Optional[str] = None
    is_active: Optional[bool] = None


class WorkResponse(WorkBase):
    id: PyObjectId = Field(alias="_id")
    designer_id: PyObjectId
    designer_name: str
    designer_avatar: Optional[str] = None
    likes_count: int = 0
    favorites_count: int = 0
    comments_count: int = 0
    views_count: int = 0
    is_active: bool = True
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class CommentBase(BaseModel):
    content: str


class CommentCreate(CommentBase):
    work_id: str


class CommentResponse(CommentBase):
    id: PyObjectId = Field(alias="_id")
    work_id: PyObjectId
    user_id: PyObjectId
    user_name: str
    user_avatar: Optional[str] = None
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class LikeResponse(BaseModel):
    id: PyObjectId = Field(alias="_id")
    work_id: PyObjectId
    user_id: PyObjectId
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class FavoriteResponse(BaseModel):
    id: PyObjectId = Field(alias="_id")
    work_id: PyObjectId
    user_id: PyObjectId
    created_at: datetime = Field(default_factory=datetime.utcnow)

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class OrderItem(BaseModel):
    work_id: PyObjectId
    title: str
    price: float
    image_url: Optional[str] = None


class OrderBase(BaseModel):
    items: List[OrderItem]
    total_amount: float
    status: str = "pending"


class OrderCreate(BaseModel):
    work_ids: List[str]


class OrderResponse(OrderBase):
    id: PyObjectId = Field(alias="_id")
    user_id: PyObjectId
    order_number: str
    status: str
    created_at: datetime = Field(default_factory=datetime.utcnow)
    updated_at: Optional[datetime] = None

    class Config:
        populate_by_name = True
        arbitrary_types_allowed = True
        json_encoders = {ObjectId: str}


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: UserResponse


class TokenData(BaseModel):
    user_id: Optional[str] = None
    email: Optional[str] = None
