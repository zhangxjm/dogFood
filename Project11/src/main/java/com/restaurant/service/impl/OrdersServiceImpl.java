package com.restaurant.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Employee;
import com.restaurant.entity.Orders;
import com.restaurant.entity.OrderDetail;
import com.restaurant.mapper.OrdersMapper;
import com.restaurant.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    
    private final EmployeeService employeeService;
    private final OrderDetailService orderDetailService;
    private final DiningTableService diningTableService;
    
    @Override
    public Page<Orders> pageList(Integer page, Integer pageSize, String orderNo, Integer status, Long tableId,
                                   LocalDate startDate, LocalDate endDate) {
        Page<Orders> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(orderNo)) {
            wrapper.like(Orders::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        if (tableId != null) {
            wrapper.eq(Orders::getTableId, tableId);
        }
        if (startDate != null) {
            wrapper.ge(Orders::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(Orders::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        
        Page<Orders> result = this.page(pageParam, wrapper);
        result.getRecords().forEach(this::fillStatusText);
        
        return result;
    }
    
    @Override
    public String generateOrderNo() {
        String date = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
        String random = IdUtil.randomUUID().substring(0, 6).toUpperCase();
        return "ORD" + date + random;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Orders orders, Long employeeId) {
        orders.setOrderNo(generateOrderNo());
        orders.setStatus(Orders.STATUS_PENDING_ORDER);
        orders.setKitchenStatus(Orders.KITCHEN_STATUS_PENDING);
        
        if (employeeId != null) {
            Employee employee = employeeService.getById(employeeId);
            if (employee != null) {
                orders.setCashierId(employeeId);
                orders.setCashierName(employee.getName());
            }
        }
        
        if (orders.getTotalAmount() == null) {
            orders.setTotalAmount(BigDecimal.ZERO);
        }
        if (orders.getDiscountAmount() == null) {
            orders.setDiscountAmount(BigDecimal.ZERO);
        }
        if (orders.getPayableAmount() == null) {
            orders.setPayableAmount(orders.getTotalAmount().subtract(orders.getDiscountAmount()));
        }
        if (orders.getPaidAmount() == null) {
            orders.setPaidAmount(BigDecimal.ZERO);
        }
        if (orders.getCustomerCount() == null) {
            orders.setCustomerCount(1);
        }
        
        this.save(orders);
        
        if (orders.getTableId() != null) {
            diningTableService.updateStatus(orders.getTableId(), 
                    com.restaurant.entity.DiningTable.STATUS_OCCUPIED);
        }
        
        return orders;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders payOrder(Long orderId, String paymentMethod, Long cashierId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        
        orders.setStatus(Orders.STATUS_PAID);
        orders.setPaymentMethod(paymentMethod);
        orders.setPaidAmount(orders.getPayableAmount());
        orders.setPaidTime(LocalDateTime.now());
        orders.setKitchenStatus(Orders.KITCHEN_STATUS_CONFIRMED);
        
        if (cashierId != null) {
            Employee employee = employeeService.getById(cashierId);
            if (employee != null) {
                orders.setCashierId(cashierId);
                orders.setCashierName(employee.getName());
            }
        }
        
        this.updateById(orders);
        
        return orders;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders cancelOrder(Long orderId, String reason, Long operatorId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (orders.getStatus() >= Orders.STATUS_PAID) {
            throw new RuntimeException("已支付订单无法取消，请进行退款操作");
        }
        
        orders.setStatus(Orders.STATUS_CANCELLED);
        this.updateById(orders);
        
        if (orders.getTableId() != null) {
            diningTableService.updateStatus(orders.getTableId(), 
                    com.restaurant.entity.DiningTable.STATUS_FREE);
        }
        
        return orders;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders completeOrder(Long orderId, Long operatorId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (orders.getStatus() != Orders.STATUS_PAID) {
            throw new RuntimeException("只有已支付订单可以完成");
        }
        
        orders.setStatus(Orders.STATUS_COMPLETED);
        this.updateById(orders);
        
        if (orders.getTableId() != null) {
            diningTableService.updateStatus(orders.getTableId(), 
                    com.restaurant.entity.DiningTable.STATUS_FREE);
        }
        
        return orders;
    }
    
    @Override
    public List<Orders> listKitchenOrders(Integer kitchenStatus) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (kitchenStatus != null) {
            wrapper.eq(Orders::getKitchenStatus, kitchenStatus);
        }
        wrapper.in(Orders::getStatus, Orders.STATUS_PAID, Orders.STATUS_COMPLETED)
                .orderByAsc(Orders::getCreateTime);
        
        List<Orders> list = this.list(wrapper);
        list.forEach(this::fillStatusText);
        return list;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateKitchenStatus(Long orderId, Integer kitchenStatus, Long operatorId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            return false;
        }
        
        orders.setKitchenStatus(kitchenStatus);
        return this.updateById(orders);
    }
    
    @Override
    public Map<String, Object> getStatistics(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Orders::getCreateTime, startTime, endTime)
                .in(Orders::getStatus, Orders.STATUS_PAID, Orders.STATUS_COMPLETED);
        
        List<Orders> orders = this.list(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", orders.size());
        result.put("totalAmount", orders.stream()
                .map(Orders::getPaidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        Map<String, Long> paymentMethodCount = new HashMap<>();
        Map<String, BigDecimal> paymentMethodAmount = new HashMap<>();
        
        for (Orders order : orders) {
            String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "其他";
            paymentMethodCount.merge(method, 1L, Long::sum);
            paymentMethodAmount.merge(method, 
                    order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO, 
                    BigDecimal::add);
        }
        
        result.put("paymentMethodCount", paymentMethodCount);
        result.put("paymentMethodAmount", paymentMethodAmount);
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getDailySales(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Orders::getCreateTime, startTime, endTime)
                .in(Orders::getStatus, Orders.STATUS_PAID, Orders.STATUS_COMPLETED)
                .orderByAsc(Orders::getCreateTime);
        
        List<Orders> orders = this.list(wrapper);
        
        Map<LocalDate, List<Orders>> grouped = new LinkedHashMap<>();
        for (Orders order : orders) {
            LocalDate date = order.getCreateTime().toLocalDate();
            grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(order);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Orders>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey().toString());
            item.put("orderCount", entry.getValue().size());
            item.put("totalAmount", entry.getValue().stream()
                    .map(Orders::getPaidAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            result.add(item);
        }
        
        return result;
    }
    
    @Override
    public Orders getOrderWithDetails(Long orderId) {
        Orders orders = this.getById(orderId);
        if (orders != null) {
            fillStatusText(orders);
        }
        return orders;
    }
    
    private void fillStatusText(Orders orders) {
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(Orders.STATUS_PENDING_ORDER, "待点餐");
        statusMap.put(Orders.STATUS_PENDING_PAYMENT, "待支付");
        statusMap.put(Orders.STATUS_PAID, "已支付");
        statusMap.put(Orders.STATUS_COMPLETED, "已完成");
        statusMap.put(Orders.STATUS_CANCELLED, "已取消");
        statusMap.put(Orders.STATUS_REFUNDED, "已退款");
        orders.setStatusText(statusMap.getOrDefault(orders.getStatus(), "未知"));
        
        Map<Integer, String> kitchenStatusMap = new HashMap<>();
        kitchenStatusMap.put(Orders.KITCHEN_STATUS_PENDING, "未确认");
        kitchenStatusMap.put(Orders.KITCHEN_STATUS_CONFIRMED, "已确认");
        kitchenStatusMap.put(Orders.KITCHEN_STATUS_COOKING, "制作中");
        kitchenStatusMap.put(Orders.KITCHEN_STATUS_COMPLETED, "已完成");
        orders.setKitchenStatusText(kitchenStatusMap.getOrDefault(orders.getKitchenStatus(), "未知"));
    }
}
