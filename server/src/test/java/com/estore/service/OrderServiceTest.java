package com.estore.service;

import com.estore.common.PageResult;
import com.estore.entity.Order;
import com.estore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 单元测试")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserAddressRepository userAddressRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        order1 = new Order();
        order1.setId(1L);
        order1.setOrderNo("20240501100001");
        order1.setUserId(1L);
        order1.setTotalAmount(new BigDecimal("8999.00"));
        order1.setPayAmount(new BigDecimal("8999.00"));
        order1.setStatus(0);
        order1.setCreateTime(LocalDateTime.now());

        order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("20240501100002");
        order2.setUserId(1L);
        order2.setTotalAmount(new BigDecimal("6999.00"));
        order2.setPayAmount(new BigDecimal("6999.00"));
        order2.setStatus(1);
        order2.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("根据用户ID获取订单列表 - 带状态过滤")
    void getByUserId_WithStatus_ShouldReturnFilteredOrders() {
        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2));

        when(orderRepository.findByUserIdAndStatus(eq(1L), eq(1), any(Pageable.class)))
                .thenReturn(page);

        PageResult<Order> result = orderService.getByUserId(1L, 1, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(orderRepository).findByUserIdAndStatus(eq(1L), eq(1), any(Pageable.class));
    }

    @Test
    @DisplayName("根据用户ID获取订单列表 - 不带状态过滤")
    void getByUserId_WithoutStatus_ShouldReturnAllUserOrders() {
        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2));

        when(orderRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        PageResult<Order> result = orderService.getByUserId(1L, null, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(orderRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("获取所有订单列表（管理员）- 带状态过滤")
    void listAll_WithStatus_ShouldReturnFilteredOrders() {
        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2));

        when(orderRepository.findByStatus(eq(1), any(Pageable.class)))
                .thenReturn(page);

        PageResult<Order> result = orderService.listAll(1, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(orderRepository).findByStatus(eq(1), any(Pageable.class));
    }

    @Test
    @DisplayName("获取所有订单列表（管理员）- 不带状态过滤")
    void listAll_WithoutStatus_ShouldReturnAllOrders() {
        Page<Order> page = new PageImpl<>(Arrays.asList(order1, order2));

        when(orderRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        PageResult<Order> result = orderService.listAll(null, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(orderRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("根据ID和用户ID获取订单 - 成功")
    void getById_WithUserId_ShouldReturnOrder_WhenExists() {
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));

        Order result = orderService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals("20240501100001", result.getOrderNo());
        verify(orderRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("根据ID和用户ID获取订单 - 失败")
    void getById_WithUserId_ShouldThrowException_WhenNotExists() {
        when(orderRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getById(999L, 1L));
        verify(orderRepository).findByIdAndUserId(999L, 1L);
    }

    @Test
    @DisplayName("根据订单号获取订单 - 成功")
    void getByOrderNo_ShouldReturnOrder_WhenExists() {
        when(orderRepository.findByOrderNo("20240501100001")).thenReturn(Optional.of(order1));

        Order result = orderService.getByOrderNo("20240501100001");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findByOrderNo("20240501100001");
    }

    @Test
    @DisplayName("根据订单号获取订单 - 失败")
    void getByOrderNo_ShouldThrowException_WhenNotExists() {
        when(orderRepository.findByOrderNo("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getByOrderNo("INVALID"));
        verify(orderRepository).findByOrderNo("INVALID");
    }

    @Test
    @DisplayName("支付订单 - 成功")
    void pay_ShouldUpdateStatusToPaid() {
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.pay(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getStatus());
        assertNotNull(result.getPayTime());
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("支付订单 - 状态不正确")
    void pay_ShouldThrowException_WhenStatusNotPending() {
        order1.setStatus(1);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));

        assertThrows(RuntimeException.class, () -> orderService.pay(1L, 1L));
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("发货订单 - 成功")
    void delivery_ShouldUpdateStatusToShipped() {
        order2.setStatus(1);
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.delivery(2L);

        assertNotNull(result);
        assertEquals(2, result.getStatus());
        assertNotNull(result.getDeliveryTime());
        verify(orderRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("发货订单 - 状态不正确")
    void delivery_ShouldThrowException_WhenStatusNotPaid() {
        order2.setStatus(0);
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order2));

        assertThrows(RuntimeException.class, () -> orderService.delivery(2L));
        verify(orderRepository).findById(2L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("确认收货 - 成功")
    void confirm_ShouldUpdateStatusToCompleted() {
        order1.setStatus(2);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.confirm(1L, 1L);

        assertNotNull(result);
        assertEquals(3, result.getStatus());
        assertNotNull(result.getConfirmTime());
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("取消订单 - 成功")
    void cancel_ShouldUpdateStatusToCancelled() {
        order1.setStatus(0);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.cancel(1L, 1L);

        assertNotNull(result);
        assertEquals(4, result.getStatus());
        assertNotNull(result.getCloseTime());
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("取消订单 - 只能取消未支付订单")
    void cancel_ShouldThrowException_WhenStatusNotPending() {
        order1.setStatus(1);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order1));

        assertThrows(RuntimeException.class, () -> orderService.cancel(1L, 1L));
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
