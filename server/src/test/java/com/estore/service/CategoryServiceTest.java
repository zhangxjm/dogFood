package com.estore.service;

import com.estore.entity.Category;
import com.estore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService 单元测试")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("手机数码");
        category1.setSort(1);
        category1.setStatus(1);

        category2 = new Category();
        category2.setId(2L);
        category2.setName("电脑办公");
        category2.setSort(2);
        category2.setStatus(1);
    }

    @Test
    @DisplayName("获取所有启用的分类")
    void getAll_ShouldReturnActiveCategories() {
        when(categoryRepository.findByStatusOrderBySortAsc(1))
                .thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("手机数码", result.get(0).getName());
        verify(categoryRepository).findByStatusOrderBySortAsc(1);
    }

    @Test
    @DisplayName("获取所有分类（包括禁用的）")
    void listAll_ShouldReturnAllCategories() {
        when(categoryRepository.findAll(any(Sort.class)))
                .thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("根据ID获取分类 - 成功")
    void getById_ShouldReturnCategory_WhenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        Category result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals("手机数码", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("根据ID获取分类 - 失败")
    void getById_ShouldThrowException_WhenNotExists() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getById(999L));
        verify(categoryRepository).findById(999L);
    }

    @Test
    @DisplayName("创建分类 - 成功")
    void create_ShouldSaveCategory() {
        Category newCategory = new Category();
        newCategory.setName("家用电器");

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        Category result = categoryService.create(newCategory);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(0, result.getSort());
        assertEquals(1, result.getStatus());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("更新分类 - 成功")
    void update_ShouldUpdateCategory() {
        Category updateRequest = new Category();
        updateRequest.setName("更新后的名称");
        updateRequest.setSort(10);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals("更新后的名称", result.getName());
        assertEquals(10, result.getSort());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("删除分类 - 成功")
    void delete_ShouldSetStatusToZero() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        categoryService.delete(1L);

        assertEquals(0, category1.getStatus());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(category1);
    }

    @Test
    @DisplayName("删除分类 - 失败")
    void delete_ShouldThrowException_WhenNotExists() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.delete(999L));
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
