package com.timeco.application.Service.categoryservice;


import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.Dto.SubCategoryDto;
import com.timeco.application.Repository.SubCategoryRepository;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubCategoryService {

    Subcategory getSubCategoryById(Long SubCategoryId) ;

    public void deleteSubCategoryById(Long id);
    public void updateSubCategoryById(Long id, SubCategoryDto subCategoryDto);

    public void lockSubCategory(Long id) ;

    public void unlockSubCategory(Long id) ;


//    public List<Subcategory> findByCategoryId(Long categoryId) {
//        // Implement the logic to fetch subcategories by categoryId using the repository
//        return subCategoryRepository.findByCategoryId(categoryId);
//    }
}
