package com.restaurant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.DiningTable;
import com.restaurant.mapper.DiningTableMapper;
import com.restaurant.service.DiningTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Override
    public Page<DiningTable> pageList(Integer page, Integer pageSize, String tableNo, Integer status) {
        Page<DiningTable> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<DiningTable> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(tableNo)) {
            wrapper.like(DiningTable::getTableNo, tableNo);
        }
        if (status != null) {
            wrapper.eq(DiningTable::getStatus, status);
        }
        wrapper.orderByAsc(DiningTable::getTableNo);
        
        return this.page(pageParam, wrapper);
    }
    
    @Override
    public List<DiningTable> listByStatus(Integer status) {
        return this.lambdaQuery()
                .eq(DiningTable::getStatus, status)
                .orderByAsc(DiningTable::getTableNo)
                .list();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTable(DiningTable diningTable) {
        diningTable.setStatus(DiningTable.STATUS_FREE);
        boolean result = this.save(diningTable);
        if (result) {
            diningTable.setQrCode(generateQrCode(diningTable.getId()));
            this.updateById(diningTable);
        }
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTable(DiningTable diningTable) {
        return this.updateById(diningTable);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        DiningTable diningTable = new DiningTable();
        diningTable.setId(id);
        diningTable.setStatus(status);
        return this.updateById(diningTable);
    }
    
    @Override
    public DiningTable getByTableNo(String tableNo) {
        return this.lambdaQuery()
                .eq(DiningTable::getTableNo, tableNo)
                .one();
    }
    
    @Override
    public String generateQrCode(Long tableId) {
        return "http://localhost:" + serverPort + "/order/scan?tableId=" + tableId;
    }
}
