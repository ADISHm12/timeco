package com.timeco.application.Service.categoryservice;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.model.category.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    void addCategory(CategoryDto categoryDto);

    public void deleteCategoryById(Long id);

    public boolean updateCategoryById(Long id, String name);

    public void lockCategory(Long id);

    public void unlockCategory(Long id);

    Category getCategoryById(Long categoryId);

    List<Category> searchCategory(String searchTerm);

    public boolean categoryExists(String categoryName);

    public void calculateAndUpdateDiscountsForCategory(Category category);

    public double calculateDiscountedAmount(double price, Integer percentage);

    public void resetDiscountedAmountsForCategoryProducts(Category category) ;


    }