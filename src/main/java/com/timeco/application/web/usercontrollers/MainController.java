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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
        List<Product> products = productRepository.findAll();
        model.addAttribute("products",products);
        return"index";
    }

    //forgot password--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/forgotPassword")
    public String getFindAccountForm() {
        return "findAccount";
    }
    public boolean userExists(String email) {
        User user = userRepository.findByEmail(email); // Assuming you have a findByEmail method in your UserRepository
        return user != null; // Return true if the user exists, false otherwise
    }

    @PostMapping("/forgotPassword")
    public String sendPasswordResetOTP(@RequestParam("email") String email, HttpSession session, Model model) {

        if (userExists(email)) {
            otpService.sendRegistrationOtp(email); // Send OTP for password reset
            session.setAttribute("userEmail", email);
            return "redirect:/main/ResetOtpVerification"; // Return the HTML page for OTP confirmation
        } else {
            // Handle the case where the email is not found
            model.addAttribute("error", "Email not found. Please check your email and try again.");
            return "findAccount"; // Return the page indicating that the email was not found
        }
    }

    @GetMapping("/ResetOtpVerification")
    public String resetOtpConfirmForm(){
        return "resetOtpConfirm";
    }
    @GetMapping("/resendResetOtp")
    public String resendResetOtp(HttpSession session){
        String email=session.getAttribute("userEmail").toString();

        otpService.sendRegistrationOtp(email);
        return "redirect:/main/ResetOtpVerification";
    }


    @PostMapping("/ResetOtpVerification")
    public String resetOtpValidation(@RequestParam("otp") String enteredOtp,HttpSession session,Model model) {
        String email=session.getAttribute("userEmail").toString();
         // Retrieve the stored OTP for the user from your repository
        Otp storedOtp = otpRepository.findByEmail(email); // You should replace "userEmail" with the actual user's email
       if (storedOtp != null) {
            // Check if the entered OTP matches the stored OTP
            if (enteredOtp.equals(storedOtp.getOtp().toString())) {
                otpRepository.delete(storedOtp);
                return "redirect:/main/resetPassword"; // Replace with the appropriate view name for password reset
            } else {
                // Invalid OTP, show an error message
                model.addAttribute("error", "invalidOTP");
                return "resetOtpConfirm";
            }
       }
//       else {
//           // No stored OTP found for the user, add an error message and redirect to the forgotPassword page
//           redirectAttributes.addFlashAttribute("error", "noStoredOTP");
//           return "redirect:/ResetOtpVerification";
//       }
        return "redirect:/main/ResetOtpVerification";
    }


    @GetMapping("/resetPassword")
    public String getResetPasswordForm() {
        return "resetPassword";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,Model model,HttpSession session){
         System.out.println(password+"5436751672354367245367456743567435674251736541273654273645276345217365412736452736452738654127364527365");
         System.out.println(confirmPassword+"sdfisduhfoisjdugfjkusdagfsdlggggggggggggggglglglglglglglglglglglglglglglglglgg");
        String email=session.getAttribute("userEmail").toString();
        User user=userRepository.findByEmail(email);
        System.out.println(user.getEmail()+"8476287685762348957623849576348765892346598234765892346587932469999999999999999999999999999999999999999999999999999999999");

        if(user==null){
            // User with the provided email does not exist, show an error message
            return "redirect:/main/resetPassword?error=notfound";
        }

        // Update the user's password securely
        if (password.equals(confirmPassword)) {
            // Passwords match, perform password reset logic here
            user.setPassword(passwordEncoder.encode(password));
            System.out.println(user.getPassword()+"389261741114141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414141414");
            // Redirect to a success page or login page
            userRepository.save(user);
            model.addAttribute("success", true);

            return "resetPassword";
        } else {
            // Passwords don't match, show an error message
            model.addAttribute("error", "Passwords do not match. Please try again.");

            // Return to the reset password page
            return "resetPassword";
        }
        // Redirect to the login page with a success message
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }



}
