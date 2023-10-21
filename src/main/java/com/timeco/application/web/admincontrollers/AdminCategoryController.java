package com.timeco.application.web.admincontrollers;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.Dto.SubCategoryDto;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.SubCategoryRepository;
import com.timeco.application.Service.categoryservice.CategoryService;
import com.timeco.application.Service.categoryservice.SubCategoryService;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.Subcategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private SubCategoryService subCategoryService;


    @GetMapping("/category-list")
    public String categoryList(Model model) {
        List<Category> category = categoryRepository.findAll();
        model.addAttribute("category", category);

        return "categoryList";
    }

    @GetMapping("/addCategory")
    public String showAddCategoryForm(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);

        return "addCategory";
    }

    @PostMapping("/addCategory")
    public String addCategory(@ModelAttribute("category") CategoryDto categoryDto) {
        categoryService.addCategory(categoryDto);

        return "redirect:/admin/category-list";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);

        return "redirect:/admin/category-list";
    }

    @GetMapping("/updateCategory/{id}")
    public String editCategory(@PathVariable(value = "id") Long CategoryId, Model model) {
        Optional<Category> optionalCategory = categoryRepository.findById(CategoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            model.addAttribute("category", category);
            model.addAttribute("categoryId", CategoryId);
            return "updatecategory";
        } else {
            return "redirect:/admin/category-list?error";
        }


    }

    @PostMapping("/updateCategory/{id}")
    public String editCategory(@PathVariable("id") Long categoryId, @ModelAttribute("category") CategoryDto categoryDto) {
        categoryService.updateCategoryById(categoryId, categoryDto);
        return "redirect:/admin/category-list";
    }


    @GetMapping("/blockCategory/{id}")
    public String listCategory(@PathVariable Long id) {

        categoryService.lockCategory(id);

        return "redirect:/admin/category-list";
    }

    @GetMapping("/unblockCategory/{id}")
    public String unlistCategory(@PathVariable Long id) {

        categoryService.unlockCategory(id);

        return "redirect:/admin/category-list";
    }

    @GetMapping("/searchCategory")
    public String searchProducts(@RequestParam("searchTerm") String searchTerm, Model model) {
        System.out.println(searchTerm + "77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777");
        List<Category> category = categoryService.searchCategory(searchTerm);
        model.addAttribute("category", category);
        return "categoryList";
    }

//subcategory---------------------------------------------------------------------------------------------------------------------------------------------------



    @GetMapping("/SubCategory-list")
    public String subcategoryList(Model model) {
        List<Subcategory> subcategories = subCategoryRepository.findAll(); // Retrieve all subcategories

        model.addAttribute("subcategories", subcategories); // Subcategories

        return "subcategoryList";
    }


    @GetMapping("/addSubCategory")
    public String showAddSubCategoryForm(Model model) {
        List<Category> categories = categoryRepository.findAll(); // Retrieve all categories

        Subcategory subcategory = new Subcategory();
        model.addAttribute("subcategory", subcategory);
        return "addSubCategory";
    }
    @PostMapping("/addSubCategory")
    public String addSubCategory(@ModelAttribute("subcategory") SubCategoryDto subcategoryDto, Model model) {
        // Create a Subcategory object from the DTO
        Subcategory subcategory = new Subcategory();
        subcategory.setName(subcategoryDto.getName());

        subCategoryRepository.save(subcategory); // Save the subcategory

        return "redirect:/admin/SubCategory-list";
    }




    @GetMapping("/updateSubCategory/{id}")
    public String editSubCategory(@PathVariable(value = "id") Long subCategoryId, Model model) {
        Optional<Subcategory> optionalSubCategory = subCategoryRepository.findById(subCategoryId);

        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        if (optionalSubCategory.isPresent()) {
            Subcategory subcategory = optionalSubCategory.get();
            model.addAttribute("subcategory", subcategory);
            model.addAttribute("subcategoryId", subCategoryId);
            return "updatesubcategory";
        } else {
            return "redirect:/admin/category-list?error";
        }
    }



    @PostMapping("/updateSubCategory/{id}")
    public String editSubCategory(
            @PathVariable("id") Long subcategoryId,
            @ModelAttribute("subcategory") SubCategoryDto subcategory
    ) {
        subCategoryService.updateSubCategoryById(subcategoryId, subcategory);
        return "redirect:/admin/SubCategory-list";
    }



    @GetMapping("/blockSubCategory/{id}")
    public String listSubCategory(@PathVariable Long id) {

        subCategoryService.lockSubCategory(id);

        return "redirect:/admin/SubCategory-list";
    }

    @GetMapping("/unblockSubCategory/{id}")
    public String unlistSubCategory(@PathVariable Long id) {

        subCategoryService.unlockSubCategory(id);

        return "redirect:/admin/SubCategory-list";
    }

    @GetMapping("/deleteSubCategory/{id}")
    public String deleteSubCategory(@PathVariable Long id){

        subCategoryService.deleteSubCategoryById(id);

        return "redirect:/admin/SubCategory-list";
    }


}





