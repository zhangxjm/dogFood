package com.restaurant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.Result;
import com.restaurant.dto.OrderRequest;
import com.restaurant.entity.*;
import com.restaurant.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrdersService ordersService;
    private final OrderDetailService orderDetailService;
    private final DiningTableService diningTableService;
    private final EmployeeService employeeService;
    private final CategoryService categoryService;
    private final DishService dishService;
    
    @GetMapping
    public String list() {
        return "order/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public Result<Page<Orders>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Page<Orders> result = ordersService.pageList(page, pageSize, orderNo, status, tableId, startDate, endDate);
        return Result.success(result);
    }
    
    @GetMapping("/create")
    public String createOrder(Model model) {
        List<DiningTable> tables = diningTableService.listByStatus(DiningTable.STATUS_FREE);
        List<Category> categories = categoryService.listEnabled();
        List<Dish> dishes = dishService.listEnabled();
        
        model.addAttribute("tables", tables);
        model.addAttribute("categories", categories);
        model.addAttribute("dishes", dishes);
        return "order/create";
    }
    
    @GetMapping("/scan")
    public String scanOrder(@RequestParam Long tableId, Model model) {
        DiningTable table = diningTableService.getById(tableId);
        if (table == null) {
            model.addAttribute("error", "餐桌不存在");
            return "error/500";
        }
        
        List<Category> categories = categoryService.listEnabled();
        List<Dish> dishes = dishService.listEnabled();
        
        model.addAttribute("table", table);
        model.addAttribute("categories", categories);
        model.addAttribute("dishes", dishes);
        return "order/scan";
    }
    
    @PostMapping("/save")
    @ResponseBody
    public Result<Orders> saveOrder(@RequestBody Orders order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        Long employeeId = employee != null ? employee.getId() : null;
        
        Orders saved = ordersService.createOrder(order, employeeId);
        return Result.success(saved);
    }
    
    @PostMapping("/create-full")
    @ResponseBody
    public Result<Orders> createFullOrder(@RequestBody OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        Long employeeId = employee != null ? employee.getId() : null;
        
        if (request.getDetails() == null || request.getDetails().isEmpty()) {
            return Result.error("未选择菜品");
        }
        
        Orders order = new Orders();
        order.setTableId(request.getTableId());
        order.setTableName(request.getTableName());
        order.setCustomerCount(request.getCustomerCount() != null ? request.getCustomerCount() : 1);
        order.setTotalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayableAmount(request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO);
        order.setRemark(request.getRemark());
        
        Orders saved = ordersService.createOrder(order, employeeId);
        
        orderDetailService.saveOrderDetails(saved.getId(), request.getDetails());
        
        List<OrderDetail> allDetails = orderDetailService.getByOrderId(saved.getId());
        BigDecimal totalAmount = allDetails.stream()
                .filter(d -> !OrderDetail.STATUS_REFUNDED.equals(d.getStatus()))
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        saved.setTotalAmount(totalAmount);
        saved.setPayableAmount(totalAmount);
        ordersService.updateById(saved);
        
        return Result.success(saved);
    }
    
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrderWithDetails(id);
        List<OrderDetail> details = orderDetailService.getByOrderId(id);
        
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "order/detail";
    }
    
    @GetMapping("/detail-data/{id}")
    @ResponseBody
    public Result<List<OrderDetail>> detailData(@PathVariable Long id) {
        List<OrderDetail> details = orderDetailService.getByOrderId(id);
        return Result.success(details);
    }
    
    @PostMapping("/add-items")
    @ResponseBody
    public Result<Boolean> addItems(@RequestParam Long orderId,
                                     @RequestParam String details) {
        if (details == null || details.isEmpty()) {
            return Result.error("未选择菜品");
        }
        
        List<OrderDetail> detailList;
        try {
            detailList = com.alibaba.fastjson2.JSON.parseArray(details, OrderDetail.class);
        } catch (Exception e) {
            return Result.error("订单详情格式错误");
        }
        
        if (detailList == null || detailList.isEmpty()) {
            return Result.error("未选择菜品");
        }
        
        orderDetailService.saveOrderDetails(orderId, detailList);
        
        Orders order = ordersService.getById(orderId);
        List<OrderDetail> allDetails = orderDetailService.getByOrderId(orderId);
        
        BigDecimal totalAmount = allDetails.stream()
                .filter(d -> !OrderDetail.STATUS_REFUNDED.equals(d.getStatus()))
                .map(OrderDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalAmount(totalAmount);
        order.setPayableAmount(totalAmount.subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO));
        ordersService.updateById(order);
        
        return Result.success(true);
    }
    
    @GetMapping("/checkout/{id}")
    public String checkout(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrderWithDetails(id);
        List<OrderDetail> details = orderDetailService.getByOrderId(id);
        
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "order/checkout";
    }
    
    @PostMapping("/pay")
    @ResponseBody
    public Result<Orders> pay(@RequestParam Long orderId,
                               @RequestParam String paymentMethod,
                               @RequestParam(required = false) BigDecimal discountAmount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        Orders order = ordersService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            order.setDiscountAmount(discountAmount);
            order.setPayableAmount(order.getTotalAmount().subtract(discountAmount));
            ordersService.updateById(order);
        }
        
        Orders paid = ordersService.payOrder(orderId, paymentMethod, 
                employee != null ? employee.getId() : null);
        return Result.success(paid);
    }
    
    @PostMapping("/cancel")
    @ResponseBody
    public Result<Orders> cancel(@RequestParam Long orderId,
                                  @RequestParam(required = false) String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        try {
            Orders cancelled = ordersService.cancelOrder(orderId, reason, 
                    employee != null ? employee.getId() : null);
            return Result.success(cancelled);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/complete")
    @ResponseBody
    public Result<Orders> complete(@RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        try {
            Orders completed = ordersService.completeOrder(orderId, 
                    employee != null ? employee.getId() : null);
            return Result.success(completed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
