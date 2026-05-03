package com.jobrecruitment.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Resume {
    private Long id;
    private Long userId;
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private LocalDate birthday;
    private String city;
    private String avatar;
    private String jobIntention;
    private Integer expectedSalaryMin;
    private Integer expectedSalaryMax;
    private String expectedCity;
    private String education;
    private String workExperience;
    private String projectExperience;
    private String skills;
    private String selfEvaluation;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<EducationItem> educationList;
    private List<WorkExperienceItem> workExperienceList;
    private List<ProjectExperienceItem> projectExperienceList;

    @Data
    public static class EducationItem {
        private String school;
        private String major;
        private String degree;
        private String startTime;
        private String endTime;
    }

    @Data
    public static class WorkExperienceItem {
        private String company;
        private String position;
        private String startTime;
        private String endTime;
        private String description;
    }

    @Data
    public static class ProjectExperienceItem {
        private String projectName;
        private String role;
        private String startTime;
        private String endTime;
        private String description;
    }

    public static final int STATUS_HIDDEN = 0;
    public static final int STATUS_PUBLIC = 1;
}
