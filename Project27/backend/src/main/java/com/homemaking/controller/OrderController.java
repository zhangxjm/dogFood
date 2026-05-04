package com.homemaking.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homemaking.common.PageResult;
import com.homemaking.common.Result;
import com.homemaking.entity.*;
import com.homemaking.mapper.*;
import com.homemaking.security.JwtTokenUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private WorkerMapper workerMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @PostMapping
    public Result<String> createOrder(@RequestBody CreateOrderRequest request,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        ServiceItem serviceItem = serviceItemMapper.selectById(request.getServiceItemId());
        if (serviceItem == null) {
            return Result.error("服务项目不存在");
        }
        
        Orders order = new Orders();
        order.setOrderNo("HM" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setServiceItemId(request.getServiceItemId());
        order.setServiceName(serviceItem.getName());
        order.setServiceQuantity(request.getQuantity());
        order.setTotalAmount(serviceItem.getPrice().multiply(new BigDecimal(request.getQuantity())));
        order.setServiceAddress(request.getServiceAddress());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setServiceTime(request.getServiceTime());
        order.setStatus("PENDING");
        order.setRemark(request.getRemark());
        
        if (request.getWorkerId() != null) {
            order.setWorkerId(request.getWorkerId());
        }
        
        ordersMapper.insert(order);
        return Result.success(order.getOrderNo());
    }
    
    @GetMapping
    public Result<PageResult<Orders>> getOrders(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        String role = jwtTokenUtil.getRoleFromToken(jwt);
        
        Page<Orders> page = new Page<>(current, size);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        
        if ("USER".equals(role)) {
            queryWrapper.eq(Orders::getUserId, userId);
        } else if ("WORKER".equals(role)) {
            LambdaQueryWrapper<Worker> workerQuery = new LambdaQueryWrapper<>();
            workerQuery.eq(Worker::getUserId, userId);
            Worker worker = workerMapper.selectOne(workerQuery);
            if (worker != null) {
                queryWrapper.eq(Orders::getWorkerId, worker.getId());
            } else {
                queryWrapper.eq(Orders::getId, -1);
            }
        }
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Orders::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Orders::getCreateTime);
        
        Page<Orders> result = ordersMapper.selectPage(page, queryWrapper);
        
        for (Orders order : result.getRecords()) {
            SysUser user = userMapper.selectById(order.getUserId());
            if (user != null) {
                order.setUserName(user.getRealName());
                order.setUserPhone(user.getPhone());
            }
            if (order.getWorkerId() != null) {
                LambdaQueryWrapper<Worker> workerQuery = new LambdaQueryWrapper<>();
                workerQuery.eq(Worker::getId, order.getWorkerId());
                Worker worker = workerMapper.selectOne(workerQuery);
                if (worker != null) {
                    SysUser workerUser = userMapper.selectById(worker.getUserId());
                    if (workerUser != null) {
                        order.setWorkerName(workerUser.getRealName());
                        order.setWorkerPhone(workerUser.getPhone());
                    }
                }
            }
        }
        
        PageResult<Orders> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setSize(result.getSize());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setPages(result.getPages());
        
        return Result.success(pageResult);
    }
    
    @GetMapping("/{id}")
    public Result<Orders> getOrderById(@PathVariable Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        SysUser user = userMapper.selectById(order.getUserId());
        if (user != null) {
            order.setUserName(user.getRealName());
            order.setUserPhone(user.getPhone());
        }
        
        if (order.getWorkerId() != null) {
            LambdaQueryWrapper<Worker> workerQuery = new LambdaQueryWrapper<>();
            workerQuery.eq(Worker::getId, order.getWorkerId());
            Worker worker = workerMapper.selectOne(workerQuery);
            if (worker != null) {
                SysUser workerUser = userMapper.selectById(worker.getUserId());
                if (workerUser != null) {
                    order.setWorkerName(workerUser.getRealName());
                    order.setWorkerPhone(workerUser.getPhone());
                }
            }
        }
        
        return Result.success(order);
    }
    
    @PutMapping("/{id}/accept")
    public Result<String> acceptOrder(@PathVariable Long id,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        LambdaQueryWrapper<Worker> workerQuery = new LambdaQueryWrapper<>();
        workerQuery.eq(Worker::getUserId, userId);
        Worker worker = workerMapper.selectOne(workerQuery);
        
        if (worker == null || !"APPROVED".equals(worker.getAuditStatus())) {
            return Result.error("您不是认证师傅，无法接单");
        }
        
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            return Result.error("订单状态不允许接单");
        }
        
        order.setWorkerId(worker.getId());
        order.setStatus("ACCEPTED");
        ordersMapper.updateById(order);
        
        worker.setOrderCount(worker.getOrderCount() + 1);
        workerMapper.updateById(worker);
        
        return Result.success("接单成功");
    }
    
    @PutMapping("/{id}/complete")
    public Result<String> completeOrder(@PathVariable Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!"ACCEPTED".equals(order.getStatus())) {
            return Result.error("订单状态不允许完成");
        }
        
        order.setStatus("COMPLETED");
        ordersMapper.updateById(order);
        
        return Result.success("订单已完成");
    }
    
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id,
                                        @RequestParam(required = false) String reason,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            return Result.error("订单状态不允许取消");
        }
        
        if (!order.getUserId().equals(userId)) {
            return Result.error("只能取消自己的订单");
        }
        
        order.setStatus("CANCELLED");
        order.setRejectReason(reason);
        ordersMapper.updateById(order);
        
        return Result.success("订单已取消");
    }
    
    @PostMapping("/{id}/review")
    public Result<String> reviewOrder(@PathVariable Long id,
                                        @RequestBody ReviewRequest request,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
        
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!"COMPLETED".equals(order.getStatus())) {
            return Result.error("订单状态不允许评价");
        }
        
        if (!order.getUserId().equals(userId)) {
            return Result.error("只能评价自己的订单");
        }
        
        LambdaQueryWrapper<Review> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(Review::getOrderId, id);
        if (reviewMapper.selectOne(existQuery) != null) {
            return Result.error("该订单已评价");
        }
        
        Review review = new Review();
        review.setOrderId(id);
        review.setUserId(userId);
        review.setWorkerId(order.getWorkerId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        reviewMapper.insert(review);
        
        order.setStatus("RATED");
        ordersMapper.updateById(order);
        
        if (order.getWorkerId() != null) {
            Worker worker = workerMapper.selectById(order.getWorkerId());
            if (worker != null) {
                LambdaQueryWrapper<Review> reviewQuery = new LambdaQueryWrapper<>();
                reviewQuery.eq(Review::getWorkerId, worker.getId());
                List<Review> reviews = reviewMapper.selectList(reviewQuery);
                
                double avgRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(5.0);
                
                worker.setRating(new BigDecimal(String.format("%.2f", avgRating)));
                workerMapper.updateById(worker);
            }
        }
        
        return Result.success("评价成功");
    }
    
    @Data
    public static class CreateOrderRequest {
        private Long serviceItemId;
        private Integer quantity;
        private String serviceAddress;
        private String contactName;
        private String contactPhone;
        private LocalDateTime serviceTime;
        private Long workerId;
        private String remark;
    }
    
    @Data
    public static class ReviewRequest {
        private Integer rating;
        private String content;
        private String images;
    }
}
