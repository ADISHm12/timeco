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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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



    @GetMapping("/listProducts")
    public String productList(Model model){
        List<Product> product = productService.getAllProducts();
        model.addAttribute("product", product);
        return "productList";
    }



   //  Product -------------

//    @GetMapping("/addProducts")
//    public String addproductsForm(Model model)
//    {
//        ProductDto productDto=new ProductDto();
//        model.addAttribute("product",productDto);
//        model.addAttribute("categories",categoryRepository.findByIsListed(false));
//        return "add-product";
//    }
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
public String addProduct(@ModelAttribute ProductDto productDto,
                         @RequestParam("imagePaths") String imagePaths) {
    // Split the entered image paths by a comma and trim any leading/trailing spaces
    String[] imagePathArray = imagePaths.split(",");

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

    // Create ProductImage entities for each image path and associate them with the product
    for (String imagePath : imagePathArray) {
        ProductImage productImage = new ProductImage();
        productImage.setImageName(imagePath.trim());
        productImage.setProduct(product);

        // Save the ProductImage entity to the database
        productImageService.saveProductImage(productImage);
    }

    return "redirect:/admin/listProducts"; // Redirect to a success page or the product listing page
}



    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable Long id){

        productService.deleteProductById(id);

        return "redirect:/admin/listProducts";
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
    public String updateProduct(@ModelAttribute ProductDto updatedProduct,@PathVariable Long id) {
        System.out.println(id+"666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666");
//        if (updatedProduct.getImageNames() == null) {
//            // Handle the null case, for example, by initializing it as an empty list
//            updatedProduct.setImageNames(new ArrayList <>());
//        }
        
        productService.updateProductById(updatedProduct,id);
        System.out.println("Received id: " + id);

        return "redirect:/admin/listProducts";
    }
    @GetMapping("/searchProduct")
    public String searchProducts(@RequestParam("searchTerm") String searchTerm, Model model) {
        List<Product> products = productService.searchProducts(searchTerm);
        model.addAttribute("product", products);
        return "productList";
    }
}
