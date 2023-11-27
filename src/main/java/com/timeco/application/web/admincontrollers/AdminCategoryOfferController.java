package com.timeco.application.web.admincontrollers;

import com.timeco.application.Dto.CategoryDto;
import com.timeco.application.Dto.CategoryOfferDto;
import com.timeco.application.Repository.CategoryOfferRepository;
import com.timeco.application.Repository.CategoryRepository;
import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Service.OrderService.OrderItemService;
import com.timeco.application.Service.categoryservice.CategoryService;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.category.CategoryOffer;
import com.timeco.application.model.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminCategoryOfferController {


    @Autowired
    private CategoryOfferRepository categoryOfferRepository ;

    @Autowired
    private CategoryRepository categoryRepository ;

    @Autowired
    private CategoryService categoryService ;

    @Autowired
    private OrderItemRepository orderItemRepository ;

    @Autowired
    private OrderItemService orderItemService ;


    @GetMapping("/categoryOfferList")
    public String categoryOfferList(Model model){
        List<Category> categories = categoryRepository.findByIsListed(false);

        model.addAttribute("categories",categories);


        List<CategoryOffer> categoryOffers = categoryOfferRepository.findAll();

        model.addAttribute("categoryOffers",categoryOffers);

        return "categoryOffer";
    }


    @GetMapping("/addCategoryOffer")
    public String addOfferForm(Model model ){
        CategoryOfferDto  categoryOfferDto = new CategoryOfferDto();
        List<Category> categories = categoryRepository.findByIsListed(false);
        model.addAttribute("categoryOfferDto",categoryOfferDto);
        model.addAttribute("categories",categories);
        return"addCategoryOffer";
    }
    @PostMapping("/addCategoryOffer")
    public String addCategoryOffer(@ModelAttribute("categoryOfferDto") CategoryOfferDto categoryOfferDto, RedirectAttributes redirectAttributes ) {
        DateTimeFormatter  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        // Create a CategoryOffer entity
        CategoryOffer categoryOffer = new CategoryOffer();
        categoryOffer.setCategoryOfferCode(categoryOfferDto.getCategoryOfferCode());
        categoryOffer.setStartDate(categoryOfferDto.getStartDate());
        categoryOffer.setEndDate(categoryOfferDto.getEndDate());
        categoryOffer.setPercentage(categoryOfferDto.getPercentage());

        categoryOfferRepository.save(categoryOffer);

        if (categoryOffer.getCategoryOfferId() != null) {
            redirectAttributes.addFlashAttribute("addOfferSuccess", "Offer added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("addOfferFail", "Failed to add offer");
        }

        // Redirect to the category offer list page
        return "redirect:/admin/categoryOfferList";
    }

    @PostMapping("/addCategoryToOffer")
    public ResponseEntity<String> addCategoryToOffer(@RequestParam Long categoryId, @RequestParam Integer offerId) {


        // Retrieve Category and CategoryOffer from the database using their IDs
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Optional<CategoryOffer> offerOptional = categoryOfferRepository.findById(offerId);

        if (categoryOptional.isPresent() && offerOptional.isPresent()) {
            Category category = categoryOptional.get();
            CategoryOffer offer = offerOptional.get();


            // Check if the category is not already associated with the offer
            if (!offer.getCategories().contains(category)) {
                // Add category to the offer and update the association
                category.setCategoryOffer(offer);
                offer.getCategories().add(category);

                categoryService.calculateAndUpdateDiscountsForCategory(category);
                // Save the changes to the database
                categoryRepository.save(category);
                categoryOfferRepository.save(offer);

                return ResponseEntity.ok("Category added to the offer successfully!");
            } else {
                return ResponseEntity.badRequest().body("Category is already associated with the offer.");
            }
        } else {
            return ResponseEntity.badRequest().body("Category or Offer not found!");
        }
    }
    @PostMapping("/removeCategoryFromOffer")
    public ResponseEntity<String> removeCategoryFromOffer(@RequestParam Integer offerId, @RequestParam Long categoryId) {
            CategoryOffer categoryOffer = categoryOfferRepository.findById(offerId).orElse(null);
            Category category = categoryRepository.findById(categoryId).orElse(null);

        if (categoryOffer != null && category != null) {
            // Remove category from the offer and offer from the category
            categoryOffer.getCategories().remove(category);
            category.setCategoryOffer(null); // Assuming a single category can belong to only one offer

            categoryService.resetDiscountedAmountsForCategoryProducts(category);
            List<OrderItem> orderItemsToUpdate = orderItemService.findOrderItemsByCategory(category);


            // Reset discounted amounts for the identified order items
            for (OrderItem orderItem : orderItemsToUpdate) {
                orderItem.setOrderedPrice(null); // Resetting discounted amount
                orderItemRepository.save(orderItem); // Update order item in the database
            }
            // Save the updated entities back to the database (assuming you have save/update methods)
            categoryOfferRepository.save(categoryOffer);
            categoryRepository.save(category);

            return new ResponseEntity<>("Category removed from offer", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Offer or Category not found", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/blockCategoryOffer/{id}")
    public String listCategoryOffer(@PathVariable Integer id) {
        System.out.println(id+"9 88888888888888888888888888888888888888888888888888");

        CategoryOffer lockCategoryOffer = categoryOfferRepository.findById(id).get();
        lockCategoryOffer.setActive(true);
        categoryOfferRepository.save(lockCategoryOffer);

        return "redirect:/admin/categoryOfferList";
    }

    @GetMapping("/unblockCategoryOffer/{id}")
    public String unlistCategoryOffer(@PathVariable Integer id) {

        CategoryOffer unlockCategoryOffer = categoryOfferRepository.findById(id).get();
        unlockCategoryOffer.setActive(false);
        categoryOfferRepository.save(unlockCategoryOffer);

        return "redirect:/admin/categoryOfferList";
    }

    @GetMapping("/editCategoryOffer/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        // Assuming you have a service or repository to fetch CategoryOffer by its ID
        CategoryOffer categoryOffer = categoryOfferRepository.findById(id).orElse(null);

        // Check if the categoryOffer is not null before proceeding
        if (categoryOffer != null) {
            model.addAttribute("categoryOffer", categoryOffer);
            return "editCategoryOffer"; // Return the name of the edit page Thymeleaf template
        } else {
            // Handle the case where the categoryOffer with the given ID is not found
            return "errorPage"; // Return an error page or appropriate response
        }
    }

    @PostMapping("/editCategoryOffer/{id}")
    public String editSubmit(@PathVariable Integer id , @ModelAttribute("categoryOffer") CategoryOffer categoryOffer) {

        CategoryOffer updateCategoryOffer = categoryOfferRepository.findById(id).orElse(null);
        if(updateCategoryOffer != null){
            updateCategoryOffer.setCategoryOfferCode(categoryOffer.getCategoryOfferCode());
            updateCategoryOffer.setPercentage(categoryOffer.getPercentage());
            updateCategoryOffer.setStartDate(categoryOffer.getStartDate());
            updateCategoryOffer.setEndDate(categoryOffer.getEndDate());
            categoryOfferRepository.save(updateCategoryOffer);
        }

        return "redirect:/admin/categoryOfferList"; // Redirect to a success page
    }

    @GetMapping("/getCategoriesByOffer")
    public List<Category> getCategoriesByOffer(Integer offerId) {
        // Assuming there's an entity named CategoryOffer with a reference to Category entities

        // Retrieve the CategoryOffer by ID
        CategoryOffer categoryOffer = categoryOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("CategoryOffer not found"));

        return categoryOffer.getCategories();
    }


}
