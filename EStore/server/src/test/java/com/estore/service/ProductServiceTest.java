package com.estore.service;

import com.estore.common.PageResult;
import com.estore.entity.Product;
import com.estore.repository.ProductRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 单元测试")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setCategoryId(1L);
        product1.setName("iPhone 15 Pro Max");
        product1.setOriginalPrice(new BigDecimal("9999.00"));
        product1.setPrice(new BigDecimal("8999.00"));
        product1.setStock(100);
        product1.setSales(520);
        product1.setStatus(1);
        product1.setIsDeleted(0);

        product2 = new Product();
        product2.setId(2L);
        product2.setCategoryId(1L);
        product2.setName("华为Mate 60 Pro");
        product2.setOriginalPrice(new BigDecimal("7999.00"));
        product2.setPrice(new BigDecimal("6999.00"));
        product2.setStock(80);
        product2.setSales(380);
        product2.setStatus(1);
        product2.setIsDeleted(0);
    }

    @Test
    @DisplayName("获取商品列表 - 按分类")
    void getList_WithCategoryId_ShouldReturnFilteredProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findByCategoryIdAndStatusAndIsDeleted(
                eq(1L), eq(1), eq(0), any(Pageable.class)))
                .thenReturn(page);

        PageResult<Product> result = productService.getList(1, 10, 1L);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRecords().size());
        verify(productRepository).findByCategoryIdAndStatusAndIsDeleted(
                eq(1L), eq(1), eq(0), any(Pageable.class));
    }

    @Test
    @DisplayName("获取商品列表 - 不按分类")
    void getList_WithoutCategoryId_ShouldReturnAllProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findByStatusAndIsDeleted(eq(1), eq(0), any(Pageable.class)))
                .thenReturn(page);

        PageResult<Product> result = productService.getList(1, 10, null);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(productRepository).findByStatusAndIsDeleted(eq(1), eq(0), any(Pageable.class));
    }

    @Test
    @DisplayName("获取所有商品列表（管理员）")
    void listAll_ShouldReturnAllProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResult<Product> result = productService.listAll(1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("根据ID获取商品 - 成功")
    void getById_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("iPhone 15 Pro Max", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("根据ID获取商品 - 失败")
    void getById_ShouldThrowException_WhenNotExists() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getById(999L));
        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("获取热销商品")
    void getHotProducts_ShouldReturnTop10BySales() {
        when(productRepository.findTop10ByStatusAndIsDeletedOrderBySalesDesc(1, 0))
                .thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getHotProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository).findTop10ByStatusAndIsDeletedOrderBySalesDesc(1, 0);
    }

    @Test
    @DisplayName("创建商品 - 成功")
    void create_ShouldSaveProductWithDefaults() {
        Product newProduct = new Product();
        newProduct.setName("测试商品");
        newProduct.setCategoryId(1L);
        newProduct.setPrice(new BigDecimal("100.00"));

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        Product result = productService.create(newProduct);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(0, result.getStock());
        assertEquals(0, result.getSales());
        assertEquals(1, result.getStatus());
        assertEquals(0, result.getIsDeleted());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("更新商品 - 成功")
    void update_ShouldUpdateProduct() {
        Product updateRequest = new Product();
        updateRequest.setName("更新后的名称");
        updateRequest.setPrice(new BigDecimal("7999.00"));
        updateRequest.setStock(200);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("更新后的名称", result.getName());
        assertEquals(new BigDecimal("7999.00"), result.getPrice());
        assertEquals(200, result.getStock());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("删除商品 - 成功")
    void delete_ShouldSetIsDeletedToOne() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.delete(1L);

        assertEquals(1, product1.getIsDeleted());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product1);
    }

    @Test
    @DisplayName("删除商品 - 失败")
    void delete_ShouldThrowException_WhenNotExists() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.delete(999L));
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
