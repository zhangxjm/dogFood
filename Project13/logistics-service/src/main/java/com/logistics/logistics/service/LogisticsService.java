package com.logistics.logistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.common.constant.OrderStatus;
import com.logistics.common.dto.LogisticsUpdateDTO;
import com.logistics.common.entity.Logistics;
import com.logistics.common.entity.LogisticsTrack;
import com.logistics.common.entity.Order;
import com.logistics.common.feign.OrderFeignClient;
import com.logistics.logistics.mapper.LogisticsMapper;
import com.logistics.logistics.mapper.LogisticsTrackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsService extends ServiceImpl<LogisticsMapper, Logistics> {

    private final LogisticsTrackMapper logisticsTrackMapper;
    private final OrderFeignClient orderFeignClient;

    @Transactional(rollbackFor = Exception.class)
    public Logistics initLogistics(Long orderId) {
        Order order = orderFeignClient.getById(orderId).getData();
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics existing = this.getOne(wrapper);
        if (existing != null) {
            return existing;
        }
        
        Logistics logistics = new Logistics();
        logistics.setOrderId(orderId);
        logistics.setOrderNo(order.getOrderNo());
        logistics.setCurrentLocation(order.getSenderAddress());
        logistics.setCurrentLat(order.getSenderLat());
        logistics.setCurrentLng(order.getSenderLng());
        logistics.setStatus(OrderStatus.PENDING.getCode());
        this.save(logistics);
        
        addTrack(logistics, order.getSenderAddress(), order.getSenderLat(), order.getSenderLng(), 
                "订单已创建，等待审核", OrderStatus.PENDING.getCode());
        
        log.info("物流信息初始化成功：orderId={}", orderId);
        return logistics;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLogistics(LogisticsUpdateDTO dto) {
        Long orderId = dto.getOrderId();
        
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics logistics = this.getOne(wrapper);
        
        if (logistics == null) {
            logistics = initLogistics(orderId);
        }
        
        logistics.setCurrentLocation(dto.getLocation());
        logistics.setCurrentLat(dto.getLat());
        logistics.setCurrentLng(dto.getLng());
        logistics.setStatus(dto.getStatus());
        if (dto.getCourierName() != null) {
            logistics.setCourierName(dto.getCourierName());
        }
        if (dto.getCourierPhone() != null) {
            logistics.setCourierPhone(dto.getCourierPhone());
        }
        this.updateById(logistics);
        
        addTrack(logistics, dto.getLocation(), dto.getLat(), dto.getLng(), 
                dto.getDescription(), dto.getStatus());
        
        orderFeignClient.updateStatus(orderId, dto.getStatus());
        
        log.info("物流状态更新成功：orderId={}, status={}", orderId, dto.getStatus());
    }

    public Logistics getByOrderId(Long orderId) {
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Logistics::getOrderId, orderId);
        return this.getOne(wrapper);
    }

    public List<LogisticsTrack> getTracksByOrderId(Long orderId) {
        LambdaQueryWrapper<LogisticsTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LogisticsTrack::getOrderId, orderId)
                .orderByAsc(LogisticsTrack::getCreateTime);
        return logisticsTrackMapper.selectList(wrapper);
    }

    public Page<Logistics> pageList(Integer current, Integer size, Integer status, String keyword) {
        Page<Logistics> page = new Page<>(current, size);
        LambdaQueryWrapper<Logistics> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Logistics::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Logistics::getOrderNo, keyword)
                    .or().like(Logistics::getCurrentLocation, keyword)
                    .or().like(Logistics::getCourierName, keyword);
        }
        wrapper.orderByDesc(Logistics::getUpdateTime);
        return this.page(page, wrapper);
    }

    private void addTrack(Logistics logistics, String location, Double lat, Double lng, 
                          String description, Integer status) {
        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logistics.getId());
        track.setOrderId(logistics.getOrderId());
        track.setOrderNo(logistics.getOrderNo());
        track.setLocation(location);
        track.setLat(lat);
        track.setLng(lng);
        track.setDescription(description);
        track.setStatus(status);
        logisticsTrackMapper.insert(track);
    }
}
