package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Job {
    private Long id;
    private Long companyId;
    private String jobTitle;
    private String jobType;
    private Integer salaryMin;
    private Integer salaryMax;
    private String city;
    private String address;
    private String experience;
    private String education;
    private String jobDescription;
    private String jobRequirement;
    private String welfare;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String companyName;
    private String companyLogo;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_OFFLINE = 2;
    public static final int STATUS_REJECTED = 3;
}
