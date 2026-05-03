package com.jobrecruitment.mapper;

import com.ibeetl.starter.mapper.BaseMapper;
import com.jobrecruitment.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message, Long> {
}
