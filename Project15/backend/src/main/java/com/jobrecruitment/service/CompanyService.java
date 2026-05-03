package com.jobrecruitment.service;

import com.jobrecruitment.context.UserContext;
import com.jobrecruitment.dto.PageDTO;
import com.jobrecruitment.entity.Company;
import com.jobrecruitment.exception.BusinessException;
import com.jobrecruitment.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyMapper companyMapper;

    public Company getByUserId(Long userId) {
        return companyMapper.createLambdaQuery()
                .andEq(Company::getUserId, userId)
                .single();
    }

    public Company getCurrentCompany() {
        Long userId = UserContext.getUserId();
        return getByUserId(userId);
    }

    public Company getById(Long id) {
        return companyMapper.single(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Company save(Company company) {
        Long userId = UserContext.getUserId();
        Company exist = getByUserId(userId);

        if (exist == null) {
            company.setUserId(userId);
            company.setStatus(Company.STATUS_PENDING);
            companyMapper.insert(company);
            return company;
        } else {
            company.setId(exist.getId());
            company.setUserId(userId);
            companyMapper.updateById(company);
            return company;
        }
    }

    public List<Company> list(PageDTO dto) {
        var query = companyMapper.createLambdaQuery();
        if (dto.getStatus() != null) {
            query.andEq(Company::getStatus, dto.getStatus());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.andLike(Company::getCompanyName, "%" + dto.getKeyword() + "%");
        }
        return query.orderByDesc(Company::getCreateTime)
                .limit((dto.getPageNum() - 1) * dto.getPageSize(), dto.getPageSize())
                .select();
    }

    public Long count(PageDTO dto) {
        var query = companyMapper.createLambdaQuery();
        if (dto.getStatus() != null) {
            query.andEq(Company::getStatus, dto.getStatus());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.andLike(Company::getCompanyName, "%" + dto.getKeyword() + "%");
        }
        return query.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer status) {
        Company company = companyMapper.single(id);
        if (company == null) {
            throw new BusinessException("公司信息不存在");
        }
        company.setStatus(status);
        companyMapper.updateById(company);
    }
}
