from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import Optional, List
from app.database import get_db
from app.models.models import User, Course, Chapter, Enrollment
from app.schemas.schemas import (
    CourseCreate, CourseUpdate, CourseResponse,
    ChapterCreate, ChapterResponse,
    ApiResponse, PaginatedResponse
)
from app.utils.auth import get_teacher_user
from math import ceil

router = APIRouter(prefix="/api/teacher", tags=["教师后台"])


@router.get("/courses", response_model=PaginatedResponse)
def get_my_courses(
    page: int = 1,
    page_size: int = 10,
    keyword: Optional[str] = None,
    is_published: Optional[bool] = None,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    query = db.query(Course).filter(Course.teacher_id == current_user.id)
    
    if keyword:
        query = query.filter(Course.title.contains(keyword))
    if is_published is not None:
        query = query.filter(Course.is_published == is_published)
    
    query = query.order_by(Course.created_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    courses = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for course in courses:
        course_dict = CourseResponse.model_validate(course).model_dump()
        enroll_count = db.query(Enrollment).filter(
            Enrollment.course_id == course.id
        ).count()
        course_dict["enroll_count"] = enroll_count
        items.append(course_dict)
    
    return PaginatedResponse(
        data={
            "items": items,
            "total": total,
            "page": page,
            "page_size": page_size,
            "total_pages": total_pages
        }
    )


@router.post("/courses", response_model=ApiResponse)
def create_course(
    course_data: CourseCreate,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = Course(
        title=course_data.title,
        description=course_data.description,
        cover_image=course_data.cover_image,
        price=course_data.price,
        category=course_data.category,
        teacher_id=current_user.id,
        is_published=False
    )
    
    db.add(course)
    db.commit()
    db.refresh(course)
    
    return ApiResponse(
        code=200,
        message="课程创建成功",
        data={"course": CourseResponse.model_validate(course).model_dump()}
    )


@router.get("/courses/{course_id}", response_model=ApiResponse)
def get_course_detail(
    course_id: int,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    chapters = db.query(Chapter).filter(Chapter.course_id == course_id
    ).order_by(Chapter.position).all()
    
    course_dict = CourseResponse.model_validate(course).model_dump()
    course_dict["chapters"] = [
        ChapterResponse.model_validate(ch).model_dump() for ch in chapters
    ]
    
    return ApiResponse(
        code=200,
        message="success",
        data={"course": course_dict}
    )


@router.put("/courses/{course_id}", response_model=ApiResponse)
def update_course(
    course_id: int,
    course_data: CourseUpdate,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    update_dict = course_data.model_dump(exclude_unset=True)
    for key, value in update_dict.items():
        setattr(course, key, value)
    
    db.commit()
    db.refresh(course)
    
    return ApiResponse(
        code=200,
        message="课程更新成功",
        data={"course": CourseResponse.model_validate(course).model_dump()}
    )


@router.delete("/courses/{course_id}", response_model=ApiResponse)
def delete_course(
    course_id: int,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    db.delete(course)
    db.commit()
    
    return ApiResponse(code=200, message="课程删除成功")


@router.post("/courses/{course_id}/publish", response_model=ApiResponse)
def toggle_publish(
    course_id: int,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    chapters_count = db.query(Chapter).filter(Chapter.course_id == course_id).count()
    
    if not course.is_published and chapters_count == 0:
        raise HTTPException(
            status_code=400,
            detail="请至少添加一个章节后再发布"
        )
    
    course.is_published = not course.is_published
    db.commit()
    db.refresh(course)
    
    return ApiResponse(
        code=200,
        message="课程已" + ("发布" if course.is_published else "下架"),
        data={"is_published": course.is_published}
    )


@router.get("/courses/{course_id}/chapters", response_model=ApiResponse)
def get_course_chapters(
    course_id: int,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    chapters = db.query(Chapter).filter(
        Chapter.course_id == course_id
    ).order_by(Chapter.position).all()
    
    return ApiResponse(
        code=200,
        message="success",
        data={
            "chapters": [
                ChapterResponse.model_validate(ch).model_dump() for ch in chapters
            ]
        }
    )


@router.post("/courses/{course_id}/chapters", response_model=ApiResponse)
def create_chapter(
    course_id: int,
    chapter_data: ChapterCreate,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    chapter = Chapter(
        course_id=course_id,
        title=chapter_data.title,
        description=chapter_data.description,
        video_url=chapter_data.video_url,
        position=chapter_data.position,
        is_free=chapter_data.is_free,
        duration=chapter_data.duration
    )
    
    db.add(chapter)
    db.commit()
    db.refresh(chapter)
    
    return ApiResponse(
        code=200,
        message="章节创建成功",
        data={"chapter": ChapterResponse.model_validate(chapter).model_dump()}
    )


@router.put("/chapters/{chapter_id}", response_model=ApiResponse)
def update_chapter(
    chapter_id: int,
    chapter_data: ChapterCreate,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    chapter = db.query(Chapter).filter(Chapter.id == chapter_id).first()
    
    if not chapter:
        raise HTTPException(status_code=404, detail="章节不存在")
    
    course = db.query(Course).filter(
        Course.id == chapter.course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=403, detail="无权限修改此章节")
    
    update_dict = chapter_data.model_dump(exclude_unset=True)
    for key, value in update_dict.items():
        setattr(chapter, key, value)
    
    db.commit()
    db.refresh(chapter)
    
    return ApiResponse(
        code=200,
        message="章节更新成功",
        data={"chapter": ChapterResponse.model_validate(chapter).model_dump()}
    )


@router.delete("/chapters/{chapter_id}", response_model=ApiResponse)
def delete_chapter(
    chapter_id: int,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    chapter = db.query(Chapter).filter(Chapter.id == chapter_id).first()
    
    if not chapter:
        raise HTTPException(status_code=404, detail="章节不存在")
    
    course = db.query(Course).filter(
        Course.id == chapter.course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=403, detail="无权限删除此章节")
    
    db.delete(chapter)
    db.commit()
    
    return ApiResponse(code=200, message="章节删除成功")


@router.get("/courses/{course_id}/students", response_model=PaginatedResponse)
def get_course_students(
    course_id: int,
    page: int = 1,
    page_size: int = 10,
    current_user: User = Depends(get_teacher_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(
        Course.id == course_id,
        Course.teacher_id == current_user.id
    ).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在或无权限")
    
    query = db.query(Enrollment).filter(
        Enrollment.course_id == course_id
    ).order_by(Enrollment.enrolled_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    enrollments = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for enrollment in enrollments:
        student = db.query(User).filter(User.id == enrollment.student_id).first()
        if student:
            items.append({
                "id": enrollment.id,
                "student_id": student.id,
                "username": student.username,
                "email": student.email,
                "avatar": student.avatar,
                "progress_percent": enrollment.progress_percent,
                "enrolled_at": enrollment.enrolled_at.isoformat() if enrollment.enrolled_at else None
            })
    
    return PaginatedResponse(
        data={
            "items": items,
            "total": total,
            "page": page,
            "page_size": page_size,
            "total_pages": total_pages
        }
    )
