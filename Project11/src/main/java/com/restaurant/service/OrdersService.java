package com.restaurant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Orders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrdersService extends IService<Orders> {
    
    Page<Orders> pageList(Integer page, Integer pageSize, String orderNo, Integer status, Long tableId, 
                           LocalDate startDate, LocalDate endDate);
    
    String generateOrderNo();
    
    Orders createOrder(Orders orders, Long employeeId);
    
    Orders payOrder(Long orderId, String paymentMethod, Long cashierId);
    
    Orders cancelOrder(Long orderId, String reason, Long operatorId);
    
    Orders completeOrder(Long orderId, Long operatorId);
    
    List<Orders> listKitchenOrders(Integer kitchenStatus);
    
    boolean updateKitchenStatus(Long orderId, Integer kitchenStatus, Long operatorId);
    
    Map<String, Object> getStatistics(LocalDate startDate, LocalDate endDate);
    
    List<Map<String, Object>> getDailySales(LocalDate startDate, LocalDate endDate);
    
    Orders getOrderWithDetails(Long orderId);
}
