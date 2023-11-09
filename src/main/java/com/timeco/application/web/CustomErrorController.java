//package com.timeco.application.web;
//
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//@Controller
//public class CustomErrorController implements ErrorController {
//
//    private static final String PATH = "/error"; // Custom error path
//
//
//    public String getErrorPath() {
//        return PATH;
//    }
//
//    @RequestMapping(PATH)
//    public String handleError() {
//        // Custom error handling logic
//        return "errorPage";
//    }
//    @RequestMapping("/error/401")
//    public String UnAutherised(){
//        return "404";
//    }
//
//    @RequestMapping("/blocked")
//    public String UserBlocked(){
//        return "userblocked";
//    }
//}
