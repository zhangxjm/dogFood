package com.logistics.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.logistics.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
