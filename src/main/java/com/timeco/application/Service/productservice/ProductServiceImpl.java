package com.timeco.application.Service.productservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.product.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
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
        System.out.println(id + "99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999");

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
//            List<ProductImage > newImages = new ArrayList<>() ;
//            for (ProductImage imageName : products.getImageNames()) {
//                ProductImage productImage = new ProductImage(imageName, product);
//                newImages.add(productImage);
//            }

            // Update the product images collection
//            product.setProductImages(newImages);
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
    public List<Product> getAllProducts(){

        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String searchTerm) {

       return productRepository.findByProductNameContaining(searchTerm);
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




}
