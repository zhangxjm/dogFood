package com.estore.service;

import com.estore.entity.Category;
import com.estore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<Category> getAll() {
        return categoryRepository.findByStatusOrderBySortAsc(1);
    }
    
    public List<Category> listAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "sort"));
    }
    
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }
    
    @Transactional
    public Category create(Category category) {
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        return categoryRepository.save(category);
    }
    
    @Transactional
    public Category update(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if (category.getName() != null) {
            existingCategory.setName(category.getName());
        }
        if (category.getIcon() != null) {
            existingCategory.setIcon(category.getIcon());
        }
        if (category.getSort() != null) {
            existingCategory.setSort(category.getSort());
        }
        if (category.getStatus() != null) {
            existingCategory.setStatus(category.getStatus());
        }
        
        return categoryRepository.save(existingCategory);
    }
    
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        category.setStatus(0);
        categoryRepository.save(category);
    }
}
