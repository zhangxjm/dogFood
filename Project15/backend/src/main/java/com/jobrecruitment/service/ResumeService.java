package com.jobrecruitment.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.entity.Resume;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeMapper resumeMapper;

    public Resume getCurrentResume() {
        Long userId = UserContext.getUserId();
        Resume resume = resumeMapper.createLambdaQuery()
                .andEq(Resume::getUserId, userId)
                .single();

        if (resume != null) {
            fillResumeDetail(resume);
        }
        return resume;
    }

    public Resume getByUserId(Long userId) {
        Resume resume = resumeMapper.createLambdaQuery()
                .andEq(Resume::getUserId, userId)
                .single();
        if (resume != null) {
            fillResumeDetail(resume);
        }
        return resume;
    }

    public Resume getById(Long id) {
        Resume resume = resumeMapper.single(id);
        if (resume != null) {
            fillResumeDetail(resume);
        }
        return resume;
    }

    @Transactional(rollbackFor = Exception.class)
    public Resume save(Resume resume) {
        Long userId = UserContext.getUserId();
        Resume exist = getByUserId(userId);

        if (resume.getEducationList() != null) {
            resume.setEducation(JSONUtil.toJsonStr(resume.getEducationList()));
        }
        if (resume.getWorkExperienceList() != null) {
            resume.setWorkExperience(JSONUtil.toJsonStr(resume.getWorkExperienceList()));
        }
        if (resume.getProjectExperienceList() != null) {
            resume.setProjectExperience(JSONUtil.toJsonStr(resume.getProjectExperienceList()));
        }

        if (exist == null) {
            resume.setUserId(userId);
            resume.setStatus(Resume.STATUS_PUBLIC);
            resumeMapper.insert(resume);
            return resume;
        } else {
            resume.setId(exist.getId());
            resume.setUserId(userId);
            resumeMapper.updateById(resume);
            return resume;
        }
    }

    public List<Resume> matchJobs(Long jobId) {
        return resumeMapper.createLambdaQuery()
                .andEq(Resume::getStatus, Resume.STATUS_PUBLIC)
                .orderByDesc(Resume::getUpdateTime)
                .limit(0, 20)
                .select();
    }

    private void fillResumeDetail(Resume resume) {
        if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
            try {
                List<Resume.EducationItem> list = JSONUtil.toList(resume.getEducation(), Resume.EducationItem.class);
                resume.setEducationList(list);
            } catch (Exception e) {
                // ignore
            }
        }
        if (resume.getWorkExperience() != null && !resume.getWorkExperience().isEmpty()) {
            try {
                List<Resume.WorkExperienceItem> list = JSONUtil.toList(resume.getWorkExperience(), Resume.WorkExperienceItem.class);
                resume.setWorkExperienceList(list);
            } catch (Exception e) {
                // ignore
            }
        }
        if (resume.getProjectExperience() != null && !resume.getProjectExperience().isEmpty()) {
            try {
                List<Resume.ProjectExperienceItem> list = JSONUtil.toList(resume.getProjectExperience(), Resume.ProjectExperienceItem.class);
                resume.setProjectExperienceList(list);
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
