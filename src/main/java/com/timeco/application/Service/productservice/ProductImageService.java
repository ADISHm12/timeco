package com.timeco.application.Service.productservice;

import com.timeco.application.Repository.ProductImageRepository;
import com.timeco.application.model.product.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public void saveProductImage(ProductImage productImage) {
        productImageRepository.save(productImage);
        // You can add additional logic here if needed, such as handling image storage.
    }
}