package com.timeco.application.web.admincontrollers;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.ProductImageRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.SubCategoryRepository;
import com.timeco.application.Service.categoryservice.CategoryService;
import com.timeco.application.Service.categoryservice.SubCategoryService;
import com.timeco.application.Service.productservice.ProductImageService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.product.ProductImage;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private SubCategoryService subCategoryService;
    @Autowired
    private ProductImageRepository  productImageRepository;
//    public static final String UPLOAD_DIR = "C:\\Users\\hp\\OneDrive\\Desktop\\PROJECT\\application\\src\\main\\resources\\static\\assets\\productImages";
      public static final String UPLOAD_DIR = "/home/ubuntu/timeco/src/main/resources/static/assets/productImages";


    @GetMapping("/listProducts")
    public String productList(Model model,@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int pageSize){

        Page<Product> product = productService.getAllProducts(page,pageSize);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", product.getTotalPages());
        model.addAttribute("product", product);
        return "productList";
    }



   //  Product -------------

@GetMapping("/addProducts")
public String addproductsForm(Model model) {
    ProductDto productDto = new ProductDto();
    model.addAttribute("product", productDto);

    // Retrieve all categories that are listed (you can adjust this query as needed)
    List<Category> categories = categoryRepository.findByIsListed(false);
    model.addAttribute("categories", categories);

    // Retrieve all subcategories that are listed (you can adjust this query as needed)
    List<Subcategory> subcategories = subCategoryRepository.findAll();
    model.addAttribute("subcategories", subcategories);

    return "add-product";

}

@PostMapping("/addProducts")
public String addProduct(@ModelAttribute ProductDto productDto,  @RequestParam("croppedImage1") MultipartFile croppedImage1,
                         @RequestParam("croppedImage2") MultipartFile croppedImage2,
                         @RequestParam("croppedImage3") MultipartFile croppedImage3) throws IOException {
    // Create a Product entity from the ProductDto
    if (productRepository.existsByProductNameIgnoreCase(productDto.getProductName())) {
        // Handle case where product with the same name exists
        return "redirect:/admin/addProducts?error=Duplicate product name. Please choose a different name.";
    }
    Product product = new Product();
    product.setProductName(productDto.getProductName());
    product.setQuantity(productDto.getQuantity());
    product.setPrice(productDto.getPrice());
    product.setCategory(categoryRepository.findById(productDto.getCategoryId()).orElse(null));
    product.setSubcategory(subCategoryRepository.findById(productDto.getSubcategoryId()).orElse(null));
    product.setDescription(productDto.getDescription());

    // Save the Product entity to the database
    productRepository.save(product);

    Category productCategory = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

    // Check if the product's category has an active offer
    if (productCategory != null && productCategory.getCategoryOffer() != null) {
        // Calculate discounted amount if an offer exists
        double discountPercentage = productCategory.getCategoryOffer().getPercentage();
        double discountedAmount = productDto.getPrice() - (productDto.getPrice() * discountPercentage / 100);

        // Set the discounted amount for the product
        product.setDiscountedAmount(discountedAmount);
    }
    handleCroppedImage(croppedImage1, product.getProductName().trim() + "_1",product);
    handleCroppedImage(croppedImage2, product.getProductName().trim() + "_2",product);
    handleCroppedImage(croppedImage3, product.getProductName().trim() + "_3",product);

    // Process and save the uploaded images


    return "redirect:/admin/listProducts";
}
    private void handleCroppedImage(MultipartFile croppedImageData, String fileName,Product product) throws IOException {
        if (croppedImageData != null && !croppedImageData.isEmpty()) {
            byte[] decodedImageData = croppedImageData.getBytes();
            BufferedImage  croppedImage = ImageIO.read(new ByteArrayInputStream(decodedImageData) );
            File croppedImageFile = new File(UPLOAD_DIR, fileName + ".jpg");
            FileUtils.writeByteArrayToFile(croppedImageFile, decodedImageData);

            ProductImage productImage = new ProductImage();
            productImage.setImageName("/productImages/" + fileName + ".jpg");
             productImage.setProduct(product);

            // Add product image to the list in the Product entity
            productImageRepository.save(productImage);
        }
    }


    @GetMapping("/updateProduct/{id}")
    public String updateProduct(@PathVariable Long id,Model model){

        Product product=productRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("in valid product Id: "+id));
        model.addAttribute("category",categoryRepository.findAll());
        model.addAttribute("subcategory",subCategoryRepository.findAll());
        model.addAttribute("product",product);
        model.addAttribute("pro",id);

            return "editProduct";

    }



@PostMapping("/updateProduct/{id}")
public String updateProduct(
        @ModelAttribute ProductDto updatedProduct,
        @PathVariable Long id,
        @RequestParam(value = "croppedImage1", required = false) MultipartFile croppedImage1,
        @RequestParam(value = "croppedImage2", required = false) MultipartFile croppedImage2,
        @RequestParam(value = "croppedImage3", required = false) MultipartFile croppedImage3) {

    // Retrieve the existing Product from the database
    Product product = productRepository.findById(id).orElse(null);

    if (product != null) {
        // Update the product information based on the ProductDto
        product.setProductName(updatedProduct.getProductName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());

        // Retrieve the Category based on categoryId from the ProductDto
        Category category = categoryRepository.findById(updatedProduct.getCategory().getId()).orElse(null);

        if (category != null) {
            product.setCategory(category);
        }

        try {
            // Update only provided images, if any
            if (croppedImage1 != null && !croppedImage1.isEmpty()) {
                handleCroppedImage2(croppedImage1, product.getProductName().trim() + "_1", 1,product);
            }
            if (croppedImage2 != null && !croppedImage2.isEmpty()) {
                handleCroppedImage2(croppedImage2, product.getProductName().trim() + "_2", 2,product);
            }
            if (croppedImage3 != null && !croppedImage3.isEmpty()) {
                handleCroppedImage2(croppedImage3, product.getProductName().trim() + "_3",3 ,product);
            }
        } catch (IOException e) {
            // Handle the exception appropriately
            return "redirect:/admin/updateProduct/" + id + "?error=ImageUploadFailed";
        }

        // Save the updated Product
        productRepository.save(product);
    }

    return "redirect:/admin/listProducts";
}
    private void handleCroppedImage2(MultipartFile croppedImageData, String fileName, int index, Product product) throws IOException {
        if (croppedImageData != null && !croppedImageData.isEmpty()) {
            List<ProductImage> productImages = productImageRepository.findByProduct(product);

            if (index >= 1 && index <= 3) { // Assuming images are indexed from 1 to 3
                int imageIndex = index - 1; // Convert index to array index (0-based)

                byte[] decodedImageData = croppedImageData.getBytes();
                BufferedImage croppedImage = ImageIO.read(new ByteArrayInputStream(decodedImageData));
                File croppedImageFile = new File(UPLOAD_DIR, fileName + ".jpg");
                FileUtils.writeByteArrayToFile(croppedImageFile, decodedImageData);

                if (!productImages.isEmpty() && imageIndex < productImages.size()) {
                    ProductImage existingImage = productImages.get(imageIndex);

                    // Update the existing image with the new one
                    existingImage.setImageName("/productImages/" + fileName + ".jpg");
                    productImageRepository.save(existingImage);
                } else {
                    // No image found at the specified index, create and save a new image
                    ProductImage productImage = new ProductImage();
                    productImage.setImageName("/productImages/" + fileName + ".jpg");
                    productImage.setProduct(product);

                    productImageRepository.save(productImage);
                }
            }
        }
    }



    @DeleteMapping("/removeImage/{imageId}")
    public ResponseEntity<String> removeImage(@PathVariable Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElse(null);

        if (productImage != null) {
            try {

                productImage.getProduct().getProductImages().remove(productImage);
                productImage.setProduct(null);

                // Delete the image entry from the database
                productImageRepository.delete(productImage);

                return ResponseEntity.ok("Image removed successfully");
            } catch (Exception e) {
                // Handle exceptions
                return ResponseEntity.status(HttpStatus .INTERNAL_SERVER_ERROR).body("Failed to remove image");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }





    @GetMapping("/searchProduct")
    public String searchProducts(@RequestParam("searchTerm") String searchTerm, Model model){
        List<Product> products = productService.searchProduct(searchTerm);
        model.addAttribute("product", products);
        return "productList";
    }

    @GetMapping("/blockProduct/{id}")
    public String blockProduct(@PathVariable Long id) {

        productService.lockProduct(id);

        return "redirect:/admin/listProducts";
    }

    @GetMapping("/unBlockProduct/{id}")
    public String unblockProduct(@PathVariable Long id) {

        productService.unlockProduct(id);
        return "redirect:/admin/listProducts";
    }
}
