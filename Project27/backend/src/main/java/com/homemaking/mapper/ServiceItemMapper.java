package com.homemaking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homemaking.entity.ServiceItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ServiceItemMapper extends BaseMapper<ServiceItem> {
    
    @Select("SELECT si.*, sc.name as category_name FROM service_item si " +
            "LEFT JOIN service_category sc ON si.category_id = sc.id " +
            "WHERE si.deleted = 0 ORDER BY si.sort, si.create_time DESC")
    List<ServiceItem> selectServiceItemList();
}
