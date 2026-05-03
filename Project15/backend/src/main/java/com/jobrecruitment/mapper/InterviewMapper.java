package com.jobrecruitment.mapper;

import com.ibeetl.starter.mapper.BaseMapper;
import com.jobrecruitment.entity.Interview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterviewMapper extends BaseMapper<Interview, Long> {
}
