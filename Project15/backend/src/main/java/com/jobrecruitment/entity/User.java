package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int ROLE_JOBSEEKER = 1;
    public static final int ROLE_HR = 2;
    public static final int ROLE_ADMIN = 3;

    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_PENDING = 2;
}
