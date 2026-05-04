package com.homemaking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homemaking.entity.Worker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface WorkerMapper extends BaseMapper<Worker> {
    
    @Select("SELECT w.*, u.real_name, u.phone, u.avatar, sc.name as category_name " +
            "FROM worker w LEFT JOIN sys_user u ON w.user_id = u.id " +
            "LEFT JOIN service_category sc ON w.service_category_id = sc.id " +
            "WHERE w.deleted = 0 ORDER BY w.create_time DESC")
    List<Worker> selectWorkerList();
}
