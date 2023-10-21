package com.timeco.application.web.usercontrollers;

import com.timeco.application.Repository.OtpRepository;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.otpservice.OtpService;
import com.timeco.application.Service.productservice.ProductService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.user.Otp;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Optional;

@Controller
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



    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

    @GetMapping("/")
    public String home(Principal principal,HttpSession session){
        if(principal==null){
            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            return "index";
        }
        String username=principal.getName();
        User user = userService.findUserByUsername(username);
        System.out.println(username+"777777777777777777777777777777777777777777777777777777");
        session.setAttribute("user",user);
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
            return "redirect:/ResetOtpVerification"; // Return the HTML page for OTP confirmation
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
        return "redirect:/ResetOtpVerification";
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
                return "redirect:/resetPassword"; // Replace with the appropriate view name for password reset
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
        return "redirect:/ResetOtpVerification";
    }


    @GetMapping("/resetPassword")
    public String getResetPasswordForm() {
        return "resetPassword";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,HttpSession session,Model model){
         String email=session.getAttribute("userEmail").toString();
        User user=userRepository.findByEmail(email);

        if(user==null){
            // User with the provided email does not exist, show an error message
            return "redirect:/resetPassword?error=notfound";
        }

        // Update the user's password securely
        if (password.equals(confirmPassword)) {
            // Passwords match, perform password reset logic here
            user.setPassword(passwordEncoder.encode(password));

            // Redirect to a success page or login page
            return "redirect:/login";
        } else {
            // Passwords don't match, show an error message
            model.addAttribute("error", "Passwords do not match. Please try again.");

            // Return to the reset password page
            return "resetPassword";
        }
        // Redirect to the login page with a success message
    }


}
