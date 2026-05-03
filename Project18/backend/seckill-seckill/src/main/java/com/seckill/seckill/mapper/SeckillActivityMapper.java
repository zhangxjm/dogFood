package com.seckill.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.common.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    @Update("UPDATE t_seckill_activity SET stock = stock - #{quantity}, sales = sales + #{quantity} WHERE id = #{activityId} AND stock >= #{quantity}")
    int deductStock(@Param("activityId") Long activityId, @Param("quantity") Integer quantity);

    @Update("UPDATE t_seckill_activity SET stock = stock + #{quantity}, sales = sales - #{quantity} WHERE id = #{activityId}")
    int addStock(@Param("activityId") Long activityId, @Param("quantity") Integer quantity);

    @Select("SELECT * FROM t_seckill_activity WHERE product_id = #{productId} AND status = 1 LIMIT 1")
    SeckillActivity selectActiveByProductId(@Param("productId") Long productId);
}
