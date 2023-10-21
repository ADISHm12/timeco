package com.timeco.application.Service.categoryservice;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.model.category.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public void addCategory(CategoryDto categoryDto) {
        Category category=new Category();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long id) {
        this.categoryRepository.deleteById(id);
    }

//
    @Override
    @Transactional
    public void updateCategoryById(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if(category!=null)
        {
            category.setName(categoryDto.getName());
            categoryRepository.save(category);
        }
//        categoryRepository.save(category);

    }
    @Override
    public void lockCategory(Long id) {
        Category lockCategory = categoryRepository.findById(id).get();
        lockCategory.setListed(false);
        categoryRepository.save(lockCategory);
    }


    @Override
    public void unlockCategory(Long id) {
        Category lockCustomer = categoryRepository.findById(id).get();
        lockCustomer.setListed(true);
        categoryRepository.save(lockCustomer);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).get(); // Handle null if not found
    }

    @Override
    public List<Category> searchCategory(String searchTerm) {
        return categoryRepository.findByNameContaining(searchTerm);
    }

}
