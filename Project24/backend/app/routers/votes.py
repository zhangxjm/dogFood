from datetime import datetime
from typing import Optional, List

from fastapi import APIRouter, Depends, HTTPException, Request, status
from sqlalchemy import func
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Poll, Candidate, Vote
from app.schemas.vote import VoteCreate
from app.utils.response import success_response, error_response, ResponseModel

router = APIRouter(prefix="/api/votes", tags=["投票"])


def get_voter_identifier(request: Request) -> str:
    x_forwarded_for = request.headers.get("X-Forwarded-For")
    if x_forwarded_for:
        ip = x_forwarded_for.split(",")[0].strip()
    else:
        ip = request.client.host if request.client else "unknown"
    return ip


@router.post("", response_model=ResponseModel)
def cast_vote(
    request: Request,
    vote_data: VoteCreate,
    db: Session = Depends(get_db)
):
    voter_ip = get_voter_identifier(request)
    
    poll = db.query(Poll).filter(Poll.id == vote_data.poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    if not poll.is_active:
        raise HTTPException(status_code=400, detail="投票活动已关闭")
    
    now = datetime.now()
    if now < poll.start_time:
        raise HTTPException(status_code=400, detail="投票活动尚未开始")
    if now > poll.end_time:
        raise HTTPException(status_code=400, detail="投票活动已结束")
    
    existing_votes = db.query(Vote).filter(
        Vote.poll_id == vote_data.poll_id,
        Vote.voter_ip == voter_ip
    ).count()
    
    if existing_votes >= poll.max_votes_per_user:
        raise HTTPException(
            status_code=400,
            detail=f"您已投过票了，每人最多可投{poll.max_votes_per_user}票"
        )
    
    candidate_ids = vote_data.candidate_ids
    if len(candidate_ids) > (poll.max_votes_per_user - existing_votes):
        raise HTTPException(
            status_code=400,
            detail=f"投票数量超过限制，您还可以投{poll.max_votes_per_user - existing_votes}票"
        )
    
    for candidate_id in candidate_ids:
        candidate = db.query(Candidate).filter(
            Candidate.id == candidate_id,
            Candidate.poll_id == vote_data.poll_id
        ).first()
        if not candidate:
            raise HTTPException(status_code=404, detail=f"候选人 {candidate_id} 不存在")
    
    for candidate_id in candidate_ids:
        vote = Vote(
            poll_id=vote_data.poll_id,
            candidate_id=candidate_id,
            voter_ip=voter_ip
        )
        db.add(vote)
    
    db.commit()
    
    return success_response(message="投票成功", data={"voted_candidates": candidate_ids})


@router.get("/check", response_model=ResponseModel)
def check_vote_status(
    request: Request,
    poll_id: int,
    db: Session = Depends(get_db)
):
    voter_ip = get_voter_identifier(request)
    
    poll = db.query(Poll).filter(Poll.id == poll_id).first()
    if not poll:
        raise HTTPException(status_code=404, detail="投票活动不存在")
    
    voted_candidates = db.query(Candidate).join(Vote).filter(
        Vote.poll_id == poll_id,
        Vote.voter_ip == voter_ip
    ).all()
    
    vote_count = len(voted_candidates)
    
    return success_response(data={
        "poll_id": poll_id,
        "has_voted": vote_count > 0,
        "vote_count": vote_count,
        "max_votes": poll.max_votes_per_user,
        "voted_candidates": [
            {"id": c.id, "name": c.name} for c in voted_candidates
        ],
        "can_vote_more": vote_count < poll.max_votes_per_user
    })
