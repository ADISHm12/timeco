package com.timeco.application.Service.categoryservice;

import com.timeco.application.Dto.SubCategoryDto;
import com.timeco.application.Repository.SubCategoryRepository;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class SubCategoryServiceImpl implements SubCategoryService{

    @Autowired
    SubCategoryRepository subCategoryRepository;


    @Override
    public Subcategory getSubCategoryById(Long SubCategoryId) {
        return subCategoryRepository.findById(SubCategoryId).get();
    }

    @Override
    public void deleteSubCategoryById(Long id) {
        this.subCategoryRepository.deleteById(id);
    }


    @Override
    @Transactional
    public boolean  updateSubCategoryById(Long subcategoryId, String  name) {
        Subcategory subcategory = subCategoryRepository.findById(subcategoryId).orElse(null);

        if (subcategory != null) {
            Subcategory existingSubCategory = subCategoryRepository.findByNameIgnoreCase(name);

            if (existingSubCategory == null || existingSubCategory.getId().equals(subcategoryId)) {
                subcategory.setName(name);
                subCategoryRepository.save(subcategory);
                return true;
            }
        }
        return false;
    }

    public void updateSubCategoryById(Long id, SubCategoryDto subCategoryDto) {
        Optional<Subcategory> subcategoryOptional = subCategoryRepository.findById(id);
        if (subcategoryOptional.isPresent()) {
            Subcategory subcategory = subcategoryOptional.get();
            subcategory.setName(subCategoryDto.getName()); // Set the name from the SubCategoryDto
            subCategoryRepository.save(subcategory); // Save the updated subcategory
        }
        // Handle the case where the subcategory is not present (optional is empty) if needed.
    }

    public void lockSubCategory(Long id) {
        Optional<Subcategory> lockSubCategory = subCategoryRepository.findById(id);
        if (lockSubCategory.isPresent()) {
            Subcategory subCategory = lockSubCategory.get();
            subCategory.setListed(false); // Set isListed to false
            subCategoryRepository.save(subCategory);
        }
    }

    @Override
    public void unlockSubCategory(Long id) {
        Optional<Subcategory> subCategoryOptional = subCategoryRepository.findById(id);
        if (subCategoryOptional.isPresent()) {
            Subcategory subCategory = subCategoryOptional.get();
            subCategory.setListed(true); // Set isListed to true
            subCategoryRepository.save(subCategory);
        }
    }
//    @Override
//    public void lockCategory(Long id) {
//        Category lockCategory = categoryRepository.findById(id).get();
//        lockCategory.setListed(false);
//        categoryRepository.save(lockCategory);
//    }
//
//
//    @Override
//    public void unlockCategory(Long id) {
//        Category lockCustomer = categoryRepository.findById(id).get();
//        lockCustomer.setListed(true);
//        categoryRepository.save(lockCustomer);
//    }



}
