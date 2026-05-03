package com.jobrecruitment.dto;

import lombok.Data;

@Data
public class PageDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String city;
    private String jobType;
    private String industry;
    private Integer status;
    private Long companyId;
    private Long userId;
}
