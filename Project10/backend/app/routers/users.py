from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import Optional
from app.database import get_db
from app.models.models import User, Enrollment, Favorite, Course
from app.schemas.schemas import (
    UserUpdate, UserResponse, EnrollmentResponse, 
    FavoriteResponse, ApiResponse, PaginatedResponse
)
from app.utils.auth import get_current_active_user
from math import ceil

router = APIRouter(prefix="/api/users", tags=["用户"])


@router.put("/profile", response_model=ApiResponse)
def update_profile(
    update_data: UserUpdate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    update_dict = update_data.model_dump(exclude_unset=True)
    
    for key, value in update_dict.items():
        setattr(current_user, key, value)
    
    db.commit()
    db.refresh(current_user)
    
    return ApiResponse(
        code=200,
        message="更新成功",
        data={"user": UserResponse.model_validate(current_user).model_dump()}
    )


@router.get("/enrollments", response_model=PaginatedResponse)
def get_my_enrollments(
    page: int = 1,
    page_size: int = 10,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    query = db.query(Enrollment).filter(
        Enrollment.student_id == current_user.id
    ).order_by(Enrollment.enrolled_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    enrollments = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for enrollment in enrollments:
        enrollment_dict = EnrollmentResponse.model_validate(enrollment).model_dump()
        course = db.query(Course).filter(Course.id == enrollment.course_id).first()
        if course:
            enrollment_dict["course"] = {
                "id": course.id,
                "title": course.title,
                "cover_image": course.cover_image,
                "teacher_id": course.teacher_id
            }
        items.append(enrollment_dict)
    
    return PaginatedResponse(
        data={
            "items": items,
            "total": total,
            "page": page,
            "page_size": page_size,
            "total_pages": total_pages
        }
    )


@router.get("/favorites", response_model=PaginatedResponse)
def get_my_favorites(
    page: int = 1,
    page_size: int = 10,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    query = db.query(Favorite).filter(
        Favorite.user_id == current_user.id
    ).order_by(Favorite.created_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    favorites = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for fav in favorites:
        fav_dict = FavoriteResponse.model_validate(fav).model_dump()
        course = db.query(Course).filter(Course.id == fav.course_id).first()
        if course:
            fav_dict["course"] = {
                "id": course.id,
                "title": course.title,
                "cover_image": course.cover_image,
                "teacher_id": course.teacher_id,
                "price": course.price,
                "category": course.category
            }
        items.append(fav_dict)
    
    return PaginatedResponse(
        data={
            "items": items,
            "total": total,
            "page": page,
            "page_size": page_size,
            "total_pages": total_pages
        }
    )
