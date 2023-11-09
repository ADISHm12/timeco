package com.timeco.application.Repository;

import com.timeco.application.model.product.Product;
import com.timeco.application.model.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
    // You can add custom query methods if needed
}
