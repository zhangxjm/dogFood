package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Interview {
    private Long id;
    private Long applicationId;
    private Long jobId;
    private Long userId;
    private Long companyId;
    private LocalDateTime interviewTime;
    private Integer interviewType;
    private String interviewAddress;
    private String contactPerson;
    private String contactPhone;
    private String notes;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String jobTitle;
    private String companyName;
    private String realName;

    public static final int TYPE_OFFLINE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_PHONE = 2;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_CANCELLED = 4;
}
