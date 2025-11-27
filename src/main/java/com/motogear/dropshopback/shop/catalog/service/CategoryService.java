package com.motogear.dropshopback.shop.catalog.service;

import com.motogear.dropshopback.shop.catalog.domain.Category;
import com.motogear.dropshopback.shop.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        // Devuelve lista vacía si no hay categorías (OK, no es un error)
        return categoryRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Category findById(Long id) {return categoryRepository.findById(id).orElse(null); }
    @Transactional
    public Category saveCategory(Category category) {return categoryRepository.save(category); }
    @Transactional
    public void deleteCategory(Long id) {categoryRepository.deleteById(id); }
}
