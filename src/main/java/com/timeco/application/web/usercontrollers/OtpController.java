package com.timeco.application.web.usercontrollers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OtpController {

    @GetMapping("/otpSignUp")
    public String OtpSignUp(){

        return"otpSignUp";

    }
    @GetMapping("/verify")
    public String verifyOtp(){
        return"verify";
    }

}
