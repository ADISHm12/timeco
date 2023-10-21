package com.timeco.application.web.usercontrollers;


import com.timeco.application.Dto.RegistrationDto;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Service.otpservice.OtpService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.user.LoginDto;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/registration")

public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public RegistrationDto registrationDto(){
        return new RegistrationDto();
    }


    @GetMapping("/RegForm")
    public String RegForm(){

        return "registration";

    }

    @PostMapping("/Register")
    public String register(@Valid @ModelAttribute("user") RegistrationDto registrationDto, BindingResult bindingResult, HttpSession session)
    {
        User verifyUser = userService.save(registrationDto);

        if (verifyUser == null) {
            session.setAttribute("registrationError", "The email address is already taken. Please choose another one.");
            return "redirect:/registration/RegForm"; // Redirect back to the registration page
        }
        if (bindingResult.hasErrors()) {
            // There are validation errors
            return "redirect:/registration/RegForm"; // Return to the registration form
        }


        otpService.sendRegistrationOtp(verifyUser.getEmail());
        session.setAttribute("validEmailId", verifyUser.getEmail());
        session.setAttribute("verifyCustomer", verifyUser);


        return "redirect:/registration/otpVerification?success";
    }

    @GetMapping("/otpVerification")
    public String otpVerification(Model model, HttpSession session,@ModelAttribute("error") String error) {

        LoginDto otpBasedLoginAccount = new LoginDto();
        model.addAttribute("otpBasedLoginAccount", otpBasedLoginAccount);
        model.addAttribute("error", error);
        return "userValidation";
    }

@PostMapping("/otpRegistrationValidation")
public String otpRegistrationValidation(@ModelAttribute("otpBasedLoginAccount") LoginDto LoginAccount, HttpSession session, RedirectAttributes redirectAttributes) {
    String emailId = session.getAttribute("validEmailId").toString();
    boolean flag = otpService.validateRegistrationOtp(emailId, LoginAccount.getOtp());
    if (flag) {
        User verifyUser = (User) session.getAttribute("verifyCustomer");
        Cart cart = new Cart();
        cart.setUser(verifyUser);
        verifyUser.setCart(cart);
        userRepository.save(verifyUser);

        return "redirect:/";
    } else {
        redirectAttributes.addFlashAttribute("error", "Incorrect OTP. Please try again.");
        return "redirect:/registration/otpVerification";
    }
}
    @GetMapping("/resendOTP")
    public String resendOTP(HttpSession session) {
        // Retrieve the email from the session
        String emailId = session.getAttribute("validEmailId").toString();
        System.out.println("99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999");
        // Send the registration OTP again
        otpService.sendRegistrationOtp(emailId);
        // Redirect to the OTP verification page
        return "redirect:/registration/otpVerification";
    }



}
