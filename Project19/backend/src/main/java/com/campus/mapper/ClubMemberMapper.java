package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.ClubMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {

    @Select("SELECT cm.*, c.name as club_name, u.username, u.real_name " +
            "FROM club_member cm " +
            "LEFT JOIN club c ON cm.club_id = c.id " +
            "LEFT JOIN user u ON cm.user_id = u.id " +
            "WHERE cm.club_id = #{clubId} " +
            "ORDER BY cm.create_time DESC")
    List<ClubMember> listByClubId(Long clubId);

    @Select("SELECT cm.*, c.name as club_name, c.status as club_status, " +
            "c.description as club_description, c.current_members as club_current_members, " +
            "c.max_members as club_max_members, cc.name as category_name, " +
            "u_president.real_name as president_name, " +
            "u.username, u.real_name " +
            "FROM club_member cm " +
            "LEFT JOIN club c ON cm.club_id = c.id " +
            "LEFT JOIN club_category cc ON c.category_id = cc.id " +
            "LEFT JOIN user u_president ON c.president_id = u_president.id " +
            "LEFT JOIN user u ON cm.user_id = u.id " +
            "WHERE cm.user_id = #{userId} " +
            "ORDER BY cm.create_time DESC")
    List<ClubMember> listByUserId(Long userId);
}
