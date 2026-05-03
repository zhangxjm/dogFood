from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class VoteCreate(BaseModel):
    poll_id: int
    candidate_ids: list[int]


class VoteResponse(BaseModel):
    id: int
    poll_id: int
    candidate_id: int
    voter_ip: str
    created_at: datetime

    class Config:
        from_attributes = True
