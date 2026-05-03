package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Club;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClubMapper extends BaseMapper<Club> {

    @Select("SELECT c.*, cc.name as category_name, u.real_name as president_name " +
            "FROM club c " +
            "LEFT JOIN club_category cc ON c.category_id = cc.id " +
            "LEFT JOIN user u ON c.president_id = u.id " +
            "WHERE c.id = #{id}")
    Club getByIdWithDetail(Long id);

    @Select("SELECT c.*, cc.name as category_name, u.real_name as president_name " +
            "FROM club c " +
            "LEFT JOIN club_category cc ON c.category_id = cc.id " +
            "LEFT JOIN user u ON c.president_id = u.id " +
            "ORDER BY c.create_time DESC")
    List<Club> listWithDetail();
}
