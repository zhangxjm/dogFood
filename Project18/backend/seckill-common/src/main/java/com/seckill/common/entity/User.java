package com.seckill.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String phone;

    private String email;

    private String nickname;

    private String avatar;

    private Integer status;

    private java.time.LocalDateTime lastLoginTime;
}
