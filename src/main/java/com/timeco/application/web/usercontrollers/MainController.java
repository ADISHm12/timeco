package com.timeco.application.web.usercontrollers;

import com.timeco.application.Repository.OtpRepository;
import com.timeco.application.Repository.ProductRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.otpservice.OtpService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.Otp;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.print.Pageable;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/main")
public class MainController {

    @Autowired
    ProductService productService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    public OtpService otpService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;


    @Autowired
    private ProductRepository productRepository ;

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }
    @GetMapping("/")
    public String home(Model model){
        List<Product> latestProducts = productRepository.findTop3ByOrderByIdDesc();
        model.addAttribute("products", latestProducts);
        return"index";
    }

    //forgot password--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/forgotPassword")
    public String getFindAccountForm() {
        return "findAccount";
    }
    public boolean userExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    @PostMapping("/forgotPassword")
    public String sendPasswordResetOTP(@RequestParam("email") String email, HttpSession session, Model model) {

        if (userExists(email)) {
            otpService.sendRegistrationOtp(email);
            session.setAttribute("userEmail", email);
            return "redirect:/main/ResetOtpVerification";
        } else {
            model.addAttribute("error", "Email not found. Please check your email and try again.");
            return "findAccount";
        }
    }

    @GetMapping("/ResetOtpVerification")
    public String resetOtpConfirmForm(Model model,@ModelAttribute("error") String error){
        model.addAttribute("error", error);
        return "resetOtpConfirm";
    }
    @GetMapping("/resendResetOtp")
    public String resendResetOtp(HttpSession session,Model model ,@ModelAttribute("error") String error){
        String email=session.getAttribute("userEmail").toString();

        otpService.sendRegistrationOtp(email);
        model.addAttribute("error", error);

        return "redirect:/main/ResetOtpVerification";
    }


@PostMapping("/ResetOtpVerification")
public ResponseEntity <String> resetOtpValidation(@RequestParam("otp") String enteredOtp, HttpSession session, Model model) {
    String email = session.getAttribute("userEmail").toString();
    Otp storedOtp = otpRepository.findByEmail(email);

    if (storedOtp != null) {
        if (enteredOtp.equals(storedOtp.getOtp().toString())) {
            otpRepository.delete(storedOtp);
            return ResponseEntity.ok().body("{\"message\": \"success\"}");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect OTP. Please try again.\"}");
        }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect OTP. Please try again.\"}");
}



    @GetMapping("/resetPassword")
    public String getResetPasswordForm() {
        return "resetPassword";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,Model model,HttpSession session){
       String email=session.getAttribute("userEmail").toString();
        User user=userRepository.findByEmail(email);

        if(user==null){
            return "redirect:/main/resetPassword?error=notfound";
        }

        if (password.equals(confirmPassword)) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            model.addAttribute("success", true);

            return "resetPassword";
        } else {
            model.addAttribute("error", "Passwords do not match. Please try again.");

            return "resetPassword";
        }
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }



}
