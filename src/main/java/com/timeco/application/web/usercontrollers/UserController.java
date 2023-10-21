package com.timeco.application.web.usercontrollers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timeco.application.Dto.AddressDto;
import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.AddressRepository;
import com.timeco.application.Repository.ProductImageRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.addressService.AddressService;
import com.timeco.application.Service.cartitemsService.CartItemsService;
import com.timeco.application.Service.cartservice.CartService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemsService cartItemsService;
    @Autowired
    private ProductRepository productRepository;


    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProductsWithImages();
        model.addAttribute("products", products);

        System.out.println();
        return "products"; // Name of the Thymeleaf view
    }

    @GetMapping("/productDetails/{productId}")
    public String showProductDetails(@PathVariable("productId") Long productId, Model model) {
        // Retrieve the product details based on the productId
        Product product = productService.getProductById(productId);

        // Add the product to the model to be displayed on the product details page
        model.addAttribute("product", product);
        return "product-details";
    }


    //profile and other related to user---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName();
        User userName = userService.findUserByUsername(username);
        List<UserAddress> addresses = addressService.getAllAddresses();

        // Unwrap the Optional<User> using orElseThrow and throw an exception if it's not present
        User user = userRepository.findById(userName.getId()).orElseThrow();
        model.addAttribute("editMode", false);
        model.addAttribute("user", user); // Pass the user object to the template
        model.addAttribute("addresses", addresses);

        return "UserProfile";
    }

    @GetMapping("/resetPasswordProfile")
    public String resetOnProfile() {
        return "resetPasswordOnProfile";
    }

    @PostMapping("/resetPasswordProfile")
    public String resetPassword(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, Principal principal, Model model) {
        // Get the currently logged-in user
        String username = principal.getName();
        User currentUser = userService.findUserByUsername(username);

        // Check if the current password matches the user's stored password
        if (!userService.isPasswordCorrect(currentUser, currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            return "resetPasswordOnProfile";
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "resetPasswordOnProfile";
        }

        // Update the user's password
        userService.updatePassword(currentUser, newPassword);

        // Redirect or display a success message
        return "redirect:/user/profile"; // Redirect to the user's profile page
    }

    @GetMapping("/addAddress")
    public String ShowAddressRegForm(Model model) {
        AddressDto addressDto = new AddressDto();
        model.addAttribute("address", addressDto);
        return "addAddress";
    }

    @PostMapping("/addAddress")
    public String addAddressToDataBase(Principal principal, @ModelAttribute("address") AddressDto address, HttpSession session) {

        String username = principal.getName();

        User user = userService.findUserByUsername(username);
        Long userId = user.getId();
        session.setAttribute("userId", userId);
        session.setAttribute("user", user);
        addressService.save(address, userId);


        return "redirect:/user/profile";
    }
    //cart section-====================================================================================================================================================================================
    //============================================================================================================================================================================================================================

    @GetMapping("/cart")
    public String showCart() {
        return "cart";
    }

    @PostMapping("/addProductToCart")
    public ResponseEntity<String> addToCart(@RequestBody ProductDto productDTO, Principal principal) {


//        try {
        // Implement the logic to add the product to the user's cart using the cartService
        cartService.addProductToCart(productDTO, principal);

        return ResponseEntity.ok("Product added to cart.");
//        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add product to cart.");
//           return "7777";
//        }
    }

    @GetMapping("/viewCart")
    public String viewCart(Model model, Principal principal) {
        String username = principal.getName(); // Get the username of the authenticated user
        User user = userService.findUserByUsername(username);
        double totalPrice = cartItemsService.totalPrice(username);
        if (user != null) {
            List<CartItems> cartItems = cartService.getCartItemsForUser(user);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
        }
        return "cart";
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<String> updateQuantity(@RequestParam Product id, @RequestParam Integer count, Principal principal) {


        String username = principal.getName();
        int updatedQuantity = cartItemsService.updateQuantity(count, id.getId(), username);

        double totalPrice = cartItemsService.totalPrice(username);

        Map<String, Object> response = new HashMap<>();
        response.put("updatedQuantity", updatedQuantity);
        response.put("totalPrice", totalPrice);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            // Handle the exception, e.g., log it and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON response");

//        return ResponseEntity.ok(Integer.toString(updatedQuantity));
        }

    }
}
