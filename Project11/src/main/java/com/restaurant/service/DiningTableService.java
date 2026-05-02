package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.DiningTable;

import java.util.List;

public interface DiningTableService extends IService<DiningTable> {
    
    Page<DiningTable> pageList(Integer page, Integer pageSize, String tableNo, Integer status);
    
    List<DiningTable> listByStatus(Integer status);
    
    boolean saveTable(DiningTable diningTable);
    
    boolean updateTable(DiningTable diningTable);
    
    boolean updateStatus(Long id, Integer status);
    
    DiningTable getByTableNo(String tableNo);
    
    String generateQrCode(Long tableId);
}
