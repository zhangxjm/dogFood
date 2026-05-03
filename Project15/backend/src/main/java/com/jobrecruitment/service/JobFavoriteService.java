package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Job;
import com.jobrecruitment.entity.JobFavorite;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.JobFavoriteMapper;
import com.jobrecruitment.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobFavoriteService {

    private final JobFavoriteMapper jobFavoriteMapper;
    private final JobMapper jobMapper;

    @Transactional(rollbackFor = Exception.class)
    public void toggle(Long jobId) {
        Long userId = UserContext.getUserId();

        JobFavorite exist = jobFavoriteMapper.createLambdaQuery()
                .andEq(JobFavorite::getUserId, userId)
                .andEq(JobFavorite::getJobId, jobId)
                .single();

        if (exist != null) {
            jobFavoriteMapper.deleteById(exist.getId());
        } else {
            Job job = jobMapper.single(jobId);
            if (job == null) {
                throw new BusinessException("职位不存在");
            }
            JobFavorite favorite = new JobFavorite();
            favorite.setUserId(userId);
            favorite.setJobId(jobId);
            jobFavoriteMapper.insert(favorite);
        }
    }

    public boolean isFavorited(Long jobId) {
        Long userId = UserContext.getUserId();
        return jobFavoriteMapper.createLambdaQuery()
                .andEq(JobFavorite::getUserId, userId)
                .andEq(JobFavorite::getJobId, jobId)
                .count() > 0;
    }

    public List<JobFavorite> list(PageDTO dto) {
        Long userId = UserContext.getUserId();

        List<JobFavorite> list = jobFavoriteMapper.createLambdaQuery()
                .andEq(JobFavorite::getUserId, userId)
                .orderByDesc(JobFavorite::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();

        list.forEach(this::fillJobInfo);
        return list;
    }

    public Long count(PageDTO dto) {
        Long userId = UserContext.getUserId();
        return jobFavoriteMapper.createLambdaQuery()
                .andEq(JobFavorite::getUserId, userId)
                .count();
    }

    private void fillJobInfo(JobFavorite favorite) {
        if (favorite.getJobId() != null) {
            Job job = jobMapper.single(favorite.getJobId());
            if (job != null) {
                favorite.setJobTitle(job.getJobTitle());
                favorite.setCompanyName(job.getCompanyName());
                favorite.setSalaryMin(job.getSalaryMin());
                favorite.setSalaryMax(job.getSalaryMax());
                favorite.setCity(job.getCity());
            }
        }
    }
}
