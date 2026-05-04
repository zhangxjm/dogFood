package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityRegistrationMapper extends BaseMapper<ActivityRegistration> {

    @Select("SELECT ar.*, a.title as activity_title, u.username, u.real_name " +
            "FROM activity_registration ar " +
            "LEFT JOIN activity a ON ar.activity_id = a.id " +
            "LEFT JOIN user u ON ar.user_id = u.id " +
            "WHERE ar.activity_id = #{activityId} " +
            "ORDER BY ar.create_time DESC")
    List<ActivityRegistration> listByActivityId(Long activityId);

    @Select("SELECT ar.*, a.title as activity_title, a.location, a.start_time, " +
            "a.end_time, a.status as activity_status, a.current_participants, a.max_participants, " +
            "c.name as club_name, " +
            "u.username, u.real_name " +
            "FROM activity_registration ar " +
            "LEFT JOIN activity a ON ar.activity_id = a.id " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "LEFT JOIN user u ON ar.user_id = u.id " +
            "WHERE ar.user_id = #{userId} " +
            "ORDER BY ar.create_time DESC")
    List<ActivityRegistration> listByUserId(Long userId);
}
