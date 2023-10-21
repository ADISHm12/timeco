package com.timeco.application.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.nio.channels.AcceptPendingException;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExeptionHandler {

    @ExceptionHandler(AcceptPendingException.class)
    public ModelAndView handleAcessDeniedExeption(AccessDeniedException ex)
    {
        ModelAndView modelAndView=new ModelAndView("redirect:/acessdenied");
        return modelAndView;
    }
}
