package com.eventfull.scraper.service;

import com.eventfull.scraper.dao.CategoryRepository;
import com.eventfull.scraper.model.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findCategoryByTitle(String categoryTitle) {
        return categoryRepository.getCategoryByTitle(categoryTitle);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
