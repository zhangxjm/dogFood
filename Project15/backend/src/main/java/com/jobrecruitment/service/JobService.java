package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Company;
import com.jobrecruitment.entity.Job;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.CompanyMapper;
import com.jobrecruitment.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final CompanyService companyService;

    public Job getById(Long id) {
        Job job = jobMapper.single(id);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }
        fillCompanyInfo(job);
        return job;
    }

    public List<Job> list(PageDTO dto) {
        var query = jobMapper.createLambdaQuery()
                .andEq(Job::getStatus, Job.STATUS_PUBLISHED);

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.andLike(Job::getJobTitle, "%" + dto.getKeyword() + "%");
        }
        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            query.andEq(Job::getCity, dto.getCity());
        }
        if (dto.getJobType() != null && !dto.getJobType().isEmpty()) {
            query.andEq(Job::getJobType, dto.getJobType());
        }

        List<Job> jobs = query.orderByDesc(Job::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        jobs.forEach(this::fillCompanyInfo);
        return jobs;
    }

    public Long count(PageDTO dto) {
        var query = jobMapper.createLambdaQuery()
                .andEq(Job::getStatus, Job.STATUS_PUBLISHED);

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.andLike(Job::getJobTitle, "%" + dto.getKeyword() + "%");
        }
        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            query.andEq(Job::getCity, dto.getCity());
        }
        if (dto.getJobType() != null && !dto.getJobType().isEmpty()) {
            query.andEq(Job::getJobType, dto.getJobType());
        }
        return query.count();
    }

    public List<Job> listByCompany(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        var query = jobMapper.createLambdaQuery()
                .andEq(Job::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(Job::getStatus, dto.getStatus());
        }

        List<Job> jobs = query.orderByDesc(Job::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        jobs.forEach(this::fillCompanyInfo);
        return jobs;
    }

    public Long countByCompany(PageDTO dto) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            return 0L;
        }

        var query = jobMapper.createLambdaQuery()
                .andEq(Job::getCompanyId, company.getId());

        if (dto.getStatus() != null) {
            query.andEq(Job::getStatus, dto.getStatus());
        }
        return query.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public Job publish(Job job) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }
        if (company.getStatus() != Company.STATUS_APPROVED) {
            throw new BusinessException("公司信息尚未审核通过");
        }

        job.setCompanyId(company.getId());
        job.setStatus(Job.STATUS_PENDING);
        job.setViewCount(0);
        jobMapper.insert(job);
        return job;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Job job) {
        Long userId = UserContext.getUserId();
        Company company = companyService.getByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先完善公司信息");
        }

        Job existJob = jobMapper.single(job.getId());
        if (existJob == null) {
            throw new BusinessException("职位不存在");
        }
        if (!existJob.getCompanyId().equals(company.getId())) {
            throw new BusinessException("无权限操作此职位");
        }

        job.setStatus(Job.STATUS_PENDING);
        jobMapper.updateById(job);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Job job = jobMapper.single(id);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }
        job.setStatus(status);
        jobMapper.updateById(job);
    }

    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Job job = jobMapper.single(id);
        if (job != null) {
            job.setViewCount(job.getViewCount() + 1);
            jobMapper.updateById(job);
        }
    }

    private void fillCompanyInfo(Job job) {
        if (job.getCompanyId() != null) {
            Company company = companyMapper.single(job.getCompanyId());
            if (company != null) {
                job.setCompanyName(company.getCompanyName());
                job.setCompanyLogo(company.getLogo());
            }
        }
    }
}
