package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("club_member")
public class ClubMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private Long userId;

    private Integer role;

    private Integer status;

    private String applyRemark;

    private String auditRemark;

    private LocalDateTime joinTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String clubName;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private Integer clubStatus;

    @TableField(exist = false)
    private String clubDescription;

    @TableField(exist = false)
    private Integer clubCurrentMembers;

    @TableField(exist = false)
    private Integer clubMaxMembers;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String presidentName;

    public static final int ROLE_PRESIDENT = 1;
    public static final int ROLE_MEMBER = 2;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_QUIT = 3;
}
