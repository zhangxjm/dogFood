package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobFavorite {
    private Long id;
    private Long userId;
    private Long jobId;
    private LocalDateTime createTime;

    private String jobTitle;
    private String companyName;
    private Integer salaryMin;
    private Integer salaryMax;
    private String city;
}
