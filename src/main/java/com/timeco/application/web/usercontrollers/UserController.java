package com.timeco.application.web.usercontrollers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timeco.application.Dto.AddressDto;
import com.timeco.application.Dto.ProductDto;
import com.timeco.application.Repository.*;
import com.timeco.application.Service.addressService.AddressService;
import com.timeco.application.Service.cartitemsService.CartItemsService;
import com.timeco.application.Service.cartservice.CartService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.HttpSession;

import java.security.Principal;
import java.util.*;

import static org.springframework.http.ResponseEntity.*;

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
    private CartRepository cartRepository;
    @Autowired
    private CartItemsService cartItemsService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository ;
    @Autowired
    private CartItemsRepository cartItemsRepository ;
    @Autowired
    private OrderItemRepository orderItemRepository ;
    @Autowired
    private CategoryRepository categoryRepository ;
    @Autowired
    private CouponRepository couponRepository;



@GetMapping("/products")
public String searchProducts(@RequestParam(name = "page", required = false, defaultValue = "0") int page, Model model) {
    int nextPage = page + 1;

    Pageable pageable = PageRequest.of(page, 6); // Page 1, 6 results per page
    Page<Product> productsPage = productRepository.findAll(pageable);
    List<Product> productSortHighToLow = productService.sortProductsByPriceHighToLow();
    List<Category> categories = categoryRepository.findAll();
    List<Product> productSortLowToHigh = productService.sortProductsByPriceLowToHigh();

    model.addAttribute("categories", categories);
    model.addAttribute("nextPage", nextPage);
    model.addAttribute("products", productsPage);
    model.addAttribute("productSortHighToLow",productSortHighToLow);
    model.addAttribute("productSortLowToHigh",productSortLowToHigh);


    return "products"; // Return the name of your Thymeleaf template
}



//searching products--------------------------------------------------------------------------------------------------------------------------------------------------------



    @GetMapping("/productDetails/{productId}")
    public String showProductDetails(@PathVariable("productId") Long productId, Model model) {
        // Retrieve the product details based on the productId
        Product product = productService.getProductById(productId);
        List<Product> allProductsExceptCurrent = productRepository.findAllByIdNot(productId);
        model.addAttribute("allProductsExceptCurrent", allProductsExceptCurrent);
        model.addAttribute("product", product);
        return "product-details";
    }


    //profile and other related to user---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        List<UserAddress> addresses = addressRepository.getAddressByUser(user);
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByUserId(user.getId());
        List<OrderItem> orderItemsList = new ArrayList<>();

        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            List<OrderItem> orderItemsForPurchaseOrder = orderItemRepository.findOrderItemByOrder(purchaseOrder);


            orderItemsList.addAll(orderItemsForPurchaseOrder);
          }
        Collections.reverse(orderItemsList);


        model.addAttribute("editMode", false);
        model.addAttribute("user", user); // Pass the user object to the template
        model.addAttribute("addresses", addresses);
        model.addAttribute("orderItemsList", orderItemsList);

        return "UserProfile";
    }


    @PostMapping("/updateAccount")
    public String updateAccountDetails(@ModelAttribute("user") User updatedUser,Principal principal) {


        System.out.println(updatedUser+"sjdhvfjsdhvfjsdhvfjahsdvfjs");
        // Update the user's account details with the data from the form
        userService.updateUserDetails(updatedUser,principal); // Implement this method in your service

        // Redirect to a success page or back to the update form with a success message
        return "redirect:/user/profile"; // Replace with your success page or update form
    }

    @GetMapping("/resetPasswordProfile")
    public String resetOnProfile() {
        return "resetPasswordOnProfile";
    }

    @PostMapping("/resetPasswordProfile")
    public String resetPassword(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, Principal principal, RedirectAttributes  redirectAttributes, Model model) {
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
        redirectAttributes.addFlashAttribute("success", "Password reset successfully.");

        // Redirect or display a success message
        return "redirect:/user/profile"; // Redirect to the user's profile page
    }

    @GetMapping("/addAddress")
    public String ShowAddressRegForm(@RequestParam(name = "source") String source, Model model) {
        AddressDto addressDto = new AddressDto();
        model.addAttribute("address", addressDto);
        model.addAttribute("source", source); //  Pass the source as a model attribute
        return "addAddress";
    }
    @PostMapping("/addAddress")
    public String addAddressToDataBase(Principal principal, @ModelAttribute("address") AddressDto address, HttpSession session , @RequestParam(name = "source") String source) {

        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        Long userId = user.getId();
        session.setAttribute("userId", userId);
        session.setAttribute("user", user);
        addressService.save(address, userId);

        if ("profile".equals(source)) {
            // Redirect to the profile page
            return "redirect:/user/profile";
        } else if ("checkout".equals(source)) {

            // Redirect to the checkout page
            return "redirect:/checkout";
        } else {

            // Handle the case where the source is not recognized
            return "redirect:/user/profile"; // You can replace this with your default page
        }

    }


    @PostMapping("/updateAddress{addressId}")
    public String updateAddressPost(@PathVariable("addressId") Long addressId,
                                    @ModelAttribute("user_address") AddressDto addressDto) {

        UserAddress address = addressRepository.findById(addressId).orElse(null);


        if(address != null) {
            address.setUserName(addressDto.getUserName());
            address.setCity(addressDto.getCity());
            address.setCountry(addressDto.getCountry());
            address.setState(addressDto.getState());
            address.setPinCode(addressDto.getPinCode());
            address.setMobile(addressDto.getMobile());
            address.setAddress(addressDto.getAddress());
            addressRepository.save(address);


        }


        return "redirect:/user/profile";
    }

    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        try {
            addressRepository.deleteById(addressId);
            return ok("Address deleted successfully");
        } catch (Exception e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting address");
        }
    }

    //cart section-====================================================================================================================================================================================
    //============================================================================================================================================================================================================================

    @GetMapping("/cart")
    public String showCart() {
        System.out.println("ksdjfhdsklhfhkadsjfklsjdhfkjasdhf;kjsjdhf;kjasjdhf;jkshd");
        return "cart";
    }

@PostMapping("/addProductToCart")
public ResponseEntity<String> addToCart(@RequestBody ProductDto productDTO, Principal principal) {


    try {
        // Check if the product is already in the cart
        boolean isProductInCart = cartService.isProductInCart(productDTO, principal);
        System.out.println(isProductInCart+"555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
            // Product is not in the cart, so you can add it
            cartService.addProductToCart(productDTO, principal);
            return ok("Product added to cart.");

    } catch (Exception e) {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add product to cart.");
    }
}

@GetMapping("/checkProductInCart")
public ResponseEntity<Boolean> checkProductInCart(@RequestParam Long id,Principal principal) {
    Boolean exists = false;
    String username = principal.getName();
    User user = userRepository.findByEmail(username);

    // Assuming you have some way to identify the current user's cart
    Cart  userCart = cartRepository.findById(user.getId()).orElse(null);// Implement this method

    if (userCart != null) {
        List<CartItems> cartItems = cartItemsRepository.findByCart(userCart);

        // Replace 'id' with the ID of the product you want to check
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // Check if the product is in the cart items
            exists = cartItems.stream()
                    .anyMatch(cartItem -> cartItem.getProduct().equals(product));
        }
    }

    return ok(exists);
}
    @GetMapping("/getProductQuantity")
    public ResponseEntity<Integer> getProductQuantity(@RequestParam Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return ResponseEntity.ok(product.getQuantity());
        } else {
            // Handle product not found (e.g., return a 404 Not Found response)
            return ResponseEntity.notFound().build();
        }
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
            return ok(jsonResponse);
        } catch (JsonProcessingException e) {
            // Handle the exception, e.g., log it and return an error response
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON response");

//        return ResponseEntity.ok(Integer.toString(updatedQuantity));
        }

    }

    @PutMapping("/updateCartProduct")
    public ResponseEntity<String> updateCartProduct(
            @RequestParam Long id,
            @RequestParam Integer quantity
    ) {

        try {
            // Call the service to update the cart product
            cartItemsService.updateProductQuantity(id, quantity);
            return new ResponseEntity<>("Cart updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update the cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/remove-from-cart/{itemId}")
    public String removeFromCart(@PathVariable Long itemId){

        cartItemsService.removeFromCart(itemId);

        System.out.println(itemId);
       return "redirect:/user/viewCart";
    }
    @GetMapping("/orderShow/{orderItemId}")
    public String orderShow(@PathVariable Long orderItemId ,Model model){

        OrderItem orderItem = orderItemRepository.findByOrderItemId(orderItemId);
        UserAddress address = orderItem.getOrder().getAddress();
        model.addAttribute("orderItem",orderItem);
        model.addAttribute("address",address);


        return "orderShow";
    }




@PostMapping("/cancel-order/{orderItemId}")
public String cancelOrder(@PathVariable Long orderItemId) {
    OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);

    if (orderItem != null && !orderItem.getOrderStatus().equals("delivered")) {
        Product product = orderItem.getProduct();
        if (product != null) {
            int orderedQuantity = orderItem.getOrderItemCount();
            int currentQuantity = product.getQuantity();
            // Increase the available quantity of the product
            product.setQuantity(currentQuantity + orderedQuantity);

            // Set the order status to "cancelled"
            orderItem.setOrderStatus("cancelled");

            // Update the product and order item
            productRepository.save(product);
            orderItemRepository.save(orderItem);
        }
    }

    return "redirect:/user/orderShow/" + orderItemId;
}

    @GetMapping("/searchProduct")
    public String searchProduct(@RequestParam("searchTerm") String searchTerm, Model model) {
        List<Product> searchResults = productRepository.findByProductNameContaining(searchTerm); // Implement the search logic
        model.addAttribute("products", searchResults);
        return "products"; // Assuming the template name is "products.html"
    }




    @GetMapping("/filterProducts")
    public String filterProducts(@RequestParam("category") Long categoryId, Model model) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        List<Product> filteredProducts = productService.getProductsByCategory(category);
        List<Category> categories =categoryRepository.findAll();
        model.addAttribute("categories",categories );
        model.addAttribute("products", filteredProducts);

        return "products"; // Return a view to display the filtered products
    }


}
