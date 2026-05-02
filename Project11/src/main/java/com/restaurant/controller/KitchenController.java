package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.entity.Employee;
import com.restaurant.entity.Orders;
import com.restaurant.entity.OrderDetail;
import com.restaurant.service.EmployeeService;
import com.restaurant.service.OrdersService;
import com.restaurant.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/kitchen")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'KITCHEN')")
public class KitchenController {
    
    private final OrdersService ordersService;
    private final OrderDetailService orderDetailService;
    private final EmployeeService employeeService;
    
    @GetMapping
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        model.addAttribute("employee", employee);
        return "kitchen/index";
    }
    
    @GetMapping("/orders")
    @ResponseBody
    public Result<List<Orders>> getOrders(@RequestParam(required = false) Integer kitchenStatus) {
        List<Orders> orders = ordersService.listKitchenOrders(kitchenStatus);
        return Result.success(orders);
    }
    
    @GetMapping("/details/{orderId}")
    @ResponseBody
    public Result<List<OrderDetail>> getDetails(@PathVariable Long orderId) {
        List<OrderDetail> details = orderDetailService.getByOrderId(orderId);
        return Result.success(details);
    }
    
    @PostMapping("/confirm")
    @ResponseBody
    public Result<Boolean> confirm(@RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        boolean result = ordersService.updateKitchenStatus(orderId, 
                Orders.KITCHEN_STATUS_CONFIRMED, 
                employee != null ? employee.getId() : null);
        return result ? Result.success(true) : Result.error("操作失败");
    }
    
    @PostMapping("/start-cooking")
    @ResponseBody
    public Result<Boolean> startCooking(@RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        boolean result = ordersService.updateKitchenStatus(orderId, 
                Orders.KITCHEN_STATUS_COOKING, 
                employee != null ? employee.getId() : null);
        return result ? Result.success(true) : Result.error("操作失败");
    }
    
    @PostMapping("/complete")
    @ResponseBody
    public Result<Boolean> complete(@RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Employee employee = employeeService.getByUsername(username);
        
        boolean result = ordersService.updateKitchenStatus(orderId, 
                Orders.KITCHEN_STATUS_COMPLETED, 
                employee != null ? employee.getId() : null);
        return result ? Result.success(true) : Result.error("操作失败");
    }
    
    @PostMapping("/detail/complete")
    @ResponseBody
    public Result<Boolean> completeDetail(@RequestParam Long detailId) {
        boolean result = orderDetailService.updateDetailStatus(detailId, OrderDetail.STATUS_COMPLETED);
        return result ? Result.success(true) : Result.error("操作失败");
    }
}
