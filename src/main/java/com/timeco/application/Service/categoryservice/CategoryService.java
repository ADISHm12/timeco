package com.timeco.application.Service.categoryservice;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.model.category.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    void addCategory(CategoryDto categoryDto);
    public void deleteCategoryById(Long id);
    public void updateCategoryById(Long id,CategoryDto categoryDto);

    public void lockCategory(Long id) ;

    public void unlockCategory(Long id) ;
    Category getCategoryById(Long categoryId) ;

    List<Category> searchCategory(String searchTerm);

}
