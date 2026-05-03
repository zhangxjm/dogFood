package com.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.inventory.common.PageResult;
import com.inventory.dto.SaleOrderDTO;
import com.inventory.entity.SaleOrder;
import com.inventory.entity.SaleOrderItem;
import com.inventory.exception.BusinessException;
import com.inventory.mapper.SaleOrderItemMapper;
import com.inventory.mapper.SaleOrderMapper;
import com.inventory.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleOrderService extends ServiceImpl<SaleOrderMapper, SaleOrder> {

    private final SaleOrderMapper saleOrderMapper;
    private final SaleOrderItemMapper saleOrderItemMapper;
    private final InventoryService inventoryService;

    public PageResult<SaleOrder> pageQuery(Long current, Long size, String keyword, Integer status) {
        Page<SaleOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SaleOrder::getOrderNo, keyword)
                    .or()
                    .like(SaleOrder::getCustomerName, keyword);
        }
        
        if (status != null) {
            wrapper.eq(SaleOrder::getStatus, status);
        }
        
        wrapper.orderByDesc(SaleOrder::getCreateTime);
        Page<SaleOrder> result = this.page(page, wrapper);
        
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent()
        );
    }

    public SaleOrder getDetail(Long id) {
        SaleOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }
        return order;
    }

    public List<SaleOrderItem> getItems(Long orderId) {
        return saleOrderItemMapper.selectList(
                new LambdaQueryWrapper<SaleOrderItem>()
                        .eq(SaleOrderItem::getOrderId, orderId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public SaleOrder createOrder(SaleOrderDTO dto, UserPrincipal user) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("请添加销售商品");
        }
        
        SaleOrder order = new SaleOrder();
        order.setOrderNo(generateOrderNo());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setWarehouseId(dto.getWarehouseId());
        order.setOperatorId(user.getUserId());
        order.setOperatorName(user.getUsername());
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SaleOrderDTO.SaleOrderItemDTO item : dto.getItems()) {
            BigDecimal amount = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(amount);
        }
        order.setTotalAmount(totalAmount);
        
        this.save(order);
        
        for (SaleOrderDTO.SaleOrderItemDTO item : dto.getItems()) {
            SaleOrderItem orderItem = new SaleOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setAmount(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            saleOrderItemMapper.insert(orderItem);
        }
        
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void outStock(Long orderId, UserPrincipal user) {
        SaleOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许出库");
        }
        
        List<SaleOrderItem> items = getItems(orderId);
        if (items.isEmpty()) {
            throw new BusinessException("订单没有商品明细");
        }
        
        for (SaleOrderItem item : items) {
            inventoryService.subtractStock(
                    item.getProductId(),
                    order.getWarehouseId(),
                    item.getQuantity(),
                    "SALE",
                    order.getId(),
                    order.getOrderNo(),
                    user.getUserId(),
                    user.getUsername(),
                    "销售出库"
            );
        }
        
        order.setStatus(1);
        this.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        SaleOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        order.setStatus(2);
        this.updateById(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "SO" + date + random;
    }
}
