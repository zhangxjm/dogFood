package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Company;
import com.jobrecruitment.entity.Interview;
import com.jobrecruitment.entity.Job;
import com.jobrecruitment.entity.JobApplication;
import com.jobrecruitment.entity.Resume;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.CompanyMapper;
import com.jobrecruitment.mapper.InterviewMapper;
import com.jobrecruitment.mapper.JobApplicationMapper;
import com.jobrecruitment.mapper.JobMapper;
import com.jobrecruitment.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewMapper interviewMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final ResumeMapper resumeMapper;
    private final CompanyService companyService;
    private final MessageService messageService;

    @Transactional(rollbackFor = Exception.class)
    public Interview create(Interview interview) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        JobApplication application = jobApplicationMapper.single(interview.getApplicationId());
        if (application == null) {
            throw new BusinessException("投递记录不存在");
        }
        if (!application.getCompanyId().equals(company.getId())) {
            throw new BusinessException("无权限操作");
        }

        interview.setJobId(application.getJobId());
        interview.setUserId(application.getUserId());
        interview.setCompanyId(company.getId());
        interview.setStatus(Interview.STATUS_PENDING);

        interviewMapper.insert(interview);

        application.setStatus(JobApplication.STATUS_INTERVIEW);
        jobApplicationMapper.updateById(application);

        messageService.sendSystemMessage(application.getUserId(),
            "您收到面试邀请：" + getJobTitle(application.getJobId()) + "，请及时查看");

        return interview;
    }

    public List<Interview> listByCompany(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        var query = interviewMapper.createLambdaQuery()
                .andEq(Interview::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(Interview::getStatus, dto.getStatus());
        }

        List<Interview> list = query.orderByDesc(Interview::getInterviewTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        list.forEach(this::fillDetail);
        return list;
    }

    public List<Interview> listByUser(PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = interviewMapper.createLambdaQuery()
                .andEq(Interview::getUserId, userId);

        if (dto.getStatus() != null) {
            query.andEq(Interview::getStatus, dto.getStatus());
        }

        List<Interview> list = query.orderByDesc(Interview::getInterviewTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        list.forEach(this::fillDetail);
        return list;
    }

    public Long countByCompany(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            return 0L;
        }

        var query = interviewMapper.createLambdaQuery()
                .andEq(Interview::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(Interview::getStatus, dto.getStatus());
        }

        return query.count();
    }

    public Long countByUser(PageDTO dto) {
        Long userId = UserContext.getUserId();

        var query = interviewMapper.createLambdaQuery()
                .andEq(Interview::getUserId, userId);

        if (dto.getStatus() != null) {
            query.andEq(Interview::getStatus, dto.getStatus());
        }

        return query.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Interview interview = interviewMapper.single(id);
        if (interview == null) {
            throw new BusinessException("面试安排不存在");
        }

        Long userId = UserContext.getUserId();
        if (!interview.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }

        interview.setStatus(status);
        interviewMapper.updateById(interview);

        String statusDesc = switch (status) {
            case Interview.STATUS_ACCEPTED -> "已接受面试";
            case Interview.STATUS_REJECTED -> "已拒绝面试";
            case Interview.STATUS_CANCELLED -> "已取消面试";
            default -> "状态更新";
        };

        messageService.sendSystemMessage(interview.getCompanyId(),
            "求职者" + getRealName(interview.getUserId()) + statusDesc + "：" + getJobTitle(interview.getJobId()));
    }

    public Interview getById(Long id) {
        Interview interview = interviewMapper.single(id);
        if (interview != null) {
            fillDetail(interview);
        }
        return interview;
    }

    private void fillDetail(Interview interview) {
        if (interview.getJobId() != null) {
            Job job = jobMapper.single(interview.getJobId());
            if (job != null) {
                interview.setJobTitle(job.getJobTitle());
            }
        }
        if (interview.getCompanyId() != null) {
            Company company = companyMapper.single(interview.getCompanyId());
            if (company != null) {
                interview.setCompanyName(company.getCompanyName());
            }
        }
        if (interview.getUserId() != null) {
            Resume resume = resumeMapper.createLambdaQuery()
                    .andEq(Resume::getUserId, interview.getUserId())
                    .single();
            if (resume != null) {
                interview.setRealName(resume.getRealName());
            }
        }
    }

    private String getJobTitle(Long jobId) {
        Job job = jobMapper.single(jobId);
        return job != null ? job.getJobTitle() : "";
    }

    private String getRealName(Long userId) {
        Resume resume = resumeMapper.createLambdaQuery()
                .andEq(Resume::getUserId, userId)
                .single();
        return resume != null ? resume.getRealName() : "";
    }
}
