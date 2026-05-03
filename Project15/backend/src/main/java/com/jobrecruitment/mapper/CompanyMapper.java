package com.jobrecruitment.mapper;

import com.ibeetl.starter.mapper.BaseMapper;
import com.jobrecruitment.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company, Long> {
}
