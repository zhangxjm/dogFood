package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplication {
    private Long id;
    private Long jobId;
    private Long userId;
    private Long resumeId;
    private Long companyId;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime updateTime;

    private String jobTitle;
    private String companyName;
    private String realName;
    private String jobIntention;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_VIEWED = 1;
    public static final int STATUS_INTERVIEW = 2;
    public static final int STATUS_REJECTED = 3;
    public static final int STATUS_HIRED = 4;
}
