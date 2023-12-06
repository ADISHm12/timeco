package com.timeco.application.web.usercontrollers;

import com.lowagie.text.DocumentException;
import com.razorpay.*;
import com.timeco.application.Dto.AddressDto;
import com.timeco.application.Dto.UserDto;
import com.timeco.application.Repository.*;
import com.timeco.application.Service.addressService.AddressService;
import com.timeco.application.Service.otpservice.EmailSender;
import com.timeco.application.Service.shared.ReturnService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.user.User;
import com.timeco.application.model.user.UserAddress;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wallet.WalletTransaction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import com.razorpay.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Controller
@RequestMapping("/pro")
public class demoController {

    @Autowired
    private UserService userService ;

    @Autowired
    private PurchaseOrderRepository  purchaseOrderRepository ;

    @Autowired
    private OrderItemRepository orderItemRepository ;

    @Autowired
    private AddressRepository addressRepository ;

    @Autowired
    private UserRepository userRepository ;


    @Autowired
    private WalletRepository walletRepository ;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository ;

    @Autowired
    private AddressService addressService ;

    @Autowired
    private ReturnService returnService;

    @Autowired
    private EmailSender emailSender ;

    @GetMapping("/profile")
    public String getMapping(Model model, Principal principal ){

        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        Wallet wallet = user.getWallet();
            if (wallet == null) {
                wallet = new Wallet();
                wallet.setUser(user);
                wallet.setBalance(0.0);
                user.setWallets(wallet);
                walletRepository.save(wallet);
            }
        String link = emailSender.generateReferralLink(user);

        model.addAttribute("link",link);
        model.addAttribute("user", user);
        model.addAttribute("wallet" , wallet);

        return"demo";
    }
    @PostMapping("/invite")
    public ResponseEntity<String> invite(@RequestParam String recipientEmail,
                                         @RequestParam String referralLink) {
        try {
            emailSender.sendReferralEmail(recipientEmail, referralLink);
            return ResponseEntity.ok("Invitation email sent successfully!");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send invitation email");
        }
    }

//    @GetMapping("/userOrders")
//    public String userOrderList(Model model , Principal principal,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "6") int pageSize){
//
//        String username = principal.getName();
//        User user = userService.findUserByUsername(username);
//
//        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByUserId(user.getId());
//        List<OrderItem> orderItemsList = new ArrayList<>();
//
//        for (PurchaseOrder purchaseOrder : purchaseOrders) {
//            List<OrderItem> orderItemsForPurchaseOrder = orderItemRepository.findOrderItemByOrder(purchaseOrder);
//
//
//            orderItemsList.addAll(orderItemsForPurchaseOrder);
//        }
//        Collections.reverse(orderItemsList);
//
//        UserDto userDto = new UserDto();
//        model.addAttribute("user",userDto);
//        model.addAttribute("orderItemsList",orderItemsList);
//        return"user-Orders";
//    }
@GetMapping("/userOrders")
public String userOrderList(Model model, Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int pageSize) {
    String username = principal.getName();
    User user = userService.findUserByUsername(username);

    List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByUserId(user.getId());
    List<OrderItem> orderItemsList = new ArrayList<>();

    for (PurchaseOrder purchaseOrder : purchaseOrders) {
        List<OrderItem> orderItemsForPurchaseOrder = orderItemRepository.findOrderItemByOrder(purchaseOrder);
        orderItemsList.addAll(orderItemsForPurchaseOrder);
    }
    Collections.reverse(orderItemsList);

    // Paginating the orderItemsList
    int start = page * pageSize;
    int end = Math.min(start + pageSize, orderItemsList.size());
    List<OrderItem> paginatedList = orderItemsList.subList(start, end);

    UserDto userDto = new UserDto();
    model.addAttribute("user", userDto);
    model.addAttribute("orderItemsList", paginatedList);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", (int) Math.ceil((double) orderItemsList.size() / pageSize));

    return "user-Orders";
}



    @PostMapping("/updateAccount")
    public String updateAccountDetails(@ModelAttribute("user") UserDto updatedUser, Principal principal,RedirectAttributes redirectAttributes ) {


       boolean user= userService.updateUserDetails(updatedUser,principal);
       if(user){
           redirectAttributes.addAttribute("successAccount", true);
       }
        return "redirect:/pro/profile";
    }
    @GetMapping("/resetPasswordProfile")
    public String resetOnProfile() {
        return "resetPasswordOnProfile";
    }

    @PostMapping("/resetPasswordProfile")
    public String resetPassword(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        String username = principal.getName();
        User currentUser = userService.findUserByUsername(username);

        if (!userService.isPasswordCorrect(currentUser, currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            return "resetPasswordOnProfile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "resetPasswordOnProfile";
        }
        userService.updatePassword(currentUser, newPassword);
        redirectAttributes.addAttribute("successPassword", true);

        return "redirect:/pro/profile";
    }

    @GetMapping("/addressList")
    public String listAddress(@RequestParam(name = "source", required = false) String source,Model model , Principal principal ){
        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        List<UserAddress> addresses = addressRepository.getAddressByUser(user);
        AddressDto address = new AddressDto();
        model.addAttribute("address",address);
        model.addAttribute("addresses",addresses);

        if (source != null) {
            model.addAttribute("source", source);
        }
        return "addressList";
    }

    @GetMapping("/wallet")
    public String wallet(Model model , Principal principal){

        String username = principal.getName();
        User user = userRepository.findByEmail(username);

        Wallet wallet = walletRepository.findByUser(user);
        List<WalletTransaction> walletTransaction = walletTransactionRepository.findByWallet(wallet);
        Collections.reverse( walletTransaction);
        model.addAttribute("wallet",wallet);
        model.addAttribute("walletTransaction",walletTransaction);
        return "wallet";
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
    @PostMapping("/addAddress")
    public String addAddressToDataBase(Principal principal, @ModelAttribute("address") AddressDto address , @RequestParam(name = "source", required = false) String source,RedirectAttributes redirectAttributes ) {

        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        Long userId = user.getId();

        UserAddress isAddressAdded = addressService.save(address, userId);

        if (isAddressAdded!=null) {
            redirectAttributes.addAttribute("success", true); // Adding success parameter
        }

        if ("checkout".equals(source)) {
            return "redirect:/checkout";
        } else {

            return "redirect:/pro/addressList";
        }

    }
    @PostMapping("/updateAddress{addressId}")
    public String updateAddressPost(@PathVariable("addressId") Long addressId,
                                    @ModelAttribute("user_address") AddressDto addressDto,RedirectAttributes redirectAttributes ) {

        UserAddress address = addressRepository.findById(addressId).orElse(null);


        if(address != null) {
            address.setUserName(addressDto.getUserName());
            address.setCity(addressDto.getCity());
            address.setCountry(addressDto.getCountry());
            address.setState(addressDto.getState());
            address.setPinCode(addressDto.getPinCode());
            address.setMobile(addressDto.getMobile());
            address.setAddress(addressDto.getAddress());
            UserAddress  address1 =addressRepository.save(address);

            if(address1!= null){
                redirectAttributes.addAttribute("successUp", true);
            }

        }



        return "redirect:/pro/addressList";
    }

    @PostMapping("/process")
    public ResponseEntity<String> processReturnAndRefund(@RequestParam Long orderId,
                                                         @RequestParam(required = false) String returnReason,
                                                         @RequestParam(required = false) Double refundedAmount,
                                                         @RequestParam(required = false) String method ) {

        OrderItem orderItem = orderItemRepository.findByOrderItemId(orderId);

        if (orderItem == null) {
            return ResponseEntity.badRequest().body("Invalid order ID");
        }

        if ("RETURN".equals(method)) {
            // Process return and refund
            returnService.processReturnAndRefund(orderItem, returnReason, method);
            return ResponseEntity.ok("Return and refund processed successfully");
        } else if ("cancelled".equals(method) && !"delivered".equals(orderItem.getOrderStatus())) {
            // Process cancel and refund
            returnService.processCancelAndRefund(orderItem, returnReason, method);
            return ResponseEntity.ok("Order cancellation and refund processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid method or order status for the specified action");
        }
    }

    @Autowired
    private TemplateEngine  templateEngine;

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam Long orderItemId, @RequestParam Long addressId) {


        try {
            OrderItem order = orderItemRepository.findById(orderItemId).orElse(null);
            UserAddress address = addressRepository.findById(addressId).orElse(null);
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(dateFormatter);

            Context  context = new Context();
            context.setVariable("formattedDate", formattedDate);
            context.setVariable("order", order);
            context.setVariable("address", address);
            String processedHtml = templateEngine.process("invoice", context);

            // Convert HTML to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            byte[] pdfBytes = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "output.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (DocumentException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inv")
        public String get(){
            return"invoice";
    }


    @PostMapping("/razorpay")
    public ResponseEntity<Map<String, String>> initiateRazorpayPayment(double amount, Principal principal) {
        try {

            String username = principal.getName();
            User user = userRepository.findByEmail(username);

            RazorpayClient razorpayClient = new RazorpayClient("rzp_test_CiR2CrX4sA44LS", "z5RbPNpHnx9le0sSSLCu4DIk");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_order_" + user.getId());
            orderRequest.put("payment_capture", 1);
            Order order = razorpayClient.orders.create(orderRequest);

            String razorpayPaymentId = order.get("id");
            String razorpayOrderId = order.get("id");
            System.out.println(user.getId()+"fsdnfksjdfksfklsdflks");

            user.getWallet().deposit(amount);

            // Add transaction details
            WalletTransaction transaction = new WalletTransaction();

            transaction.setWallet(user.getWallet());
            transaction.setAmount(amount);
            transaction.setTransactionType("Deposit");
            transaction.setTransactionTime(LocalDateTime.now());

            userRepository.save(user);
            walletTransactionRepository.save(transaction);


            Map<String, String> razorpayDetails = new HashMap<>();
            razorpayDetails.put("razorpayPaymentId", razorpayPaymentId);
            razorpayDetails.put("razorpayOrderId", razorpayOrderId);
//            razorpayDetails.put("userId", new String(String.valueOf(user.getId())));



            return ResponseEntity.ok(razorpayDetails);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error initiating Razorpay payment"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyRazorpayPayment(
            @RequestParam String razorpayPaymentId,
            Principal principal,
            @RequestParam String razorpayOrderId,
            @RequestParam double amount) {

        try {
            String username = principal.getName();
            User user = userRepository.findByEmail(username);

            boolean paymentVerified = returnService.verifyRazorpayPayment(razorpayPaymentId, razorpayOrderId, amount);

            if (paymentVerified) {
                return ResponseEntity.ok(Map.of("message", "Payment verification successful"));
            } else {
                boolean rollbackSuccess = returnService.rollbackDeposit(user, amount);

                if (rollbackSuccess) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Payment verification failed. Reversed the deposited amount."));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error reversing deposit."));
                }
            }
        } catch (RazorpayException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error with Razorpay: " + ex.getMessage()));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

@PostMapping("/rollback-deposit")
public ResponseEntity<?> rollbackDeposit(@RequestBody Map<String, Object> requestData, Principal principal) {
    try {
        System.out.println("Rollback Deposit Endpoint Hit");

        double amount = Double.parseDouble(requestData.get("amount").toString());
        String username = principal.getName();
        User user = userRepository.findByEmail(username);

        boolean rollbackSuccess = returnService.rollbackDeposit(user, amount);

        if (rollbackSuccess) {
            return ResponseEntity.ok("Deposit rollback successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deposit rollback failed");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred during deposit rollback: " + e.getMessage());
    }
}

}



