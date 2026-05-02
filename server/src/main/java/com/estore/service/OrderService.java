package com.estore.service;

import com.estore.common.PageResult;
import com.estore.entity.*;
import com.estore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final UserAddressRepository userAddressRepository;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    @Transactional
    public Order create(Long userId, Long addressId, List<Long> cartIds) {
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        List<ShoppingCart> carts = shoppingCartRepository.findByUserId(userId);
        List<ShoppingCart> selectedCarts = carts.stream()
                .filter(cart -> cartIds.contains(cart.getId()) && cart.getSelected() == 1)
                .toList();
        
        if (selectedCarts.isEmpty()) {
            throw new RuntimeException("请选择要购买的商品");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (ShoppingCart cart : selectedCarts) {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            
            if (product.getStatus() == 0 || product.getIsDeleted() == 1) {
                throw new RuntimeException("商品" + product.getName() + "已下架");
            }
            
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品" + product.getName() + "库存不足");
            }
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setProductPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalAmount(itemTotal);
            orderItems.add(item);
        }
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order = orderRepository.save(order);
        
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
            orderItemRepository.save(item);
        }
        
        for (ShoppingCart cart : selectedCarts) {
            Product product = productRepository.findById(cart.getProductId()).orElseThrow();
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productRepository.save(product);
        }
        
        shoppingCartRepository.deleteByUserIdAndIdIn(userId, cartIds);
        
        return order;
    }
    
    public Order getById(Long id, Long userId) {
        return orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }
    
    public Order getByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }
    
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    public PageResult<Order> getByUserId(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> page;
        
        if (status != null) {
            page = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            page = orderRepository.findByUserId(userId, pageable);
        }
        
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize);
    }
    
    public PageResult<Order> listAll(Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> page;
        
        if (status != null) {
            page = orderRepository.findByStatus(status, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNum, pageSize);
    }
    
    @Transactional
    public Order pay(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order delivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(2);
        order.setDeliveryTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order confirm(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(3);
        order.setConfirmTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order cancel(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消未支付的订单");
        }
        
        order.setStatus(4);
        order.setCloseTime(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String random = String.format("%06d", RANDOM.nextInt(1000000));
        return timestamp + random;
    }
}
