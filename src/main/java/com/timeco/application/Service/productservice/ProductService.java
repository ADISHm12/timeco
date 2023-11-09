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
    public List<Product> getAllProducts();

    List<Product> searchProducts(String searchTerm);

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
}
