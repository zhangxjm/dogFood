package com.jobrecruitment.mapper;

import com.ibeetl.starter.mapper.BaseMapper;
import com.jobrecruitment.entity.JobFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobFavoriteMapper extends BaseMapper<JobFavorite, Long> {
}
