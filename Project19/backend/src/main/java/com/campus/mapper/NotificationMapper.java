package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("SELECT n.*, u.real_name as sender_name, c.name as club_name " +
            "FROM notification n " +
            "LEFT JOIN user u ON n.sender_id = u.id " +
            "LEFT JOIN club c ON n.club_id = c.id " +
            "WHERE n.user_id = #{userId} " +
            "ORDER BY n.create_time DESC")
    List<Notification> listByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnreadByUserId(Long userId);

    @Update("UPDATE notification SET is_read = 1, read_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(Long id, Long userId);

    @Update("UPDATE notification SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(Long userId);
}
