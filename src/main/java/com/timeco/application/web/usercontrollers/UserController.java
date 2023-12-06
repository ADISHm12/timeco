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
import com.timeco.application.Service.shared.WishListService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.category.Category;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PaymentMethod;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wishlist.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDate;
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
    @Autowired
    private WishListService wishListService ;
    @Autowired
    private WishlistRepository wishlistRepository ;

    @Autowired
    private WalletRepository walletRepository ;

    @GetMapping("/status")
    public ResponseEntity<String> getUserStatus(Principal principal ) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);

        if (user != null) {
            if (user.isBlocked()) {
                SecurityContextHolder.clearContext(); // Log out the user
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("BLOCKED"); // Provide a blocked message
            } else {
                return ResponseEntity.ok("User is active");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/products")
    public String listProducts(Model model,@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "6") int pageSize){
     Page<Product> productsPage = productService.findAllProducts(page,pageSize);
     List<Category>categories = categoryRepository.findAll();

        model.addAttribute("products", productsPage);
        model.addAttribute("orderItem", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories",categories);
        return "products";
    }



//searching products--------------------------------------------------------------------------------------------------------------------------------------------------------



    @GetMapping("/productDetails/{productId}")
    public String showProductDetails(@PathVariable("productId") Long productId, Model model) {
        // Retrieve the product details based on the productId
        Product product = productService.getProductById(productId);
        List<Product> allProductsExceptCurrent = productRepository.findAllByIdNot(productId);
        int maxProductsToShow = 6;
        int endIndex = Math.min(maxProductsToShow, allProductsExceptCurrent.size());
        List<Product> limitedProducts = allProductsExceptCurrent.subList(0, endIndex);

        model.addAttribute("allProductsExceptCurrent", limitedProducts);
        model.addAttribute("product", product);
        return "product-details";
    }


    //profile and other related to user---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //cart section-====================================================================================================================================================================================
    //============================================================================================================================================================================================================================

    @GetMapping("/cart")
    public String showCart() {
        return "cart";
    }

@PostMapping("/addProductToCart")
public ResponseEntity<String> addToCart(@RequestBody ProductDto productDTO, Principal principal) {

    try {
        boolean isProductInCart = cartService.isProductInCart(productDTO, principal);
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

    Cart  userCart = cartRepository.findById(user.getId()).orElse(null);// Implement this method

    if (userCart != null) {
        List<CartItems> cartItems = cartItemsRepository.findByCart(userCart);

        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();


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
        Product product = productRepository.findById(id.getId()).orElse(null);
        double totalPrice = cartItemsService.totalPrice(username);
        Double updatedPrice;
        LocalDate currentDate = LocalDate.now();

        if (product.getCategory() != null && product.getCategory().getCategoryOffer() != null && product.getCategory().getCategoryOffer().getStartDate() != null && !product.getCategory().getCategoryOffer().isActive()) {
            LocalDate  startDate = product.getCategory().getCategoryOffer().getStartDate();
            LocalDate endDate = product.getCategory().getCategoryOffer().getEndDate();

            // Check if the current date is after or equal to the start date
            boolean hasStarted = !currentDate.isBefore(startDate);

            // Check if the current date is before the end date
            boolean hasNotExpired = currentDate.isBefore(endDate) || currentDate.isEqual(endDate);

            if (hasStarted && hasNotExpired) {
                updatedPrice = product.getDiscountedAmount() * updatedQuantity;
            } else {
                updatedPrice = product.getPrice() * updatedQuantity;
            }

        } else {
            updatedPrice = product.getPrice() * updatedQuantity;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("updatedQuantity", updatedQuantity);
        response.put("totalPrice", totalPrice);
        response.put("updatedPrice", updatedPrice);

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
            System.out.println(id+" "+quantity);
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

                product.setQuantity(currentQuantity + orderedQuantity);

                orderItem.setOrderStatus("cancelled");


                PaymentMethod  paymentMethod = orderItem.getOrder().getPaymentMethod();
                if (paymentMethod != null && paymentMethod.getPaymentMethodName().equals("ONLINE PAYMENT")) {
                    User user = orderItem.getOrder().getUser();


                    Wallet userWallet = user.getWallet();
                    if (userWallet == null) {
                        // Create a new wallet for the user
                        userWallet = new Wallet();
                        userWallet.setUser(user);
                        user.setWallets(userWallet);
                    }


                    double refundAmount = orderItem.getOrderItemCount() * orderItem.getProduct().getPrice();


                    userWallet.deposit(refundAmount);
                    userWallet.recordTransaction(refundAmount, "Refund");


                    walletRepository.save(userWallet);
                    userRepository.save(user);
                }

                productRepository.save(product);
                orderItemRepository.save(orderItem);
            }
        }

        return "redirect:/user/orderShow/" + orderItemId;
    }

    @GetMapping("/searchProduct")
    public String searchProduct(@RequestParam("searchTerm") String searchTerm, Model model,@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "6") int pageSize) {
        Page<Product> searchResults = productService.findByProductNameContaining(searchTerm,page,pageSize); // Implement the search logic
        model.addAttribute("products", searchResults.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());


        return "products"; // Assuming the template name is "products.html"
    }


@GetMapping("/filterProducts")
public String filterProducts(@RequestParam(value = "category", required = false) Long categoryId,
                             @RequestParam(value = "price", required = false) String priceRange,
                             Model model,@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "6") int pageSize) {


    Category category = null;

     if (categoryId != null) {
        category = categoryRepository.findById(categoryId).orElse(null);
    }

    Page<Product> filteredProducts;

    if (priceRange != null && !priceRange.isEmpty()) {

        // Example logic:
        double minPrice = 0;
        double maxPrice = 0;

        switch (priceRange) {
            case "0-500":
                minPrice = 0;
                maxPrice = 500;
                break;
            case "501-1000":
                minPrice = 501;
                maxPrice = 1000;
                break;
            case "1001-1500":
                minPrice = 1001;
                maxPrice = 1500;
                break;
            case "1501-2000":
                minPrice = 1501;
                maxPrice = 2000;
                break;
        }

        // Use productService method to get filtered products by category and price range
        filteredProducts = productService.getProductsByCategoryAndPriceRange(category, minPrice, maxPrice, page, pageSize);
    } else {
        // If no price range is selected, filter products only by category
        filteredProducts = productService.getProductsByCategoryPage(category, page, pageSize);
    }

    List<Category> categories = categoryRepository.findAll();
    model.addAttribute("categories", categories);
    model.addAttribute("products", filteredProducts.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", filteredProducts.getTotalPages());


    return "products"; // Return a view to display the filtered products
}



    //wishList==================================================================================================================================================
    @GetMapping("/wishlist")
    public String getWishList(Model model,Principal principal ){
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        Wishlist wishList = wishListService.findWishList(user);
            if(wishList==null){
                wishList= new Wishlist();
                wishList.setUser(user);

                user.setWishlist(wishList);
                wishlistRepository.save(wishList);
            }


        Set<Product> productsInWishList = wishList.getProducts();


        model.addAttribute("productsInWishList", productsInWishList);

        return "wishList";
    }


    @PostMapping("/addToWishlist")
    public ResponseEntity<String> addWishToList(@RequestParam Long productId, Principal principal) {
        try {
            String username = principal.getName();
            User user = userRepository.findByEmail(username);

            // Fetch the user's wishlist or create one if it doesn't exist
            Wishlist  wishlist = user.getWishlist();
            if (wishlist == null) {
                wishlist = new Wishlist();
                user.setWishlist(wishlist);
            }
            wishlist.setUser(user);
            // Create or fetch the product entity based on the 'id'
            Product product = productRepository.findById(productId).orElse(null);


            // Add the product to the wishlist
            wishlist.getProducts().add(product);

            // Update the user entity to save the wishlist
            userRepository.save(user);

            return ResponseEntity.ok("Added to wishlist successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add to wishlist: " + e.getMessage());
        }
    }

    @GetMapping("/checkProductInWishlist")
    public ResponseEntity<String> checkProductInWishlist(@RequestParam Long productId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);

        Product product = productRepository.findById(productId).orElse(null);

        if (wishListService.isProductInWishlist(user, product)) {
            return ResponseEntity.ok("Product is already in the wishlist.");
        } else {
            return ResponseEntity.ok("Product is not in the wishlist.");
        }
    }

    @PostMapping("/addToCartFromWishlist")
    public ResponseEntity<String> addToCart(@RequestParam Long productId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);

        // Fetch the user's cart
        Cart cart = user.getCart();

        // Fetch the product based on the productId
        Product product = productRepository.findById(productId).orElse(null);
        if (cart != null && product != null) {
            // Check if the product is already in the cart
            boolean productAlreadyInCart = cart.getCartItems().stream()
                    .anyMatch(cartItem -> cartItem.getProduct().equals(product));

            if (productAlreadyInCart) {
                return ResponseEntity.ok("Product is already in the cart.");
            }
            if (product.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Product is out of stock.");
            }
            // If the product is not in the cart, add it
            cartService.addToCartFromWishlist(cart, product);

            return ResponseEntity.ok("Product added to the cart.");
        } else {
            return ResponseEntity.badRequest().body("Invalid request or product not found.");
        }
    }

    @PostMapping("/removeFromWishlist")
    public ResponseEntity<String> removeWishlist(@RequestParam Long productId,Principal principal){
        String username = principal.getName();
        User user = userRepository.findByEmail(username);

        Wishlist  wishlist = user.getWishlist();

        Product productToRemove = null;
        for (Product product : wishlist.getProducts()) {
            if (product.getId().equals(productId)) {
                productToRemove = product;
                break;
            }
        }

        if (productToRemove != null) {
            // Remove the product from the wishlist
            wishlist.getProducts().remove(productToRemove);
            // Update the user's wishlist
            userRepository.save(user);

            return ResponseEntity.ok("Product removed from wishlist");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in wishlist");
        }
    }


}
