package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.ClubAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClubAnnouncementMapper extends BaseMapper<ClubAnnouncement> {

    @Select("SELECT a.*, c.name as club_name, u.real_name as publisher_name " +
            "FROM club_announcement a " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "LEFT JOIN user u ON a.publisher_id = u.id " +
            "WHERE a.id = #{id} AND a.status = 1")
    ClubAnnouncement getByIdWithDetail(Long id);

    @Select("SELECT a.*, c.name as club_name, u.real_name as publisher_name " +
            "FROM club_announcement a " +
            "LEFT JOIN club c ON a.club_id = c.id " +
            "LEFT JOIN user u ON a.publisher_id = u.id " +
            "WHERE a.club_id = #{clubId} AND a.status = 1 " +
            "ORDER BY a.is_top DESC, a.create_time DESC")
    List<ClubAnnouncement> listByClubId(Long clubId);
}
