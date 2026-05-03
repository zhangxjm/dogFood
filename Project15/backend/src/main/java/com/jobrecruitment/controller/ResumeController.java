package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.entity.Resume;
import com.jobrecruitment.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping("/my")
    public Result<Resume> getMyResume() {
        Resume resume = resumeService.getCurrentResume();
        return Result.success(resume);
    }

    @GetMapping("/detail/{id}")
    public Result<Resume> detail(@PathVariable Long id) {
        Resume resume = resumeService.getById(id);
        return Result.success(resume);
    }

    @PostMapping("/save")
    public Result<Resume> save(@RequestBody Resume resume) {
        Resume result = resumeService.save(resume);
        return Result.success(result);
    }

    @GetMapping("/match/{jobId}")
    public Result<?> matchJobs(@PathVariable Long jobId) {
        var resumes = resumeService.matchJobs(jobId);
        return Result.success(resumes);
    }
}
