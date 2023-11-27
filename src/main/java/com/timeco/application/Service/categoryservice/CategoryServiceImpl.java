package com.timeco.application.Service.categoryservice;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.CategoryOffer;
import com.timeco.application.model.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private ProductService  productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository ;
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
public boolean updateCategoryById(Long categoryId, String  name) {
    Category category = categoryRepository.findById(categoryId).orElse(null);

    if (category != null) {
        Category existingCategory = categoryRepository.findByNameIgnoreCase(name);

        if (existingCategory == null || existingCategory.getId().equals(categoryId)) {
            category.setName(name);
            categoryRepository.save(category);
            return true;
        }
    }
    return false;
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

    @Override
    public boolean categoryExists(String categoryName) {
        Category existingCategory = categoryRepository.findByNameIgnoreCase(categoryName);
        return existingCategory != null;
    }

    @Override
    public void calculateAndUpdateDiscountsForCategory(Category category) {
        CategoryOffer  offer = category.getCategoryOffer();

        // Fetch products related to this category
        List<Product > products = productService.getProductsByCategory(category);

        // Calculate and update discounted amounts for products
        for (Product product : products) {
            double discount = calculateDiscountedAmount(product.getPrice(), offer.getPercentage());
            double discountedAmount=product.getPrice()-discount;
            product.setDiscountedAmount(discountedAmount);
        }

        // Save or update the products with updated discounted amounts
        productRepository.saveAll(products);
    }


    @Override
    public double calculateDiscountedAmount(double price, Integer percentage) {
        return (percentage / 100.0) * price;
    }

    @Override
    public void resetDiscountedAmountsForCategoryProducts(Category category) {
        List<Product> products = productService.getProductsByCategory(category);

        for (Product product : products) {
            // Reset or remove discounted amount for each product
            product.setDiscountedAmount(null); // Assuming 0.0 means no discount
        }
        // Save or update products with reset discounted amounts
        productRepository.saveAll(products);
    }

}
