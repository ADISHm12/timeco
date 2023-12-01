package com.timeco.application.Service.productservice;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

//    public Product addProduct(Product product);
    Product addProduct(Product product);

    public void updateProductById(ProductDto product,Long id);

    public void deleteProductById(Long id);
    public Page<Product> getAllProducts();

    Page<Product> searchProducts(String searchTerm);

//    List<ProductImageDto> getAllProductsWithImages();
   public List<Product> getAllProductsWithImages() ;

    public Product getProductById(Long productId) ;

    public int getTotalStock() ;


    public List<Product> sortProductsByPriceLowToHigh() ;

    public List<Product> sortProductsByPriceHighToLow() ;

    List<Product> getProductsByCategory(Category category);


    Page<Product> getProductsPage(int page, int size);

    void lockProduct(Long id);

    void unlockProduct(Long id);

    Page<Product> getAllProducts(int page, int pageSize);



    Page<Product> searchProductsAsPage(String searchTerm, int page, int pageSize);

    List<Product> searchProduct(String searchTerm);


    Page<Product> findAllProducts(int page, int pageSize);

    Page<Product> findByProductNameContaining(String searchTerm, int page, int pageSize);


    Page<Product> getProductsByCategoryAndPriceRange(Category category, double minPrice, double maxPrice, int page, int pageSize);

    Page<Product> getProductsByCategoryPage(Category category, int page, int pageSize);
}
