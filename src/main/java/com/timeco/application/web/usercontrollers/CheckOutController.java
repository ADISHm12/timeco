package com.timeco.application.web.usercontrollers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay .*;
import com.timeco.application.Repository.*;
import com.timeco.application.Service.OrderService.OrderItemService;
import com.timeco.application.Service.OrderService.PurchaseOrderService;
import com.timeco.application.Service.addressService.AddressService;
import com.timeco.application.Service.cartitemsService.CartItemsService;
import com.timeco.application.Service.coupon.CouponService;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.coupon.Coupon;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PaymentMethod;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wallet.WalletTransaction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckOutController {


    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private AddressService addressService;
    @Autowired
    private CartItemsService cartItemsService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository ;

    @Autowired
    private PurchaseOrderService purchaseOrderService ;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository ;


    @Autowired
    private AddressRepository addressRepository ;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemRepository orderItemRepository ;

    @Autowired
    private ProductRepository productRepository ;

    @Autowired
    private CouponRepository couponRepository ;

    @Autowired
    private CouponService couponService ;

    @Autowired
    private WalletRepository walletRepository ;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository ;



@GetMapping("/checkout")
public String checkOutPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
    String username = principal.getName();
    User user = userRepository.findByEmail(username);
    List<UserAddress> address = addressService.findByUserId(user.getId());
    List<CartItems> cartItems = cartItemsService.findCartItems(user);
    List<Coupon> listOfCoupon = couponRepository.findAll();
    // Check if any product quantity exceeds available stock
    boolean hasQuantityExceededStock = cartItems.stream().anyMatch(cartItem -> cartItem.getQuantity() > cartItem.getProduct().getQuantity());

    if (hasQuantityExceededStock) {
        redirectAttributes.addFlashAttribute("error", "Quantity exceeds available stock");
        return "redirect:/user/viewCart"; // Redirect to the cart page or an appropriate page
    }

    double totalPrice = cartItemsService.totalPrice(username);
    List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();

    model.addAttribute("address", address);
    model.addAttribute("cartItems", cartItems);
    model.addAttribute("totalPrice", totalPrice);
    model.addAttribute("paymentMethod", paymentMethods);
    model.addAttribute("listCoupon",listOfCoupon);

    return "checkOut";
}


    @PostMapping("/placeOrder")
    public ResponseEntity <String> submitOrder(@RequestParam("selectedAddressId") Long selectedAddressId,
                                              @RequestParam("MethodId") Long selectedPaymentMethodId,
                                              @RequestParam("totalAmount") double totalAmount,
                                               @RequestParam(value = "couponCode", required = false) String couponCode,
                                              Principal principal) throws RazorpayException {




        Map<String, Object> response = new HashMap<>();
        String username=principal.getName();
        User user = userRepository.findByEmail(username);

        if (couponCode != null ) {
            Coupon coupon = couponRepository.findByCouponCode(couponCode);
            if (coupon != null) {
                coupon.incrementUsageCount();
                coupon.getUsers().add(user);
                user.getCoupons().add(coupon);
                // Save the updated entities to the database
                couponRepository.save(coupon);
                userRepository.save(user);
            }
        }

        List<CartItems> cartItems = cartItemsService.findCartItems(user);
        // Convert cart items to order items
        List<OrderItem> orderItems =orderItemService.convertCartItemsToOrderItems(cartItems);

        PurchaseOrder purchaseOrder = new PurchaseOrder();

        // Retrieve the UserAddress and PaymentMethod based on the selected IDs
        UserAddress selectedAddress = addressRepository.findById(selectedAddressId).orElse(null);
        PaymentMethod selectedPaymentMethod = paymentMethodRepository.findById(selectedPaymentMethodId).orElse(null);
        assert selectedPaymentMethod != null;
        String selectedMethodName=selectedPaymentMethod.getPaymentMethodName();
        purchaseOrder.setAddress(selectedAddress);
        purchaseOrder.setPaymentMethod(selectedPaymentMethod);
        purchaseOrder.setOrderItems(orderItems);


        double orderAmount = totalAmount ;
        int orderedQuantity = purchaseOrderService.calculateOrderedQuantity(orderItems);
        purchaseOrder.setOrderAmount(orderAmount);
        purchaseOrder.setOrderedQuantity(orderedQuantity);
        purchaseOrder.setOrderedDate(LocalDate.now());
        purchaseOrder.setUser(user);

        purchaseOrder.setPaymentStatus("pending");
        response.put("isValid", true);
        assert selectedAddress != null;
        response.put("contact",selectedAddress.getMobile());
        purchaseOrderRepository.save(purchaseOrder);


        Long orderId = purchaseOrder.getOrderId();

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(purchaseOrder);
        }
        for(OrderItem orderItem : orderItems) {

            Product  product = orderItem.getProduct();
            int OrderedQuantity = orderItem.getOrderItemCount();
            // Update the product quantity
            int currentQuantity = product.getQuantity();
            if (currentQuantity >= OrderedQuantity) {
                product.setQuantity(currentQuantity - OrderedQuantity);
                productRepository.save(product); // Update the product in the database
            } else {
            }
        }
        orderItemRepository.saveAll(orderItems);

        if (selectedMethodName.equals("ONLINE PAYMENT")) {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_CiR2CrX4sA44LS", "z5RbPNpHnx9le0sSSLCu4DIk");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", orderAmount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#1");
            JSONObject notes = new JSONObject();
            notes.put("notes_key_1", "Tea, Earl Grey, Hot");
            orderRequest.put("notes", notes);

            Order order = razorpay.orders.create(orderRequest);
            response.put("isValid", false);
            response.put("orderId", order.get("id")); // Get orderId from Razorpay Order object
            response.put("amount", order.get("amount"));
            response.put("purchaseId", purchaseOrder.getOrderId());
            response.put("email", username);
            assert selectedAddress != null;
            response.put("username", selectedAddress.getUserName());
            response.put("contact", selectedAddress.getMobile());
        } else if (selectedMethodName.equals("WALLET")) {
            Wallet userWallet = walletRepository.findByUser(user);

            if (userWallet != null) {
                double walletBalance = userWallet.getBalance();

                if (walletBalance >= orderAmount) {
                    userWallet.withdraw(orderAmount);

                    WalletTransaction walletTransaction = new WalletTransaction();
                    walletTransaction.setWallet(userWallet);
                    walletTransaction.setAmount(orderAmount);
                    walletTransaction.setTransactionType("DEBIT");
                    walletTransaction.setTransactionTime(LocalDateTime.now());
                    walletTransactionRepository.save(walletTransaction);
                    List<CartItems> userCartItems = cartItemsService.findCartItems(user);
                    cartItemsService.deleteCartItems(userCartItems);
                    response.put("isValid", true);
                    response.put("orderId", "Wallet payment");
                    response.put("amount", orderAmount);
                    response.put("purchaseId", purchaseOrder.getOrderId());
                    response.put("email", username);
                    response.put("username", selectedAddress.getUserName());
                    response.put("contact", selectedAddress.getMobile());
                } else {
                    response.put("isValid", true);
                    response.put("error", "Insufficient funds in the wallet");
                }
            } else {
                response.put("isValid", true);
                response.put("error", "User does not have a wallet");
            }
        }else if (selectedMethodName.equals("CASH ON DELIVERY")) {
            List<CartItems> userCartItems = cartItemsService.findCartItems(user);
            cartItemsService.deleteCartItems(userCartItems);

        }
        try {
            ObjectMapper  objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON response");
        }

    }
    @PostMapping("/verifyPayment")
    @ResponseBody
    public ResponseEntity<Boolean> verifyPayment(@RequestParam("orderId") String orderId,
                                                 @RequestParam("signature") String signature,
                                                 @RequestParam("paymentId") String paymentId,
                                                 @RequestParam("purchaseId") Long  purchaseId,
                                                 Principal principal) throws RazorpayException {


        String username=principal.getName();
        User user = userRepository.findByEmail(username);
        RazorpayClient razorpay = new RazorpayClient("rzp_test_CiR2CrX4sA44LS", "z5RbPNpHnx9le0sSSLCu4DIk");

        String secret = "z5RbPNpHnx9le0sSSLCu4DIk";

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        boolean status =  Utils.verifyPaymentSignature(options, secret);
        if(status)
        {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseId).orElse(null);
            if(purchaseOrder != null)
            {
                purchaseOrder.setPaymentStatus("success");
                purchaseOrder.setTranscationId(paymentId);

                List<OrderItem> orderItems = purchaseOrder.getOrderItems();
                for (OrderItem orderItem : orderItems) {
                    Product product = orderItem.getProduct();
                    int orderedQuantity = orderItem.getOrderItemCount();
                    int currentQuantity = product.getQuantity();
                    if (currentQuantity >= orderedQuantity) {
                        List<CartItems> userCartItems = cartItemsService.findCartItems(user);
                        cartItemsService.deleteCartItems(userCartItems);
                        productRepository.save(product);
                        purchaseOrderRepository.save(purchaseOrder);
                    } else {
                    }
                }
            }
            if (!status) {
                PurchaseOrder purchaseOrder1 = purchaseOrderRepository.findById(purchaseId).orElse(null);
                if (purchaseOrder1 != null) {
                    purchaseOrderRepository.delete(purchaseOrder1);
                    return ResponseEntity.ok(false); // Indicate failure due to payment verification
                }
            }

        }

        return ResponseEntity.ok(status);
}

    @GetMapping("/orderPlaced")
    public String orderPlaced(){
         return "OrderConfirmation";
    }



    @PostMapping("/applyCoupon")
    @ResponseBody
    public ResponseEntity<?> applyCoupon(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") Double totalAmount,
            Principal principal// Receive the total amount from the AJAX request
    ) {
    String username = principal.getName();

        // Your existing code remains the same, but you can now use 'totalAmount' as needed.

        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        Coupon coupon = couponRepository.findByCouponCode(couponCode);
        if (coupon == null) {
            return ResponseEntity.badRequest().body("Invalid coupon code.");
        }

        if (!coupon.isActive()) {
            return ResponseEntity.badRequest().body("Coupon is not active.");
        }

        if (couponService.isExpired(coupon)) {
            return ResponseEntity.badRequest().body("Coupon has expired.");
        }

        if (couponService.hasUsedCoupon(user, coupon)) {
            return ResponseEntity.badRequest().body("User has already used this coupon.");
        }

        double purchaseAmount = totalAmount; // Implement this method to retrieve the user's purchase amount
        if (purchaseAmount < Double.parseDouble(coupon.getMinimumAmount())) {
            return ResponseEntity.badRequest().body("Purchase amount does not meet the minimum required amount for this coupon.");

        }

//        if (purchaseAmount > Double.parseDouble(coupon.getUserLimit())) {
//            return ResponseEntity.badRequest().body("Purchase amount exceeds user limit for this coupon.");
//        }

        // If all checks pass, calculate the coupon discount amount and return it as a response
        double couponDiscountedAmount = purchaseAmount - coupon.getCouponAmount();
        double couponAmount = coupon.getCouponAmount();
//        coupon.getUsers().add(user);
//        user.getCoupons().add(coupon);
//
//        // Save the updated entities to the database
//        couponRepository.save(coupon);
//        userRepository.save(user);
        // You can return the discount amount as a response
        return ResponseEntity.ok(couponAmount);
    }
    @PostMapping("/removeCoupon")
    public ResponseEntity<String> removeCoupon(@RequestParam String couponCode) {
        try {




            Coupon coupon = couponRepository.findByCouponCode(couponCode);

            if (coupon == null) {
                return ResponseEntity.badRequest().body("Invalid coupon code.");
            }

            // Remove the coupon from the user's set of coupons
//            user.getCoupons().remove(coupon);
//
//            // Update the user in the database
//            userRepository.save(user);

            return ResponseEntity.ok("Coupon removed successfully.");
        } catch (Exception e) {
            // Handle exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the coupon.");
        }
    }


}
