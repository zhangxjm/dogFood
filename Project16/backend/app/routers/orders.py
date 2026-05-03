from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, status, Query
from bson import ObjectId
from motor.motor_asyncio import AsyncIOMotorDatabase
from typing import List, Optional
import uuid

from app.auth import get_current_user
from app.database import get_database
from app.models import (
    OrderCreate,
    OrderResponse,
    OrderItem,
    UserResponse
)

router = APIRouter(prefix="/api/orders", tags=["订单"])


def generate_order_number() -> str:
    timestamp = datetime.utcnow().strftime("%Y%m%d%H%M%S")
    random_suffix = uuid.uuid4().hex[:6].upper()
    return f"ORD-{timestamp}-{random_suffix}"


@router.get("/", response_model=List[OrderResponse])
async def get_my_orders(
    status: Optional[str] = None,
    limit: int = Query(default=20, ge=1, le=100),
    offset: int = Query(default=0, ge=0),
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    query = {"user_id": ObjectId(current_user.id)}
    
    if status:
        query["status"] = status
    
    cursor = db.orders.find(query).sort("created_at", -1).skip(offset).limit(limit)
    
    orders = []
    async for order in cursor:
        orders.append(OrderResponse(**order))
    
    return orders


@router.get("/{order_id}", response_model=OrderResponse)
async def get_order(
    order_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(order_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid order ID"
        )
    
    order = await db.orders.find_one({
        "_id": ObjectId(order_id),
        "user_id": ObjectId(current_user.id)
    })
    
    if not order:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Order not found"
        )
    
    return OrderResponse(**order)


@router.post("/", response_model=OrderResponse)
async def create_order(
    order_data: OrderCreate,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not order_data.work_ids:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No works specified"
        )
    
    items = []
    total_amount = 0.0
    
    for work_id in order_data.work_ids:
        if not ObjectId.is_valid(work_id):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Invalid work ID: {work_id}"
            )
        
        work = await db.works.find_one({"_id": ObjectId(work_id), "is_active": True})
        if not work:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"Work not found: {work_id}"
            )
        
        item = OrderItem(
            work_id=ObjectId(work_id),
            title=work["title"],
            price=work["price"],
            image_url=work["images"][0] if work["images"] else None
        )
        items.append(item)
        total_amount += work["price"]
    
    order_dict = {
        "user_id": ObjectId(current_user.id),
        "order_number": generate_order_number(),
        "items": [item.model_dump(by_alias=True) for item in items],
        "total_amount": total_amount,
        "status": "pending",
        "created_at": datetime.utcnow(),
        "updated_at": None
    }
    
    result = await db.orders.insert_one(order_dict)
    order_dict["_id"] = result.inserted_id
    
    return OrderResponse(**order_dict)


@router.post("/{order_id}/pay")
async def pay_order(
    order_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(order_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid order ID"
        )
    
    order = await db.orders.find_one({
        "_id": ObjectId(order_id),
        "user_id": ObjectId(current_user.id)
    })
    
    if not order:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Order not found"
        )
    
    if order["status"] != "pending":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Order cannot be paid"
        )
    
    await db.orders.update_one(
        {"_id": ObjectId(order_id)},
        {"$set": {"status": "paid", "updated_at": datetime.utcnow()}}
    )
    
    return {"message": "Order paid successfully", "order_number": order["order_number"]}


@router.post("/{order_id}/cancel")
async def cancel_order(
    order_id: str,
    current_user: UserResponse = Depends(get_current_user),
    db: AsyncIOMotorDatabase = Depends(get_database)
):
    if not ObjectId.is_valid(order_id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid order ID"
        )
    
    order = await db.orders.find_one({
        "_id": ObjectId(order_id),
        "user_id": ObjectId(current_user.id)
    })
    
    if not order:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Order not found"
        )
    
    if order["status"] != "pending":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Order cannot be cancelled"
        )
    
    await db.orders.update_one(
        {"_id": ObjectId(order_id)},
        {"$set": {"status": "cancelled", "updated_at": datetime.utcnow()}}
    )
    
    return {"message": "Order cancelled successfully"}
