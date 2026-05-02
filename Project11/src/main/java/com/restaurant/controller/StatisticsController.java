package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.entity.Employee;
import com.restaurant.entity.RefundRecord;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.OrdersService;
import com.restaurant.service.RefundRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    private final OrdersService ordersService;
    private final RefundRecordService refundRecordService;
    private final EmployeeService employeeService;
    
    @GetMapping
    public String index(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        Map<String, Object> todayStats = ordersService.getStatistics(today, today);
        Map<String, Object> monthStats = ordersService.getStatistics(monthStart, today);
        List<Map<String, Object>> dailySales = ordersService.getDailySales(
                today.minusDays(7), today);
        
        model.addAttribute("todayStats", todayStats);
        model.addAttribute("monthStats", monthStats);
        model.addAttribute("dailySales", dailySales);
        return "statistics/index";
    }
    
    @GetMapping("/data")
    @ResponseBody
    public Result<Map<String, Object>> getData(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> stats = ordersService.getStatistics(startDate, endDate);
        List<Map<String, Object>> dailySales = ordersService.getDailySales(startDate, endDate);
        
        stats.put("dailySales", dailySales);
        return Result.success(stats);
    }
    
    @GetMapping("/refund")
    public String refundList() {
        return "statistics/refund";
    }
    
    @GetMapping("/refund/list")
    @ResponseBody
    public Result<Page<RefundRecord>> refundList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Page<RefundRecord> result = refundRecordService.pageList(page, pageSize, orderNo, startDate, endDate);
        return Result.success(result);
    }
    
    @PostMapping("/refund")
    @ResponseBody
    public Result<RefundRecord> createRefund(
            @RequestParam Long orderId,
            @RequestParam(required = false) String reason) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        try {
            RefundRecord refund = refundRecordService.createRefund(orderId, reason, 
                    employee != null ? employee.getId() : null);
            return Result.success(refund);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
