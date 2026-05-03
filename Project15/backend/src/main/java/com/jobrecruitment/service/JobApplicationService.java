package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Company;
import com.jobrecruitment.entity.Job;
import com.jobrecruitment.entity.JobApplication;
import com.jobrecruitment.entity.Resume;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.CompanyMapper;
import com.jobrecruitment.mapper.JobApplicationMapper;
import com.jobrecruitment.mapper.JobMapper;
import com.jobrecruitment.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final JobMapper jobMapper;
    private final ResumeMapper resumeMapper;
    private final CompanyMapper companyMapper;
    private final CompanyService companyService;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public void apply(Long jobId) {
        Long userId = UserContext.getUserId();

        Job job = jobMapper.single(jobId);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }
        if (job.getStatus() != Job.STATUS_PUBLISHED) {
            throw new BusinessException("职位不可投递");
        }

        Resume resume = resumeMapper.createLambdaQuery()
                .andEq(Resume::getUserId, userId)
                .single();
        if (resume == null) {
            throw new BusinessException("请先完善简历");
        }

        JobApplication exist = jobApplicationMapper.createLambdaQuery()
                .andEq(JobApplication::getJobId, jobId)
                .andEq(JobApplication::getUserId, userId)
                .single();
        if (exist != null) {
            throw new BusinessException("已投递过该职位");
        }

        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setUserId(userId);
        application.setResumeId(resume.getId());
        application.setCompanyId(job.getCompanyId());
        application.setStatus(JobApplication.STATUS_PENDING);

        jobApplicationMapper.insert(application);

        messageService.sendSystemMessage(job.getCompanyId(), 
            "您有新的简历投递：" + job.getJobTitle() + " - " + resume.getRealName());
    }

    public List<JobApplication> listMyApplications(PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = jobApplicationMapper.createLambdaQuery()
                .andEq(JobApplication::getUserId, userId);

        if (dto.getStatus() != null) {
            query.andEq(JobApplication::getStatus, dto.getStatus());
        }

        List<JobApplication> list = query.orderByDesc(JobApplication::getApplyTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        list.forEach(this::fillDetail);
        return list;
    }

    public Long countMyApplications(PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = jobApplicationMapper.createLambdaQuery()
                .andEq(JobApplication::getUserId, userId);

        if (dto.getStatus() != null) {
            query.andEq(JobApplication::getStatus, dto.getStatus());
        }

        return query.count();
    }

    public List<JobApplication> listReceivedApplications(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        var query = jobApplicationMapper.createLambdaQuery()
                .andEq(JobApplication::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(JobApplication::getStatus, dto.getStatus());
        }

        List<JobApplication> list = query.orderByDesc(JobApplication::getApplyTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        list.forEach(this::fillDetail);
        return list;
    }

    public Long countReceivedApplications(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            return 0L;
        }

        var query = jobApplicationMapper.createLambdaQuery()
                .andEq(JobApplication::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(JobApplication::getStatus, dto.getStatus());
        }

        return query.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        JobApplication application = jobApplicationMapper.single(id);
        if (application == null) {
            throw new BusinessException("投递记录不存在");
        }
        if (!application.getCompanyId().equals(company.getId())) {
            throw new BusinessException("无权限操作");
        }

        application.setStatus(status);
        jobApplicationMapper.updateById(application);

        String statusDesc = switch (status) {
            case JobApplication.STATUS_VIEWED -> "已查看";
            case JobApplication.STATUS_INTERVIEW -> "已邀请面试";
            case JobApplication.STATUS_REJECTED -> "不合适";
            case JobApplication.STATUS_HIRED -> "已录用";
            default -> "状态更新";
        };

        messageService.sendSystemMessage(application.getUserId(),
            "您投递的职位状态已更新：" + getJobTitle(application.getJobId()) + " - " + statusDesc);
    }

    public JobApplication getById(Long id) {
        JobApplication application = jobApplicationMapper.single(id);
        if (application != null) {
            fillDetail(application);
        }
        return application;
    }

    private void fillDetail(JobApplication application) {
        if (application.getJobId() != null) {
            Job job = jobMapper.single(application.getJobId());
            if (job != null) {
                application.setJobTitle(job.getJobTitle());
            }
        }
        if (application.getCompanyId() != null) {
            Company company = companyMapper.single(application.getCompanyId());
            if (company != null) {
                application.setCompanyName(company.getCompanyName());
            }
        }
        if (application.getResumeId() != null) {
            Resume resume = resumeMapper.single(application.getResumeId());
            if (resume != null) {
                application.setRealName(resume.getRealName());
                application.setJobIntention(resume.getJobIntention());
            }
        }
    }

    private String getJobTitle(Long jobId) {
        Job job = jobMapper.single(jobId);
        return job != null ? job.getJobTitle() : "";
    }
}
