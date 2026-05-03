package com.jobrecruitment.controller;

import com.jobrecruitment.common.Result;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Company;
import com.jobrecruitment.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(PageDTO dto) {
        var companies = companyService.list(dto);
        var total = companyService.count(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("records", companies);
        result.put("total", total);
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Company> getMyCompany() {
        Company company = companyService.getCurrentCompany();
        return Result.success(company);
    }

    @GetMapping("/detail/{id}")
    public Result<Company> detail(@PathVariable Long id) {
        Company company = companyService.getById(id);
        return Result.success(company);
    }

    @PostMapping("/save")
    public Result<Company> save(@RequestBody Company company) {
        Company result = companyService.save(company);
        return Result.success(result);
    }

    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        companyService.audit(id, status);
        return Result.success();
    }
}
