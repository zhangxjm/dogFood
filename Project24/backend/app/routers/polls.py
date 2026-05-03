from datetime import datetime
from typing import Optional, List

from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy import func
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Poll, Candidate, Vote, User
from app.schemas.poll import (
    PollCreate, PollUpdate, PollResponse, PollWithCandidates,
    CandidateCreate, CandidateUpdate, CandidateResponse
)
from app.services.auth import get_current_admin
from app.utils.response import success_response, error_response, ResponseModel

router = APIRouter(prefix="/api/polls", tags=["投票活动"])


@router.get("", response_model=ResponseModel)
def get_polls(
    is_active: Optional[bool] = None,
    include_expired: bool = False,
    db: Session = Depends(get_db)
):
    query = db.query(Poll)
    
    if is_active is not None:
        query = query.filter(Poll.is_active == is_active)
    
    if not include_expired:
        query = query.filter(Poll.end_time > datetime.now())
    
    polls = query.order_by(Poll.created_at.desc()).all()
    
    poll_list = []
    for poll in polls:
        total_votes = db.query(func.count(Vote.id)).filter(Vote.poll_id == poll.id).scalar() or 0
        poll_list.append({
            "id": poll.id,
            "title": poll.title,
            "description": poll.description,
            "start_time": poll.start_time.isoformat(),
            "end_time": poll.end_time.isoformat(),
            "is_active": poll.is_active,
            "max_votes_per_user": poll.max_votes_per_user,
            "created_at": poll.created_at.isoformat(),
            "total_votes": total_votes,
            "is_voting_open": (
                poll.is_active and 
                poll.start_time <= datetime.now() <= poll.end_time
            )
        })
    
    return success_response(data=poll_list)


@router.get("/{poll_id}", response_model=ResponseModel)
def get_poll(poll_id: int, db: Session = Depends(get_db)):
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    candidates = []
    for cand in poll.candidates:
        vote_count = db.query(func.count(Vote.id)).filter(
            Vote.candidate_id == cand.id
        ).scalar() or 0
        candidates.append({
            "id": cand.id,
            "name": cand.name,
            "description": cand.description,
            "avatar_url": cand.avatar_url,
            "vote_count": vote_count
        })
    
    total_votes = db.query(func.count(Vote.id)).filter(Vote.poll_id == poll.id).scalar() or 0
    
    return success_response(data={
        "id": poll.id,
        "title": poll.title,
        "description": poll.description,
        "start_time": poll.start_time.isoformat(),
        "end_time": poll.end_time.isoformat(),
        "is_active": poll.is_active,
        "max_votes_per_user": poll.max_votes_per_user,
        "created_at": poll.created_at.isoformat(),
        "total_votes": total_votes,
        "is_voting_open": (
            poll.is_active and 
            poll.start_time <= datetime.now() <= poll.end_time
        ),
        "candidates": candidates
    })


@router.post("", response_model=ResponseModel)
def create_poll(
    poll_data: PollCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    poll = Poll(
        title=poll_data.title,
        description=poll_data.description,
        start_time=poll_data.start_time,
        end_time=poll_data.end_time,
        is_active=poll_data.is_active,
        max_votes_per_user=poll_data.max_votes_per_user
    )
    db.add(poll)
    db.flush()
    
    if poll_data.candidates:
        for cand_data in poll_data.candidates:
            candidate = Candidate(
                poll_id=poll.id,
                name=cand_data.name,
                description=cand_data.description,
                avatar_url=cand_data.avatar_url
            )
            db.add(candidate)
    
    db.commit()
    db.refresh(poll)
    
    return success_response(data={"id": poll.id, "message": "投票活动创建成功"})


@router.put("/{poll_id}", response_model=ResponseModel)
def update_poll(
    poll_id: int,
    poll_data: PollUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    update_data = poll_data.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(poll, key, value)
    
    db.commit()
    
    return success_response(message="投票活动更新成功")


@router.delete("/{poll_id}", response_model=ResponseModel)
def delete_poll(
    poll_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    db.delete(poll)
    db.commit()
    
    return success_response(message="投票活动删除成功")


@router.post("/{poll_id}/candidates", response_model=ResponseModel)
def add_candidate(
    poll_id: int,
    candidate_data: CandidateCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    candidate = Candidate(
        poll_id=poll_id,
        name=candidate_data.name,
        description=candidate_data.description,
        avatar_url=candidate_data.avatar_url
    )
    db.add(candidate)
    db.commit()
    db.refresh(candidate)
    
    return success_response(data={"id": candidate.id, "message": "候选人添加成功"})


@router.put("/candidates/{candidate_id}", response_model=ResponseModel)
def update_candidate(
    candidate_id: int,
    candidate_data: CandidateUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    candidate = db.query(Candidate).filter(Candidate.id == candidate_id).first()
    if not candidate:
        raise HTTPException(status_code=404, detail="候选人不存在")
    
    update_data = candidate_data.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(candidate, key, value)
    
    db.commit()
    
    return success_response(message="候选人更新成功")


@router.delete("/candidates/{candidate_id}", response_model=ResponseModel)
def delete_candidate(
    candidate_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_admin)
):
    candidate = db.query(Candidate).filter(Candidate.id == candidate_id).first()
    if not candidate:
        raise HTTPException(status_code=404, detail="候选人不存在")
    
    db.delete(candidate)
    db.commit()
    
    return success_response(message="候选人删除成功")


@router.get("/{poll_id}/stats", response_model=ResponseModel)
def get_poll_stats(poll_id: int, db: Session = Depends(get_db)):
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    total_votes = db.query(func.count(Vote.id)).filter(Vote.poll_id == poll_id).scalar() or 0
    
    candidates_stats = []
    for cand in poll.candidates:
        vote_count = db.query(func.count(Vote.id)).filter(
            Vote.candidate_id == cand.id
        ).scalar() or 0
        percentage = (vote_count / total_votes * 100) if total_votes > 0 else 0
        candidates_stats.append({
            "id": cand.id,
            "name": cand.name,
            "vote_count": vote_count,
            "percentage": round(percentage, 2)
        })
    
    candidates_stats.sort(key=lambda x: x["vote_count"], reverse=True)
    
    return success_response(data={
        "poll_id": poll_id,
        "poll_title": poll.title,
        "total_votes": total_votes,
        "candidates_stats": candidates_stats
    })
