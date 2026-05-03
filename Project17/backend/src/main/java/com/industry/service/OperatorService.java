package com.industry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.industry.entity.Operator;
import com.industry.mapper.OperatorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorMapper operatorMapper;

    public List<Operator> listAll() {
        return operatorMapper.selectList(
            new LambdaQueryWrapper<Operator>().eq(Operator::getDeleted, 0)
        );
    }

    public Page<Operator> page(int page, int size, String realName, Integer role, Integer status) {
        Page<Operator> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Operator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Operator::getDeleted, 0);
        
        if (StringUtils.hasText(realName)) {
            wrapper.like(Operator::getRealName, realName);
        }
        if (role != null) {
            wrapper.eq(Operator::getRole, role);
        }
        if (status != null) {
            wrapper.eq(Operator::getStatus, status);
        }
        
        wrapper.orderByDesc(Operator::getCreateTime);
        return operatorMapper.selectPage(pageParam, wrapper);
    }

    public Operator getById(Long id) {
        return operatorMapper.selectById(id);
    }

    public Operator login(String username, String password) {
        return operatorMapper.selectOne(
            new LambdaQueryWrapper<Operator>()
                .eq(Operator::getUsername, username)
                .eq(Operator::getPassword, password)
                .eq(Operator::getStatus, 1)
                .eq(Operator::getDeleted, 0)
        );
    }

    public boolean save(Operator operator) {
        return operatorMapper.insert(operator) > 0;
    }

    public boolean update(Operator operator) {
        return operatorMapper.updateById(operator) > 0;
    }

    public boolean delete(Long id) {
        Operator operator = new Operator();
        operator.setId(id);
        operator.setDeleted(1);
        return operatorMapper.updateById(operator) > 0;
    }

    public boolean updateStatus(Long id, Integer status) {
        Operator operator = new Operator();
        operator.setId(id);
        operator.setStatus(status);
        return operatorMapper.updateById(operator) > 0;
    }
}
