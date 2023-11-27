package com.timeco.application.Service.productservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.product.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    @Transactional
    public Product addProduct(Product product) {

        return productRepository.save(product);
    }

    //    @Override
//    @Transactional
//    public void updateProductById( ProductDto products,Long id) {
//        System.out.println(id + "99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999");
//
//        // Retrieve the existing Product from the database
//        Product product = productRepository.findById(id).orElse(null);
//        System.out.println("Received id n  sjkdhfgajhs: " + product.getId());
//
//
//        System.out.println(products.getCategory().getId() + "9999999999999999999999999999999999999999999999900000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000878888");
//
//        // Retrieve the Category based on categoryId from the ProductDto
//        Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
//        System.out.println(category);
//
//        if (product != null && category != null) {
//            product.setProductName(products.getProductName());
//            product.setDescription(products.getDescription());
//            product.setProductImages(products.getImageNames());
//            product.setPrice(products.getPrice());
//            product.setQuantity(products.getQuantity());
//
//            // Set the Category on the Product
//            product.setCategory(category);
//        }
//
//        // Save the updated Product
//        productRepository.save(product);
//    }
    @Override
    @Transactional
    public void updateProductById(ProductDto products, Long id) {
        // Retrieve the existing Product from the database
        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            // Retrieve the Category based on categoryId from the ProductDto
            Category category = categoryRepository.findById(products.getCategory().getId()).orElse(null);

            if (category != null) {
                product.setProductName(products.getProductName());
                product.setDescription(products.getDescription());
                product.setPrice(products.getPrice());
                product.setQuantity(products.getQuantity());

                // Set the Category on the Product
                product.setCategory(category);

                // Handle product images
            }

            // Save the updated Product
            productRepository.save(product);
        }
    }

    @Override
    public void deleteProductById(Long id) {

        this.productRepository.deleteById(id);

    }

    @Override
    public Page<Product> getAllProducts() {
        return null;
    }

    @Override
    public Page<Product> searchProducts(String searchTerm) {
        return null;
    }


    @Override
    public List<Product> getAllProductsWithImages() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @Override
    public Product getProductById(Long productId) {
        // Use the repository to retrieve a product by its ID
        return productRepository.findById(productId).orElse(null);
    }

    @Override
    public int getTotalStock() {
        int totalStock = 0;

        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            totalStock += product.getQuantity();
        }

        return totalStock;
    }

    @Override
    public List<Product> sortProductsByPriceLowToHigh() {
        // Fetch products from the database and sort by price in ascending order
        return productRepository.findAll(Sort.by(Sort.Order.asc("price")));
    }

    @Override
    public List<Product> sortProductsByPriceHighToLow() {
        // Fetch products from the database and sort by price in descending order
        return productRepository.findAll(Sort.by(Sort.Order.desc("price")));
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        return products;
    }

    @Override
    public Page<Product> getProductsPage(int page, int size)

    {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productRepository.findAll(pageRequest); // Use the findAll(PageRequest) method to get a Page of products
    }

    @Override
    public void lockProduct(Long id) {
        Product product = productRepository.findById(id).get();
        product.setBlocked(true);
        productRepository.save(product);
    }

    @Override
    public void unlockProduct(Long id) {
        Product product = productRepository.findById(id).get();
        product.setBlocked(false);
        productRepository.save(product);
    }

    @Override
    public Page<Product> getAllProducts(int page, int pageSize) {
        Pageable  pageable = PageRequest.of(page,pageSize);

        return productRepository.findAll(pageable);
    }



    @Override
    public Page<Product> searchProductsAsPage(String searchTerm, int page, int pageSize) {
        return null;
    }

    @Override
    public Page<Product> findAllProducts(int page, int pageSize) {
        Pageable pageable =PageRequest.of(page,pageSize);

        return productRepository.findAllByIsBlockedFalse(pageable);
    }

    @Override
    public Page<Product> findByProductNameContaining(String searchTerm, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findByProductNameContaining(searchTerm,  pageable);

    }

    @Override
    public Page<Product> getProductsByCategoryAndPriceRange(Category category, double minPrice, double maxPrice, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page,pageSize);

        if (category != null) {
            return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice,pageable );
        } else {
            return productRepository.findByPriceBetween(minPrice, maxPrice,pageable);
        }
    }

    @Override
    public Page<Product> getProductsByCategoryPage(Category category, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);

        return productRepository.findByCategory(category ,pageable);
    }


}