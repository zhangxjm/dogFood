from pydantic import BaseModel, EmailStr, Field
from typing import Optional, List
from datetime import datetime


class UserBase(BaseModel):
    username: str = Field(..., min_length=3, max_length=50)
    email: EmailStr


class UserCreate(UserBase):
    password: str = Field(..., min_length=6)
    role: str = "student"


class UserLogin(BaseModel):
    username: str
    password: str


class UserUpdate(BaseModel):
    email: Optional[EmailStr] = None
    avatar: Optional[str] = None
    bio: Optional[str] = None


class UserResponse(UserBase):
    id: int
    role: str
    avatar: Optional[str]
    bio: Optional[str]
    is_active: bool
    created_at: datetime

    class Config:
        from_attributes = True


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: UserResponse


class TokenData(BaseModel):
    user_id: Optional[int] = None
    username: Optional[str] = None
    role: Optional[str] = None


class ChapterBase(BaseModel):
    title: str
    description: Optional[str] = None
    video_url: Optional[str] = None
    position: int = 0
    is_free: bool = False
    duration: int = 0


class ChapterCreate(ChapterBase):
    pass


class ChapterResponse(ChapterBase):
    id: int
    course_id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class ChapterWithProgress(ChapterResponse):
    is_completed: bool = False


class CourseBase(BaseModel):
    title: str
    description: Optional[str] = None
    cover_image: Optional[str] = None
    price: float = 0.0
    category: Optional[str] = None


class CourseCreate(CourseBase):
    pass


class CourseUpdate(CourseBase):
    is_published: Optional[bool] = None


class CourseResponse(CourseBase):
    id: int
    teacher_id: int
    is_published: bool
    view_count: int
    created_at: datetime
    updated_at: Optional[datetime] = None
    teacher: Optional[UserResponse] = None

    class Config:
        from_attributes = True


class CourseDetailResponse(CourseResponse):
    chapters: List[ChapterResponse] = []


class CourseWithEnrollment(CourseResponse):
    is_enrolled: bool = False
    is_favorite: bool = False
    progress_percent: float = 0.0


class CommentBase(BaseModel):
    content: str
    rating: Optional[int] = None


class CommentCreate(CommentBase):
    course_id: int
    parent_id: Optional[int] = None


class CommentResponse(CommentBase):
    id: int
    user_id: int
    course_id: int
    parent_id: Optional[int]
    is_approved: bool
    created_at: datetime
    user: Optional[UserResponse] = None

    class Config:
        from_attributes = True


class EnrollmentBase(BaseModel):
    course_id: int


class EnrollmentResponse(BaseModel):
    id: int
    student_id: int
    course_id: int
    enrolled_at: datetime
    progress_percent: float
    completed_at: Optional[datetime]
    course: Optional[CourseResponse] = None

    class Config:
        from_attributes = True


class FavoriteResponse(BaseModel):
    id: int
    user_id: int
    course_id: int
    created_at: datetime
    course: Optional[CourseResponse] = None

    class Config:
        from_attributes = True


class ProgressCreate(BaseModel):
    chapter_id: int
    is_completed: bool = False
    watch_duration: int = 0


class ProgressResponse(BaseModel):
    id: int
    user_id: int
    chapter_id: int
    course_id: int
    is_completed: bool
    watch_duration: int
    updated_at: datetime

    class Config:
        from_attributes = True


class ApiResponse(BaseModel):
    code: int = 200
    message: str = "success"
    data: Optional[dict] = None


class PaginatedResponse(BaseModel):
    code: int = 200
    message: str = "success"
    data: dict = {
        "items": [],
        "total": 0,
        "page": 1,
        "page_size": 10,
        "total_pages": 0
    }
