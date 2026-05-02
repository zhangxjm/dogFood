from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import Optional
from app.database import get_db
from app.models.models import Course, Chapter, Enrollment, Favorite, Comment, Progress, User
from app.schemas.schemas import (
    CourseResponse, CourseDetailResponse, CourseWithEnrollment,
    ChapterResponse, ChapterWithProgress, CommentCreate, CommentResponse,
    ProgressCreate, ProgressResponse, ApiResponse, PaginatedResponse
)
from app.utils.auth import get_current_active_user
from math import ceil

router = APIRouter(prefix="/api/courses", tags=["课程"])


@router.get("", response_model=PaginatedResponse)
def get_courses(
    page: int = 1,
    page_size: int = 12,
    category: Optional[str] = None,
    keyword: Optional[str] = None,
    teacher_id: Optional[int] = None,
    db: Session = Depends(get_db)
):
    query = db.query(Course).filter(Course.is_published == True)
    
    if category:
        query = query.filter(Course.category == category)
    if keyword:
        query = query.filter(Course.title.contains(keyword))
    if teacher_id:
        query = query.filter(Course.teacher_id == teacher_id)
    
    query = query.order_by(Course.created_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    courses = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for course in courses:
        course_dict = CourseResponse.model_validate(course).model_dump()
        teacher = db.query(User).filter(User.id == course.teacher_id).first()
        if teacher:
            course_dict["teacher"] = {
                "id": teacher.id,
                "username": teacher.username,
                "avatar": teacher.avatar
            }
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


@router.get("/{course_id}", response_model=ApiResponse)
def get_course_detail(
    course_id: int,
    current_user: Optional[User] = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在")
    
    course.view_count += 1
    db.commit()
    db.refresh(course)
    
    course_dict = CourseResponse.model_validate(course).model_dump()
    
    chapters = db.query(Chapter).filter(Chapter.course_id == course_id).order_by(Chapter.position).all()
    chapters_list = []
    
    if current_user:
        enrollment = db.query(Enrollment).filter(
            Enrollment.student_id == current_user.id,
            Enrollment.course_id == course_id
        ).first()
        
        favorite = db.query(Favorite).filter(
            Favorite.user_id == current_user.id,
            Favorite.course_id == course_id
        ).first()
        
        course_dict["is_enrolled"] = enrollment is not None
        course_dict["is_favorite"] = favorite is not None
        course_dict["progress_percent"] = enrollment.progress_percent if enrollment else 0.0
        
        for chapter in chapters:
            chapter_dict = ChapterResponse.model_validate(chapter).model_dump()
            progress = db.query(Progress).filter(
                Progress.user_id == current_user.id,
                Progress.chapter_id == chapter.id
            ).first()
            chapter_dict["is_completed"] = progress.is_completed if progress else False
            chapters_list.append(chapter_dict)
    else:
        course_dict["is_enrolled"] = False
        course_dict["is_favorite"] = False
        course_dict["progress_percent"] = 0.0
        chapters_list = [ChapterResponse.model_validate(ch).model_dump() for ch in chapters]
    
    teacher = db.query(User).filter(User.id == course.teacher_id).first()
    if teacher:
        course_dict["teacher"] = {
            "id": teacher.id,
            "username": teacher.username,
            "avatar": teacher.avatar,
            "bio": teacher.bio
        }
    
    course_dict["chapters"] = chapters_list
    
    return ApiResponse(
        code=200,
        message="success",
        data={"course": course_dict}
    )


@router.post("/enroll/{course_id}", response_model=ApiResponse)
def enroll_course(
    course_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在")
    
    if not course.is_published:
        raise HTTPException(status_code=400, detail="课程未发布")
    
    existing_enrollment = db.query(Enrollment).filter(
        Enrollment.student_id == current_user.id,
        Enrollment.course_id == course_id
    ).first()
    
    if existing_enrollment:
        raise HTTPException(status_code=400, detail="已报名该课程")
    
    enrollment = Enrollment(
        student_id=current_user.id,
        course_id=course_id
    )
    
    db.add(enrollment)
    db.commit()
    db.refresh(enrollment)
    
    return ApiResponse(
        code=200,
        message="报名成功",
        data={"enrollment_id": enrollment.id}
    )


@router.post("/favorite/{course_id}", response_model=ApiResponse)
def toggle_favorite(
    course_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(Course.id == course_id).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在")
    
    favorite = db.query(Favorite).filter(
        Favorite.user_id == current_user.id,
        Favorite.course_id == course_id
    ).first()
    
    if favorite:
        db.delete(favorite)
        db.commit()
        return ApiResponse(code=200, message="已取消收藏")
    else:
        new_favorite = Favorite(
            user_id=current_user.id,
            course_id=course_id
        )
        db.add(new_favorite)
        db.commit()
        return ApiResponse(code=200, message="收藏成功")


@router.get("/chapters/{chapter_id}", response_model=ApiResponse)
def get_chapter_detail(
    chapter_id: int,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    chapter = db.query(Chapter).filter(Chapter.id == chapter_id).first()
    
    if not chapter:
        raise HTTPException(status_code=404, detail="章节不存在")
    
    course = db.query(Course).filter(Course.id == chapter.course_id).first()
    
    if not chapter.is_free:
        enrollment = db.query(Enrollment).filter(
            Enrollment.student_id == current_user.id,
            Enrollment.course_id == chapter.course_id
        ).first()
        
        if not enrollment and current_user.id != course.teacher_id:
            raise HTTPException(status_code=403, detail="请先报名该课程")
    
    progress = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.chapter_id == chapter_id
    ).first()
    
    chapter_dict = ChapterResponse.model_validate(chapter).model_dump()
    chapter_dict["is_completed"] = progress.is_completed if progress else False
    
    return ApiResponse(
        code=200,
        message="success",
        data={"chapter": chapter_dict}
    )


@router.post("/progress", response_model=ApiResponse)
def update_progress(
    progress_data: ProgressCreate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    chapter = db.query(Chapter).filter(Chapter.id == progress_data.chapter_id).first()
    
    if not chapter:
        raise HTTPException(status_code=404, detail="章节不存在")
    
    progress = db.query(Progress).filter(
        Progress.user_id == current_user.id,
        Progress.chapter_id == progress_data.chapter_id
    ).first()
    
    if progress:
        progress.is_completed = progress_data.is_completed
        progress.watch_duration = progress_data.watch_duration
    else:
        progress = Progress(
            user_id=current_user.id,
            chapter_id=progress_data.chapter_id,
            course_id=chapter.course_id,
            is_completed=progress_data.is_completed,
            watch_duration=progress_data.watch_duration
        )
        db.add(progress)
    
    db.commit()
    
    enrollment = db.query(Enrollment).filter(
        Enrollment.student_id == current_user.id,
        Enrollment.course_id == chapter.course_id
    ).first()
    
    if enrollment:
        all_chapters = db.query(Chapter).filter(
            Chapter.course_id == chapter.course_id
        ).all()
        
        completed_chapters = db.query(Progress).filter(
            Progress.user_id == current_user.id,
            Progress.course_id == chapter.course_id,
            Progress.is_completed == True
        ).count()
        
        if all_chapters:
            enrollment.progress_percent = (completed_chapters / len(all_chapters)) * 100
            db.commit()
    
    db.refresh(progress)
    
    return ApiResponse(
        code=200,
        message="进度更新成功",
        data={"progress": ProgressResponse.model_validate(progress).model_dump()}
    )


@router.get("/{course_id}/comments", response_model=PaginatedResponse)
def get_course_comments(
    course_id: int,
    page: int = 1,
    page_size: int = 10,
    db: Session = Depends(get_db)
):
    query = db.query(Comment).filter(
        Comment.course_id == course_id,
        Comment.is_approved == True,
        Comment.parent_id == None
    ).order_by(Comment.created_at.desc())
    
    total = query.count()
    total_pages = ceil(total / page_size) if page_size > 0 else 0
    
    comments = query.offset((page - 1) * page_size).limit(page_size).all()
    
    items = []
    for comment in comments:
        comment_dict = CommentResponse.model_validate(comment).model_dump()
        user = db.query(User).filter(User.id == comment.user_id).first()
        if user:
            comment_dict["user"] = {
                "id": user.id,
                "username": user.username,
                "avatar": user.avatar
            }
        items.append(comment_dict)
    
    return PaginatedResponse(
        data={
            "items": items,
            "total": total,
            "page": page,
            "page_size": page_size,
            "total_pages": total_pages
        }
    )


@router.post("/comments", response_model=ApiResponse)
def create_comment(
    comment_data: CommentCreate,
    current_user: User = Depends(get_current_active_user),
    db: Session = Depends(get_db)
):
    course = db.query(Course).filter(Course.id == comment_data.course_id).first()
    
    if not course:
        raise HTTPException(status_code=404, detail="课程不存在")
    
    comment = Comment(
        content=comment_data.content,
        rating=comment_data.rating,
        user_id=current_user.id,
        course_id=comment_data.course_id,
        parent_id=comment_data.parent_id
    )
    
    db.add(comment)
    db.commit()
    db.refresh(comment)
    
    return ApiResponse(
        code=200,
        message="评论发布成功",
        data={"comment_id": comment.id}
    )
