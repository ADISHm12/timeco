package com.timeco.application.exception;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BlockedUserAuthenticationFailureHandler implements AuthenticationFailureHandler  {



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
        // Check if the exception message indicates that the user is blocked
        if (exception.getMessage() != null && exception.getMessage().equals("User is blocked")) {
            // User is blocked, handle accordingly
            response.sendRedirect("/main/login?blocked=true");
        } else {
            // Handle other authentication failures (e.g., invalid username or password)
            response.sendRedirect("/main/login?error=true");
        }
    }

}