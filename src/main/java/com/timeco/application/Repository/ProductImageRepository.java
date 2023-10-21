package com.timeco.application.Repository;

import com.timeco.application.model.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // You can add custom query methods if needed
}
