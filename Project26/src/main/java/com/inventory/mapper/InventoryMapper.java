package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("SELECT * FROM inventory WHERE product_id = #{productId} AND warehouse_id = #{warehouseId}")
    Inventory findByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Update("UPDATE inventory SET quantity = quantity + #{quantity} WHERE product_id = #{productId} AND warehouse_id = #{warehouseId}")
    int addQuantity(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("quantity") Integer quantity);

    @Update("UPDATE inventory SET quantity = quantity - #{quantity} WHERE product_id = #{productId} AND warehouse_id = #{warehouseId} AND quantity >= #{quantity}")
    int subtractQuantity(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("quantity") Integer quantity);
}
