package com.timeco.application.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/productimages/**")
                .addResourceLocations("file:C:\\Users\\hp\\OneDrive\\Desktop\\PROJECT\\application\\src\\main\\resources\\static\\assets\\productImages");  // Change this to your actual image directory path
    }
}
