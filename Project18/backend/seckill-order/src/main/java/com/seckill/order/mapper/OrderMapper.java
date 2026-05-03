package com.seckill.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.common.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM t_order WHERE status = 0 AND expire_time < NOW()")
    List<Order> selectExpiredOrders();

    @Update("UPDATE t_order SET status = 4, cancel_time = NOW(), cancel_reason = '订单超时自动取消' WHERE id = #{id}")
    int cancelOrder(@Param("id") Long id);

    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo} LIMIT 1")
    Order selectByOrderNo(@Param("orderNo") String orderNo);
}
