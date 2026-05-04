package com.homemaking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homemaking.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
