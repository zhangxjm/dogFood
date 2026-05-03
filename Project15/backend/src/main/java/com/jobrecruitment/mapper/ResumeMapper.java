package com.jobrecruitment.mapper;

import com.ibeetl.starter.mapper.BaseMapper;
import com.jobrecruitment.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume, Long> {
}
