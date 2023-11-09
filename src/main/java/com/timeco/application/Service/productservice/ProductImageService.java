package com.timeco.application.Service.productservice;

import com.timeco.application.Repository.ProductImageRepository;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.product.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    public static final String UPLOAD_DIR = "C:\\Users\\hp\\OneDrive\\Desktop\\PROJECT\\application\\src\\main\\resources\\static\\assets\\productImages";


    public void saveProductImage(ProductImage productImage) {
        productImageRepository.save(productImage);
        // You can add additional logic here if needed, such as handling image storage.
    }

    public void deleteProductImages(Product product) {
        // Replace 'ProductImageRepository' with the actual name of your repository for product images
        // Assuming you have a 'ProductImageRepository' to manage product images
        List<ProductImage> productImages = productImageRepository.findByProduct(product);

        for (ProductImage productImage : productImages) {
            // Delete the physical image file from the storage
            // Replace 'deleteImageFile' with your method for deleting image files
            deleteImageFile(productImage.getImageName());

            // Delete the product image from the repository
            productImageRepository.delete(productImage);
        }
    }

    private void deleteImageFile(String imagePath) {
        // Replace 'UPLOAD_DIR' with the directory where your images are stored
        String uploadDirectory = UPLOAD_DIR; // Provide the path to your image directory

        // Build the absolute file path of the image to be deleted
        String absoluteFilePath = uploadDirectory + imagePath;

        File imageFile = new File(absoluteFilePath);

        if (imageFile.exists()) {
            if (imageFile.delete()) {
                // Image file deleted successfully
            } else {
                // Handle the case where the image file couldn't be deleted
                // You can log an error or throw an exception if necessary
            }
        } else {
            // Handle the case where the image file doesn't exist
            // You can log an error or throw an exception if necessary
        }
    }
}