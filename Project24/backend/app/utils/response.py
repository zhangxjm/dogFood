from typing import Any

from pydantic import BaseModel


class ResponseModel(BaseModel):
    code: int = 200
    message: str = "success"
    data: Any = None


class PaginatedResponse(BaseModel):
    code: int = 200
    message: str = "success"
    data: Any = None
    total: int = 0
    page: int = 1
    page_size: int = 10


def success_response(data: Any = None, message: str = "success") -> ResponseModel:
    return ResponseModel(code=200, message=message, data=data)


def error_response(code: int = 400, message: str = "error") -> ResponseModel:
    return ResponseModel(code=code, message=message, data=None)


def paginated_response(
    data: Any, 
    total: int, 
    page: int, 
    page_size: int, 
    message: str = "success"
) -> PaginatedResponse:
    return PaginatedResponse(
        code=200,
        message=message,
        data=data,
        total=total,
        page=page,
        page_size=page_size
    )
