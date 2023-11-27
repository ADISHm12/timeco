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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Category categoryAdd = new Category();
        model.addAttribute("categoryAdd", categoryAdd);
        CategoryDto categoryDto = new CategoryDto();
        model.addAttribute("categoryUp",categoryDto);
        return "categoryList";
    }

    @GetMapping("/addCategory")
    public String showAddCategoryForm(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);

        return "addCategory";
    }

    @PostMapping("/addCategory")
    public String addCategory(@ModelAttribute("category") CategoryDto categoryDto, RedirectAttributes  redirectAttributes) {

        if (categoryService.categoryExists(categoryDto.getName())){
            redirectAttributes.addAttribute("error", "DuplicateCategory");
            return "redirect:/admin/category-list";

        }
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
    public String editCategory(@PathVariable("id") Long categoryId, @RequestParam String name, RedirectAttributes attributes) {
        boolean isUpdated = categoryService.updateCategoryById(categoryId, name);
        if (isUpdated) {
            attributes.addAttribute("success", " updated successfully.");
        } else {
            attributes.addAttribute("error", " already exists.");
        }
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
        List<Category> category = categoryService.searchCategory(searchTerm);
        model.addAttribute("category", category);
        return "categoryList";
    }

//subcategory---------------------------------------------------------------------------------------------------------------------------------------------------



    @GetMapping("/SubCategory-list")
    public String subcategoryList(Model model) {
        List<Subcategory> subcategories = subCategoryRepository.findAll();

        model.addAttribute("subcategories", subcategories);
        Subcategory subcategoryAdd = new Subcategory();
        model.addAttribute("subcategoryAdd", subcategoryAdd);
        return "subcategoryList";
    }


    @GetMapping("/addSubCategory")
    public String showAddSubCategoryForm(Model model) {
        List<Category> categories = categoryRepository.findAll();

        Subcategory subcategory = new Subcategory();
        model.addAttribute("subcategory", subcategory);

        return "addSubCategory";
    }
    @PostMapping("/addSubCategory")
    public String addSubCategory(@ModelAttribute("subcategory") SubCategoryDto subcategoryDto, RedirectAttributes attributes) {
        if (subCategoryRepository.existsByNameIgnoreCase(subcategoryDto.getName())) {
            attributes.addAttribute("error", "DuplicateSubcategory");
            return "redirect:/admin/SubCategory-list";
        }
        Subcategory subcategory = new Subcategory();
        subcategory.setName(subcategoryDto.getName());

        subCategoryRepository.save(subcategory);

        return "redirect:/admin/SubCategory-list";
    }




    @GetMapping("/updateSubCategory/{id}")
    public String editSubCategory(@PathVariable(value = "id") Long subCategoryId, Model model) {
        Optional<Subcategory> optionalSubCategory = subCategoryRepository.findById(subCategoryId);

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
            @RequestParam String name,
            RedirectAttributes attributes
    ) {
        boolean isUpdated = subCategoryService.updateSubCategoryById(subcategoryId, name);
        if (isUpdated) {
            attributes.addAttribute("success", "added!");
        } else {
            attributes.addAttribute("error", "already created.");
        }
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





