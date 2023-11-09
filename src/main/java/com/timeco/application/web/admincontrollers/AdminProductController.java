package com.timeco.application.web.admincontrollers;

import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.CategoryRepository;
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
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static final String UPLOAD_DIR = "C:\\Users\\hp\\OneDrive\\Desktop\\PROJECT\\application\\src\\main\\resources\\static\\assets\\productImages";


    @GetMapping("/listProducts")
    public String productList(Model model){
        List<Product> product = productService.getAllProducts();
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
    public String addProduct(@ModelAttribute ProductDto productDto, @RequestParam("images") MultipartFile[] images) {
        // Create a Product entity from the ProductDto
        Product product = new Product();
        product.setProductName(productDto.getProductName());
        product.setQuantity(productDto.getQuantity());
        product.setPrice(productDto.getPrice());
        product.setCategory(categoryRepository.findById(productDto.getCategoryId()).orElse(null));
        product.setSubcategory(subCategoryRepository.findById(productDto.getSubcategoryId()).orElse(null));
        product.setDescription(productDto.getDescription());

        // Save the Product entity to the database
        productRepository.save(product);

        // Process and save the uploaded images
        for (MultipartFile  image : images) {
            try {
                // Validate uploaded file using Apache Tika
                Tika  tika = new Tika();
                String mimeType = tika.detect(image.getInputStream());
                if (!mimeType.startsWith("image")) {
                    // Handle invalid file format error
                    return "redirect:/admin/listProducts?error=Invalid file format. Please upload an image.";
                }

                // Generate a unique filename for the uploaded image
                String originalFileName = image.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // Construct the file path where the uploaded image will be stored
                String filePath = Paths.get(UPLOAD_DIR, uniqueFileName).toString();

                // Save the uploaded image to the specified file path
                File destFile = new File(filePath) ;
                image.transferTo(destFile);

                // Create a ProductImage entity and associate it with the product
                ProductImage productImage = new ProductImage();
                productImage.setImageName("/productImages/" + uniqueFileName);
                productImage.setProduct(product);

                // Save the ProductImage entity to the database
                productImageService.saveProductImage(productImage);
            } catch (IOException  e) {
                // Handle exceptions
                return "redirect:/admin/listProducts?error=Error uploading images.";
            }
        }

        return "redirect:/admin/listProducts";
    }



//    @PostMapping("/addProducts")
//    public String addProduct(@ModelAttribute ProductDto productDto, @RequestParam("images") MultipartFile[] images) {
//        // ... your existing code for saving the product ...
//        Product product = new Product();
//        product.setProductName(productDto.getProductName());
//        product.setQuantity(productDto.getQuantity());
//        product.setPrice(productDto.getPrice());
//        product.setCategory(categoryRepository.findById(productDto.getCategoryId()).orElse(null));
//        product.setSubcategory(subCategoryRepository.findById(productDto.getSubcategoryId()).orElse(null));
//        product.setDescription(productDto.getDescription());
//
//        // Save the Product entity to the database
//        productRepository.save(product);
//
//        for (MultipartFile image : images) {
//            try {
//                // Validate uploaded file using Apache Tika
//                Tika tika = new Tika();
//                String mimeType = tika.detect(image.getInputStream());
//                if (!mimeType.startsWith("image")) {
//                    // Handle invalid file format error
//                    return "redirect:/admin/listProducts?error=Invalid file format. Please upload an image.";
//                }
//
//                // Generate a unique filename for the uploaded image
//                String originalFileName = image.getOriginalFilename();
//                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
//                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
//
//                // Construct the file path where the uploaded image will be stored
//                String filePath = Paths.get(UPLOAD_DIR, uniqueFileName).toString();
//
//                // Save the uploaded image to the specified file path
//                File destFile = new File(filePath);
//                image.transferTo(destFile);
//
//
//                int cropX = 50;
//                int cropY = 20;
//                int cropWidth = 1080; // Adjust the crop width to 1080 pixels
//                int cropHeight = 1350;
//
//                // Perform server-side image cropping
//                File croppedImageFile = new File(UPLOAD_DIR, uniqueFileName + "_cropped.jpg");
//                Thumbnails.of(destFile)
//                        .sourceRegion(cropX, cropY, cropX + cropWidth, cropY + cropHeight)
//                        .size(cropWidth, cropHeight)
//                        .outputQuality(1.0)
//                        .toFile(croppedImageFile);
//
//                // Create a ProductImage entity and associate it with the product
//                ProductImage productImage = new ProductImage();
//                productImage.setImageName("/productImages/" + uniqueFileName + "_cropped.jpg");
//                productImage.setProduct(product);
//
//                // Save the ProductImage entity to the database
//                productImageService.saveProductImage(productImage);
//            } catch (IOException e) {
//                // Handle exceptions
//                return "redirect:/admin/listProducts?error=Error uploading images.";
//            }
//        }
//
//        return "redirect:/admin/listProducts";
//    }



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
            @RequestParam("newImages") MultipartFile[] newImages) {

        // Retrieve the existing Product from the database
        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            // Remove the old images associated with the product
            productImageService.deleteProductImages(product);

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

            // Process and save the new uploaded images
            for (MultipartFile newImage : newImages) {
                try {
                    // Validate uploaded file using Apache Tika
                    Tika  tika = new Tika();
                    String mimeType = tika.detect(newImage.getInputStream());
                    if (!mimeType.startsWith("image")) {
                        // Handle invalid file format error
                        return "redirect:/admin/listProducts?error=Invalid file format. Please upload an image.";
                    }

                    // Generate a unique filename for the uploaded image
                    String originalFileName = newImage.getOriginalFilename();
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                    // Construct the file path where the uploaded image will be stored
                    String filePath = Paths.get(UPLOAD_DIR, uniqueFileName).toString();

                    // Save the uploaded image to the specified file path
                    File destFile = new File(filePath) ;
                    newImage.transferTo(destFile);

                    // Create a ProductImage entity and associate it with the product
                    ProductImage productImage = new ProductImage();
                    productImage.setImageName("/productImages/" + uniqueFileName);
                    productImage.setProduct(product);

                    // Save the ProductImage entity to the database
                    productImageService.saveProductImage(productImage);

                } catch (IOException e) {
                    // Handle exceptions
                    // ...
                }
            }

            // Save the updated Product
            productRepository.save(product);
        }

        return "redirect:/admin/listProducts";
    }







    @GetMapping("/searchProduct")
    public String searchProducts(@RequestParam("searchTerm") String searchTerm, Model model) {
        List<Product> products = productService.searchProducts(searchTerm);
        model.addAttribute("product", products);
        return "productList";
    }

    @GetMapping("/blockProduct/{id}")
    public String blockProduct(@PathVariable Long id) {
        System.out.println(id+";ksjajdflskdjflaskdjfasldkjfalskdjfalsk");
        productService.lockProduct(id);

        return "redirect:/admin/listProducts";
    }

    @GetMapping("/unBlockProduct/{id}")
    public String unblockProduct(@PathVariable Long id) {
        System.out.println(id+";ksjajdflskdjflaskdjfasldkjfalskdjfalsk");

        productService.unlockProduct(id);
        return "redirect:/admin/listProducts";
    }
}
