package com.timeco.application.web.usercontrollers;


import com.timeco.application.Dto.RegistrationDto;
import com.timeco.application.Repository.UserRepository;
import com.timeco.application.Repository.WalletRepository;
import com.timeco.application.Service.otpservice.OtpService;
import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.model.cart.Cart;
import com.timeco.application.model.user.LoginDto;
import com.timeco.application.model.user.User;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wishlist.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private WalletRepository walletRepository ;

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
    public String register(@Valid @ModelAttribute("user") RegistrationDto registrationDto, BindingResult bindingResult, HttpSession session,@RequestParam(name = "referralCode", required = false) String referralCode)
    {

        User verifyUser = userService.save(registrationDto);
        User userReferee = userRepository.findByReferralCode(referralCode);

        System.out.println(referralCode);
        if (referralCode != null && !referralCode.isEmpty()) {

            if (userReferee == null) {
                System.out.println(userReferee+"sdhfgjshkdgfkjhsgdfjksghdfjkhdsg");

                session.setAttribute("registrationError", "Referral code is invalid.");
                return "redirect:/registration/RegForm";

            }
            session.setAttribute("userReferee", userReferee);
        }
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
        session.setAttribute("userReferee", userReferee);

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
@ResponseBody
public ResponseEntity<String> otpRegistrationValidation(@ModelAttribute("otpBasedLoginAccount") LoginDto loginAccount, HttpSession session, RedirectAttributes redirectAttributes) {
    String emailId = session.getAttribute("validEmailId").toString();
    boolean flag = otpService.validateRegistrationOtp(emailId, loginAccount.getOtp());

    if (flag) {
        User verifyUser = (User) session.getAttribute("verifyCustomer");

        // Create a new cart and set it for the user
        Cart cart = new Cart();
        cart.setUser(verifyUser);
        verifyUser.setCart(cart);

        // Generate a referral code for the user
        String referralCode = userService.generateRandomCode(6);
        verifyUser.setReferralCode(referralCode);
        verifyUser = userRepository.save(verifyUser);


        User userReferee = (User) session.getAttribute("userReferee");

//            Wallet referredUserWallets = new Wallet();
//            referredUserWallets.setUser(verifyUser);
//            referredUserWallets.setBalance(0.0);
//            verifyUser.setWallets(referredUserWallets);
//            walletRepository.save(referredUserWallets);
//            userRepository.save(verifyUser);


        if (userReferee != null) {
            // Assuming the referral reward amounts are 200 for the referrer and 400 for the referred user
            double referrerReward = 200;
            double referredUserReward = 400;

            // Check if the referrer has a wallet, if not, create one
            if (userReferee.getWallet() == null) {
                Wallet referrerWallet = new Wallet(userReferee, 0.0);
                userReferee.setWallets(referrerWallet);
            }

            // Deposit rewards into the referrer's wallet
            userReferee.getWallet().deposit(referrerReward);
            userReferee.getWallet().recordTransaction(referrerReward, "REFERRAL_REWARD");

            // Check if the referred user has a wallet, if not, create one
            if (verifyUser.getWallet() == null) {
                Wallet referredUserWallet = new Wallet(verifyUser, 0.0);
                verifyUser.setWallets(referredUserWallet);
            }

            // Withdraw rewards from the referred user's wallet
            verifyUser.getWallet().deposit(referredUserReward);
            verifyUser.getWallet().recordTransaction(referredUserReward, "REFERRAL_REWARD");

            Wishlist wishlist = new Wishlist();
            verifyUser.setWishlist(wishlist);


            wishlist.setUser(verifyUser);
            // Save the updated wallets
            walletRepository.save(userReferee.getWallet());
            walletRepository.save(verifyUser.getWallet());
        }

        return ResponseEntity.ok().body("{\"message\": \"success\"}");
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect OTP. Please try again.\"}");
    }
}

    @GetMapping("/resendOTP")
    public String resendOTP(HttpSession session) {
        // Retrieve the email from the session
        String emailId = session.getAttribute("validEmailId").toString();
        // Send the registration OTP again
        otpService.sendRegistrationOtp(emailId);
        // Redirect to the OTP verification page
        return "redirect:/registration/otpVerification";
    }

}
