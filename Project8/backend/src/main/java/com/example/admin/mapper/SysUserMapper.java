package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    List<SysUser> selectUserList(SysUser user);
    
    SysUser selectUserByUserName(@Param("userName") String userName);
    
    int updateUserProfile(SysUser user);
    
    int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);
    
    int resetUserPwd(@Param("userName") String userName, @Param("password") String password);
    
    int deleteUserByIds(@Param("userIds") Long[] userIds);
}
