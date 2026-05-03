from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime


class CandidateBase(BaseModel):
    name: str
    description: Optional[str] = None
    avatar_url: Optional[str] = None


class CandidateCreate(CandidateBase):
    pass


class CandidateUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    avatar_url: Optional[str] = None


class CandidateResponse(CandidateBase):
    id: int
    poll_id: int
    created_at: datetime
    vote_count: Optional[int] = 0

    class Config:
        from_attributes = True


class PollBase(BaseModel):
    title: str
    description: Optional[str] = None
    start_time: datetime
    end_time: datetime
    is_active: bool = True
    max_votes_per_user: int = 1


class PollCreate(PollBase):
    candidates: Optional[List[CandidateCreate]] = None


class PollUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    start_time: Optional[datetime] = None
    end_time: Optional[datetime] = None
    is_active: Optional[bool] = None
    max_votes_per_user: Optional[int] = None


class PollResponse(PollBase):
    id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class PollWithCandidates(PollResponse):
    candidates: List[CandidateResponse] = []


class PollStats(BaseModel):
    poll_id: int
    total_votes: int
    candidates_stats: List[dict]
