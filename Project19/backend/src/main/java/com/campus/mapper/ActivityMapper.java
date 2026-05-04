package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Select("SELECT a.*, c.name as club_name " +
            "FROM activity a " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "WHERE a.id = #{id}")
    Activity getByIdWithDetail(Long id);

    @Select("SELECT a.*, c.name as club_name " +
            "FROM activity a " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "ORDER BY a.start_time DESC")
    List<Activity> listWithDetail();

    @Select("SELECT a.*, c.name as club_name " +
            "FROM activity a " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "WHERE a.club_id = #{clubId} " +
            "ORDER BY a.start_time DESC")
    List<Activity> listByClubId(Long clubId);
}
