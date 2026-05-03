package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Company {
    private Long id;
    private Long userId;
    private String companyName;
    private String industry;
    private String scale;
    private String address;
    private String description;
    private String logo;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;
}
