import os
import uuid
from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status, UploadFile, File
from fastapi.responses import FileResponse
from typing import List
import aiofiles

from app.auth import get_current_user
from app.config import get_settings
from app.models import UserResponse

router = APIRouter(prefix="/api/upload", tags=["上传"])

ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"}


@router.post("/image")
async def upload_image(
    file: UploadFile = File(...),
    current_user: UserResponse = Depends(get_current_user)
):
    settings = get_settings()
    
    if file.content_type not in ALLOWED_IMAGE_TYPES:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid file type. Allowed types: {', '.join(ALLOWED_IMAGE_TYPES)}"
        )
    
    upload_dir = settings.upload_dir
    os.makedirs(upload_dir, exist_ok=True)
    
    file_ext = os.path.splitext(file.filename)[1].lower()
    if not file_ext:
        if file.content_type == "image/jpeg":
            file_ext = ".jpg"
        elif file.content_type == "image/png":
            file_ext = ".png"
        elif file.content_type == "image/gif":
            file_ext = ".gif"
        elif file.content_type == "image/webp":
            file_ext = ".webp"
    
    unique_filename = f"{datetime.utcnow().strftime('%Y%m%d%H%M%S')}_{uuid.uuid4().hex[:8]}{file_ext}"
    file_path = os.path.join(upload_dir, unique_filename)
    
    content = await file.read()
    if len(content) > settings.max_file_size:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"File too large. Maximum size: {settings.max_file_size / 1024 / 1024}MB"
        )
    
    async with aiofiles.open(file_path, 'wb') as f:
        await f.write(content)
    
    return {
        "filename": unique_filename,
        "url": f"/uploads/{unique_filename}",
        "content_type": file.content_type
    }


@router.post("/images")
async def upload_multiple_images(
    files: List[UploadFile] = File(...),
    current_user: UserResponse = Depends(get_current_user)
):
    results = []
    
    for file in files:
        try:
            if file.content_type not in ALLOWED_IMAGE_TYPES:
                results.append({
                    "filename": file.filename,
                    "success": False,
                    "error": f"Invalid file type: {file.content_type}"
                })
                continue
            
            settings = get_settings()
            upload_dir = settings.upload_dir
            os.makedirs(upload_dir, exist_ok=True)
            
            file_ext = os.path.splitext(file.filename)[1].lower()
            if not file_ext:
                if file.content_type == "image/jpeg":
                    file_ext = ".jpg"
                elif file.content_type == "image/png":
                    file_ext = ".png"
                elif file.content_type == "image/gif":
                    file_ext = ".gif"
                elif file.content_type == "image/webp":
                    file_ext = ".webp"
            
            unique_filename = f"{datetime.utcnow().strftime('%Y%m%d%H%M%S')}_{uuid.uuid4().hex[:8]}{file_ext}"
            file_path = os.path.join(upload_dir, unique_filename)
            
            content = await file.read()
            if len(content) > settings.max_file_size:
                results.append({
                    "filename": file.filename,
                    "success": False,
                    "error": f"File too large (max: {settings.max_file_size / 1024 / 1024}MB)"
                })
                continue
            
            async with aiofiles.open(file_path, 'wb') as f:
                await f.write(content)
            
            results.append({
                "filename": unique_filename,
                "original_name": file.filename,
                "url": f"/uploads/{unique_filename}",
                "content_type": file.content_type,
                "success": True
            })
        except Exception as e:
            results.append({
                "filename": file.filename,
                "success": False,
                "error": str(e)
            })
    
    return {"results": results}
