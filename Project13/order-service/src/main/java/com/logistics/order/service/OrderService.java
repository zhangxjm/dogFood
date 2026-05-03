package com.logistics.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.common.constant.OrderStatus;
import com.logistics.common.dto.OrderCreateDTO;
import com.logistics.common.entity.Order;
import com.logistics.common.feign.LogisticsFeignClient;
import com.logistics.common.util.FreightCalculator;
import com.logistics.common.util.OrderNoGenerator;
import com.logistics.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private final LogisticsFeignClient logisticsFeignClient;

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setOrderNo(OrderNoGenerator.generateOrderNo());
        order.setUserId(dto.getUserId());
        order.setSenderName(dto.getSenderName());
        order.setSenderPhone(dto.getSenderPhone());
        order.setSenderAddress(dto.getSenderAddress());
        order.setSenderLat(dto.getSenderLat());
        order.setSenderLng(dto.getSenderLng());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setReceiverLat(dto.getReceiverLat());
        order.setReceiverLng(dto.getReceiverLng());
        order.setGoodsName(dto.getGoodsName());
        order.setWeight(dto.getWeight());
        order.setVolume(dto.getVolume());
        order.setRemark(dto.getRemark());
        order.setStatus(OrderStatus.PENDING.getCode());
        
        BigDecimal freight = FreightCalculator.simpleCalculate(dto.getWeight(), dto.getVolume());
        order.setFreight(freight);
        
        order.setExpectedTime(LocalDateTime.now().plusDays(3));
        
        this.save(order);
        
        logisticsFeignClient.initLogistics(order.getId());
        
        log.info("订单创建成功：{}", order.getOrderNo());
        return order;
    }

    public Page<Order> pageList(Integer current, Integer size, Long userId, Integer status, String keyword) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Order::getOrderNo, keyword)
                    .or().like(Order::getSenderName, keyword)
                    .or().like(Order::getReceiverName, keyword);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return this.page(page, wrapper);
    }

    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return this.getOne(wrapper);
    }

    public List<Order> getByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long orderId, Integer status) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        order.setStatus(status);
        this.updateById(order);
        log.info("订单状态更新：orderId={}, status={}", orderId, status);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewOrder(Long orderId, Boolean approved) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new IllegalArgumentException("订单不是待审核状态");
        }
        if (approved) {
            order.setStatus(OrderStatus.REVIEWED.getCode());
            log.info("订单审核通过：{}", orderId);
        } else {
            order.setStatus(OrderStatus.CANCELLED.getCode());
            log.info("订单审核拒绝：{}", orderId);
        }
        this.updateById(order);
    }
}
